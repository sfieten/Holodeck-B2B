/*
 * Copyright (C) 2016 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.core.validation.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.HandlerDescription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Logger;
import org.holodeckb2b.common.errors.InvalidHeader;
import org.holodeckb2b.common.errors.OtherContentError;
import org.holodeckb2b.common.errors.ValueInconsistent;
import org.holodeckb2b.common.events.impl.HeaderValidationFailureEvent;
import org.holodeckb2b.common.handlers.AbstractBaseHandler;
import org.holodeckb2b.common.util.MessageUnitUtils;
import org.holodeckb2b.commons.util.Utils;
import org.holodeckb2b.core.HolodeckB2BCore;
import org.holodeckb2b.core.validation.ValidationResult;
import org.holodeckb2b.interfaces.config.IConfiguration;
import org.holodeckb2b.interfaces.core.IMessageProcessingContext;
import org.holodeckb2b.interfaces.customvalidation.IMessageValidationSpecification;
import org.holodeckb2b.interfaces.customvalidation.IMessageValidator;
import org.holodeckb2b.interfaces.customvalidation.MessageValidationError;
import org.holodeckb2b.interfaces.customvalidation.MessageValidationException;
import org.holodeckb2b.interfaces.messagemodel.IEbmsError;
import org.holodeckb2b.interfaces.messagemodel.IErrorMessage;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;
import org.holodeckb2b.interfaces.messagemodel.IPullRequest;
import org.holodeckb2b.interfaces.messagemodel.IReceipt;
import org.holodeckb2b.interfaces.messagemodel.IUserMessage;
import org.holodeckb2b.interfaces.pmode.IPMode;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;
import org.holodeckb2b.interfaces.storage.IMessageUnitEntity;

/**
 * Is the <i>IN FLOW</i> handler for checking conformance of messages to the messaging protocol specification.
 * <p>The validation performed has two modes, <i>basic</i> and <i>strict</i> validation. The <i>basic
 * validation</i> is only ensure that the messages can be processed by the Holodeck B2B Core. These validations don't
 * include detailed checks on allowed combinations or format of values, like for example the requirement from the
 * ebMS V3 Core Specification that the Service name must be an URL if no type is given. These are part of the
 * <i>strict validation</i> mode. The validation mode to use is specified in the configuration of the Holodeck B2B
 * gateway ({@link IConfiguration#useStrictHeaderValidation()}) or in the P-Mode ({@link
 * IPMode#useStrictHeaderValidation()}) with the strongest validation mode having priority.
 * <p>The actual conformance checks are performed by {@link IMessageValidator}s which are created by an
 * implementation of {@link AbstractHeaderValidatorFactory}. Which implementation the handler must use should be
 * configured in the handler's <i>validatorFactoryClass</i> parameter.
 * <p>Note that additional validations can be used for User Message message units by using custom validation (see
 * {@link IMessageValidationSpecification}). These validation can also include checks on payloads included in the
 * User Message and are separately configured in the P-Mode.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since  4.1.0
 */
public class HeaderValidationHandler extends AbstractBaseHandler {
    /**
     * Value to set as {@link MessageValidationError#details} to indicate the resulting invalidHdr for this
     * validation issue should be <i>ValueInconsistent</i> instead of <i>InvalidHeader</i>
     */
    public static final String VALUE_INCONSISTENT_REQ = "ValueInconsistent";

    /**
     * Name of the handler parameter that contains the class name of the validator factory class
     */
    public static final String P_VALIDATOR_FACTORY = "validatorFactoryClass";

    /**
	 * Maps holding the singletons of both the lax and strict header validator configs structured per message unit
	 * type
	 */
	private static Map<Class<? extends IMessageUnit>, HeaderValidationSpecification> laxValidatorSpecs;
	private static Map<Class<? extends IMessageUnit>, HeaderValidationSpecification> strictValidatorSpecs;

	/**
	 *
	 */
    @Override
	public void init(HandlerDescription handlerdesc) {
    	super.init(handlerdesc);
		// As the log is normally created dynamically when the handler is invoked (to include direction info),
    	//  we need to set it here
    	final Log log = LogFactory.getLog("org.holodeckb2b.msgproc." + (!Utils.isNullOrEmpty(handledMsgProtocol) ?
                										handledMsgProtocol + "." : "")
    												 + getHandlerDesc().getName());
    	log.trace("Retrieve the factory class for the header validators from configuration");
    	String factoryClass = null;
    	try {
    		factoryClass = (String) handlerdesc.getParameter(P_VALIDATOR_FACTORY).getValue();
    		if (!AbstractHeaderValidatorFactory.class.isAssignableFrom(Class.forName(factoryClass))) {
    			log.fatal("Specified class [" + factoryClass
    					  + "] is not an implementation of AbstractHeaderValidatorFactory!");
    			factoryClass = null;
    		}
    	} catch (NullPointerException | ClassCastException invalidParameter) {
    		log.fatal("Missing parameter specifying the validator factory class!");
    	} catch (ClassNotFoundException factoryNotAvailable) {
    		log.fatal("The specified factory class [" + factoryClass + "] is not available!");
    	}
    	if (factoryClass == null)
    		return;

    	// Create the validator specification instances
		//
		laxValidatorSpecs = new HashMap<>();
		laxValidatorSpecs.put(IUserMessage.class,
							 	new HeaderValidationSpecification(factoryClass, IUserMessage.class, false));
		laxValidatorSpecs.put(IPullRequest.class,
								new HeaderValidationSpecification(factoryClass, IPullRequest.class, false));
		laxValidatorSpecs.put(IReceipt.class,
								new HeaderValidationSpecification(factoryClass, IReceipt.class, false));
		laxValidatorSpecs.put(IErrorMessage.class,
								new HeaderValidationSpecification(factoryClass, IErrorMessage.class, false));
		strictValidatorSpecs = new HashMap<>();
		strictValidatorSpecs.put(IUserMessage.class,
								new HeaderValidationSpecification(factoryClass, IUserMessage.class, true));
		strictValidatorSpecs.put(IPullRequest.class,
								new HeaderValidationSpecification(factoryClass, IPullRequest.class, true));
		strictValidatorSpecs.put(IReceipt.class,
								new HeaderValidationSpecification(factoryClass, IReceipt.class, true));
		strictValidatorSpecs.put(IErrorMessage.class,
								new HeaderValidationSpecification(factoryClass, IErrorMessage.class, true));
	}

    @Override
    protected InvocationResponse doProcessing(IMessageProcessingContext procCtx, final Logger log) throws Exception {
    	if (laxValidatorSpecs == null || strictValidatorSpecs == null) {
    		log.fatal("Handler not correctly initialized, header validators not available!");
    		throw new AxisFault("Configuration error!");
    	}

        // Get all message units and then validate each one at configured mode
        Collection<IMessageUnitEntity> msgUnits = procCtx.getReceivedMessageUnits();

        if (!Utils.isNullOrEmpty(msgUnits)) {
            for (IMessageUnitEntity m : msgUnits) {
                // Determine the validation to use
                boolean useStrictValidation = shouldUseStrictMode(m);

                log.debug("Validate " + MessageUnitUtils.getMessageUnitName(m) + " header meta-data using "
                         + (useStrictValidation ? "strict" : "basic") + " validation");

                final IMessageValidationSpecification validationSpec = useStrictValidation ?
                						strictValidatorSpecs.get(MessageUnitUtils.getMessageUnitType(m)) :
                						laxValidatorSpecs.get(MessageUnitUtils.getMessageUnitType(m));
                try {
	                ValidationResult validationResult = HolodeckB2BCore.getValidationExecutor()
	                																	.validate(m, validationSpec);

	                if (validationResult == null || Utils.isNullOrEmpty(validationResult.getValidationErrors()))
	                    log.debug("Header of " + MessageUnitUtils.getMessageUnitName(m) + " [" + m.getMessageId()
	                            + "] successfully validated");
	                else {
	                	log.warn("Header of " + MessageUnitUtils.getMessageUnitName(m) + " [" + m.getMessageId()
	                            + "] is invalid!\n\tDetails: " + printErrors(validationResult.getValidationErrors()));
	                    for(IEbmsError e : createEbMSErrors(m.getMessageId(), validationResult.getValidationErrors()))
	                        procCtx.addGeneratedError(e);
	                    HolodeckB2BCore.getStorageManager().setProcessingState(m, ProcessingState.FAILURE);
	                    HolodeckB2BCore.getEventProcessor().raiseEvent(
	                					new HeaderValidationFailureEvent(m,
	                								validationResult.getValidationErrors().values().iterator().next()));
	                }
                } catch (MessageValidationException validationFailure) {
                	log.error("Error during header validaton [msgId={}] : {}", m.getMessageId(),
                				Utils.getExceptionTrace(validationFailure));
                	procCtx.addGeneratedError(new OtherContentError("Internal error", m.getMessageId()));
                    HolodeckB2BCore.getStorageManager().setProcessingState(m, ProcessingState.FAILURE);
                    HolodeckB2BCore.getEventProcessor().raiseEvent(
                												new HeaderValidationFailureEvent(m, validationFailure));
                }
            }
        }

        return InvocationResponse.CONTINUE;
    }

    /**
     * Helper method to print the description of all validation errors to the log.
     *
     * @param validationErrors	The validation errors found in the message
     * @return					List of the error descriptions
     */
    private String printErrors(final Map<String, Collection<MessageValidationError>> validationErrors) {
    	StringBuilder errList = new StringBuilder();
    	for(MessageValidationError validationError : validationErrors.values().iterator().next())
    		errList.append("\n\t\t").append(validationError.getDescription());
    	return errList.toString();
    }

    /**
     * Creates the required ebMS errors based on the found validation issues. As specified in the ebMS V3 Core
     * Specification some issues with the meta-data from header should be reported using the <i>ValueInconsistent<i>
     * instead of the <i>InvalidHeader</i> validationError. The header validators indicate this by setting the {@link
     * MessageValidationError#details} field to <i>"ValueInconsistent"<i>.
     *
     * @param validationErrors  The validation errors found in the message (grouped by validator)
     * @return  One or more <code>IEbMSError<code>s to signal the issues found during validation.
     */
    private Collection<IEbmsError> createEbMSErrors(final String messageId,
                              final Map<String, Collection<MessageValidationError>> validationErrors) {
        Collection<IEbmsError>  ebmsErrors = new ArrayList<>();

        // For all validation errors that don't need to be translated into a ValueInconsitent validationError we create one
        // InvalidHeader validationError which in the errorDetail contains a description of all issues found
        StringBuilder   invalidHeaderErrorDetails = new StringBuilder();
        // Add a line describing each validationError, count number of errors and check the maximum severity level
        int totalErrors = 0;
        MessageValidationError.Severity maxSeverity = MessageValidationError.Severity.Warning;
        for(MessageValidationError validationError : validationErrors.values().iterator().next()) {
            if (VALUE_INCONSISTENT_REQ.equals(validationError.getDetails()))
                ebmsErrors.add(new ValueInconsistent(validationError.getDescription(), messageId));
            else {
                // Error does not require special treatment, just add to description for the InvalidHeader invalidHdr
                invalidHeaderErrorDetails.append(validationError.getDescription()).append('\n');
                if (validationError.getSeverityLevel().compareTo(maxSeverity) > 0)
                    maxSeverity = validationError.getSeverityLevel();
                totalErrors++;
            }
        }
        if (totalErrors > 0) {
            // Add intro, including number of errors found.
            invalidHeaderErrorDetails.insert(0, " validation error(s) found in the message:\n")
                                     .insert(0, String.valueOf(totalErrors))
                                     .insert(0, "The message was found to be invalid!\n");
            // Create the validationError
            InvalidHeader invalidHdr = new InvalidHeader(invalidHeaderErrorDetails.toString(), messageId);
            invalidHdr.setSeverity(maxSeverity == MessageValidationError.Severity.Failure ? IEbmsError.Severity.failure
                                                                                         : IEbmsError.Severity.warning);
            ebmsErrors.add(invalidHdr);
        }

        return ebmsErrors;
    }

    /**
     * Helper method to determine which validation mode should be used for the given message unit.
     *
     * @param m     The message unit for which the validation mode should be determined
     * @return      <code>true</code> if strict validation should be used,<code>false</code> otherwise
     */
    private boolean shouldUseStrictMode(IMessageUnitEntity m) throws NullPointerException {
        // First get global setting which may be enough when it is set to strict
        boolean useStrictValidation = HolodeckB2BCore.getConfiguration().useStrictHeaderValidation();
        if (!useStrictValidation && !Utils.isNullOrEmpty(m.getPModeId()))
            useStrictValidation = HolodeckB2BCore.getPModeSet().get(m.getPModeId()).useStrictHeaderValidation();

        return useStrictValidation;
    }
}

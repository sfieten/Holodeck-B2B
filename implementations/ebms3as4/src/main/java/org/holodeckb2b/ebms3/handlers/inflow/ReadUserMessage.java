/**
 * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.ebms3.handlers.inflow;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.logging.log4j.Logger;
import org.holodeckb2b.common.handlers.AbstractBaseHandler;
import org.holodeckb2b.common.messagemodel.UserMessage;
import org.holodeckb2b.core.HolodeckB2BCore;
import org.holodeckb2b.core.validation.header.HeaderValidationHandler;
import org.holodeckb2b.ebms3.packaging.Messaging;
import org.holodeckb2b.ebms3.packaging.UserMessageElement;
import org.holodeckb2b.interfaces.core.IMessageProcessingContext;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;
import org.holodeckb2b.interfaces.storage.providers.StorageException;

/**
 * Is the handler in the <i>IN_FLOW</i> responsible for reading the meta data on an user message message unit from the
 * received message (if such a message unit is included in the message). This data is contained in the
 * <code>eb:UserMessage</code> element in the ebMS header.
 * <p>This handler will only read the meta-data that is available in the ebMS header without performing a validation on
 * it. This is done later in the {@link HeaderValidationHandler} and optionally in custom validators specified in the
 * P-Mode. The meta data is stored in an entity object which is stored in the database and added to the message
 * processing context. The processing state of the user message is set to {@link ProcessingState#RECEIVED}.
 * <p><b>NOTE:</b> The XML schema definition from the ebMS specification allows for multiple <code>eb:UserMessage</code>
 * elements in the ebMS header. In the Core Specification however the number of user message units in the message
 * is limited to just one. Holodeck B2B therefor only uses the first occurrence of <code>eb:UserMessage</code> and
 * ignores others.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class ReadUserMessage extends AbstractBaseHandler {

    @Override
    protected InvocationResponse doProcessing(final IMessageProcessingContext procContext, final Logger log)
    																					throws StorageException {
        // First get the ebMS header block, that is the eb:Messaging element
        final SOAPHeaderBlock messaging = Messaging.getElement(procContext.getParentContext().getEnvelope());

        if (messaging != null) {
            // Check if there is a user message unit
            log.trace("Check for UserMessage element");
            final Iterator<?> it = UserMessageElement.getElements(messaging);
            if (it.hasNext()) {
                log.debug("UserMessage found, read information from message");
                final OMElement umElement = (OMElement) it.next();
                // Read information into UserMessage entity object
                UserMessage userMessage = UserMessageElement.readElement(umElement);
                log.debug("Succesfully read user message meta data from header. Msg-id="
                        	+ userMessage.getMessageId());

                // Store it in both database and message context for further processing
                log.trace("Saving user message meta data to database and message context");
                procContext.setUserMessage(HolodeckB2BCore.getStorageManager().storeReceivedMessageUnit(userMessage));
                log.info("User message with msgId " + userMessage.getMessageId() + " succesfully read");
            }
        }

        return InvocationResponse.CONTINUE;
    }

}

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
package org.holodeckb2b.ebms3.handlers.inflow;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.ModuleConfiguration;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.Handler;
import org.holodeckb2b.common.messagemodel.EbmsError;
import org.holodeckb2b.common.messagemodel.ErrorMessage;
import org.holodeckb2b.common.messagemodel.PullRequest;
import org.holodeckb2b.common.messagemodel.Receipt;
import org.holodeckb2b.common.messagemodel.UserMessage;
import org.holodeckb2b.common.pmode.ErrorHandlingConfig;
import org.holodeckb2b.common.pmode.Leg;
import org.holodeckb2b.common.pmode.PMode;
import org.holodeckb2b.common.pmode.UserMessageFlow;
import org.holodeckb2b.common.testhelpers.HB2BTestUtils;
import org.holodeckb2b.common.testhelpers.HolodeckB2BTestCore;
import org.holodeckb2b.commons.util.MessageIdUtils;
import org.holodeckb2b.core.HolodeckB2BCore;
import org.holodeckb2b.core.MessageProcessingContext;
import org.holodeckb2b.core.storage.StorageManager;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.interfaces.core.IMessageProcessingContext;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.holodeckb2b.interfaces.general.ReplyPattern;
import org.holodeckb2b.interfaces.messagemodel.IEbmsError.Severity;
import org.holodeckb2b.interfaces.pmode.ILeg.Label;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;
import org.holodeckb2b.interfaces.storage.IErrorMessageEntity;
import org.holodeckb2b.interfaces.storage.IPullRequestEntity;
import org.holodeckb2b.interfaces.storage.IReceiptEntity;
import org.holodeckb2b.interfaces.storage.IUserMessageEntity;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created at 12:07 15.03.17
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class DetermineErrorReportingTest {
	private static StorageManager storageManager;

    private DetermineErrorReporting handler;

    private static HolodeckB2BTestCore		testCore;

    @BeforeAll
    public static void setUpClass() throws Exception {
    	testCore = new HolodeckB2BTestCore();
    	storageManager = testCore.getStorageManager();
        HolodeckB2BCoreInterface.setImplementation(testCore);
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        testCore.cleanStorage();
    }

    @BeforeEach
    public void setUp() throws Exception {
        handler = new DetermineErrorReporting();
        ModuleConfiguration moduleDescr = new ModuleConfiguration("test", null);
        moduleDescr.addParameter(new Parameter("HandledMessagingProtocol", "TEST"));
        HandlerDescription handlerDescr = new HandlerDescription();
        handlerDescr.setParent(moduleDescr);
        handler.init(handlerDescr);
    }

    @After
    public void tearDown() throws Exception {
        HolodeckB2BCore.getPModeSet().removeAll();
    }

    /**
     * Error that references the specific message unit, which is not a pull request and has no specific error handling
     * config
     */
    @Test
    public void testDefaultConfig() throws Exception {

        // Setup P-Mode
        PMode pmode = HB2BTestUtils.create1WayReceivePMode();
        Leg leg = pmode.getLeg(Label.REQUEST);
        leg.setUserMessageFlow(new UserMessageFlow());
        HolodeckB2BCore.getPModeSet().add(pmode);

        // Prepare message in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        userMessage.setPModeId(pmode.getId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setServerSide(true);
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setRefToMessageInError(userMessageEntity.getMessageId());
        error1.setErrorDetail("Some error for testing.");
        ErrorMessage errorMsg = new ErrorMessage(error1);
        errorMsg.setRefToMessageId(userMessage.getMessageId());
        errorMsg.setPModeId(pmode.getId());
        procCtx.addSendingError(storageManager.storeOutGoingMessageUnit(errorMsg));

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(procCtx.responseNeeded());
        assertEquals(1, procCtx.getSendingErrors().size());
    }

    /**
     * Error that references the pull request
     */
    @Test
    public void testErrorRefsPullRequest() throws Exception {

        // Setup P-Mode
        PMode pmode = HB2BTestUtils.create1WaySendPushPMode();
        Leg leg = pmode.getLeg(Label.REQUEST);
        pmode.setMepBinding(EbMSConstants.ONE_WAY_PULL);
        UserMessageFlow umFlow = new UserMessageFlow();
        ErrorHandlingConfig errorHandlingConfig = new ErrorHandlingConfig();
        errorHandlingConfig.setPattern(ReplyPattern.CALLBACK);
        umFlow.setErrorHandlingConfiguration(errorHandlingConfig);
        leg.setUserMessageFlow(umFlow);
        HolodeckB2BCore.getPModeSet().add(pmode);

        // Prepare message in error
        PullRequest pullRequest = new PullRequest();
        pullRequest.setMPC("some_mpc");
        pullRequest.setMessageId(MessageIdUtils.createMessageId());
        IPullRequestEntity pullRequestEntity = storageManager.storeReceivedMessageUnit(pullRequest);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setServerSide(true);
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setPullRequest(pullRequestEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setRefToMessageInError(pullRequestEntity.getMessageId());
        error1.setErrorDetail("Some error for testing.");
        ErrorMessage errorMsg = new ErrorMessage(error1);
        errorMsg.setPModeId(pmode.getId());
        errorMsg.setRefToMessageId(pullRequest.getMessageId());
        procCtx.addSendingError(storageManager.storeOutGoingMessageUnit(errorMsg));

        try {
        	assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(procCtx.responseNeeded());
        assertEquals(1, procCtx.getSendingErrors().size());
    }

    /**
     * Error that references the specific message unit, which is not a pull request and has set error handling
     * config to report async
     */
    @Test
    public void testAsyncReporting() throws Exception {
        // Setup P-Mode
        PMode pmode = HB2BTestUtils.create1WayReceivePMode();
        Leg leg = pmode.getLeg(Label.REQUEST);

        // Prepare message in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        userMessage.setPModeId(pmode.getId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);

        UserMessageFlow umFlow = new UserMessageFlow();
        ErrorHandlingConfig errorHandlingConfig = new ErrorHandlingConfig();
        errorHandlingConfig.setPattern(ReplyPattern.CALLBACK);
        umFlow.setErrorHandlingConfiguration(errorHandlingConfig);
        leg.setUserMessageFlow(umFlow);

        HolodeckB2BCore.getPModeSet().add(pmode);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setRefToMessageInError(userMessageEntity.getMessageId());
        error1.setErrorDetail("Some error for testing.");
        ErrorMessage errorMsg = new ErrorMessage(error1);
        errorMsg.setRefToMessageId(userMessage.getMessageId());
        errorMsg.setPModeId(pmode.getId());
        IErrorMessageEntity errorSig = storageManager.storeOutGoingMessageUnit(errorMsg);
        procCtx.addSendingError(errorSig);

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertFalse(procCtx.responseNeeded());
        assertTrue(procCtx.getSendingErrors().isEmpty());
        assertEquals(ProcessingState.READY_TO_PUSH, errorSig.getCurrentProcessingState().getState());
    }

    /**
     * Error that references a specific message unit but has no P-Mode associated
     */
    @Test
    public void testNoPModeButWithRef() throws Exception {

        // Prepare message in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setServerSide(true);
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setRefToMessageInError(userMessageEntity.getMessageId());
        error1.setErrorDetail("Some error for testing.");
        ErrorMessage errorMsg = new ErrorMessage(error1);
        errorMsg.setRefToMessageId(userMessage.getMessageId());
        procCtx.addSendingError(storageManager.storeOutGoingMessageUnit(errorMsg));

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(procCtx.responseNeeded());
        assertEquals(1, procCtx.getSendingErrors().size());
    }

    /**
     * Error without reference but with only failed messages
     */
    @Test
    public void testNoRefAllFailed() throws Exception {
        // Prepare messages in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);
        storageManager.setProcessingState(userMessageEntity, ProcessingState.FAILURE);

        Receipt receipt = new Receipt();
        receipt.setMessageId(MessageIdUtils.createMessageId());
        IReceiptEntity rcptEntity = storageManager.storeReceivedMessageUnit(receipt);
        storageManager.setProcessingState(rcptEntity, ProcessingState.FAILURE);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setServerSide(true);
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);
        procCtx.addReceivedReceipt(rcptEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setErrorDetail("Some error for testing.");
        error1.setSeverity(Severity.failure);
        ErrorMessage errorMsg = new ErrorMessage(error1);
        procCtx.addSendingError(storageManager.storeOutGoingMessageUnit(errorMsg));

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(procCtx.responseNeeded());
        assertEquals(1, procCtx.getSendingErrors().size());
    }

    /**
     * Error without reference conflicting with successfully processed messages
     */
    @Test
    public void testNoRefConflict() throws Exception {
        // Prepare messages in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);
        storageManager.setProcessingState(userMessageEntity, ProcessingState.FAILURE);

        Receipt receipt = new Receipt();
        receipt.setMessageId(MessageIdUtils.createMessageId());
        IReceiptEntity rcptEntity = storageManager.storeReceivedMessageUnit(receipt);
        storageManager.setProcessingState(rcptEntity, ProcessingState.DONE);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setServerSide(true);
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);
        procCtx.addReceivedReceipt(rcptEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setErrorDetail("Some error for testing.");
        error1.setSeverity(Severity.failure);
        ErrorMessage errorMsg = new ErrorMessage(error1);
        IErrorMessageEntity errorSig = storageManager.storeOutGoingMessageUnit(errorMsg);
        procCtx.addSendingError(errorSig);

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertFalse(procCtx.responseNeeded());
        assertTrue(procCtx.getSendingErrors().isEmpty());
        assertEquals(ProcessingState.DONE, errorSig.getCurrentProcessingState().getState());
        assertEquals(ProcessingState.WARNING,
        				errorSig.getProcessingStates().get(errorSig.getProcessingStates().size()-2).getState());
    }

    /**
     * Error that references a specific message unit that was received as response but has no P-Mode associated
     */
    @Test
    public void testNoPModeOnResponse() throws Exception {
    	// Prepare messages in error
    	UserMessage userMessage = new UserMessage();
        userMessage.setMessageId(MessageIdUtils.createMessageId());
        IUserMessageEntity userMessageEntity = storageManager.storeReceivedMessageUnit(userMessage);
        storageManager.setProcessingState(userMessageEntity, ProcessingState.FAILURE);

        // Prepare msg ctx
        MessageContext mc = new MessageContext();
        mc.setFLOW(MessageContext.IN_FLOW);

        IMessageProcessingContext procCtx = MessageProcessingContext.getFromMessageContext(mc);
        procCtx.setUserMessage(userMessageEntity);

        // Create the Error Signal referencing the message unit
        EbmsError error1 = new EbmsError();
        error1.setErrorDetail("Some error for testing.");
        error1.setSeverity(Severity.failure);
        ErrorMessage errorMsg = new ErrorMessage(error1);
        errorMsg.setRefToMessageId(userMessage.getMessageId());
        IErrorMessageEntity errorSig = storageManager.storeOutGoingMessageUnit(errorMsg);
        procCtx.addSendingError(errorSig);

        try {
            assertEquals(Handler.InvocationResponse.CONTINUE, handler.invoke(mc));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertFalse(procCtx.responseNeeded());
        assertTrue(procCtx.getSendingErrors().isEmpty());
        assertEquals(ProcessingState.DONE, errorSig.getCurrentProcessingState().getState());
        assertEquals(ProcessingState.WARNING,
        				errorSig.getProcessingStates().get(errorSig.getProcessingStates().size()-2).getState());
    }
}
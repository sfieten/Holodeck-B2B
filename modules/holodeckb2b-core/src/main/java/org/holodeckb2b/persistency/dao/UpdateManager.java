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
package org.holodeckb2b.persistency.dao;

import java.util.Collection;
import java.util.Date;
import org.holodeckb2b.common.messagemodel.ErrorMessage;
import org.holodeckb2b.common.messagemodel.MessageUnit;
import org.holodeckb2b.common.messagemodel.PullRequest;
import org.holodeckb2b.common.messagemodel.Receipt;
import org.holodeckb2b.common.messagemodel.UserMessage;
import org.holodeckb2b.common.util.MessageIdGenerator;
import org.holodeckb2b.common.util.Utils;
import org.holodeckb2b.interfaces.messagemodel.IErrorMessage;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;
import org.holodeckb2b.interfaces.messagemodel.IPayload;
import org.holodeckb2b.interfaces.messagemodel.IPullRequest;
import org.holodeckb2b.interfaces.messagemodel.IReceipt;
import org.holodeckb2b.interfaces.messagemodel.IUserMessage;
import org.holodeckb2b.interfaces.persistency.PersistenceException;
import org.holodeckb2b.interfaces.persistency.dao.IUpdateManager;
import org.holodeckb2b.interfaces.persistency.entities.IErrorMessageEntity;
import org.holodeckb2b.interfaces.persistency.entities.IMessageUnitEntity;
import org.holodeckb2b.interfaces.persistency.entities.IUserMessageEntity;
import org.holodeckb2b.interfaces.pmode.ILeg;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;

/**
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since HB2B_NEXT_VERSION
 */
public class UpdateManager implements IUpdateManager {

    /**
     * The update manager provided by the persistency provider which does the "real" work of storing the data
     */
    private IUpdateManager  parent;

    /**
     * Creates a new facade to the given update manager of the persistency provider so other Core classes can update the
     * meta-data of a message unit.
     *
     * @param parent    The update manager from the persistency provider
     */
    public UpdateManager(final IUpdateManager parent) {
        this.parent = parent;
    }

    /**
     * Creates a new persistency object to store the meta-data of the given message unit that is received by Holodeck
     * B2B.
     *
     * @param <T>           Limits the <code>messageUnit</code> to message units types
     * @param <V>           Only entity objects will be returned, V and T will be of the same message type
     * @param messageUnit   The meta-data on message unit that should be stored in the new persistent object
     * @return              The created persistency object.
     * @throws PersistenceException        If an error occurs when saving the new message unit to the database.
     */
    public <T extends IMessageUnit, V extends IMessageUnitEntity> V storeIncomingMessageUnit(T messageUnit)
                                                                                           throws PersistenceException {
        MessageUnit tempObject = createTempObject(messageUnit);
        // Set correct direction
        tempObject.setDirection(IMessageUnit.Direction.IN);
        tempObject.setProcessingState(ProcessingState.RECEIVED);
        return parent.storeMessageUnit(tempObject);
    }

    /**
     * Creates a new persistency object to store the meta-data of the given message unit that is sent by Holodeck
     * B2B.
     *
     * @param <T>           Limits the <code>messageUnit</code> to message units types
     * @param <V>           Only entity objects will be returned, V and T will be of the same message type
     * @param messageUnit   The meta-data on message unit that should be stored in the new persistent object
     * @return              The created persistency object.
     * @throws PersistenceException        If an error occurs when saving the new message unit to the database.
     */
    public <T extends IMessageUnit, V extends IMessageUnitEntity> V storeOutGoingMessageUnit(T messageUnit)
                                                                                        throws PersistenceException {
        MessageUnit tempObject = createTempObject(messageUnit);
        // Set correct direction
        tempObject.setDirection(IMessageUnit.Direction.IN);
        if (messageUnit instanceof IUserMessage || messageUnit instanceof IPullRequest)
            tempObject.setProcessingState(ProcessingState.SUBMITTED);
        else
            tempObject.setProcessingState(ProcessingState.CREATED);

        if (Utils.isNullOrEmpty(tempObject.getMessageId()))
            tempObject.setMessageId(MessageIdGenerator.createMessageId());
        if (tempObject.getTimestamp() == null)
            tempObject.setTimestamp(new Date());

        V storedObject = parent.storeMessageUnit(tempObject);
        parent.setLeg(storedObject, ILeg.Label.REQUEST);

        return storedObject;
    }

    /**
     * Creates a new persistency object to store the meta-data of the given message unit.
     *
     * @param messageUnit The meta-data on message unit that should be stored in the new persistent object
     * @return The created persistency object.
     * @throws PersistenceException If an error occurs when saving the new message unit to the database.
     */
    @Override
    public <T extends IMessageUnit, V extends IMessageUnitEntity> V storeMessageUnit(T messageUnit) throws PersistenceException {
        return null;
    }

    /**
     * Sets the ID of the P-Mode that defines how the message unit should be processed.
     *
     * @param msgUnit   The entity object representing the message unit
     * @param pmodeId   The ID of the P-Mode that defines how the message unit must be processed
     * @throws PersistenceException If an error occurs when saving the P-Mode ID to the database
     */
    public void setPModeId(final IMessageUnitEntity msgUnit, final String pmodeId) throws PersistenceException {
        parent.setPModeId(msgUnit, pmodeId);
    }

    /**
     * Updates the processing state of the given message unit to the specified state.<br>
     * The new processing state's  start time will be set to the current time.
     *
     * @param msgUnit           The entity object representing the message unit
     * @param newProcState      The new processing state
     * @throws PersistenceException When a problem occurs updating the processing state of the message unit
     */
    public void setProcessingState(final IMessageUnitEntity msgUnit, final ProcessingState newProcState)
                                                                                        throws PersistenceException {
        this.setProcessingState(msgUnit, msgUnit.getCurrentProcessingState().getState(), newProcState);
    }

    /**
     * Updates the processing state of the given message unit to the specified state.
     * <p>Before changing the processing state this method checks that the message unit is in a specific state. The
     * check and change need to be executed in one transaction to ensure that no other thread can make changes to the
     * message unit's processing state.<br>
     * The new processing state's  start time will be set to the current time.
     *
     * @param msgUnit           The entity object representing the message unit
     * @param currentProcState  The required current processing state of the message unit
     * @param newProcState      The new processing state
     * @return                  <code>true</code> if the processing state was changed,<br>
     *                          <code>false</code> if not because the current processing state has already changed
     * @throws PersistenceException When a problem occurs updating the processing state of the message unit
     */
    public boolean setProcessingState(final IMessageUnitEntity msgUnit, final ProcessingState currentProcState
                                                                      , final ProcessingState newProcState)
                                                                                        throws PersistenceException {
        //@todo Check if the processing state is allowed and ensure events are triggered using the ProcessingStateManager
        return parent.setProcessingState(msgUnit, currentProcState, newProcState);
    }

    /**
     * Sets the multi-hop indicator of the message unit.
     *
     * @param msgUnit       The entity object representing the message unit which multi-hop indicator should be set
     * @param isMultihop    The indicator whether this message unit uses multi-hop
     * @throws PersistenceException When a database error occurs while updating the entity object
     */
    public void setMultiHop(final IMessageUnitEntity msgUnit, final boolean isMultihop) throws PersistenceException {
        parent.setMultiHop(msgUnit, isMultihop);
    }

    /**
     * Sets the leg on which the message unit is exchanged.
     *
     * @param msgUnit       The entity object representing the message unit which leg should be set
     * @parem legLabel      The label of the leg on which the message unit is exchanged
     * @throws PersistenceException When a database error occurs while updating the entity object
     */
    public void setLeg(final IMessageUnit msgUnit, final ILeg.Label legLabel) throws PersistenceException {
        parent.setLeg(msgUnit, legLabel);
    }

    /**
     * Sets the information on the payloads included in the given User Message.
     * <p>Note that this is really a <b>setter</b> and overwrites any current payload meta-data.
     *
     * @param userMessage   The entity object representing the User Message
     * @param payloadInfo   The meta-data on the payloads included with the User Message which must be persisted
     * @throws PersistenceException     If an error occurs when saving the payload meta-data to the database
     */
    public void setPayloadInformation(final IUserMessageEntity userMessage,
                                      final Collection<? extends IPayload> payloadInfo) throws PersistenceException {
        parent.setPayloadInformation(userMessage, payloadInfo);
    }

    /**
     * Sets the indicator on an error message whether a SOAP Fault should be added to the message when sending the
     * error message.
     * <p>Note that the decision whether the SOAP Fault will be added to the message is taken when the actual message
     * is constructed and depends also on the other message units that are included in the same message.
     *
     * @param errorMessage      The entity object representing the Error Message
     * @param addSOAPFault      The indicator whether to add a SOAP fault.
     * @throws PersistenceException  If an error occurs when updating the indicator
     */
    public void setAddSOAPFault(final IErrorMessageEntity errorMessage, final boolean addSOAPFault)
                                                                                        throws PersistenceException {
        parent.setAddSOAPFault(errorMessage, addSOAPFault);
    }

    /**
     * Deletes the meta-data of the given message unit from the database.
     *
     * @param messageUnit       The {@link IMessageUnitEntity} object to be deleted
     * @throws PersistenceException     When a problem occurs while removing the message unit from the database.
     */
    public void deleteMessageUnit(IMessageUnitEntity messageUnit) throws PersistenceException {
        parent.deleteMessageUnit(messageUnit);
    }

    /**
     * Helper method to create a temporary message unit object to enable setting of the correct processing state and
     * generation of a messageId.
     *
     * @param messageUnit   The message unit that needs to be stored
     * @return              A {@link MessageUnit} representing the given message unit
     */
    private MessageUnit createTempObject(IMessageUnit messageUnit) {
        if (messageUnit instanceof IUserMessage)
            return new UserMessage((IUserMessage) messageUnit);
        else if (messageUnit instanceof IPullRequest)
            return new PullRequest((IPullRequest) messageUnit);
        else if (messageUnit instanceof IReceipt)
            return new Receipt((IReceipt) messageUnit);
        else
            return new ErrorMessage((IErrorMessage) messageUnit);
    }
}

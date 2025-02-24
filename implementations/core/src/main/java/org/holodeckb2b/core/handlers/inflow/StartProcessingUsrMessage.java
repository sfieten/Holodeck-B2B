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
package org.holodeckb2b.core.handlers.inflow;

import org.apache.logging.log4j.Logger;
import org.holodeckb2b.common.handlers.AbstractUserMessageHandler;
import org.holodeckb2b.core.HolodeckB2BCore;
import org.holodeckb2b.interfaces.core.IMessageProcessingContext;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;
import org.holodeckb2b.interfaces.storage.IUserMessageEntity;
import org.holodeckb2b.interfaces.storage.providers.StorageException;

/**
 * Is the <i>IN_FLOW</i> handler that starts the process of delivering the user message message unit to the business
 * application by changing the message units processing state to {@link ProcessingState#PROCESSING}.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class StartProcessingUsrMessage extends AbstractUserMessageHandler {

    @Override
    protected InvocationResponse doProcessing(final IUserMessageEntity um, final IMessageProcessingContext procCtx,
    										  final Logger log) throws StorageException {
        final String msgId = um.getMessageId();
        String t=msgId;
        log.trace("Change processing state to indicate start of processing of message [" + msgId + "]" );
        if (!HolodeckB2BCore.getStorageManager().setProcessingState(um, ProcessingState.PROCESSING)) {
            log.warn("User message [msgId= " + msgId + "] is already being processed");
            // Remove the User Message from the context to prevent further processing
            procCtx.setUserMessage(null);
        } else
            log.trace("User message [msgId= " + msgId + "] is ready for processing");

        return InvocationResponse.CONTINUE;
    }
}

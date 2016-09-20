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
package org.holodeckb2b.ebms3.handlers.outflow;

import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.common.handler.BaseHandler;
import org.holodeckb2b.ebms3.persistency.entities.EbmsMessage;

/**
 * Is the handler in the <i>OUT_FLOW</i> responsible for creating a {@link EbmsMessage} object that contains the meta-
 * data on the sent ebMS message. Note that the ebMS/SOAP message itself does not have meta-data in the message itself,
 * but it is really meta-data on the message like timestamp, information from HTTP headers.
 * <p>The meta data is stored in an {@link EbmsMessage} entity object which is stored in the database and added to the
 * message context under key {@link MessageContextProperties#EBMS_MESSAGE}.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since  HB2B_NEXT_VERSION
 */
public class WriteEbmsMessage extends BaseHandler {

    @Override
    protected byte inFlows() {
        return OUT_FLOW | OUT_FAULT_FLOW;
    }

    @Override
    protected InvocationResponse doProcessing(MessageContext mc) throws Exception {

        /*
        @todo: Assume that this handler runs as one of the last in the out flow and all information is know in the mc
        */
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

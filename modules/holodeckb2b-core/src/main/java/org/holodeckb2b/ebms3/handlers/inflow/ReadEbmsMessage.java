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

import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.common.handler.BaseHandler;
import org.holodeckb2b.ebms3.constants.MessageContextProperties;

/**
 * Is the handler in the <i>IN_FLOW</i> responsible for reading the meta data on the received ebMS message (if such a
 * message is available). Note that the ebMS / SOAP message itself does not have meta-data in the message itself, but it
 * is really meta-data on the message like timestamp, information from HTTP headers.
 * <p>The meta data is stored in an {@link EbmsMessage} entity object which is stored in the database and added to the
 * message context under key {@link MessageContextProperties#EBMS_MESSAGE}.
 * <p>The meta-data also includes the collection of ebMS message units contained in the message. These are not read here
 * but in successive <i>"read"</i> handlers.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since  HB2B_NEXT_VERSION
 */
public class ReadEbmsMessage extends BaseHandler {

    @Override
    protected byte inFlows() {
        return IN_FLOW | IN_FAULT_FLOW;
    }

    @Override
    protected InvocationResponse doProcessing(MessageContext mc) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

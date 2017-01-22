/*
 * Copyright (C) 2015 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.multihop;

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.common.handler.BaseHandler;
import org.holodeckb2b.axis2.MessageContextUtils;
import org.holodeckb2b.packaging.Messaging;
import org.holodeckb2b.interfaces.persistency.entities.IMessageUnitEntity;
import org.holodeckb2b.module.HolodeckB2BCore;
import org.holodeckb2b.persistency.dao.UpdateManager;


/**
 * Is the <i>IN_FLOW</i> handler that checks whether the received message was sent through the I-Cloud. This is
 * determined by the actor/role set on the <code>eb:Messaging</code> element. If the <i>nextMSH</i> is targeted it is
 * assumed that the message was sent through the I-Cloud.
 * <p>All message units in the received message are updated to indicate whether they use multi-hop. In the current
 * version the <i>RoutingInput</i> from the message is not saved with the message units, so only multi-hop replies to
 * User Messages can be created. This however allows to support the AS4 Multi-hop profile which does not specify support
 * for signals on signals.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
public class CheckFromICloud extends BaseHandler {

    @Override
    protected byte inFlows() {
        return IN_FLOW | IN_FAULT_FLOW;
    }

    @Override
    protected InvocationResponse doProcessing(final MessageContext mc) throws Exception {
        // First get the ebMS header block, that is the eb:Messaging element
        final SOAPHeaderBlock messaging = Messaging.getElement(mc.getEnvelope());

        if (messaging != null) {
            // Check if the message was received through I-Cloud, i.e. if eb:Messaging header was targeted to
            // nextMSH SOAP role/actor
            final boolean isMultiHop = MultiHopConstants.NEXT_MSH_TARGET.equalsIgnoreCase(messaging.getRole());

            if (isMultiHop) {
                log.debug("Message received through I-Cloud, update message units");
                UpdateManager updateManager = HolodeckB2BCore.getUpdateManager();
                for (final IMessageUnitEntity mu : MessageContextUtils.getReceivedMessageUnits(mc))
                    updateManager.setMultiHop(mu, isMultiHop);
            }
        }

        return InvocationResponse.CONTINUE;
    }

}

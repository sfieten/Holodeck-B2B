/*
 * Copyright (C) 2016 The Holodeck B2B Team.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.interfaces.messagemodel;

import java.util.Collection;
import java.util.Date;

/**
 * Represents the overall ebMS / SOAP message that contains the individual ebMS message units, i.e. the SOAP envelope.
 * Because the SOAP Envelope itself has no identifier, transport meta-data must be used to identify it. In addition
 * to these transport meta-data this interface also adds a <i>Holodeck B2B CoreId</i> attribute for the whole message.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since HB2B_NEXT_VERSION
 */
public interface IEbmsMessage {

    /**
     * Gets the Holodeck B2B internal identifier assigned to this specific <i>ebMS message</i>.
     *
     * @return  The unique Holodeck B2B internal identifier for this <code>EbmsMessage</code> instance
     */
    public String getHolodeckB2BCoreId();

    /**
     * Gets the timestamp when the message was received or sent by Holodeck B2B. This is the time as registered by the
     * Holodeck B2B Core and it may be slightly different from the actual time a message was received or sent on the
     * transport level.
     *
     * @return  The timestamp the message was received or sent by Holodeck B2B.
     */
    public Date   getTimestamp();

    /**
     * Gets the list of <i>ebMS message units</i> that is contained in this ebMS message.
     * <p>For incoming messages only the message units that Holodeck B2B could read from the ebMS Message are available.
     * It is therefore possible that an empty collection is returned when no message could be read from the received
     * message.
     *
     * @return  The {@link Collection} of {@link IMessageUnit} objects that represent the message units in this ebMS
     *          message.<br>
     *          NOTE: Any ordering in the collection does not relate to the order the message units appear in the
     *          message.
     */
    public Collection<IMessageUnit>   getMessageUnits();

    /**
     * Gets the URL to which this message was addressed as known to Holodeck B2B.
     * <p>Note that this URL may be different from the URL as it is known by the other MSH due to URL rewriting by
     * proxies.
     *
     * @return  The String representation of the URL the message was addressed to
     */
    public String getTargetURL();

    /**
     * Gets the remote hostname or IP address the message is sent to or originates from as known by Holodeck B2B.
     * <p>Note that this address may be different from the actual address of the other MSH because intermediary
     * components like proxies reroute the message.
     *
     * @return The hostname or IP address of the other MSH (as known by Holodeck B2B)
     */
    public String getRemoteAddress();

    /**
     * Gets information on the remote MSH implementation as provided in the <i>User-Agent</i> or <i>Server</i> HTTP
     * header.
     *
     * @return Information on the remote MSH implementation as provided in HTTP headers
     */
    public String getRemoteSoftwareId();
}

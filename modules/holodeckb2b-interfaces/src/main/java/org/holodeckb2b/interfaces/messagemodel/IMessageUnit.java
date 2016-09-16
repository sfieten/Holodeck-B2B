/**
 * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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


import java.util.Date;

/**
 * Is a general representation for all types of ebMS message units and defines methods to access the information
 * available to all ebMS message units. This is the information contained in the <code>eb:MessageInfo</code> and child
 * elements of the ebMS messaging header. See ebMS V3 Core specification, section 5 for more information on the message
 * header. Added is the relation to the P-Mode that governs the processing of the message unit.
 * <p>Descendants of this base interface define how information specific for a type of message unit can be accessed.
 * Together they are used in Holodeck B2B to define the interfaces between the Core and the external <i>business</i>
 * applications. This decoupling allows for more easy extension of both the Core as the external functionality.
 * <p><b>NOTE:</b> The information that is available at some point during runtime depends on the context of processing!
 * If for example a user message is submitted to Holodeck B2B as a response of a Two-Way MEP, only the
 * <i>RefToMessageId</i> might be known.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @see IUserMessage
 * @see ISignalMessage
 * @see IErrorMessage
 * @see IPullRequest
 * @see IReceipt
 */
public interface IMessageUnit {

    /**
     * Gets the Holodeck B2B internal identifier for this specific <code>MessageUnit</code> instance.
     * <p>As documented at {@link #getMessageId()} the ebMS MessageId can not be used as the (unique) identifier of
     * a specific message unit instance so this internal identifier is introduced. The CoreId is included in this
     * interface to enable external components to reference a specific message unit when needed.
     * <p><b>NOTE:</b> This identifier is assigned by the Holodeck B2B Core and only available when the instance is/has
     * been processed by it. It SHOULD NOT be set by external component as it will be overwritten by the Core.
     *
     * @return  The unique Holodeck B2B internal identifier for this <code>MessageUnit</code> instance
     * @since HB2B_NEXT_VERSION
     */
    public String getHolodeckB2BCoreId();

    /**
     * Gets the meta-data of the ebMS message this message unit is contained in.
     * <p><b>NOTE:</b> The relation between the message unit and the ebMS message is created by the Holodeck B2B Core
     * during processing of the message. External components therefore SHOULD NOT set the relation. It also implies that
     * an outgoing message unit does not need to be associated with a parent ebMS message when it is not yet sent.
     *
     * @return The meta-data of the containing ebMS message as a {@link IEbmsMessage} object
     * @since HB2B_NEXT_VERSION
     */
    public IEbmsMessage getParentEbmsMessage();

    /**
     * Gets the timestamp when the message unit was created.
     * <p>Corresponds to the <code>MessageInfo/Timestamp</code> element. See section 5.2.2.1 of the ebMS Core
     * specification.
     *
     * @return  The timestamp when the message unit was created as a {@link Date}
     */
    public Date getTimestamp();

    /**
     * Gets the message id of the message unit.
     * <p>Corresponds to the <code>MessageInfo/MessageId</code> element. See section 5.2.2.1 of the ebMS Core
     * specification.
     *
     * @return  The message id as a globally unique identifier conforming to RFC2822.
     */
    public String getMessageId();

    /**
     * Get the message id of the message unit to which this message unit is a response.
     * <p>Corresponds to the <code>MessageInfo/RefToMessageId</code> element. See section 5.2.2.1 of the ebMS Core
     * specification.
     * <p>NOTE: Although the <code>eb:MessageId</code> values must be globally unique identifier it can not be used as
     * the primary identifier of a specific instance because a message can be resent and there is also no guarantee that
     * other implementations will adhere to this requirement. Therefore Holodeck B2B uses its own
     * <i>HolodeckB2BCoreId</i> to identify a specific <code>MessageUnit</code> instance.
     *
     * @return  The message id of the message this message unit is a response to
     */
    public String getRefToMessageId();

    /**
     * Gets the identifier of the P-Mode that governs the processing of this message unit.
     * <p>Note that the P-Mode may not always be known, for example when a signal message unit is received which can not
     * be related to a sent message.
     *
     * @return  If known, the identifier of the P-Mode that governs processing of this message unit,<br>
     *          otherwise <code>null</code>
     * @since   2.1.0
     */
    public String getPModeId();
}

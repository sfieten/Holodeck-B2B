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
package org.holodeckb2b.interfaces.pmode;

import org.holodeckb2b.interfaces.general.Interval;


/**
 * Represents P-Mode parameters related to the <i>AS4 Reception Awareness feature</i>. This feature is a (simple)
 * reliability protocol that uses ebMS Receipt signals for acknowledgements. See section 3.2 of the AS4 profile for more
 * information.
 * <p>Although the AS4 profile defines five P-Mode parameters for this feature, this interface defines only two methods.
 * This is due to the fact that the other parameters are derived. The explicitly mapped parameters are:<ol>
 * <li><b>PMode[1].ReceptionAwareness.Retry.Parameters</b> -> {@link #getWaitIntervals()} : Indicates the intervals to
 * wait for a Receipt and after which the User Message should be resend. Note that after the last interval there will be
 * no resending, so the actual number of retries is one less then the number of intervals specified. If no interval is
 * specified no message resending will take place, i.e. <b>PMode[1].ReceptionAwareness.Retry</b> = <i>false</i>. In this
 * case Holodeck B2B may immediately generate a <i>MissingReceipt</i> error. When using asynchronous Receipts there
 * should be at least one interval to specify the period to wait for the  <i>Receipt</i> to arrive after the initial
 * send.</li>
 * <li><b>Use duplicate elimination</b> : Indication whether a message that is received twice should be delivered to the
 * business application or not. If enabled Holodeck B2B will search all received messages in database to check if the
 * message was received (and delivered) before. There is no further parameterization. This corresponds to the <b>
 * PMode[1].ReceptionAwareness.DuplicateDetection</b> and <b>DuplicateDetection.Parameters</b> P-Mode parameters from
 * the AS4 profiile.</li></ol>
 * NOTE: Either the {@link #getWaitIntervals()} or the {@link #useDuplicateDetection()} should be used in a specific
 * instance on a Leg as these settings apply to sent or received User Messages.
 * <p>Enabling the Reception Awareness feature itself (<b>PMode[1].ReceptionAwareness</b>) is done by including an
 * object of this type on the leg, i.e. when {@link ILeg#getReceptionAwareness()} returns a non-null value.
 * <b>NOTE:</b> In version 4.0.0 the interface changed to allow a more flexible configuration of the
 * intervals between retries. To allow an easy migration path all methods related to the retry configuration were
 * depracted and had a default implementation. Since version 5.0.0 the deprecated methods have been removed.
 * <p><b>NOTE:</b> Although Reception Awareness is based on the AS4 specification it is implemented in Holodeck B2B
 * generically for all messaging protocols. When the feature is used for other protocols the {@link
 * #useDuplicateDetection()} may also be used on a sending Leg to indicate whether duplicate eliminiation should be
 * requested, as for example done in ebMS V2.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public interface IReceptionAwareness {

    /**
     * Gets an array of intervals to wait for a <i>Receipt</i> Signal before a <i>User Message</i> should be
     * retransmitted.
     * <p>Note that the last interval only indicates the time to wait for the Receipt and no resending will take place.
     * Therefore the number of retries is one less then the number of intervals specified!.
     *
     * @return The periods to wait for a <i>Receipt</i> Signal expressed as an array of {@link Interval}
     * @since 4.0.0
     */
    public Interval[] getWaitIntervals();

    /**
     * Indicates whether duplicate detection and elimination should be used.
     *
     * @return  <code>true</code> if duplicates should be detected and eliminated,<br>
     *          <code>false</code> if messages should always be delivered even when duplicate
     */
    public boolean useDuplicateDetection();
}

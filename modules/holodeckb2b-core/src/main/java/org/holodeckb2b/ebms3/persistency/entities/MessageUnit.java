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
package org.holodeckb2b.ebms3.persistency.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.holodeckb2b.common.util.Utils;
import org.holodeckb2b.ebms3.persistent.dao.MessageUnitDAO;
import org.holodeckb2b.interfaces.messagemodel.IEbmsMessage;
import org.holodeckb2b.interfaces.pmode.ILeg.Label;

/**
 * Is a persistency class representing an ebMS message unit that is processed by Holodeck B2B.
 * <p>This class stores the general meta-data relevant to all kinds of message units. In addition to the information
 * included in the <code>eb:MessageInfo</code> element contained in the ebMS header this consists of the processing
 * states.
 * <p>It also declares two JPA queries:<ol>
 * <li><i>MessageUnit.findWithMessageIdInDirection</i> finds all message units with the given message id and which where
 *        either sent or received by Holodeck B2B. Although the message id must be unique multiple message unit
 *        instances with the same id can exist because of retransmissions (and because other applications may not
 *        guarantee uniqueness).</li>
 * <li><i>MessageUnit.findWithLastStateChangeBefore</i> finds all message units which last change in processing state is
 *        before the specified date.</li></ol>
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
@Entity
@Table(name = "MSG_UNIT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "UM_TYPE")
@NamedQueries({
        @NamedQuery(name="MessageUnit.findWithMessageIdInDirection",
            query = "SELECT mu " +
                    "FROM MessageUnit mu " +
                    "WHERE mu.MESSAGE_ID = :msgId " +
                    "AND mu.DIRECTION = :direction " +
                    "ORDER BY mu.MU_TIMESTAMP DESC"
            ),
        @NamedQuery(name="MessageUnit.findWithLastStateChangeBefore",
            query = "SELECT mu " +
                    "FROM MessageUnit mu JOIN FETCH mu.states s1 " +
                    "WHERE s1.PROC_STATE_NUM = (SELECT MAX(s2.PROC_STATE_NUM) FROM mu.states s2) " +
                    "AND   s1.START <= :beforeDate"
            )}
)
public abstract class MessageUnit implements Serializable, org.holodeckb2b.interfaces.messagemodel.IMessageUnit {

    /**
     * Indicates whether the message unit was sent (OUT) or received (IN) by Holodeck B2B
     */
    public enum Direction { IN, OUT }

    /*
     * Getters and setters
     */
    public long getOID() {
        return OID;
    }

    /**
     * Gets the Holodeck B2B internal id for this instance.
     *
     * @return  A <code>String</code> containing the hex representation of the OID
     * @since   HB2B_NEXT_VERSION
     */
    @Override
    public String getHolodeckB2BCoreId() {
        return Long.toHexString(OID);
    }

    @Override
    public Date getTimestamp() {
        return MU_TIMESTAMP;
    }

    public void setTimestamp(final Date timestamp) {
        MU_TIMESTAMP = timestamp;
    }

    @Override
    public String getMessageId() {
        return MESSAGE_ID;
    }

    public void setMessageId(final String messageId) {
        MESSAGE_ID = messageId;
    }

    @Override
    public String getRefToMessageId() {
        return REF_TO_MSG_ID;
    }

    public void setRefToMessageId(final String refToMsgId) {
        REF_TO_MSG_ID = refToMsgId;
    }

    /**
     * Sets the current state of processing to the given state.
     * <p>
     * This method will also set order of the new {@link ProcessingState} to ensure it will be the most recent state.
     * Note that this does require the message unit to be locked for updates. This is a responsibility for the
     * {@link MessageUnitDAO}.
     *
     * @param state The new {@link ProcessingState} for the message unit
     */
    public void setProcessingState(final ProcessingState state) {
        if (states == null)
            states = new ArrayList<>();

        state.setSeqNumber(Utils.isNullOrEmpty(states) ? 0 : states.get(0).getSeqNumber() + 1);
        states.add(0, state);
    }

    public ProcessingState getCurrentProcessingState() {
        return (Utils.isNullOrEmpty(states) ? null : states.get(0));
    }

    public List<ProcessingState> getProcessingStates() {
        return states;
    }

    /**
     * Associate this message unit with the given P-Mode
     *
     * @param pmodeId   The id of the P-Mode that contains the processing parameters
     *                  for this message unit
     */
    public void setPMode(final String pmodeId) {
        this.PMODE_ID = pmodeId;
    }

    /**
     * Gets the id of the P-Mode for this message unit.
     * <p>The P-Mode contains the parameters for processing the message unit.
     *
     * @return The id of the P-Mode
     */
    @Override
    public String getPModeId() {
        return PMODE_ID;
    }

    /**
     * Associate this message unit with a leg in the specified P-Mode
     *
     * @param  label    The label of the leg that contains the processing parameters for handling this message unit
     */
    public void setLeg(final Label label) {
        this.LEG_LABEL = label;
    }

    /**
     * Gets the label of the leg that configures the processing of this message unit
     *
     * @return  The {@link Ileg} that contains the processing parameters for handling this message unit
     */
    public Label getLeg() {
        return this.LEG_LABEL;
    }

    /**
     * Gets the indication whether this message unit is send using a multi-hop exchange
     *
     * @return  <code>true</code> if multi-hop is used for exchange of this message unit,<br>
     *          <code>false</code> otherwise
     * @todo: Choose version number!
     * @since 2.1.0
     */
    public boolean usesMultiHop() {
        return USES_MULTI_HOP;
    }

    public void setMultiHop(final boolean usesMultiHop) {
        this.USES_MULTI_HOP = usesMultiHop;
    }

    /**
     * Sets the direction the message unit flows.
     *
     * @param direction     The direction of the message unit
     */
    public void setDirection(final Direction direction) {
        DIRECTION = direction;
    }

    /**
     * Gets the direction the message unit flows.
     *
     * @returns     The direction of the message unit
     */
    public Direction getDirection() {
        return DIRECTION;
    }

    /**
     * Gets the meta-data of the ebMS message this message unit is contained in.
     * <p><b>NOTE:</b> The relation between the message unit and the ebMS message is created by the Holodeck B2B Core
     * during processing of the message. External components therefore SHOULD NOT set the relation. It also implies that
     * an outgoing message unit does not need to be associated with a parent ebMS message when it is not yet sent.
     *
     * @return The meta-data of the containing ebMS message as a {@link IEbmsMessage} object
     * @since HB2B_NEXT_VERSION
     */
    public IEbmsMessage getParentEbmsMessage() {
        return containingEbmsMessage;
    }

    /**
     * Sets the parent ebMS message that this message unit is contained in.
     * <p>NOTE: The specified object should be persisted before this message unit instance is saved!
     *
     * @param parent The {@link EbmsMessage} that represent the ebMS message this message unit is contained in.
     */
    public void setParentEbmsMessage(final EbmsMessage parent) {
        containingEbmsMessage = parent;
    }

    /*
     * Fields
     *
     * NOTE: The JPA @Column annotation is not used so the attribute names are
     * used as column names. Therefor the attribute names are in CAPITAL.
     */

    /*
     * Technical object id acting as the primary key
     */
    @Id
    @GeneratedValue
    private long    OID;
    /*
     * Field to use for JPA optimistic locking
     */
    @Version
    private long    VERSION;

    private String  MESSAGE_ID;

    private String  REF_TO_MSG_ID;

    private String  PMODE_ID;

    private Label   LEG_LABEL;

    private Direction   DIRECTION;

    private boolean     USES_MULTI_HOP = false;

    /*
     * Because timestamp is a reserved SQL-99 word it is prefixed
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date    MU_TIMESTAMP;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("PROC_STATE_NUM DESC")
    private List<ProcessingState>       states;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MSG_OID")
    private EbmsMessage    containingEbmsMessage;
}

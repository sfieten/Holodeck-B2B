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
package org.holodeckb2b.ebms3.persistency.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.holodeckb2b.interfaces.messagemodel.IEbmsMessage;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;

/**
 * Is a persistency class representing an ebMS message that is processed by Holodeck B2B. Note that this represent the
 * SOAP message and not the individual message units that contain the actual message information.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since  HB2B_NEXT_VERSION
 */
@Entity
@Table(name = "MESSAGE")
public class EbmsMessage implements IEbmsMessage, PersistentEntity {

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
        return MSG_TIMESTAMP;
    }

    public void setTimestamp(final Date timestamp) {
        MSG_TIMESTAMP = timestamp;
    }

    @Override
    public boolean isRequest() {
        return IS_REQUEST;
    }

    public void setIsRequest(final boolean isRequest) {
        IS_REQUEST = isRequest;
    }

    @Override
    public IEbmsMessage getRelatedMessage() {
        return relatedMessage;
    }

    public void setRelatedMessage(final EbmsMessage relatedMsg) {
        this.relatedMessage = relatedMsg;
    }

    @Override
    public Collection<IMessageUnit> getMessageUnits() {
        return msg_units;
    }

    public void addMessageUnit(final MessageUnit msgUnit) {
        if (msg_units != null)
            msg_units = new ArrayList<>();

        msg_units.add(msgUnit);
    }

    @Override
    public String getTargetURL() {
        return TARGET_URL;
    }

    public void setTargetURL(final String url) {
        TARGET_URL = url;
    }

    @Override
    public String getRemoteAddress() {
        return REMOTE_ADDRESS;
    }

    public void setRemoteAddress(final String remoteAddr) {
        REMOTE_ADDRESS = remoteAddr;
    }

    @Override
    public String getRemoteSoftwareId() {
        return SOFTWARE_ID;
    }

    public void setRemoteSoftwareId(final String softwareId) {
        SOFTWARE_ID = softwareId;
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
     * Because timestamp is a reserved SQL-99 word it is prefixed
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date    MSG_TIMESTAMP;

    private boolean IS_REQUEST;

    @Column(length = 1024)
    private String  TARGET_URL;

    private String  REMOTE_ADDRESS;

    private String  SOFTWARE_ID;

    @OneToOne
    @JoinColumn(name="RELMSG_OID")
    private EbmsMessage relatedMessage;

    @OneToMany(mappedBy = "containingEbmsMessage", targetEntity = MessageUnit.class)
    private Collection<IMessageUnit>    msg_units;
}

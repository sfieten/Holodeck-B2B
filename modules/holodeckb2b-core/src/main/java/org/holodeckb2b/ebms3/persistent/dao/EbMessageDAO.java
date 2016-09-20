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
package org.holodeckb2b.ebms3.persistent.dao;

import org.holodeckb2b.common.exceptions.DatabaseException;
import org.holodeckb2b.ebms3.persistency.entities.EbmsMessage;
import org.holodeckb2b.ebms3.persistency.entities.MessageUnit;

/**
 * Is the data access object for {@link EbmsMessage} objects that manages all database operations. All other classes
 * must use the methods of this class whenever message meta-data needs to be saved to the database.
 * <p>The methods of this class use {@link EntityProxy} objects as parameters and in results. This is done to allow
 * one object in the {@link MessageContext} when processing messages even if the actual JPA entity object changes. In
 * the documentation we often just refer to the entity object directly but it will always be wrapped in a <code>
 * EntityProxy</code> object.
 * <p>Note that this means that a <b>side effect</b> of the methods that update the database the entity object enclosed
 * in the provided {@link EntityProxy} is replaced by the updated version when the update is successful.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @since  HB2B_NEXT_VERSION
 */
public class EbMessageDAO {

    /**
     * Stores the meta-data of an EbMS message in the database. If the given {@link EbmsMessage} object already contains
     * {@link MessageUnit} objects these MUST already have been persisted as this DAO will only update the link between
     * the persisted objects.
     *
     * @param ebmsMessage   The meta-data of the ebMS message to be saved as an {@link EbmsMessage}. Note that the
     *                      information in this object is stored, not necessarily the object itself.
     * @return              An <code>EntityProxy</code> object that contains the saved {@link EbmsMessage} object
     * @throws DatabaseException    If an error occurs when saving the new message unit to the database.
     */
    public static EntityProxy<EbmsMessage> storeEbmsMessage(final EbmsMessage ebmsMessage) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Adds information on a message unit to the ebMS message meta-data. This method actually creates the relationship
     * between the {@link EbmsMessage} and {@link MessageUnit} objects. Therefore both MUST already been persisted
     * before this called.
     *
     * @param ebmsMessage   The ebMS message to which the message unit should be added
     * @param msgUnit       The message unit to add to the ebMS message
     * @throws DatabaseException    If an error occurs when adding the message unit to the ebMS message
     */
    public static void addMessageUnit(final EntityProxy<EbmsMessage> ebmsMessage,
                                      final EntityProxy<MessageUnit> msgUnit) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Loads the information on the message units contained in the EbMS message. This method must be called before the
     * collection of message units is accessed as this relation is lazily loaded.
     *
     * @param ebmsMessage   The {@link EntityProxy} of the ebMS message for which the information on the contained
     *                      message units should be loaded.
     * @throws DatabaseException When there is a problem loading the message unit information
     */
    public static void loadMessageUnits(final EntityProxy<EbmsMessage> ebmsMessage) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

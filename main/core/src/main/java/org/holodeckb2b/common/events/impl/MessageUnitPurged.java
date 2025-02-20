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
package org.holodeckb2b.common.events.impl;

import org.holodeckb2b.interfaces.events.IMessageUnitPurged;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;

/**
 * Is the implementation class of {@link IMessageUnitPurged} to indicate that a message unit is to deleted from the
 * Holodeck B2B Core message database because the period for maintaining it's meta-data has expired.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since  2.1.0
 */
public class MessageUnitPurged extends AbstractMessageProcessingEvent implements IMessageUnitPurged {

    public MessageUnitPurged(final IMessageUnit subject) {
        super(subject);
    }

}

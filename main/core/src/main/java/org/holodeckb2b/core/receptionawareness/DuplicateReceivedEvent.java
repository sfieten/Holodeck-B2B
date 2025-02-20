/*
 * Copyright (C) 2017 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.core.receptionawareness;

import org.holodeckb2b.common.events.impl.AbstractMessageProcessingEvent;
import org.holodeckb2b.interfaces.events.IDuplicateReceived;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;

/**
 * Is the implementation class of {@link IDuplicateReceived} to indicate that the received User Message has already
 * been received and processed.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 4.0.0
 */
public class DuplicateReceivedEvent extends AbstractMessageProcessingEvent implements IDuplicateReceived {

    public DuplicateReceivedEvent(IMessageUnit subject) {
        super(subject);
    }

    @Override
    public boolean isEliminated() {
        return true; // Currently duplicates are always eliminated
    }

}

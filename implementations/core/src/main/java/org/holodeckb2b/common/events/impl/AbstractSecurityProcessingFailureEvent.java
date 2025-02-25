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
package org.holodeckb2b.common.events.impl;

import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;
import org.holodeckb2b.interfaces.security.SecurityProcessingException;

/**
 * Is an abstract implementation of a <i>message processing event</i> that indicates that there was a problem in the
 * processing of the WS-Security header.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 4.0.0
 */
abstract class AbstractSecurityProcessingFailureEvent
											extends AbstractMessageProcessingFailureEvent<SecurityProcessingException> {

    /**
     * Creates a new <code>AbstractSecurityProcessingFailureEvent</code> for the given message unit and failure reason.
     *
     * @param subject   The message unit
     * @param reason    The reason why the security processing failed
     */
    AbstractSecurityProcessingFailureEvent(final IMessageUnit subject, final SecurityProcessingException reason) {
        super(subject, reason);
    }
}

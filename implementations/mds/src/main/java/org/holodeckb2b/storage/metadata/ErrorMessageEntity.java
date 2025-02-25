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
package org.holodeckb2b.storage.metadata;

import java.util.Collection;

import org.hibernate.Hibernate;
import org.holodeckb2b.interfaces.messagemodel.IEbmsError;
import org.holodeckb2b.interfaces.pmode.ILeg.Label;
import org.holodeckb2b.interfaces.storage.IErrorMessageEntity;
import org.holodeckb2b.storage.metadata.jpa.ErrorMessage;

/**
 * Is the {@link IErrorMessageEntity} implementation of the default persistency provider of Holodeck B2B.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since  3.0.0
 */
public class ErrorMessageEntity extends MessageUnitEntity<ErrorMessage> implements IErrorMessageEntity {

    public ErrorMessageEntity(ErrorMessage jpaObject) {
        super(jpaObject);
    }

    @Override
    public void loadCompletely() {
    	super.loadCompletely();
    	Hibernate.initialize(jpaEntityObject.getErrors());
    }

    @Override
    public boolean shouldHaveSOAPFault() {
        return jpaEntityObject.shouldHaveSOAPFault();
    }

    @Override
    public Collection<IEbmsError> getErrors() {
        return jpaEntityObject.getErrors();
    }

	@Override
	public Label getLeg() {
		return jpaEntityObject.getLeg();
	}

	@Override
	public void setAddSOAPFault(boolean addSOAPFault) {
		jpaEntityObject.setAddSOAPFault(addSOAPFault);
	}

	@Override
	public void setLeg(Label leg) {
		jpaEntityObject.setLeg(leg);
	}
}

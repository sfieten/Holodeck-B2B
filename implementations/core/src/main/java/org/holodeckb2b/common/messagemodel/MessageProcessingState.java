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
package org.holodeckb2b.common.messagemodel;

import java.io.Serializable;
import java.util.Date;

import org.holodeckb2b.interfaces.processingmodel.IMessageUnitProcessingState;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;

/**
 * Is an implementation of {@link IMessageUnitProcessingState} used in the
 * parent class to hold the processing states of the message unit.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 3.0.0
 */
public class MessageProcessingState implements IMessageUnitProcessingState, Serializable {

	private static final long serialVersionUID = -3508733471289194518L;

	private ProcessingState state;
	private Date startTime;
	private String description;

	/**
	 * Creates a new <code>MessageProcessingState</code> object with the given state
	 * and the current time as start
	 * 
	 * @param state The processing state to set
	 */
	public MessageProcessingState(final ProcessingState state) {
		this(state, null);
	}

	/**
	 * Creates a new <code>MessageProcessingState</code> object with the given state
	 * and description and sets the current time as start
	 * 
	 * @param state       The processing state to set
	 * @param description Additional description of the processing state
	 * @since 4.1.0
	 */
	public MessageProcessingState(final ProcessingState state, final String description) {
		this.state = state;
		this.startTime = new Date();
		this.description = description;
	}

	/**
	 * Creates a new <code>MessageProcessingState</code> object by copying the
	 * information from the given state
	 *
	 * @param sourceState The source processing state to copy the information from
	 */
	public MessageProcessingState(final IMessageUnitProcessingState sourceState) {
		if (sourceState != null) {
			this.state = sourceState.getState();
			this.startTime = sourceState.getStartTime();
			this.description = sourceState.getDescription();
		}
	}

	public void setState(final ProcessingState newState) {
		this.state = newState;
	}
	
	@Override
	public ProcessingState getState() {
		return state;
	}

	public void setStartTime(final Date newTime) {
		this.startTime = newTime;
	}
	
	@Override
	public Date getStartTime() {
		return startTime;
	}

    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }
	
	@Override
	public String getDescription() {
		return description;
	}
}

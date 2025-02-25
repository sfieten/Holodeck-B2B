/*
 * Copyright (C) 2024 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.storage.metadata.jpa.wrappers;

import javax.persistence.Entity;

import org.holodeckb2b.storage.metadata.jpa.Description;

/**
 * JPA Entity wrapper for testing the {@link Description} embedable
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
@Entity
public class WDescription extends BaseEmbedableWrapper<Description> {

	@Override
	Description createEmbedded() {
		return new Description();
	}

}

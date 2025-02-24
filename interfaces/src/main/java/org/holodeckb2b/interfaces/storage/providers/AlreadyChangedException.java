/*
 * Copyright (C) 2016 The Holodeck B2B Team.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
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
package org.holodeckb2b.interfaces.storage.providers;

/**
 * Indicates that an update of the message unit meta-data failed because more up to date data was found in the database.  
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since  7.0.0
 * @see IMetadataStorageProvider
 */
public class AlreadyChangedException extends StorageException {
	private static final long serialVersionUID = 5309676598979015162L;
}

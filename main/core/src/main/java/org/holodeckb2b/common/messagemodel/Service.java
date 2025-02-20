/**
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
package org.holodeckb2b.common.messagemodel;

import java.io.Serializable;

import org.holodeckb2b.interfaces.general.IService;

/**
 * Is an in memory only implementation of {@link IService} to temporarily store the meta-data about the Service
 * specified in the header of a User Message message unit.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since  3.0.0
 */
public class Service implements IService, Serializable {
	private static final long serialVersionUID = -4008024058397816930L;

	private String      name;
    private String      type;

    /**
     * Default constructor creates empty object
     */
    public Service() {}

    /**
     * Creates a <code>Service</code> object using the meta-data provided in the given source object
     *
     * @param source   The data to use
     */
    public Service(final IService source) {
        this.name = source.getName();
        this.type = source.getType();
    }

    /**
     * Creates a <code>Service</code> object with the provided name
     *
     * @param name      The name of the service
     */
    public Service(final String name) {
        this.name = name;
    }

    /**
     * Creates a <code>Service</code> object with the provided meta-data
     *
     * @param name      The name of the service
     * @param type      The type of the service
     */
    public Service(final String name, final String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}

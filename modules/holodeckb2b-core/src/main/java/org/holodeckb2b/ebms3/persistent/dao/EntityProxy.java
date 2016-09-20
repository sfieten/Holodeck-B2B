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

import org.holodeckb2b.ebms3.persistency.entities.PersistentEntity;
import org.holodeckb2b.ebms3.persistency.entities.MessageUnit;

/**
 * Is a <i>proxy</i> to an JPA entity class for message units as defined in {@link
 * org.holodeckb2b.ebms3.persistency.entities}. This proxy class is used to enable one constant object in the <code>
 * MessageContext</code> even when the JPA object changes on updates to the database.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 * @param <T>   The {@link MessageUnit} entity class for which the instance is a facade
 */
public class EntityProxy<T extends PersistentEntity> {

    /**
     * The JPA entity object itself
     */
    public T   entity;

    /**
     * Create a new EntityProxy object for the given persistent object.
     *
     * @param entityObject The persistent object to create a new proxy for
     */
    EntityProxy(final T entityObject) {
        this.entity = entityObject;
    }

    /**
     * Cast this EntityProxy to a specific
     *
     * @param type      The type of EntityProxy that is requested
     * @return          The specific EntityProxy of the specified type for the proxied object
     * @throws ClassCastException   When the proxied object is not of the requested type and therefore the resulting
     *                              EntityProxy would be illegal
     * @since  HB2B_NEXT_VERSION
     */
    public <V extends PersistentEntity> EntityProxy<V> castToProxyOf(final Class<V> type) {
        if (type.isInstance(this))
            return (EntityProxy<V>) this;
        else
            throw new ClassCastException("The proxied object can not be cast to requested type");
    }
}

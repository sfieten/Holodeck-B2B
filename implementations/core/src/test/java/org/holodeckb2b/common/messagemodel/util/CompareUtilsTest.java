/**
 * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.common.messagemodel.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.holodeckb2b.common.util.CompareUtils;
import org.holodeckb2b.interfaces.general.IPartyId;
import org.holodeckb2b.interfaces.general.IProperty;
import org.holodeckb2b.interfaces.general.IService;
import org.holodeckb2b.interfaces.general.ITradingPartner;
import org.junit.Test;

/**
 * Created at 20:30 15.09.16
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class CompareUtilsTest {

    @Test
    public void testTradingPartnersAreEqual() {
        IPartyId p1 = new PartyIDImpl("123", "type");
        IPartyId p2 = new PartyIDImpl("124", "type1");
        HashSet<IPartyId> c1 = new HashSet<IPartyId>();
        c1.add(p1);
        c1.add(p2);
        HashSet<IPartyId> c2 = new HashSet<IPartyId>();
        c2.add(p1);
        c2.add(p2);
        ITradingPartner partner1 = new TradingPartnerImpl(c1, "rl");
        ITradingPartner partner2 = new TradingPartnerImpl(c1, "rl");
        assertTrue(CompareUtils.areEqual(partner1, partner2));
    }

    @Test
    public void testCollectionsAreEqual() {
        HashSet<IPartyId> c1 = new HashSet<IPartyId>();
        HashSet<IPartyId> c2 = new HashSet<IPartyId>();
        assertTrue(CompareUtils.areEqual(c1, c2));
        IPartyId p1 = new PartyIDImpl("123", "type");
        IPartyId p2 = new PartyIDImpl("124", "type1");
        c1.add(p1);
        c2.add(p1);
        assertTrue(CompareUtils.areEqual(c1, c2));
        c1.add(p2);
        c2.add(p2);
        assertTrue(CompareUtils.areEqual(c1, c2));
        IPartyId p3 = new PartyIDImpl("124", null);
        c1.remove(p1);
        c1.add(p3);
        assertFalse(CompareUtils.areEqual(c1, c2));
        IPartyId p4 = new PartyIDImpl(null, "type");
        c1.remove(p3);
        c1.add(p4);
        assertFalse(CompareUtils.areEqual(c1, c2));
        c1.remove(p4);
        c1.add(p1);
        assertTrue(CompareUtils.areEqual(c1, c2));
    }

    @Test
    public void testPartyIdsAreEqual() {
        IPartyId p1 = new PartyIDImpl("123", "type");
        IPartyId p2 = new PartyIDImpl("123", "type");
        assertTrue(CompareUtils.areEqual(p1, p2));
        p1 = new PartyIDImpl(null, "type");
        assertFalse(CompareUtils.areEqual(p1, p2));
        assertFalse(CompareUtils.areEqual(p2, p1));
        p1 = new PartyIDImpl("123", null);
        assertFalse(CompareUtils.areEqual(p1, p2));
        assertFalse(CompareUtils.areEqual(p2, p1));
    }

    @Test
    public void testPropertiesAreEqual() {
        IProperty p1 = new PropertyImpl("cAr", "tEsLA", "S");
        IProperty p2 = new PropertyImpl("cAr", "tEsLA", "S");
        assertTrue(CompareUtils.areEqual(p1, p2));
        p1 = new PropertyImpl("cAr", "tEsLA", null);
        assertFalse(CompareUtils.areEqual(p1, p2));
        assertFalse(CompareUtils.areEqual(p2, p1));
        p1 = new PropertyImpl("cAr", null, "S");
        assertFalse(CompareUtils.areEqual(p1, p2));
        assertFalse(CompareUtils.areEqual(p2, p1));
        p1 = new PropertyImpl(null, "tEsLA", "S");
        assertFalse(CompareUtils.areEqual(p1, p2));
        assertFalse(CompareUtils.areEqual(p2, p1));
    }

    @Test
    public void testServicesAreEqual() {
        IService s1 = new ServiceImpl("123", "sap");
        IService s2 = new ServiceImpl("123", "sap");
        assertTrue(CompareUtils.areEqual(s1, s2));
        s1 = new ServiceImpl(null, "sap");
        assertFalse(CompareUtils.areEqual(s1, s2));
        assertFalse(CompareUtils.areEqual(s2, s1));
        s1 = new ServiceImpl("123", null);
        assertFalse(CompareUtils.areEqual(s1, s2));
        assertFalse(CompareUtils.areEqual(s2, s1));
    }

    class TradingPartnerImpl implements ITradingPartner {
        private Collection<IPartyId> partyIds;
        private String role;

        public TradingPartnerImpl(Collection<IPartyId> partyIds, String role) {
            this.partyIds = partyIds;
            this.role = role;
        }

        @Override
        public Collection<IPartyId> getPartyIds() {
            return partyIds;
        }

        @Override
        public String getRole() {
            return role;
        }
    }

    class PartyIDImpl implements IPartyId {
        private String id;
        private String type;

        public PartyIDImpl(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getType() {
            return type;
        }
    }

    class PropertyImpl implements IProperty {
        private String  value;
        private String  name;
        private String  type;

        public PropertyImpl(String value, String name, String type) {
            this.value = value;
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getType() {
            return type;
        }
    }

    class ServiceImpl implements IService {
        private String  name;
        private String  type;

        public ServiceImpl(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }
    }
}
/*
 * Copyright (C) 2015 The Holodeck B2B Team, Sander Fieten
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
package org.holodeckb2b.multihop;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.holodeckb2b.common.mmd.xml.MessageMetaData;
import org.holodeckb2b.core.testhelpers.TestUtils;
import org.holodeckb2b.ebms3.packaging.SOAPEnv;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test if correct ebint:RoutingInput element is created
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
public class RoutingInputTest {

    public RoutingInputTest() {
    }

    @Test
    public void testFullUserMessageHeader() {
        MessageMetaData mmd = TestUtils.getMMD("multihop/ri/full_mmd.xml", this);
        // Create a SOAP envelope that should contain the RoutingInput element
        final SOAPEnvelope    env = SOAPEnv.createEnvelope(SOAPEnv.SOAPVersion.SOAP_12);

        final OMElement ri = RoutingInput.createElement(env, mmd);

        assertEquals(MultiHopConstants.ROUTING_INPUT_NS_URI, ri.getNamespaceURI());
        final Iterator<?> umChilds = ri.getChildrenWithLocalName("UserMessage");
        assertTrue(umChilds.hasNext());
        final OMElement umChild = (OMElement) umChilds.next();
        assertEquals(MultiHopConstants.ROUTING_INPUT_NS_URI, umChild.getNamespaceURI());

        final Iterator<?> ciChilds = umChild.getChildrenWithLocalName("CollaborationInfo");
        assertTrue(ciChilds.hasNext());
        final OMElement ciChild = (OMElement) ciChilds.next();
        assertEquals(EbMSConstants.EBMS3_NS_URI, ciChild.getNamespaceURI());
    }
}

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
package org.holodeckb2b.common.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler;
import org.junit.Test;

/**
 * Created at 12:09 15.03.17
 *
 * Checked for cases coverage (04.05.2017)
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class ReportHeaderProcessedTest {

    @Test
    public void doProcessing() throws Exception {
        // Creating SOAP envelope
        SOAPFactory omFactory = OMAbstractFactory.getSOAP12Factory();
	    final org.apache.axiom.soap.SOAPEnvelope envelope = omFactory.getDefaultEnvelope();
	    // Declare all namespaces that are needed by default
	    envelope.declareNamespace("http://www.w3.org/1999/XMLSchema-instance/", "xsi");
	    envelope.declareNamespace("http://www.w3.org/1999/XMLSchema", "xsd");

    	// Add some headers
        envelope.getHeader().addHeaderBlock(new QName("AFirstHeader", "http://dev.test.holodeck-b2b.org/not_for_real"));
        envelope.getHeader().addHeaderBlock(new QName("AnotherHeader", "http://dev.test.holodeck-b2b.org/not_for_real"));
        envelope.getHeader().addHeaderBlock(new QName("AndALastOne", "http://dev.test.holodeck-b2b.org/not_for_real"));
        
        MessageContext mc = new MessageContext();
        try {
            mc.setEnvelope(envelope);
        } catch (AxisFault axisFault) {
            fail(axisFault.getMessage());
        }
        
        SOAPHeaderBlock h = null;
        for(Iterator hdrs = envelope.getHeader().getChildElements(); hdrs.hasNext(); ) {
        	h = (SOAPHeaderBlock) hdrs.next();        
        	assertFalse(h.isProcessed());
        }

        try {
            Handler.InvocationResponse invokeResp = new ReportHeaderProcessed().invoke(mc);
            assertEquals(Handler.InvocationResponse.CONTINUE, invokeResp);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        for(Iterator hdrs = envelope.getHeader().getChildElements(); hdrs.hasNext(); ) {
        	h = (SOAPHeaderBlock) hdrs.next();        
        	assertTrue(h.isProcessed());
        }
    }
}
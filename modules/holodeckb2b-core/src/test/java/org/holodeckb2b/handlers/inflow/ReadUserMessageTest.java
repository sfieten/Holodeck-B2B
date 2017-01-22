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
package org.holodeckb2b.handlers.inflow;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler;
import org.holodeckb2b.constants.MessageContextProperties;
import org.holodeckb2b.common.mmd.xml.MessageMetaData;
import org.holodeckb2b.packaging.Messaging;
import org.holodeckb2b.packaging.SOAPEnv;
import org.holodeckb2b.packaging.UserMessage;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.core.testhelpers.HolodeckB2BTestCore;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created at 23:28 21.09.16
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class ReadUserMessageTest {

    private static String baseDir;

    private ReadUserMessage handler;

    @BeforeClass
    public static void setUpClass() {
        baseDir = ReadUserMessageTest.class.getClassLoader()
                .getResource("multihop").getPath();
        HolodeckB2BCoreInterface.setImplementation(new HolodeckB2BTestCore(baseDir));
    }

    @Before
    public void setUp() throws Exception {
        handler = new ReadUserMessage();
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test construction of the user message to be successfully consumed by
     * {@link ReadUserMessage ReadUserMessage} handler
     */
    @Test
    public void testProcessing() {
        final String mmdPath =
                this.getClass().getClassLoader()
                        .getResource("multihop/icloud/full_mmd.xml").getPath();
        final File f = new File(mmdPath);
        MessageMetaData mmd = null;
        try {
            mmd = MessageMetaData.createFromFile(f);
        } catch (final Exception e) {
            fail("Unable to test because MMD could not be read correctly!");
        }
        // Creating SOAP envelope
        SOAPEnvelope env = SOAPEnv.createEnvelope(SOAPEnv.SOAPVersion.SOAP_12);
        // Adding header
        SOAPHeaderBlock headerBlock = Messaging.createElement(env);
        // Adding UserMessage from mmd
        OMElement userMessage = UserMessage.createElement(headerBlock, mmd);

        MessageContext mc = new MessageContext();
        // Setting input message property
        mc.setProperty(MessageContextProperties.IN_USER_MESSAGE, userMessage);
        try {
            mc.setEnvelope(env);
        } catch (AxisFault axisFault) {
            fail(axisFault.getMessage());
        }

        try {
            Handler.InvocationResponse invokeResp = handler.invoke(mc);
            assertNotNull(invokeResp);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

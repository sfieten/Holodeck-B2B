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
package org.holodeckb2b.ebms3.packaging;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.holodeckb2b.interfaces.general.EbMSConstants;

/**
 * Is a helper class for handling the <code>Messaging</code> element in the SOAP header that represents the ebMS header.
 * It contains methods for creating the and accessing the child elements in the <code>Messaging</code> element.
 * <p>The messaging element is specified in section 5.2.1 of the ebMS 3 Core specification.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class Messaging {

    /**
     * The fully qualified name of the element as an {@link QName}
     */
    public static final QName  Q_ELEMENT_NAME = new QName(EbMSConstants.EBMS3_NS_URI, "Messaging");

    /**
     * Creates an <code>Messaging</code> element representing the ebMS SOAP header and adds it to given SOAP envelope
     * when it does not already exist. If there already is an <code>Messaging</code> element in the SOAP envelope that
     * one will be returned instead.
     *
     * @param env   The SOAP envelope in which to add the ebMS header
     * @return      The ebMS 3 Messaging element that is now contained in the SOAP envelope
     */
    public static SOAPHeaderBlock createElement(final org.apache.axiom.soap.SOAPEnvelope env) {
        // First check if the ebMS header block already exists
        SOAPHeaderBlock messaging = getElement(env);

        if( messaging == null) {
            // No existing messaging element, so create a new one
            messaging = env.getHeader().addHeaderBlock(Q_ELEMENT_NAME.getLocalPart(), SOAPEnv.getEbms3Namespace(env));

            // The messaging header must be understood by the MSH (see 5.2.1 core spec)
            messaging.setMustUnderstand(true);
        }

        return messaging;
    }

    /**
     * Gets the SOAP header block for the <code>Messaging</code> element in the given SOAP envelope.
     *
     * @param env   The SOAP envelope that should contain the ebMS header
     * @return      A {@link SOAPHeaderBlock} representing the <code>Messaging</code> element when one was found in the
     *              given SOAP envelope, or<br>
     *              <code>null</code> if no <code>Messaging</code> element was found
     */
    public static SOAPHeaderBlock getElement(final SOAPEnvelope env) {
        SOAPHeaderBlock messaging = null;

        try {
            // there should only be one messaging element so we can just get the first one
            messaging = (SOAPHeaderBlock) env.getHeader().getFirstChildWithName(Q_ELEMENT_NAME);
        } catch (final Exception ex) {
            // Returned element not a header block or no header available
            //      => can not be the messaging element, leave null
            messaging = null;
        }

        return messaging;
    }
}

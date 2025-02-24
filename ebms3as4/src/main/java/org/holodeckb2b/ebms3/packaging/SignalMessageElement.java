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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.holodeckb2b.interfaces.general.EbMSConstants;

/**
 * Is a helper class for handling the ebMS <code>eb:SignalMessage</code> element in the ebMS SOAP header. As this
 * element itself does not contain any data there are only methods for creating and getting the element.
 * <p>This element is specified in section 5.2.3 of the ebMS 3 Core specification.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class SignalMessageElement {

    /**
     * The fully qualified name of the element as an {@link QName}
     */
    public static final QName  Q_ELEMENT_NAME = new QName(EbMSConstants.EBMS3_NS_URI, "SignalMessage");


    /**
     * Creates a <code>SignalMessage</code> element and adds it to the given <code>Messaging</code> element.
     *
     * @param messaging     The <code>Messaging</code> element this element should be added to
     * @return              The new element
     */
    public static OMElement createElement(final OMElement messaging) {
        final OMFactory f = messaging.getOMFactory();

        // Create the element
        final OMElement signalmessage = f.createOMElement(Q_ELEMENT_NAME, messaging);

        return signalmessage;
    }

    /**
     * Gets an {@link Iterator} for all <code>eb:SignalMessage</code> elements from the given ebMS 3 Messaging header in
     * the SOAP message.
     *
     * @param messaging   The parent <code>Messaging</code> element
     * @return            An {@link Iterator} for all {@link OMElement}s representing a <code>eb:SignalMessage</code>
     *                    element in the given header
     */
    public static Iterator<OMElement> getElements(final OMElement messaging) {
        return messaging.getChildrenWithName(Q_ELEMENT_NAME);
    }
}

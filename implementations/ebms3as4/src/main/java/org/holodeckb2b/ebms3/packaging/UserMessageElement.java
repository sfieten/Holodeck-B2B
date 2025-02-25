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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.holodeckb2b.common.messagemodel.UserMessage;
import org.holodeckb2b.commons.util.Utils;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.holodeckb2b.interfaces.general.IProperty;
import org.holodeckb2b.interfaces.messagemodel.IUserMessage;

/**
 * Is a helper class for handling the ebMS User Message message units in the ebMS SOAP header, i.e the <code>
 * eb:UserMessage</code> element and its children.
 * <p>This element is specified in section 5.2.2 of the ebMS 3 Core specification.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class UserMessageElement {

    /**
     * The fully qualified name of the element as an {@link QName}
     */
    public static final QName  Q_ELEMENT_NAME = new QName(EbMSConstants.EBMS3_NS_URI, "UserMessage");

    /**
     * The local name of the mpc attribute
     */
    public static final String LN_MPC_ATTR = "mpc";

    /**
     * Creates a <code>UserMessage</code> element and adds it to the given <code>Messaging</code> element.
     *
     * @param messaging     The <code>Messaging</code> element this element should be added to
     * @param data          The data to include in the element
     * @return  The new element
     */
    public static OMElement createElement(final OMElement messaging, final IUserMessage data) {
        final OMFactory f = messaging.getOMFactory();

        // Create the element
        final OMElement usermessage = f.createOMElement(Q_ELEMENT_NAME, messaging);

        // Fill it based on the given data

        // MPC attribute only set when not default
        final String mpc = data.getMPC();
        if (mpc != null && !mpc.equals(EbMSConstants.DEFAULT_MPC))
            usermessage.addAttribute(LN_MPC_ATTR, mpc, null);

        // Create the MessageInfo element
        MessageInfoElement.createElement(usermessage, data);
        // Create the PartyInfo element
        PartyInfoElement.createElement(usermessage, data);
        // Create the CollaborationInfo element
        CollaborationInfoElement.createElement(usermessage, data.getCollaborationInfo());
        // Create the MessageProperties element (if there are message properties)
        final Collection<IProperty> msgProps = data.getMessageProperties();
        if (!Utils.isNullOrEmpty(msgProps))
            MessagePropertiesElement.createElement(usermessage, msgProps);

        // Create the eb:PayloadInfo element (if there are payloads)
        PayloadInfoElement.createElement(usermessage, data.getPayloads());

        return usermessage;
    }

    /**
     * Gets an {@link Iterator} for the <code>eb:UserMessage</code> elements from the given ebMS 3 Messaging header in
     * the SOAP message.
     *
     * @param  messaging  The SOAP Header block that contains the ebMS header,i.e. the <code>eb:Messaging</code> element
     * @return            An {@link Iterator} for all {@link OMElement}s representing a <code>eb:UserMessage</code>
     *                    element in the given header
     */
    public static Iterator<OMElement> getElements(final OMElement messaging) {
        return messaging.getChildrenWithName(Q_ELEMENT_NAME);
    }

    /**
     * Reads the meta data of a User Message message unit from the given <code>eb:UserMessage</code> element and returns
     * it as a {@link UserMessage} object.
     *
     * @param   umElement   The <code>UserMessage</code> element that contains the meta data to read
     * @return              A new {@link UserMessage} object
     */
    public static UserMessage readElement(final OMElement umElement) {
        // Create a new entity object to store the information in
        final UserMessage umData = new UserMessage();

        // Read the [optional] mpc attribute
        final String  mpc = umElement.getAttributeValue(new QName(LN_MPC_ATTR));
        // If there was no mpc attribute or it was empty (which formally is illegal because the mpc should be a valid
        // URI) it is set to the default MPC
        umData.setMPC(Utils.isNullOrEmpty(mpc) ? EbMSConstants.DEFAULT_MPC : mpc);

        // Get the MessageInfo element
        OMElement child = MessageInfoElement.getElement(umElement);
        // Read the MessageInfo element and store info in the persistency object
        MessageInfoElement.readElement(child, umData);

        // Get and read the PartyInfo element
        PartyInfoElement.readElement(PartyInfoElement.getElement(umElement), umData);

        // Get and read the CollaborationInfo element
        umData.setCollaborationInfo(CollaborationInfoElement.readElement(CollaborationInfoElement.getElement(umElement)));

        // Get the MessageProperties element and process it when available
        child = MessagePropertiesElement.getElement(umElement);
        if (child != null)
            umData.setMessageProperties(MessagePropertiesElement.readElement(child));

        // Get the PayloadInfo element and process it when available
        child = PayloadInfoElement.getElement(umElement);
        if (child != null)
            umData.setPayloads(PayloadInfoElement.readElement(child));

        return umData;
    }
}

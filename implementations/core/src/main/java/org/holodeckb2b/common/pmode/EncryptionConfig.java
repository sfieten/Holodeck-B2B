/*******************************************************************************
 * Copyright (C) 2019 The Holodeck B2B Team, Sander Fieten
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
 ******************************************************************************/
package org.holodeckb2b.common.pmode;

import java.io.Serializable;

import org.holodeckb2b.interfaces.pmode.IEncryptionConfiguration;
import org.holodeckb2b.interfaces.pmode.IKeyAgreement;
import org.simpleframework.xml.Element;

/**
 * Contains the parameters related to the message level encryption.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 5.0.0
 */
public class EncryptionConfig implements IEncryptionConfiguration, Serializable {
	private static final long serialVersionUID = -3364424899897499432L;

    @Element(name = "KeystoreAlias")
    private KeystoreAlias keyStoreRef;

    // encryption algorithm
    @Element(name = "Algorithm", required = false)
    private String algorithm = null;

    @Element(name = "KeyTransport", required = false)
    private KeyTransportConfig  keytransportCfg;

    @Element(name = "KeyAgreement", required = false)
    private KeyAgreementConfig  keyAgreementCfg;

    /**
     * Default constructor creates a new and empty <code>EncryptionConfig</code> instance.
     */
    public EncryptionConfig() {
    }

    /**
     * Creates a new <code>EncryptionConfig</code> instance using the parameters from the provided {@link
     * IEncryptionConfiguration}  object.
     *
     * @param source The source object to copy the parameters from
     */
    public EncryptionConfig(final IEncryptionConfiguration source) {
    	if (source.getKeystoreAlias() != null) {
	        this.keyStoreRef = new KeystoreAlias();
	        this.keyStoreRef.name = source.getKeystoreAlias();
	        this.keyStoreRef.password = source.getCertificatePassword();
    	} else
    		this.keyStoreRef = null;
        this.keyStoreRef.name = source.getKeystoreAlias();
        this.keyStoreRef.password = source.getCertificatePassword();
        this.algorithm = source.getAlgorithm();
        this.keytransportCfg = source.getKeyTransport() != null ? new KeyTransportConfig(source.getKeyTransport())
        														: null;
        this.keyAgreementCfg = source.getKeyAgreement() != null ? new KeyAgreementConfig(source.getKeyAgreement())
        														: null;
    }

    @Override
    public String getKeystoreAlias() {
        return keyStoreRef != null ? keyStoreRef.name : null;
    }

    public void setKeystoreAlias(final String alias) {
        if (this.keyStoreRef == null)
        	this.keyStoreRef = new KeystoreAlias();
        this.keyStoreRef.name = alias;
    }

    @Override
    public String getCertificatePassword() {
        return keyStoreRef != null ? keyStoreRef.password : null;
    }

    public void setCertificatePassword(final String password) {
        if (this.keyStoreRef == null)
        	this.keyStoreRef = new KeystoreAlias();
        this.keyStoreRef.password = password;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public KeyTransportConfig getKeyTransport() {
        return keytransportCfg;
    }

    public void setKeyTransport(final KeyTransportConfig keytransport) {
        this.keytransportCfg = keytransport;
    }

    @Override
    public IKeyAgreement getKeyAgreement() {
    	return keyAgreementCfg;
    }

	public void setKeyAgreement(final IKeyAgreement keyAgreement) {
		this.keyAgreementCfg = keyAgreement != null ? new KeyAgreementConfig(keyAgreement) : null;
	}
}

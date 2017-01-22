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
package org.holodeckb2b.core.testhelpers;

import org.holodeckb2b.common.config.InternalConfiguration;
import org.holodeckb2b.submit.core.MessageSubmitter;
import org.holodeckb2b.events.SyncEventProcessor;
import org.holodeckb2b.interfaces.core.IHolodeckB2BCore;
import org.holodeckb2b.interfaces.core.IHolodeckB2BUpdateManger;
import org.holodeckb2b.interfaces.delivery.IDeliverySpecification;
import org.holodeckb2b.interfaces.delivery.IMessageDeliverer;
import org.holodeckb2b.interfaces.delivery.MessageDeliveryException;
import org.holodeckb2b.interfaces.events.IMessageProcessingEventProcessor;
import org.holodeckb2b.interfaces.persistency.IPersistencyProvider;
import org.holodeckb2b.interfaces.persistency.dao.IDAOFactory;
import org.holodeckb2b.interfaces.persistency.dao.IQueryManager;
import org.holodeckb2b.interfaces.persistency.dao.IUpdateManager;
import org.holodeckb2b.interfaces.pmode.IPModeSet;
import org.holodeckb2b.interfaces.submit.IMessageSubmitter;
import org.holodeckb2b.interfaces.workerpool.IWorkerPoolConfiguration;
import org.holodeckb2b.interfaces.workerpool.TaskConfigurationException;
import org.holodeckb2b.persistency.DefaultProvider;
import org.holodeckb2b.persistency.dao.UpdateManager;
import org.holodeckb2b.pmode.InMemoryPModeSet;
import org.holodeckb2b.pmode.PModeManager;

/**
 * Is utility class for testing the e-SENS connector that simulates the Holodeck B2B Core.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
public class HolodeckB2BTestCore implements IHolodeckB2BCore, IHolodeckB2BUpdateManger {

    private static final class SubmitterSingletonHolder {
        static final IMessageSubmitter instance = new MessageSubmitter();
    }

    private final Config  config;

    private IPModeSet pmodeSet;

    private IMessageProcessingEventProcessor eventProcessor;

    private IPersistencyProvider provider = new DefaultProvider();

    private IDAOFactory daoFactory = null;

    public HolodeckB2BTestCore(final String homeDir) {
        this(homeDir, null, null);
    }

    public HolodeckB2BTestCore(final String homeDir,
                               final String pmodeValidatorClass) {
        this(homeDir, pmodeValidatorClass, null);
    }

    public HolodeckB2BTestCore(final String homeDir,
                               final String pmodeValidatorClass,
                               final String pmodeStorageClass) {
        config = new Config(homeDir, pmodeValidatorClass, pmodeStorageClass);
        pmodeSet = new InMemoryPModeSet();
        eventProcessor = new SyncEventProcessor();
    }

    @Override
    public InternalConfiguration getConfiguration() {
        return config;
    }

    @Override
    public IMessageDeliverer getMessageDeliverer(
            final IDeliverySpecification deliverySpec)
            throws MessageDeliveryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IMessageSubmitter getMessageSubmitter() {
        return SubmitterSingletonHolder.instance;
    }

    @Override
    public IPModeSet getPModeSet() {
        if (pmodeSet == null)
            pmodeSet = new PModeManager(config.getPModeValidatorImplClass(),
                    config.getPModeStorageImplClass());

        return pmodeSet;
    }

    public void setEventProcessor(final IMessageProcessingEventProcessor processor) {
        eventProcessor = processor;
    }

    @Override
    public IMessageProcessingEventProcessor getEventProcessor() {
        return eventProcessor;
    }

    @Override
    public void setPullWorkerPoolConfiguration(
            final IWorkerPoolConfiguration pullConfiguration)
            throws TaskConfigurationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IQueryManager getQueryManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IUpdateManager getUpdateManager() {
        return new UpdateManager(daoFactory.getUpdateManager());
    }
}

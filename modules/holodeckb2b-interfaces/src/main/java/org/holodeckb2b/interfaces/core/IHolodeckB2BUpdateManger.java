package org.holodeckb2b.interfaces.core;

import org.holodeckb2b.interfaces.persistency.dao.IUpdateManager;

/**
 * Created at 12:39 22.01.17
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public interface IHolodeckB2BUpdateManger {
    /**
     * Gets the data access object that should be used to store and update the meta-data on processed message units.
     * <p>The returned data access object is a facade to the one provided by the persistency provider to ensure that
     * changes in the message unit meta-data are managed correctly.
     *
     * @return  The {@link IUpdateManager} that Core classes should use to update meta-data of message units
     * @since HB2B_NEXT_VERSION
     */
    public IUpdateManager getUpdateManager();
}

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
package org.holodeckb2b.common.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.holodeckb2b.interfaces.workerpool.IWorkerTask;

/**
 * Is a base implementation of {@link IWorkerTask} providing basic functionality for a worker task. It provides default
 * naming of the task, a logging facility and exception management to prevent the task to halt further execution due to
 * an unexpected exception during execution.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public abstract class AbstractWorkerTask implements IWorkerTask {

    /**
     * The name of this task, will default to the class name
     */
    protected   String  name;

    /**
     * Logging facility, will create a log named like the class name
     */
    protected   Logger  log;

    /**
     * Default constructor. Initializes the log.
     */
    public AbstractWorkerTask() {
        name = this.getClass().getName();
        log = LogManager.getLogger(this.getClass().getName());
    }

    /**
     * Sets the name of this worker task.
     *
     * @param name The name to use for the worker task
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of this worker task.
     *
     * @return The name of the worker task. If no name was set, the class name will be used.
     */
    @Override
    public String getName() {
        if (name == null || name.isEmpty())
            name = this.getClass().getName();

        return name;
    }

    /**
     * Executes the functionality of the task by calling {@link #doProcessing()} and by catching all exceptions prevents
     * the task from halting further executing.
     */
    @Override
    public void run() {
        try {
            doProcessing();
        } catch (final InterruptedException interrupted) {
            log.warn("Task is interrupted!");
            Thread.currentThread().interrupt();
        } catch (final Throwable t) {
            log.error("Exception occurred during execution! Details: " + t.getMessage());
        }
    }

    /**
     * Implements the functionality of the task.
     *
     *  @throws InterruptedException In case processing gets interrupted
     */
    public abstract void doProcessing() throws InterruptedException;
}

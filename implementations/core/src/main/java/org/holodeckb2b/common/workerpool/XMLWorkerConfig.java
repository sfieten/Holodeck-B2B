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
package org.holodeckb2b.common.workerpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.holodeckb2b.interfaces.general.Interval;
import org.holodeckb2b.interfaces.workerpool.IWorkerConfiguration;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Implements the configuration of a Worker as defined by {@link IWorkerConfiguration} using XML. The element is 
 * specified by XML schema <i>http://holodeck-b2b.org/schemas/2012/12/workers</i>. The element definition is shown here:
 * <pre>
 *  &lt;worker
 *      name=<i>String : Name of this worker</i>
 *      activate=<i>boolean : Should this worker be active?</i>
 *      delay=<i>integer : The delay in seconds before starting the worker. Optional parameter, default = 0 (start immediately)</li></i>
 *      workerClass=<i>string : Name of the class that implements the workers functionality</i>
 *      concurrent=<i>integer : The number of concurrent executions. Optional parameter, default = 1</i>
 *      interval=<i>integer : If the worker should run repeatedly, the delay between executions in seconds.
 *                         Optional, when not included the worker will be executed just once. If set to 0 it will
 *                         run continuously.</i>
 *  &gt;
 *      &lt;-- <i>The worker may need some configuration settings. These can be supplied by adding one or more param child elements</i>--&gt;
 *      &lt;parameter name=<i>string : Name of the parameter</i>&gt;Value of the parameter&lt;/parameter&gt;
 *  &lt;/worker&gt;<br>
 * </pre>
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
@Root (name="worker")
public class XMLWorkerConfig implements IWorkerConfiguration {

    @Attribute
    private String name;

    @Attribute
    private boolean activate;

    @Attribute(required=false)
    private int delay = 0;

    @Attribute
    private String workerClass;

    @Attribute(required=false)
    private int concurrent;

    @Attribute(name="interval", required=false)
    private int xmlInterval = -1;

    private Interval    interval;

    @ElementMap(entry="parameter", key="name", attribute=true, inline=true, required=false)
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getWorkerTask() {
        return workerClass;
    }

    @Override
    public Map<String, String> getTaskParameters() {
        return parameters;
    }

    @Override
    public boolean activate() {
        return activate;
    }

    @Override
    public int getDelay() {
        // delay specified in XML is in seconds, but return value must be milliseconds!
        return delay * 1000;
    }

    @Override
    public int getConcurrentExecutions() {
        return concurrent;
    }

    @Override
    public Interval getInterval() {
        if (xmlInterval >= 0 && interval == null)
            interval = new Interval(xmlInterval, TimeUnit.SECONDS);

        return interval;
    }
}

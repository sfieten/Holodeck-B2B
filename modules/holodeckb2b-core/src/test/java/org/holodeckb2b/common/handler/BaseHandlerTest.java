package org.holodeckb2b.common.handler;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.holodeckb2b.core.testhelpers.HolodeckB2BTestCore;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.security.handlers.CreateWSSHeaders;
import org.holodeckb2b.security.handlers.RaiseSignatureCreatedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created at 14:55 14.01.17
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class BaseHandlerTest {

    private static String baseDir;

    private static HolodeckB2BTestCore core;

    private CreateWSSHeaders wssHeadersHandler;

    private BaseHandler handler;

    @BeforeClass
    public static void setUpClass() {
        baseDir = BaseHandlerTest.class
                .getClassLoader().getResource("common/handler").getPath();
        core = new HolodeckB2BTestCore(baseDir);
        HolodeckB2BCoreInterface.setImplementation(core);
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testIsInFlow() throws Exception {

    }

    @Test
    public void testInvoke() throws Exception {

    }

    @Test
    public void testFlowComplete() throws Exception {

    }

    @Test
    public void testRunningInCorrectFlow() throws Exception {

    }
}
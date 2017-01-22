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
package org.holodeckb2b.workers;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Sander Fieten <sander at chasquis-services.com>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PurgeOldMessagesWorkerTest {

    //todo: Refactor based on new persistency interfaces!
    
//    private static final String PAYLOAD_1_FILE = "payload1.xml";
//    private static final String PAYLOAD_1_MIMETYPE = "text/xml";
//
//    private static final String PAYLOAD_2_FILE = "payload2.jpg";
//    private static final String PAYLOAD_2_MIMETYPE = "image/jpeg";
//
//    private static final String MSGID_1 = "um1-msg-id@test";
//    private static final String MSGID_2 = "um2-msg-id@test";
//    private static final String MSGID_3 = "um3-msg-id@test";
//    private static final String MSGID_4 = "r1-msg-id@test";
//    private static final String MSGID_5 = "e1-msg-id@test";
//    private static final String MSGID_6 = "um6-msg-id@test";
//
//    private static String basePath = PurgeOldMessagesWorkerTest.class.getClassLoader().getResource("purgetest/").getPath();
//
//    public PurgeOldMessagesWorkerTest() {
//    }
//
//
//    @BeforeClass
//    public static void setUpClass() {
//        try {
//            final EntityManager em = JPAUtil.getEntityManager();
//
//            em.getTransaction().begin();
//
//            UserMessage um;
//            Path targetPath;
//            ProcessingState state;
//            Calendar stateTime = Calendar.getInstance();
//
//            // Ensure tmp directory is available
//            new File(basePath + "tmp").mkdir();
//
//            um = new UserMessage();
//            um.setMessageId(MSGID_1);
//            targetPath = Paths.get(basePath, "tmp", "1-" + PAYLOAD_1_FILE);
//            Files.copy(Paths.get(basePath, PAYLOAD_1_FILE), targetPath, StandardCopyOption.REPLACE_EXISTING);
//            um.addPayload(new Payload(targetPath.toString(), PAYLOAD_1_MIMETYPE));
//            targetPath = Paths.get(basePath, "tmp", "1-" + PAYLOAD_2_FILE);
//            Files.copy(Paths.get(basePath, PAYLOAD_2_FILE), targetPath, StandardCopyOption.REPLACE_EXISTING);
//            um.addPayload(new Payload(targetPath.toString(), PAYLOAD_2_MIMETYPE));
//            state = new ProcessingState("TEST");
//            stateTime.add(Calendar.DAY_OF_YEAR, -20);
//            state.setStartTime(stateTime.getTime());
//            um.setProcessingState(state);
//            em.persist(um);
//
//            um = new UserMessage();
//            um.setMessageId(MSGID_2);
//            targetPath = Paths.get(basePath, "tmp", "2-" + PAYLOAD_1_FILE);
//            um.addPayload(new Payload(targetPath.toString(), PAYLOAD_1_MIMETYPE));
//            state = new ProcessingState("TEST");
//            stateTime = Calendar.getInstance();
//            stateTime.add(Calendar.DAY_OF_YEAR, -13);
//            state.setStartTime(stateTime.getTime());
//            um.setProcessingState(state);
//            em.persist(um);
//
//            um = new UserMessage();
//            um.setMessageId(MSGID_3);
//            targetPath = Paths.get(basePath, "tmp", "3-" + PAYLOAD_1_FILE);
//            Files.copy(Paths.get(basePath, PAYLOAD_1_FILE), targetPath, StandardCopyOption.REPLACE_EXISTING);
//            um.addPayload(new Payload(targetPath.toString(), PAYLOAD_1_MIMETYPE));
//            state = new ProcessingState("TEST");
//            stateTime = Calendar.getInstance();
//            stateTime.add(Calendar.DAY_OF_YEAR, -8);
//            state.setStartTime(stateTime.getTime());
//            um.setProcessingState(state);
//            em.persist(um);
//
//            final Receipt rcpt = new Receipt();
//            rcpt.setMessageId(MSGID_4);
//            state = new ProcessingState("TEST");
//            stateTime = Calendar.getInstance();
//            stateTime.add(Calendar.DAY_OF_YEAR, -8);
//            state.setStartTime(stateTime.getTime());
//            rcpt.setProcessingState(state);
//            final OMFactory factory = OMAbstractFactory.getOMFactory();
//            final OMElement root = factory.createOMElement("root", null);
//            final OMElement elt11 = factory.createOMElement("fakeContent",null);
//            elt11.setText("No real Receipt");
//            rcpt.setContent(root.getChildElements());
//            em.persist(rcpt);
//
//            final ErrorMessage error = new ErrorMessage();
//            error.setMessageId(MSGID_5);
//            state = new ProcessingState("TEST");
//            stateTime = Calendar.getInstance();
//            stateTime.add(Calendar.DAY_OF_YEAR, -4);
//            state.setStartTime(stateTime.getTime());
//            error.setProcessingState(state);
//            em.persist(error);
//
//            um = new UserMessage();
//            um.setMessageId(MSGID_6);
//            targetPath = Paths.get(basePath, "tmp", "6-" + PAYLOAD_1_FILE);
//            Files.copy(Paths.get(basePath, PAYLOAD_1_FILE), targetPath, StandardCopyOption.REPLACE_EXISTING);
//            um.addPayload(new Payload(targetPath.toString(), PAYLOAD_1_MIMETYPE));
//            state = new ProcessingState("TEST");
//            stateTime = Calendar.getInstance();
//            stateTime.add(Calendar.DAY_OF_YEAR, -4);
//            state.setStartTime(stateTime.getTime());
//            um.setProcessingState(state);
//            em.persist(um);
//
//            em.getTransaction().commit();
//
//            final List<MessageUnit> muInDB = em.createQuery("from MessageUnit", MessageUnit.class).getResultList();
//            assertEquals(6, muInDB.size());
//
//            em.close();
//        } catch (final Exception ex) {
//            Logger.getLogger(PurgeOldMessagesWorkerTest.class.getName()).log(Level.SEVERE, "Could not setup test", ex);
//            fail("Test could not be prepared!");
//        }
//    }
//
//    @Test
//    public void test0_NaNPurgeDelay() {
//        final PurgeOldMessagesWorker worker = new PurgeOldMessagesWorker();
//
//        final HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put(PurgeOldMessagesWorker.P_PURGE_AFTER_DAYS, "NaN");
//        try {
//            worker.setParameters(parameters);
//        } catch (final Exception e) {
//            fail("Worker did not accept wrong delay value");
//        }
//    }
//
//    @Test
//    public void test0_NothingToPurge() throws PersistenceException {
//        final PurgeOldMessagesWorker worker = new PurgeOldMessagesWorker();
//
//        worker.setParameters(null);
//        try {
//            worker.doProcessing();
//        } catch (final InterruptedException ex) {
//            Logger.getLogger(PurgeOldMessagesWorkerTest.class.getName()).log(Level.SEVERE, null, ex);
//            fail("Exception during processing");
//        }
//
//        final EntityManager em = JPAUtil.getEntityManager();
//        final List<MessageUnit> muInDB = em.createQuery("from MessageUnit", MessageUnit.class).getResultList();
//
//        assertEquals(6, muInDB.size());
//    }
//
//    @Test
//    public void test1_OneToPurge() throws PersistenceException {
//        final PurgeOldMessagesWorker worker = new PurgeOldMessagesWorker();
//
//        final HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put(PurgeOldMessagesWorker.P_PURGE_AFTER_DAYS, 15);
//        worker.setParameters(parameters);
//
//        try {
//            // Enable event processing
//            final HolodeckB2BTestCore hb2bTestCore = new HolodeckB2BTestCore("");
//            final TestEventProcessor eventProcessor = new TestEventProcessor();
//            hb2bTestCore.setEventProcessor(eventProcessor);
//            HolodeckB2BCoreInterface.setImplementation(hb2bTestCore);
//
//            worker.doProcessing();
//
//            assertTrue(eventProcessor.allPurgeEvents);
//            assertEquals(1, eventProcessor.msgIdsPurged.size());
//            assertEquals(MSGID_1, eventProcessor.msgIdsPurged.get(0));
//        } catch (final Exception ex) {
//            Logger.getLogger(PurgeOldMessagesWorkerTest.class.getName()).log(Level.SEVERE, null, ex);
//            fail("Exception during processing");
//        }
//
//        final EntityManager em = JPAUtil.getEntityManager();
//        final List<MessageUnit> muInDB = em.createQuery("from MessageUnit", MessageUnit.class).getResultList();
//
//        assertEquals(5, muInDB.size());
//
//        boolean m1Deleted = true;
//        for (final MessageUnit mu : muInDB)
//            m1Deleted &= !MSGID_1.equals(mu.getMessageId());
//        assertTrue(m1Deleted);
//
//        assertFalse(Paths.get(basePath, "tmp", "1-" + PAYLOAD_1_FILE).toFile().exists());
//        assertFalse(Paths.get(basePath, "tmp", "2-" + PAYLOAD_2_FILE).toFile().exists());
//    }
//
//    @Test
//    public void test2_PayloadAlreadyRemoved() throws PersistenceException {
//        final PurgeOldMessagesWorker worker = new PurgeOldMessagesWorker();
//
//        final HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put(PurgeOldMessagesWorker.P_PURGE_AFTER_DAYS, 10);
//        worker.setParameters(parameters);
//
//        try {
//            // Enable event processing
//            final HolodeckB2BTestCore hb2bTestCore = new HolodeckB2BTestCore("");
//            final TestEventProcessor eventProcessor = new TestEventProcessor();
//            hb2bTestCore.setEventProcessor(eventProcessor);
//            HolodeckB2BCoreInterface.setImplementation(hb2bTestCore);
//
//            worker.doProcessing();
//
//            assertTrue(eventProcessor.allPurgeEvents);
//            assertEquals(1, eventProcessor.msgIdsPurged.size());
//            assertEquals(MSGID_2, eventProcessor.msgIdsPurged.get(0));
//        } catch (final Exception ex) {
//            Logger.getLogger(PurgeOldMessagesWorkerTest.class.getName()).log(Level.SEVERE, null, ex);
//            fail("Exception during processing");
//        }
//
//        final EntityManager em = JPAUtil.getEntityManager();
//        final List<MessageUnit> muInDB = em.createQuery("from MessageUnit", MessageUnit.class).getResultList();
//
//        assertEquals(4, muInDB.size());
//
//        boolean m2Deleted = true;
//        for (final MessageUnit mu : muInDB)
//            m2Deleted &= !MSGID_2.equals(mu.getMessageId());
//
//        assertTrue(m2Deleted);
//    }
//
//    @Test
//    public void test3_EventsOnlyForUserMsg() throws PersistenceException {
//        final PurgeOldMessagesWorker worker = new PurgeOldMessagesWorker();
//
//        final HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put(PurgeOldMessagesWorker.P_PURGE_AFTER_DAYS, 5);
//        worker.setParameters(parameters);
//
//        try {
//            // Enable event processing
//            final HolodeckB2BTestCore hb2bTestCore = new HolodeckB2BTestCore("");
//            final TestEventProcessor eventProcessor = new TestEventProcessor();
//            hb2bTestCore.setEventProcessor(eventProcessor);
//            HolodeckB2BCoreInterface.setImplementation(hb2bTestCore);
//
//            worker.doProcessing();
//
//            assertTrue(eventProcessor.allPurgeEvents);
//            assertEquals(1, eventProcessor.msgIdsPurged.size());
//            assertEquals(MSGID_3, eventProcessor.msgIdsPurged.get(0));
//        } catch (final Exception ex) {
//            Logger.getLogger(PurgeOldMessagesWorkerTest.class.getName()).log(Level.SEVERE, null, ex);
//            fail("Exception during processing");
//        }
//
//        final EntityManager em = JPAUtil.getEntityManager();
//        final List<MessageUnit> muInDB = em.createQuery("from MessageUnit", MessageUnit.class).getResultList();
//
//        assertEquals(2, muInDB.size());
//
//        boolean m3Deleted = true;
//        boolean m4Deleted = true;
//        for (final MessageUnit mu : muInDB) {
//            m3Deleted &= !MSGID_3.equals(mu.getMessageId());
//            m4Deleted &= !MSGID_4.equals(mu.getMessageId());
//        }
//        assertTrue(m3Deleted);
//        assertTrue(m4Deleted);
//
//        assertFalse(Paths.get(basePath, "tmp", "3-" + PAYLOAD_1_FILE).toFile().exists());
//    }
//
//    class TestEventProcessor implements IMessageProcessingEventProcessor {
//
//        boolean allPurgeEvents = false;
//        ArrayList<String>   msgIdsPurged = new ArrayList<>();
//
//        @Override
//        public void raiseEvent(final IMessageProcessingEvent event, final MessageContext msgContext) {
//            allPurgeEvents = event instanceof IMessageUnitPurgedEvent;
//            if (allPurgeEvents) {
//                assertNotNull(event.getSubject());
//                msgIdsPurged.add(event.getSubject().getMessageId());
//            }
//
//        }
//    }
}

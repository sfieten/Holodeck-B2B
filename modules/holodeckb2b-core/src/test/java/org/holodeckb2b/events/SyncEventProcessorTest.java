package org.holodeckb2b.events;

import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.interfaces.events.IMessageProcessingEvent;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.holodeckb2b.interfaces.messagemodel.IMessageUnit;
import org.holodeckb2b.interfaces.persistency.entities.IMessageUnitEntity;
import org.holodeckb2b.interfaces.pmode.ILeg;
import org.holodeckb2b.interfaces.processingmodel.IMessageUnitProcessingState;
import org.holodeckb2b.core.testhelpers.HolodeckB2BTestCore;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;

import java.util.Date;
import java.util.List;

/**
 * Created at 15:20 28.10.16
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class SyncEventProcessorTest {

    static final QName MESSAGE_ID_ELEMENT_NAME =
            new QName(EbMSConstants.EBMS3_NS_URI, "MessageId");

    private static String baseDir;

    private static HolodeckB2BTestCore core;

    @BeforeClass
    public static void setUpClass() {
        baseDir = SyncEventProcessorTest.class
                .getClassLoader().getResource("events").getPath();
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
    public void testRaisingEventWithEmptyMessage() throws Exception {
        MessageContext mc = new MessageContext();
        SyncEventProcessor processor = new SyncEventProcessor();
        core.setEventProcessor(processor);
        SyncEventForTest event = new SyncEventForTest("sync_event_for_test",
                "sync event for test", null);
        processor.raiseEvent(event, mc);
    }

    @Test
    public void testRaisingEvent() throws Exception {
        MessageContext mc = new MessageContext();
        SyncEventProcessor processor = new SyncEventProcessor();
        core.setEventProcessor(processor);
        SyncEventForTest event = new SyncEventForTest("sync_event_for_test",
                "sync event for test", new TestUserMessage("test_message"));
        processor.raiseEvent(event, mc);
    }

    class SyncEventForTest implements IMessageProcessingEvent {

        private String id;
        private String message;
        private IMessageUnit subject;

        public SyncEventForTest(String id, String message, IMessageUnit subject) {
            this.id = id;
            this.message = message;
            this.subject = subject;
        }

        /**
         * Gets the <b>unique</b> identifier of this event.
         * It is RECOMMENDED that this identifier is a valid XML ID so it
         * can be easily included in an XML representation of the event.
         *
         * @return A {@link String} containing the unique identifier of this event
         */
        @Override
        public String getId() {
            return id;
        }

        /**
         * Gets the timestamp when the event occurred.
         *
         * @return A {@link Date} representing the date and time the event occurred
         */
        @Override
        public Date getTimestamp() {
            return new Date();
        }

        /**
         * Gets an <b>optional</b> short description of what happened.
         * It is RECOMMENDED to limit the length of the
         * description to 100 characters.
         *
         * @return A {@link String} with a short description of the event
         * if available, <code>null</code> otherwise
         */
        @Override
        public String getMessage() {
            return message;
        }

        /**
         * Gets the message unit that the event applies to.
         * <p>NOTE: An event can only relate to one message unit.
         * If an event occurs that applies to multiple message units
         * the <i>event source component</i> must create multiple
         * <code>IMessageProcessingEvent</code> objects for each
         * message unit.
         *
         * @return The {@link IMessageUnit} this event applies to.
         */
        @Override
        public IMessageUnit getSubject() {
            return subject;
        }
    }

    class TestUserMessage implements IMessageUnitEntity {

        private String messageId;

        public TestUserMessage(String messageId) {
            this.messageId = messageId;
        }


        /**
         * Indicates whether all meta-data of the object have been loaded. See the class documentation which fields may be
         * loaded lazily.
         *
         * @return <code>true</code> if all data has been retrieved from storage,<br>
         * <code>false</code> otherwise
         */
        @Override
        public boolean isLoadedCompletely() {
            return false;
        }

        /**
         * Gets the label of the leg within the P-Mode on which this message unit is exchanged.
         *
         * @return The leg label
         */
        @Override
        public ILeg.Label getLeg() {
            return null;
        }

        /**
         * Gets the indication whether this message unit is send using a multi-hop exchange
         *
         * @return <code>true</code> if multi-hop is used for exchange of this message unit,<br>
         * <code>false</code> otherwise
         */
        @Override
        public boolean usesMultiHop() {
            return false;
        }

        /**
         * Gets the direction in which this message unit is sent, i.e. received or sent by Holodeck B2B.
         *
         * @return The direction in which this message unit flows
         * @since HB2B_NEXT_VERSION
         */
        @Override
        public Direction getDirection() {
            return null;
        }

        /**
         * Gets the timestamp when the message unit was created.
         * <p>Corresponds to the <code>MessageInfo/Timestamp</code> element. See section 5.2.2.1 of the ebMS Core
         * specification.
         *
         * @return The timestamp when the message unit was created as a {@link Date}
         */
        @Override
        public Date getTimestamp() {
            return new Date();
        }

        /**
         * Gets the message id of the message unit.
         * <p>Corresponds to the <code>MessageInfo/MessageId</code> element. See section 5.2.2.1 of the ebMS Core
         * specification.
         *
         * @return The message id as a globally unique identifier conforming to RFC2822.
         */
        @Override
        public String getMessageId() {
            return messageId;
        }

        /**
         * Get the message id of the message unit to which this message unit is a response.
         * <p>Corresponds to the <code>MessageInfo/RefToMessageId</code> element. See section 5.2.2.1 of the ebMS Core
         * specification.
         *
         * @return The message id of the message this message unit is a response to
         */
        @Override
        public String getRefToMessageId() {
            return null;
        }

        /**
         * Gets the identifier of the P-Mode that governs the processing of this message unit.
         * <p>Note that the P-Mode may not always be known, for example when a signal message unit is received which can not
         * be related to a sent message.
         *
         * @return If known, the identifier of the P-Mode that governs processing of this message unit,<br>
         * otherwise <code>null</code>
         * @since 2.1.0
         */
        @Override
        public String getPModeId() {
            return null;
        }

        /**
         * Gets the list of processing states this message unit was or is in.
         * <p>The order of the processing states as they occur in the list is the same as they applied to the message unit
         * with the last processing state in the list  (i.e. with the highest index) being the current processing state.
         *
         * @return List of {@link IMessageUnitProcessingState} in the order they applied to this message unit
         * @since HB2B_NEXT_VERSION
         */
        @Override
        public List<IMessageUnitProcessingState> getProcessingStates() {
            return null;
        }

        /**
         * Gets the current processing state the message unit is in.
         * <p>Although the current state is the last item in the list that is returned by the {@link #getProcessingStates()}
         * method this method is simpler to use and it also allows implements to optimize the handling of the current
         * processing state.
         *
         * @return The {@link IMessageUnitProcessingState} the message unit is currently in
         * @since HB2B_NEXT_VERSION
         */
        @Override
        public IMessageUnitProcessingState getCurrentProcessingState() {
            return null;
        }
    }
}
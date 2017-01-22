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
package org.holodeckb2b.handlers.inflow;

import java.util.Collection;
import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.common.handler.BaseHandler;
import org.holodeckb2b.common.util.Utils;
import org.holodeckb2b.constants.MessageContextProperties;
import org.holodeckb2b.interfaces.delivery.IDeliverySpecification;
import org.holodeckb2b.interfaces.delivery.IMessageDeliverer;
import org.holodeckb2b.interfaces.delivery.MessageDeliveryException;
import org.holodeckb2b.interfaces.persistency.PersistenceException;
import org.holodeckb2b.interfaces.persistency.entities.IReceiptEntity;
import org.holodeckb2b.interfaces.pmode.ILeg;
import org.holodeckb2b.interfaces.pmode.IReceiptConfiguration;
import org.holodeckb2b.interfaces.processingmodel.ProcessingState;
import org.holodeckb2b.module.HolodeckB2BCore;
import org.holodeckb2b.persistency.dao.UpdateManager;

/**
 * Is the <i>IN_FLOW</i> handler responsible for checking if receipt messages should be delivered to the business
 * application and if so to hand them over to the responsible {@link IMessageDeliverer}.
 * <p>To prevent that a Receipt is delivered twice (in parallel) delivery only takes place when the processing state of
 * the unit can be successfully changed from {@link ProcessingStates#READY_FOR_DELIVERY} to
 * {@link ProcessingStates#OUT_FOR_DELIVERY}.
 * <p>NOTE: The actual delivery to the business application is done through a {@link IMessageDeliverer} which is
 * specified in the P-Mode for the referenced user message. If available, the delivery method specification specific for
 * Receipt {@link IReceiptConfiguration#getReceiptDelivery()}) will be used, otherwise the default delivery method
 * ({@link ILeg#getDefaultDelivery())} will be used.
 *
 * @author Sander Fieten <sander at holodeck-b2b.org>
 */
public class DeliverReceipts extends BaseHandler {

    @Override
    protected byte inFlows() {
        return IN_FLOW | IN_FAULT_FLOW;
    }

    @Override
    protected InvocationResponse doProcessing(final MessageContext mc) throws PersistenceException {
        // Check if this message contains receipt signals
        final Collection<IReceiptEntity> rcptSignals = (Collection<IReceiptEntity>)
                                                                mc.getProperty(MessageContextProperties.IN_RECEIPTS);

        if (Utils.isNullOrEmpty(rcptSignals))
            // No receipts to deliver
            return InvocationResponse.CONTINUE;

        log.debug("Message contains " + rcptSignals.size() + " Receipt Signals");
        UpdateManager updateManager = HolodeckB2BCore.getUpdateManager();
        // Process each signal
        for(final IReceiptEntity receipt : rcptSignals) {
            // Prepare message for delivery by checking it is still ready for delivery and then
            // change its processing state to "out for delivery"
            log.debug("Prepare message [" + receipt.getMessageId() + "] for delivery");
            if(updateManager.setProcessingState(receipt, ProcessingState.READY_FOR_DELIVERY,
                                                         ProcessingState.OUT_FOR_DELIVERY)) {
                // Receipt in this signal can be delivered to business application
                try {
                    deliverReceipt(receipt);
                    // Receipt signal processed, change the processing state to done
                    updateManager.setProcessingState(receipt, ProcessingState.DONE);
                } catch (final MessageDeliveryException ex) {
                    log.warn("Could not deliver Receipt Signal (msgId=" + receipt.getMessageId()
                                    + "]) to application! Error details: " + ex.getMessage());
                    // Although the receipt could not be delivered it was processed completely on the ebMS level,
                    //  so processing state is set to warning instead of failure
                    updateManager.setProcessingState(receipt, ProcessingState.WARNING);
                }
            } else {
                log.info("Receipt signal [" + receipt.getMessageId() + "] is already processed for delivery");
            }
        }
        log.debug("Processed all Receipt signals in message");
        return InvocationResponse.CONTINUE;
    }

    /**
     * Is a helper method responsible for checking whether and if so delivering a receipt to the business application.
     * Delivery to the business application is done through a {@link IMessageDeliverer}.
     *
     * @param receipt       The Receipt Signal to process
     * @throws MessageDeliveryException When the receipt should be delivered to the business application but an error
     *                                  prevented successful delivery
     */
    private void deliverReceipt(final IReceiptEntity receipt) throws MessageDeliveryException {
        IDeliverySpecification deliverySpec = null;

        // Get delivery specification from P-Mode
        deliverySpec = getReceiptDelivery(receipt);

        // If a delivery specification was found the receipt should be delivered, else no reporting is needed
        if (deliverySpec != null) {
            log.debug("Receipt should be delivered using delivery specification with id:" + deliverySpec.getId());
            final IMessageDeliverer deliverer = HolodeckB2BCore.getMessageDeliverer(deliverySpec);
            // Deliver the Receipt using deliverer
            try {
                deliverer.deliver(receipt);
                log.debug("Receipt successfully delivered!");
            } catch (final MessageDeliveryException ex) {
                // There was an "normal/expected" issue during delivery, continue as normal
                throw ex;
            } catch (final Throwable t) {
                // Catch of Throwable used for extra safety in case the DeliveryMethod implementation does not
                // handle all exceptions correctly
                log.warn(deliverer.getClass().getSimpleName() + " threw " + t.getClass().getSimpleName()
                         + " instead of MessageDeliveryException!");
                throw new MessageDeliveryException("Unhandled exception during message delivery", t);
            }
        } else
            log.debug("Receipt does not need to be delivered");
    }


    /**
     * Is a helper method to determine if and how an error should be delivered to the business application.
     *
     * @param receipt   The Receipt Signal message unit to get delivery spec for
     * @return          When the receipt should be delivered to the business application, the {@link
     *                  IDeliverySpecification} that should be used for the delivery,<br>
     *                  <code>null</code> otherwise
     */
    protected IDeliverySpecification getReceiptDelivery(final IReceiptEntity receipt) {
        IDeliverySpecification deliverySpec = null;

        final ILeg leg = HolodeckB2BCore.getPModeSet().get(receipt.getPModeId())
                                                      .getLeg(receipt.getLeg());
        final IReceiptConfiguration rcptConfig = leg.getReceiptConfiguration();

        if (rcptConfig != null && rcptConfig.shouldNotifyReceiptToBusinessApplication()) {
            log.debug("Receipt should be delivered to business app, get delivery specification");
            deliverySpec = rcptConfig.getReceiptDelivery();
            if (deliverySpec == null) {
                log.debug("No specific delivery specified for receipt, use default delivery");
                deliverySpec = leg.getDefaultDelivery();
            }
        }

        return deliverySpec;
    }
}

package fr.polytech.invoice.components;

import java.util.List;

import javax.ejb.Local;

import fr.polytech.entities.Delivery;

@Local
public interface DeliveryBilling {

    /**
     * Generating invoice from deliveries
     * @param deliveries
     */
    void generatingInvoice(List<Delivery> deliveries);
}
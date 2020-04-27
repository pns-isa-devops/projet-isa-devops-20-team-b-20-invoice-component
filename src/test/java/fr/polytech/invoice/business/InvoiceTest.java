package fr.polytech.invoice.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import fr.polytech.entities.*;
import fr.polytech.invoice.components.DeliveryBilling;
import fr.polytech.invoice.components.InvoiceManager;
import fr.polytech.invoice.components.InvoiceBean.Invoice;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import arquillian.AbstractInvoiceTest;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class InvoiceTest extends AbstractInvoiceTest {

    @EJB(name = "invoice")
    private DeliveryBilling deliveryBilling;

    @EJB(name = "invoice")
    private InvoiceManager invoiceManager;

    private List<Delivery> deliveries;

    @Before
    public void init()
    {
        deliveries = new ArrayList<>();
        deliveries.add(new Delivery());
        deliveries.add(new Delivery());
        deliveries.add(new Delivery());
    }

    @Ignore
    @Test
    public void functionnal() {
        //Generating invoice
        deliveryBilling.generatingInvoice(deliveries);
        //Get invoices from database
        List<Invoice> invoices = invoiceManager.getInvoices();
        //Check if the new invoice inserted
        assertEquals(1, invoices.size());
        List<Delivery> deliveriesGot = invoices.get(0).getDeliveries();
        //Check if the number of deliveries of the invoice is correct
        assertEquals(deliveries.size(), deliveries.size());
        //Check if deliveries is equals with deliveries added 
        for(int i=0; i<deliveries.size(); i++) {
            assertEquals(deliveries.get(i), deliveriesGot.get(i));
        }
    }

}
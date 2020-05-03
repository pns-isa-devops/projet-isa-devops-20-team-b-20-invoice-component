package fr.polytech.invoice.business;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import arquillian.AbstractInvoiceTest;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.Invoice;
import fr.polytech.entities.Parcel;
import fr.polytech.invoice.components.DeliveryBilling;
import fr.polytech.invoice.components.InvoiceBean;
import fr.polytech.invoice.components.InvoiceManager;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class InvoiceTest extends AbstractInvoiceTest {

    @EJB(name = "invoice")
    private DeliveryBilling deliveryBilling;

    @EJB(name = "invoice")
    private InvoiceManager invoiceManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    private List<Delivery> deliveries;

    @Before
    public void init() throws Exception {
        utx.begin();
        Parcel p1 = new Parcel("AAAABBBBCA", "add1", "car1", "cust1");
        entityManager.persist(p1);
        Delivery d1 = new Delivery("AAAABBBBCD");
        d1.setParcel(p1);
        Parcel p2 = new Parcel("AAAABBBBCB", "add1", "car1", "cust1");
        entityManager.persist(p2);
        Delivery d2 = new Delivery("AAAABBBBCE");
        d2.setParcel(p2);
        Parcel p3 = new Parcel("AAAABBBBCC", "add1", "car1", "cust1");
        entityManager.persist(p3);
        Delivery d3 = new Delivery("AAAABBBBCF");
        d3.setParcel(p3);
        entityManager.persist(d1);
        entityManager.persist(d2);
        entityManager.persist(d3);

        deliveries = new ArrayList<>(Arrays.asList(d1, d2, d3));
        utx.commit();
    }

    @Test
    public void functionnal() throws Exception {
        utx.begin();
        deliveryBilling.generatingInvoice(deliveries);
        // Generating invoice
        utx.commit();
        // Get invoices from database
        utx.begin();
        List<Invoice> invoices = invoiceManager.getInvoices();
        utx.commit();
        // Check if the new invoice inserted
        assertEquals(1, invoices.size());
        List<Delivery> deliveriesGot = invoices.get(0).getDeliveries();
        // Check if the number of deliveries of the invoice is correct
        assertEquals(deliveries.size(), deliveriesGot.size());
        // Check if deliveries is equals with deliveries added
        for (int i = 0; i < deliveries.size(); i++) {
            assertEquals(deliveries.get(i), deliveriesGot.get(i));
        }

        assertEquals(this.deliveries.size() * InvoiceBean.PRICE_PER_DELIVERY + InvoiceBean.BASE_PRICE,
                (int) invoices.get(0).getPrice());
    }

}

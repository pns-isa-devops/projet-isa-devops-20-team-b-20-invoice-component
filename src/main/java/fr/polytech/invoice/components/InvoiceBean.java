package fr.polytech.invoice.components;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.polytech.entities.Delivery;
import fr.polytech.entities.Invoice;
import fr.polytech.entities.InvoiceStatus;

@Stateless
@LocalBean
@Named("invoice")
public class InvoiceBean implements DeliveryBilling, InvoiceManager {

    private static final Logger log = Logger.getLogger(Logger.class.getName());

    public static final int PRICE_PER_DELIVERY = 10;
    public static final int BASE_PRICE = 30;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void generatingInvoice(List<Delivery> deliveries) {
        List<Delivery> merged = new ArrayList<>();
        for (Delivery delivery : deliveries) {
            merged.add(entityManager.merge(delivery));
        }
        Invoice invoice = new Invoice();
        invoice.setDeliveries(merged);
        // TODO generate id
        invoice.setInvoiceId("IN1");
        invoice.setPrice(deliveries.size() * PRICE_PER_DELIVERY + BASE_PRICE);
        invoice.setStatus(InvoiceStatus.NOT_PAID);
        printStackTrace(invoice, "Invoice 1");
        entityManager.persist(invoice);
        invoice = entityManager.merge(invoice);
        printStackTrace(invoice, "Invoice 2");
        printStackTrace(find().get().get(0), "Invoice 3");
    }

    @Override
    public List<Invoice> getInvoices() {
        List<Invoice> invoices = find().get();
        printStackTrace(find().get().get(0), "Invoice 4");
        return invoices;
    }

    private Optional<List<Invoice>> find() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoice> criteria = builder.createQuery(Invoice.class);
        Root<Invoice> root = criteria.from(Invoice.class);
        criteria.select(root);

        TypedQuery<Invoice> query = entityManager.createQuery(criteria);
        try {
            return Optional.of(query.getResultList());
        } catch (NoResultException e) {
            log.log(Level.FINEST, "No result", e);
            return Optional.empty();
        }
    }

    private void printStackTrace(Object o, String str) {
        System.out.printf("******* StackTrace : %s ******\n", str);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < i; j++)
                System.out.printf("*");
            System.out.printf("\n");
        }
        System.out.println(o.toString());
        for (int i = 15; i > 0; i--) {
            for (int j = 0; j < i; j++)
                System.out.printf("*");
            System.out.printf("\n");
        }
        System.out.println("********** End **********");
    }
}

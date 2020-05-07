package fr.polytech.invoice.components;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;

import java.util.Optional;
import java.util.Random;
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
import fr.polytech.invoice.exceptions.InvoiceNotFoundException;

@Stateless
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
        invoice.setInvoiceId(generateID(merged));
        invoice.setPrice(deliveries.size() * PRICE_PER_DELIVERY + BASE_PRICE);
        invoice.setStatus(InvoiceStatus.NOT_PAID);
        entityManager.persist(invoice);
    }

    @Override
    public List<Invoice> getInvoices() {
        List<Invoice> invoices = find().get();
        return invoices;
    }

    @Override
    public Invoice confirmInvoicePayment(String invoiceID) throws InvoiceNotFoundException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoice> criteria = builder.createQuery(Invoice.class);
        Root<Invoice> root = criteria.from(Invoice.class);
        criteria.select(root).where(builder.equal(root.get("invoiceId"), invoiceID));
        Invoice found;

        TypedQuery<Invoice> query = entityManager.createQuery(criteria);
        try {
            found = query.getSingleResult();
            found.setStatus(InvoiceStatus.PAID);
            return found;
        } catch (NoResultException e) {
            log.log(Level.FINEST, "No result for [" + invoiceID + "]", e);
            throw new InvoiceNotFoundException(invoiceID);
        }
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

    /**
     * generate an ID of 15 characters : numbers followed by uppercase letters
     *
     * @param deliveries object
     * @return
     */
    private String generateID(List<Delivery> deliveries) {
        int hashList = deliveries.hashCode();
        hashList *= hashList < 0 ? -1 : 1;
        StringBuilder generated = new StringBuilder();
        String hash = Integer.toString(hashList);
        int toComplete = hash.length() > 15 ? 0 : 15 - hash.length();

        for (int i = 0; i < 15; i++) {
            if (i + toComplete < 15) {
                if (i < 10) {
                    generated.append(hash.charAt(i));
                } else {
                    generated.append((hash.charAt(i) - '0') % 26 + 'A');
                }
            } else {
                generated.append((char) (new Random().nextInt(26) + 'A')); // random uppercase char
            }
        }

        return generated.toString();
    }
}

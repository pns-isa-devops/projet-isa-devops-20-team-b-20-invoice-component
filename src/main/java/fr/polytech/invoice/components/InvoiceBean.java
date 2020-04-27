package fr.polytech.invoice.components;

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


@Stateless
@LocalBean
@Named("invoice")
public class InvoiceBean implements DeliveryBilling, InvoiceManager {

    /**
     * To remove just to not change entities for the starting
     */
    public class Invoice {
        public void setDeliveries(List<Delivery> deliveries) {}
        public void setStatus(InvoiceStatus status) {}
		public List<Delivery> getDeliveries() {
			return null;
		}

    }

    public enum InvoiceStatus {
        NOT_PAID;
    }
    /**
     * END
     */

    private static final Logger log = Logger.getLogger(Logger.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void generatingInvoice(List<Delivery> deliveries) {
        Invoice invoice = new Invoice();
        invoice.setDeliveries(deliveries);
        invoice.setStatus(InvoiceStatus.NOT_PAID);
        entityManager.persist(invoice);
    }

    @Override
    public List<Invoice> getInvoices() {
        return find().get();
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


}
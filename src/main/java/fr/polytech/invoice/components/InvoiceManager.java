package fr.polytech.invoice.components;

import java.util.List;
import javax.ejb.Local;

import fr.polytech.entities.Invoice;

@Local
public interface InvoiceManager {

    public List<Invoice> getInvoices();
}
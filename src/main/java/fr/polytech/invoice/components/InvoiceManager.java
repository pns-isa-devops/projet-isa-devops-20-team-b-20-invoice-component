package fr.polytech.invoice.components;

import java.util.List;
import javax.ejb.Local;

import fr.polytech.entities.Invoice;
import fr.polytech.invoice.exceptions.InvoiceNotFoundException;

@Local
public interface InvoiceManager {

    List<Invoice> getInvoices();

    Invoice confirmInvoicePayment(String invoiceID) throws InvoiceNotFoundException;
}
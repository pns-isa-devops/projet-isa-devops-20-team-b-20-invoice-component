package fr.polytech.invoice.exceptions;

import java.io.Serializable;

public class InvoiceNotFoundException extends Exception implements Serializable {

    final String invoiceID;

    public InvoiceNotFoundException(String invoiceID){
        this.invoiceID = invoiceID;
    }

    @Override
    public String getMessage() {
        return "There is no invoice corresponding to " + invoiceID + " in database";
    }
}

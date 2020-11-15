package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Sale;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mail bean is used to to store the information of an invoice and format it to be able to send by email
 * @author Grant
 */
@Named
@RequestScoped
public class MailBean {
    private final static Logger LOG = LoggerFactory.getLogger(MailBean.class);
    
    @Inject
    private TaxBackingBean tbb;
    private String sendTo;
    private Sale sale;
    private List<Book> invoiceBooks;
    private String plainTextMsg;
    private String htmlTextMsg;
    private final String subject = "Invoice";

    
    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
    
    public Sale getSale(){
        return this.sale;
    }
    
    public void setSale(Sale sale){
        this.sale = sale;
    }

    public List<Book> getInvoiceBooks() {
        return this.invoiceBooks;
    }

    public void setInvoiceBooks(List<Book> invoiceBooks) {
        this.invoiceBooks = invoiceBooks;
        this.setPlainTextMsg();
        this.setHtmlTextMsg();
    }

    public String getPlainTextMsg(){
        return this.plainTextMsg;
    }
    
    public void setPlainTextMsg(){
        String invoice = ("Invoice #" + this.sale.getSaleID());
        invoice = invoice.concat(this.sale.getSaleDate() + " for Account#" + this.sale.getAccountID().getAccountID());
        
        for(Book book : this.invoiceBooks){
            invoice = invoice.concat(book.getTitle() + " $" + book.getListPrice());
        }
        
        invoice = invoice.concat("GST (" + this.tbb.getGST() + "%)");
        invoice = invoice.concat("HST (" + this.tbb.getHST() + "%)");
        invoice = invoice.concat("PST (" + this.tbb.getPST() + "%)");
        invoice = invoice.concat("Total: $" + String.valueOf(this.sale.getPrice()));
               
        this.plainTextMsg = invoice;
    }
    
    public String getHtmlTextMsg() {
        return htmlTextMsg;
    }
    

    private void setHtmlTextMsg() {
        String invoice = ("<h2>Invoice #" + this.sale.getSaleID() + "</h2><br/>");
        invoice = invoice.concat(this.sale.getSaleDate() + "for Account#" + this.sale.getAccountID().getAccountID() + "<br/>");
        
        for(Book book : this.invoiceBooks){
            invoice = invoice.concat(book.getTitle() + " $" + book.getListPrice() + "<br/>");
        }
        
        invoice = invoice.concat("<br/>GST (" + this.tbb.getGST() + ")%<br/>");
        invoice = invoice.concat("HST (" + this.tbb.getHST() + ")%<br/>");
        invoice = invoice.concat("PST (" + this.tbb.getPST() + ")%<br/>");
        invoice = invoice.concat("<br/><h3>Total: $" + String.valueOf(this.sale.getPrice()) + "</h3>");
               
        this.htmlTextMsg = "<html><META http-equiv=Content-Type "
                        + "content=\"text/html; charset=utf-8\">"
                        + "<body> " + invoice + "</body></html>";
    }

    public String getSubject() {
        return subject;
    }

}

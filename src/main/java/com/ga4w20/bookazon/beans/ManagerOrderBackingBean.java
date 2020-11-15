
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Sale;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.BooksaleJpaController;
import com.ga4w20.bookazon.persistence.SaleJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lin Yang
 */
@Named
@SessionScoped
public class ManagerOrderBackingBean implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(ManagerOrderBackingBean.class);
    
    @Inject
    private SaleJpaController sjc;
    @Inject
    private BooksaleJpaController bsjc;
    @Inject
    private BookJpaController bjc;


    private int saleID;
    private String isbn;
    private String title;
    private Account accountID;
    private List<ManagerOrderBackingBean> orderList;
    private int bookSaleID;
    private ManagerOrderBackingBean mobb;   

   
   
    
    public ManagerOrderBackingBean(){
        
    }

    public ManagerOrderBackingBean(int saleID, String isbn, String title, Account accountID, int bookSaleID) {
        this.saleID = saleID;
        this.isbn = isbn;
        this.title = title;
        this.accountID = accountID;
        this.bookSaleID = bookSaleID;
    }

    public List<ManagerOrderBackingBean> getOrderList() {
        this.orderList = sjc.salesOrder();
        return orderList;
    }

    public void setOrderList(List<ManagerOrderBackingBean> orderList) {
        this.orderList = orderList;
    }
    
    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Account getAccountID() {
        return accountID;
    }

    public void setAccountID(Account accountID) {
        this.accountID = accountID;
    }
    
    public int getBookSaleID() {
        return bookSaleID;
    }
    
    public void setBookSaleID(int booksaleID) {
        this.bookSaleID = booksaleID;
}
    
    public ManagerOrderBackingBean getMobb() {
        return mobb;
    }

    public void setMobb(ManagerOrderBackingBean mobb) {
        this.mobb = mobb;
    }
    
    public void editMobb(ManagerOrderBackingBean mobb){
        this.mobb = mobb;
    }
    
    /*
     * Delete the book from that order will change the prise on sale table and delete the relation on BookSale part. 
     * 
     */
    public void deleteBookFromOrder() throws NonexistentEntityException, IllegalOrphanException{
        Book book = bjc.findBookByIsbn(this.mobb.isbn);
        Sale sale = sjc.findSale(this.mobb.saleID);
        
        double newPrice = sale.getPrice() - book.getSalePrice();
        sale.setPrice(newPrice);
        sjc.edit(sale);
        bsjc.destroy(this.mobb.bookSaleID);
        LOG.debug(book.getImagePath());
    }
    
    /*
     * When user click the button, the order will change prize to 0 
     * which won't display on frond anymore. 
     */
    public void deleteOrder() throws IllegalOrphanException{
        Sale sale = sjc.findSale(this.mobb.saleID);
        sale.setPrice(0);
        sjc.edit(sale);
    }
}

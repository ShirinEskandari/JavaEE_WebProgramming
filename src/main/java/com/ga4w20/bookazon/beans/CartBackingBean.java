package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.persistence.BookJpaController;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Cart Backing Bean that stores the information of a cart to finalize the items in the cart and taxes
 * @author Grant
 */
@Named
@SessionScoped
public class CartBackingBean implements Serializable {
    @Inject
    private BookJpaController bjc;
    @Inject
    private TaxBackingBean tbb;
    private DecimalFormat df;
    private List<Book> listBooksInCart;
    private double subtotal;
    private double total;
    private double gstVal;
    private double hstVal;
    private double pstVal;

    @PostConstruct
    public void init() {
        this.listBooksInCart = new ArrayList<>();
        this.subtotal = 0.0;
        this.total = 0.0;
        this.gstVal = 0.0;
        this.hstVal = 0.0;
        this.pstVal = 0.0;
        this.df = new DecimalFormat("#.##");
        this.df.setRoundingMode(RoundingMode.CEILING);
        this.df.setMinimumFractionDigits(2);
    }

    public int getNumBooksInCart() {
        return this.listBooksInCart.size();
    }

    public void addBook(int id) {
        Book book = this.bjc.findBook(id);
        if (!this.listBooksInCart.contains(book)) {
            subtotal += book.getListPrice();
            this.listBooksInCart.add(book);
            this.setGSTVal();
            this.setHSTVal();
            this.setPSTVal();
            this.setTotal();
        }
    }

    public void removeBook(Book book) {
        if (this.listBooksInCart.contains(book)) {
            this.listBooksInCart.remove(book);
            this.subtotal -= book.getListPrice();
            this.setGSTVal();
            this.setHSTVal();
            this.setPSTVal();
            this.setTotal();
        }
    }

    public List<Book> getListBooksInCart() {
        return this.listBooksInCart;
    }

    public void setListBooksInCart(List<Book> listBooksInCart) {
        this.listBooksInCart = listBooksInCart;
    }

    public String getSubtotal() {
        return this.df.format(this.subtotal);
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal() {
        return this.df.format(this.total);
    }

    public void setTotal() {
        this.total = (this.subtotal
                + this.hstVal
                + this.gstVal
                + this.pstVal);
    }

    public String getHSTVal() {
        return this.df.format(this.hstVal);
    }

    public String getGSTVal() {
        return this.df.format(this.gstVal);
    }

    public String getPSTVal() {
        return this.df.format(this.pstVal);
    }

    public void setHSTVal() {
        this.hstVal = this.subtotal * this.tbb.getHST() / 100.0;
    }

    public void setGSTVal() {
        this.gstVal = this.subtotal * this.tbb.getGST() / 100.0;
    }

    public void setPSTVal() {
        this.pstVal = this.subtotal * this.tbb.getPST() / 100.0;
    }

}

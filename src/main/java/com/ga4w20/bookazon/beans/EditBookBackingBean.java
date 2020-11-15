/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bellerive
 */
@Named
@SessionScoped
public class EditBookBackingBean implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(EditBookBackingBean.class);
    
    private Book editingBook;
    
    @Inject
    private BookJpaController bookJpaController;
    
    private Date today = new Date();
    
    private String title;
    private String pages;
    private String genre;
    private String wholeSalePrice;
    private String listPrice;
    private String salePrice;
    private String synopsis;
    private Date publicationDate = new Date();
    
    public Book getEditingBook() {
        return editingBook;
    }
    
    public void setEditingBook(Book editingBook) {
        this.editingBook = editingBook;
    }
    
    public String getTitle() {
        return "";
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPages() {
        return "";
    }
    
    public void setPages(String pages) {
        this.pages = pages;
    }
    
    public String getGenre() {
        return "";
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getWholeSalePrice() {
        return "";
    }
    
    public void setWholeSalePrice(String wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }
    
    public String getListPrice() {
        return "";
    }
    
    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }
    
    public String getSalePrice() {
        return "";
    }
    
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }
    
    public String getSynopsis() {
        return "";
    }
    
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    
    public Date getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    public Date getToday() {
        return today;
    }
    
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * This method checks if the field that the manager could put information is
     * edited. Once it identifies what is edited, it sets that new value right
     * away, and at the end it will call the jpa controller to edit that entry.
     *
     * @throws IllegalOrphanException
     * @throws Exception
     * @author Alex Bellerive
     */
    public void editBook() throws IllegalOrphanException, Exception {
        if (!this.title.isEmpty() && !this.title.isBlank()) {
            LOG.info("Changing " + this.editingBook.getTitle() + " to " + this.title);
            this.editingBook.setTitle(this.title.trim());
        }
        if (!this.pages.isEmpty() && !this.pages.isBlank()) {
            LOG.info("Changing " + this.editingBook.getPages() + " to " + this.pages);
            this.editingBook.setPages(this.pages.trim());
        }
        if (!this.wholeSalePrice.isEmpty() && !this.wholeSalePrice.isBlank()) {
            LOG.info("Changing " + this.editingBook.getWholeSalePrice() + " to " + this.wholeSalePrice);
            this.editingBook.setWholeSalePrice(Double.parseDouble(this.wholeSalePrice));
        }
        if (!this.listPrice.isEmpty() && !this.listPrice.isBlank()) {
            LOG.info("Changing " + this.editingBook.getListPrice() + " to " + this.wholeSalePrice);
            this.editingBook.setListPrice(Double.parseDouble(this.listPrice));
        }
        if (!this.salePrice.isEmpty() && !this.salePrice.isBlank()) {
            LOG.info("Changing " + this.editingBook.getSalePrice() + " to " + this.salePrice);
            this.editingBook.setSalePrice(Double.parseDouble(this.salePrice));
        }
        if (!this.synopsis.isEmpty() && !this.synopsis.isBlank()) {
            LOG.info("Changing " + this.editingBook.getSynopsis() + " to " + this.synopsis);
            this.editingBook.setSynopsis(this.synopsis.trim());
        }
        if (!this.genre.equals("default")) {
            LOG.info("Changing " + this.editingBook.getGenre() + " to " + this.genre);
            this.editingBook.setGenre(this.genre.trim());
        }
        if (this.publicationDate.before(this.today)) {
            this.editingBook.setPubDate(this.publicationDate);
        }
        bookJpaController.edit(editingBook);
    }
    
}

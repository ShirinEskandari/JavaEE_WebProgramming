/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "booksale", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Booksale.findAll", query = "SELECT b FROM Booksale b"),
    @NamedQuery(name = "Booksale.findByBookSaleID", query = "SELECT b FROM Booksale b WHERE b.bookSaleID = :bookSaleID")})
public class Booksale implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BookSaleID")
    private Integer bookSaleID;
    @JoinColumn(name = "ISBN", referencedColumnName = "ISBN")
    @ManyToOne(optional = false)
    private Book isbn;
    @JoinColumn(name = "SaleID", referencedColumnName = "SaleID")
    @ManyToOne(optional = false)
    private Sale saleID;

    public Booksale() {
    }

    public Booksale(Integer bookSaleID) {
        this.bookSaleID = bookSaleID;
    }

    public Integer getBookSaleID() {
        return bookSaleID;
    }

    public void setBookSaleID(Integer bookSaleID) {
        this.bookSaleID = bookSaleID;
    }

    public Book getIsbn() {
        return isbn;
    }

    public void setIsbn(Book isbn) {
        this.isbn = isbn;
    }

    public Sale getSaleID() {
        return saleID;
    }

    public void setSaleID(Sale saleID) {
        this.saleID = saleID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookSaleID != null ? bookSaleID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booksale)) {
            return false;
        }
        Booksale other = (Booksale) object;
        if ((this.bookSaleID == null && other.bookSaleID != null) || (this.bookSaleID != null && !this.bookSaleID.equals(other.bookSaleID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Booksale[ bookSaleID=" + bookSaleID + " ]";
    }
    
}

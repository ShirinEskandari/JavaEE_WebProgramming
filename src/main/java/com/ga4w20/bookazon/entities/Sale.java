package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "sale", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Sale.findAll", query = "SELECT s FROM Sale s"),
    @NamedQuery(name = "Sale.findBySaleID", query = "SELECT s FROM Sale s WHERE s.saleID = :saleID"),
    @NamedQuery(name = "Sale.findByPrice", query = "SELECT s FROM Sale s WHERE s.price = :price")})
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SaleID")
    private Integer saleID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Price")
    private double price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SaleDate")
    private Date saleDate;
    @JoinColumn(name = "AccountID", referencedColumnName = "AccountID")
    @ManyToOne(optional = false)
    private Account accountID;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "saleID")
    private List<Booksale> booksaleList;

    public Sale() {
    }

    public Sale(Integer saleID) {
        this.saleID = saleID;
    }

    public Sale(Integer saleID, double price, Date date) {
        this.saleID = saleID;
        this.price = price;
        this.saleDate = date;
    }

    public Integer getSaleID() {
        return saleID;
    }

    public void setSaleID(Integer saleID) {
        this.saleID = saleID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public Date getSaleDate(){
        return this.saleDate;
    }
    
    public void setSaleDate(Date date){
        this.saleDate = date;
    }

    public Account getAccountID() {
        return accountID;
    }

    public void setAccountID(Account accountID) {
        this.accountID = accountID;
    }

    public List<Booksale> getBooksaleList() {
        return booksaleList;
    }

    public void setBooksaleList(List<Booksale> booksaleList) {
        this.booksaleList = booksaleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (saleID != null ? saleID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sale)) {
            return false;
        }
        Sale other = (Sale) object;
        if ((this.saleID == null && other.saleID != null) || (this.saleID != null && !this.saleID.equals(other.saleID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Sale[ saleID=" + saleID + " ]";
    }
    
}

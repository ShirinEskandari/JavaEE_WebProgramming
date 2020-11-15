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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "tax", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Tax.findAll", query = "SELECT t FROM Tax t"),
    @NamedQuery(name = "Tax.findByTaxID", query = "SELECT t FROM Tax t WHERE t.taxID = :taxID"),
    @NamedQuery(name = "Tax.findByProvince", query = "SELECT t FROM Tax t WHERE t.province = :province"),
    @NamedQuery(name = "Tax.findByGst", query = "SELECT t FROM Tax t WHERE t.gst = :gst"),
    @NamedQuery(name = "Tax.findByPst", query = "SELECT t FROM Tax t WHERE t.pst = :pst"),
    @NamedQuery(name = "Tax.findByHst", query = "SELECT t FROM Tax t WHERE t.hst = :hst")})
public class Tax implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "TaxID")
    private Integer taxID;
    @Size(max = 2)
    @Column(name = "Province")
    private String province;
    @Basic(optional = false)
    @NotNull
    @Column(name = "GST")
    private int gst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PST")
    private int pst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HST")
    private int hst;

    public Tax() {
    }

    public Tax(Integer taxID) {
        this.taxID = taxID;
    }

    public Tax(Integer taxID, int gst, int pst, int hst) {
        this.taxID = taxID;
        this.gst = gst;
        this.pst = pst;
        this.hst = hst;
    }

    public Integer getTaxID() {
        return taxID;
    }

    public void setTaxID(Integer taxID) {
        this.taxID = taxID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
    }

    public int getPst() {
        return pst;
    }

    public void setPst(int pst) {
        this.pst = pst;
    }

    public int getHst() {
        return hst;
    }

    public void setHst(int hst) {
        this.hst = hst;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taxID != null ? taxID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tax)) {
            return false;
        }
        Tax other = (Tax) object;
        if ((this.taxID == null && other.taxID != null) || (this.taxID != null && !this.taxID.equals(other.taxID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Tax[ taxID=" + taxID + " ]";
    }
    
}

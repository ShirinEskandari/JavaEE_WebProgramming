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
 * @author finle
 */
@Entity
@Table(name = "ads", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Ads.findAll", query = "SELECT a FROM Ads a"),
    @NamedQuery(name = "Ads.findByAdID", query = "SELECT a FROM Ads a WHERE a.adID = :adID"),
    @NamedQuery(name = "Ads.findByImage", query = "SELECT a FROM Ads a WHERE a.image = :image"),
    @NamedQuery(name = "Ads.findByRedirect", query = "SELECT a FROM Ads a WHERE a.redirect = :redirect"),
    @NamedQuery(name = "Ads.findByAdType", query = "SELECT a FROM Ads a WHERE a.adType = :adType"),
    @NamedQuery(name = "Ads.findByActiveStatus", query = "SELECT a FROM Ads a WHERE a.activeStatus = :activeStatus")})
public class Ads implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "AdID")
    private Integer adID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Image")
    private String image;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Redirect")
    private String redirect;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AdType")
    private int adType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ActiveStatus")
    private int activeStatus;

    public Ads() {
    }

    public Ads(Integer adID) {
        this.adID = adID;
    }

    public Ads(Integer adID, String image, String redirect, int adType, int activeStatus) {
        this.adID = adID;
        this.image = image;
        this.redirect = redirect;
        this.adType = adType;
        this.activeStatus = activeStatus;
    }

    public Integer getAdID() {
        return adID;
    }

    public void setAdID(Integer adID) {
        this.adID = adID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(int activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (adID != null ? adID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ads)) {
            return false;
        }
        Ads other = (Ads) object;
        if ((this.adID == null && other.adID != null) || (this.adID != null && !this.adID.equals(other.adID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mavenproject2.Ads[ adID=" + adID + " ]";
    }
    
}

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
 * @author Alex
 */
@Entity
@Table(name = "rss", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Rss.findAll", query = "SELECT r FROM Rss r"),
    @NamedQuery(name = "Rss.findByRssID", query = "SELECT r FROM Rss r WHERE r.rssID = :rssID"),
    @NamedQuery(name = "Rss.findByLink", query = "SELECT r FROM Rss r WHERE r.link = :link"),
    @NamedQuery(name = "Rss.findByActive", query = "SELECT r FROM Rss r WHERE r.active = :active")})
public class Rss implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RssID")
    private Integer rssID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Link")
    private String link;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Active")
    private int active;

    public Rss() {
    }

    public Rss(Integer rssID) {
        this.rssID = rssID;
    }

    public Rss(Integer rssID, String link, int active) {
        this.rssID = rssID;
        this.link = link;
        this.active = active;
    }

    public Integer getRssID() {
        return rssID;
    }

    public void setRssID(Integer rssID) {
        this.rssID = rssID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rssID != null ? rssID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rss)) {
            return false;
        }
        Rss other = (Rss) object;
        if ((this.rssID == null && other.rssID != null) || (this.rssID != null && !this.rssID.equals(other.rssID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.testing.whydoesthissuck.Rss[ rssID=" + rssID + " ]";
    }
    
}

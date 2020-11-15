
package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author jason
 */
@Entity
@Table(name = "Publisher", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Publisher.findAll", query = "SELECT p FROM Publisher p"),
    @NamedQuery(name = "Publisher.findByPublisherID", query = "SELECT p FROM Publisher p WHERE p.publisherID = :publisherID"),
    @NamedQuery(name = "Publisher.findByName", query = "SELECT p FROM Publisher p WHERE p.name = :name")})
public class Publisher implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PublisherID")
    private Integer publisherID;
    @Size(max = 200)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publisherID")
    private List<Book> bookList;

    public Publisher() {
    }

    public Publisher(Integer publisherID) {
        this.publisherID = publisherID;
    }

    public Integer getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(Integer publisherID) {
        this.publisherID = publisherID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (publisherID != null ? publisherID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Publisher)) {
            return false;
        }
        Publisher other = (Publisher) object;
        if ((this.publisherID == null && other.publisherID != null) || (this.publisherID != null && !this.publisherID.equals(other.publisherID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.Publisher[ publisherID=" + publisherID + " ]";
    }
    
}

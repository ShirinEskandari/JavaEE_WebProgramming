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
@Table(name = "bookauthor", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Bookauthor.findAll", query = "SELECT b FROM Bookauthor b"),
    @NamedQuery(name = "Bookauthor.findByBookAuthorID", query = "SELECT b FROM Bookauthor b WHERE b.bookAuthorID = :bookAuthorID"),
})
public class Bookauthor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BookAuthorID")
    private Integer bookAuthorID;
    @JoinColumn(name = "AuthorID", referencedColumnName = "AuthorID")
    @ManyToOne(optional = false)
    private Author authorID;
    @JoinColumn(name = "ISBN", referencedColumnName = "ISBN")
    @ManyToOne(optional = false)
    private Book isbn;

    public Bookauthor() {
    }

    public Bookauthor(Integer bookAuthorID) {
        this.bookAuthorID = bookAuthorID;
    }

    public Integer getBookAuthorID() {
        return bookAuthorID;
    }

    public void setBookAuthorID(Integer bookAuthorID) {
        this.bookAuthorID = bookAuthorID;
    }

    public Author getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Author authorID) {
        this.authorID = authorID;
    }

    public Book getIsbn() {
        return isbn;
    }

    public void setIsbn(Book isbn) {
        this.isbn = isbn;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookAuthorID != null ? bookAuthorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bookauthor)) {
            return false;
        }
        Bookauthor other = (Bookauthor) object;
        if ((this.bookAuthorID == null && other.bookAuthorID != null) || (this.bookAuthorID != null && !this.bookAuthorID.equals(other.bookAuthorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Bookauthor[ bookAuthorID=" + bookAuthorID + " ]";
    }
    
}

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "author", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a"),
    @NamedQuery(name = "Author.findByAuthorID", query = "SELECT a FROM Author a WHERE a.authorID = :authorID"),
    @NamedQuery(name = "Author.findByFName", query = "SELECT a FROM Author a WHERE a.fName = :fName"),
    @NamedQuery(name = "Author.findByLName", query = "SELECT a FROM Author a WHERE a.lName = :lName")})
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "AuthorID")
    private Integer authorID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "FName")
    private String fName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "LName")
    private String lName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorID")
    private List<Bookauthor> bookauthorList;

    public Author() {
    }

    public Author(Integer authorID) {
        this.authorID = authorID;
    }

    public Author(Integer authorID, String fName, String lName) {
        this.authorID = authorID;
        this.fName = fName;
        this.lName = lName;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public List<Bookauthor> getBookauthorList() {
        return bookauthorList;
    }

    public void setBookauthorList(List<Bookauthor> bookauthorList) {
        this.bookauthorList = bookauthorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authorID != null ? authorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        if ((this.authorID == null && other.authorID != null) || (this.authorID != null && !this.authorID.equals(other.authorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Author[ authorID=" + authorID + " ]";
    }
    
}

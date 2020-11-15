package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Grant
 * @author finley
 */
@Entity
@Table(name = "review", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Review.findAll", query = "SELECT r FROM Review r"),
    @NamedQuery(name = "Review.findByReviewID", query = "SELECT r FROM Review r WHERE r.reviewID = :reviewID"),
    @NamedQuery(name = "Review.findByReviewText", query = "SELECT r FROM Review r WHERE r.reviewText = :reviewText"),
    @NamedQuery(name = "Review.findByPostDate", query = "SELECT r FROM Review r WHERE r.postDate = :postDate"),
    @NamedQuery(name = "Review.findByRating", query = "SELECT r FROM Review r WHERE r.rating = :rating"),
    @NamedQuery(name = "Review.findByApproval", query = "SELECT r FROM Review r WHERE r.approval = :approval")})
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ReviewID")
    private Integer reviewID;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "ReviewText")
    private String reviewText;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PostDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "Rating")
    private int rating;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "Approval")
    private Character approval;
    
    @JoinColumn(name = "AccountID", referencedColumnName = "AccountID")
    @ManyToOne(optional = false)
    private Account accountID;
    
    @JoinColumn(name = "ISBN", referencedColumnName = "ISBN")
    @ManyToOne(optional = false)
    private Book isbn;

    public Review() {
    }

    public Review(Integer reviewID) {
        this.reviewID = reviewID;
    }

    public Review(Integer reviewID, String reviewText, Date postDate, int rating, Character approval) {
        this.reviewID = reviewID;
        this.reviewText = reviewText;
        this.postDate = postDate;
        this.rating = rating;
        this.approval = approval;
    }

    public Integer getReviewID() {
        return reviewID;
    }

    public void setReviewID(Integer reviewID) {
        this.reviewID = reviewID;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Character getApproval() {
        return approval;
    }

    public void setApproval(Character approval) {
        this.approval = approval;
    }

    public Account getAccountID() {
        return accountID;
    }

    public void setAccountID(Account accountID) {
        this.accountID = accountID;
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
        hash += (reviewID != null ? reviewID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Review)) {
            return false;
        }
        Review other = (Review) object;
        if ((this.reviewID == null && other.reviewID != null) || (this.reviewID != null && !this.reviewID.equals(other.reviewID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Review[ reviewID=" + reviewID + " ]";
    }
    
}

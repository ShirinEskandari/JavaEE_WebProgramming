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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jason
 */
@Entity
@Table(name = "Book", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Book.findAll", query = "SELECT b FROM Book b"),
    @NamedQuery(name = "Book.findByBookID", query = "SELECT b FROM Book b WHERE b.bookID = :bookID"),
    @NamedQuery(name = "Book.findByIsbn", query = "SELECT b FROM Book b WHERE b.isbn = :isbn"),
    @NamedQuery(name = "Book.findByTitle", query = "SELECT b FROM Book b WHERE b.title = :title"),
    @NamedQuery(name = "Book.findByPages", query = "SELECT b FROM Book b WHERE b.pages = :pages"),
    @NamedQuery(name = "Book.findByGenre", query = "SELECT b FROM Book b WHERE b.genre = :genre"),
    @NamedQuery(name = "Book.findBySynopsis", query = "SELECT b FROM Book b WHERE b.synopsis = :synopsis"),
    @NamedQuery(name = "Book.findByPubDate", query = "SELECT b FROM Book b WHERE b.pubDate = :pubDate"),
    @NamedQuery(name = "Book.findByWholeSalePrice", query = "SELECT b FROM Book b WHERE b.wholeSalePrice = :wholeSalePrice"),
    @NamedQuery(name = "Book.findByListPrice", query = "SELECT b FROM Book b WHERE b.listPrice = :listPrice"),
    @NamedQuery(name = "Book.findBySalePrice", query = "SELECT b FROM Book b WHERE b.salePrice = :salePrice"),
    @NamedQuery(name = "Book.findByDateEntered", query = "SELECT b FROM Book b WHERE b.dateEntered = :dateEntered"),
    @NamedQuery(name = "Book.findByEBook", query = "SELECT b FROM Book b WHERE b.eBook = :eBook"),
    @NamedQuery(name = "Book.findByRemovalStatus", query = "SELECT b FROM Book b WHERE b.removalStatus = :removalStatus"),
    @NamedQuery(name = "Book.findByImagePath", query = "SELECT b FROM Book b WHERE b.imagePath = :imagePath")})
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BookID")
    private Integer bookID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 14)
    @Column(name = "ISBN")
    private String isbn;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "Title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "Pages")
    private String pages;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "Genre")
    private String genre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4000)
    @Column(name = "Synopsis")
    private String synopsis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PubDate")
    @Temporal(TemporalType.DATE)
    private Date pubDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "WholeSalePrice")
    private double wholeSalePrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ListPrice")
    private double listPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SalePrice")
    private double salePrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DateEntered")
    @Temporal(TemporalType.DATE)
    private Date dateEntered;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EBook")
    private Character eBook;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RemovalStatus")
    private Character removalStatus;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ImagePath")
    private String imagePath;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "isbn")
    private List<Booksale> booksaleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "isbn")
    private List<Review> reviewList;
    @JoinColumn(name = "PublisherID", referencedColumnName = "PublisherID")
    @ManyToOne(optional = false)
    private Publisher publisherID;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "isbn")
    private List<Bookauthor> bookauthorList;

    public Book() {
    }

    public Book(Integer bookID) {
        this.bookID = bookID;
    }

    public Book(Integer bookID, String isbn, String title, String pages, String genre, String synopsis, Date pubDate, double wholeSalePrice, double listPrice, double salePrice, Date dateEntered, Character eBook, Character removalStatus, String imagePath, Publisher publisher) {
        this.bookID = bookID;
        this.isbn = isbn;
        this.title = title;
        this.pages = pages;
        this.genre = genre;
        this.synopsis = synopsis;
        this.pubDate = pubDate;
        this.wholeSalePrice = wholeSalePrice;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.dateEntered = dateEntered;
        this.eBook = eBook;
        this.removalStatus = removalStatus;
        this.imagePath = imagePath;
        this.publisherID = publisher;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public double getWholeSalePrice() {
        return wholeSalePrice;
    }

    public void setWholeSalePrice(double wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public Character getEBook() {
        return eBook;
    }

    public void setEBook(Character eBook) {
        this.eBook = eBook;
    }

    public Character getRemovalStatus() {
        return removalStatus;
    }

    public void setRemovalStatus(Character removalStatus) {
        this.removalStatus = removalStatus;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Booksale> getBookSaleList() {
        return booksaleList;
    }

    public void setBookSaleList(List<Booksale> bookSaleList) {
        this.booksaleList = bookSaleList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public Publisher getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(Publisher publisherID) {
        this.publisherID = publisherID;
    }

    public List<Bookauthor> getBookAuthorList() {
        return bookauthorList;
    }

    public void setBookAuthorList(List<Bookauthor> bookAuthorList) {
        this.bookauthorList = bookAuthorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookID != null ? bookID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Book)) {
            return false;
        }
        Book other = (Book) object;
        if ((this.bookID == null && other.bookID != null) || (this.bookID != null && !this.bookID.equals(other.bookID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.Book[ bookID=" + bookID + " ]";
    }
    
}

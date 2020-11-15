package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Author;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Publisher;
import com.ga4w20.bookazon.persistence.AuthorJpaController;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.BookauthorJpaController;
import com.ga4w20.bookazon.persistence.PublisherJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bellerive
 */
@Named("managerBookBackingBean")
@SessionScoped
public class ManagerEditBookBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerEditBookBackingBean.class);

    @Inject
    private BookJpaController bookJpaController;

    @Inject
    private PublisherJpaController publisherJpaController;

    @Inject
    private AuthorJpaController authorJpaController;

    @Inject
    private BookauthorJpaController bookauthorJpaController;

    private String uploadStatus;
    private String isbn;
    private String title;
    private String pages;
    private String genre;
    private double wholeSalePrice = 0.0;
    private double listPrice = 0.0;
    private double salePrice = 0.0;
    private String synopsis;
    private Date publicationDate;
    private String publisher;
    private String firstName;
    private String lastName;
    private Date today = new Date();

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

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * This method creates a book by finding or creating the publisher and/or
     * the same for author. Then it uses that information to create a book.
     *
     * @author Alex Bellerive
     */
    public void createBook() {
        //Create Publisher if it doesnt already exist
        Publisher pub = this.publisherJpaController.findPublisherByName(this.publisher.trim());

        //Create Author if it doesnt already exist
        Author author = this.authorJpaController.findAuthorByName(this.firstName.trim(), this.lastName.trim());

        //Get next BookId to use for book 
        List<Book> allBooks = this.bookJpaController.findBookEntities();
        Integer largestBookId = allBooks.get(allBooks.size() - 1).getBookID() + 1;

        //Create book using pub and largestBookId
        Book newBook = new Book(largestBookId, this.isbn, this.title, this.pages, this.genre, this.synopsis, this.publicationDate, this.wholeSalePrice, this.listPrice, this.salePrice, new Date(), '1', '0', "comingsoon.jpg", pub);
        bookJpaController.create(newBook);

        //Creating book Author instance
        bookauthorJpaController.create(author, newBook);
        setUploadStatus("The Book \"" + this.title + "\" has been added to Bookazon!");
    }

    /**
     * This method changes the status of the book depending on the book and
     * status provided.
     *
     * @param book
     * @param status
     * @throws IllegalOrphanException
     * @throws Exception
     * @author Alex Bellerive
     */
    public void toggleBook(Book book, char status) throws IllegalOrphanException, Exception {
        Book updatedBook = bookJpaController.toggleBook(book.getIsbn(), status);
        bookJpaController.edit(updatedBook);
    }

}

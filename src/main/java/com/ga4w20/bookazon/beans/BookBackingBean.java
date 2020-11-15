package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.persistence.AuthorJpaController;
import com.ga4w20.bookazon.persistence.BookJpaController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Backing Bean for Books that initializes some list of books for the front page (index.xhtml)
 * @author Grant
 */
@Named
@SessionScoped
public class BookBackingBean implements Serializable {

    @Inject
    private BookJpaController bjc;
    @Inject
    private AuthorJpaController ajc;
    private List<Book> listRecentBooks;
    private List<Book> listBooksOnSale;
    private boolean searched = false;

    @PostConstruct
    public void init() {
        this.listRecentBooks = this.bjc.getBooksRecentlyAdded();
        this.listBooksOnSale = this.bjc.getBooksOnSale();
    }

    public List<Book> getListRecentBooks() {
        return listRecentBooks;
    }

    public void setListRecentBooks(List<Book> listRecentBooks) {
        this.listRecentBooks = listRecentBooks;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public List<Book> getTopSellers(String genre) {
        return this.bjc.getBooksTopSellers(genre);
    }

    public List<Book> getBooksByAuthor(String isbn, int bookID) {
        String authorName = ajc.getAuthorName(this.bjc.getAuthorIDFromBook(isbn));
        return this.bjc.getBooksFromSameAuthor(authorName, bookID);
    }

    public List<Book> getThreeBooksByGenre(String genre, int bookID) {
        return this.bjc.getThreeBooksByGenre(genre, bookID);
    }

    public List<Book> getBooksByGenre(String genre) {
        return this.bjc.getBooksByGenre(genre);
    }

    public List<Book> getRecentBooks() {
        return this.listRecentBooks;
    }

    public List<Book> getListBooksOnSale() {
        return listBooksOnSale;
    }

    public void setListBooksOnSale(List<Book> listBooksOnSale) {
        this.listBooksOnSale = listBooksOnSale;
    }

}

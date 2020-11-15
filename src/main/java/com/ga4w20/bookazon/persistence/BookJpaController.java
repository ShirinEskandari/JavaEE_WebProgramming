package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.*;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate book data.
 * @author Grant
 */
@Named
@SessionScoped
public class BookJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BookJpaController.class);

    @Resource
    private UserTransaction utx;

    //Container inserts references to the entitymanager (injection)
    @PersistenceContext
    private EntityManager em;
    
    private final DecimalFormat df = new DecimalFormat("#.##");

    public void create(Book book) {
        if (book.getBookSaleList() == null) {
            book.setBookSaleList(new ArrayList<Booksale>());
        }
        if (book.getReviewList() == null) {
            book.setReviewList(new ArrayList<Review>());
        }
        if (book.getBookAuthorList() == null) {
            book.setBookAuthorList(new ArrayList<Bookauthor>());
        }

        try {
            utx.begin();
            Publisher publisherID = book.getPublisherID();
            if (publisherID != null) {
                publisherID = em.getReference(publisherID.getClass(), publisherID.getPublisherID());
                book.setPublisherID(publisherID);
            }
            List<Booksale> attachedBookSaleList = new ArrayList<Booksale>();
            for (Booksale bookSaleListBookSaleToAttach : book.getBookSaleList()) {
                bookSaleListBookSaleToAttach = em.getReference(bookSaleListBookSaleToAttach.getClass(), bookSaleListBookSaleToAttach.getBookSaleID());
                attachedBookSaleList.add(bookSaleListBookSaleToAttach);
            }
            book.setBookSaleList(attachedBookSaleList);
            List<Review> attachedReviewList = new ArrayList<Review>();
            for (Review reviewListReviewToAttach : book.getReviewList()) {
                reviewListReviewToAttach = em.getReference(reviewListReviewToAttach.getClass(), reviewListReviewToAttach.getReviewID());
                attachedReviewList.add(reviewListReviewToAttach);
            }
            book.setReviewList(attachedReviewList);
            List<Bookauthor> attachedBookAuthorList = new ArrayList<Bookauthor>();
            for (Bookauthor bookAuthorListBookAuthorToAttach : book.getBookAuthorList()) {
                bookAuthorListBookAuthorToAttach = em.getReference(bookAuthorListBookAuthorToAttach.getClass(), bookAuthorListBookAuthorToAttach.getBookAuthorID());
                attachedBookAuthorList.add(bookAuthorListBookAuthorToAttach);
            }
            book.setBookAuthorList(attachedBookAuthorList);
            em.persist(book);
            if (publisherID != null) {
                publisherID.getBookList().add(book);
                publisherID = em.merge(publisherID);
            }
            for (Booksale bookSaleListBookSale : book.getBookSaleList()) {
                Book oldIsbnOfBookSaleListBookSale = bookSaleListBookSale.getIsbn();
                bookSaleListBookSale.setIsbn(book);
                bookSaleListBookSale = em.merge(bookSaleListBookSale);
                if (oldIsbnOfBookSaleListBookSale != null) {
                    oldIsbnOfBookSaleListBookSale.getBookSaleList().remove(bookSaleListBookSale);
                    oldIsbnOfBookSaleListBookSale = em.merge(oldIsbnOfBookSaleListBookSale);
                }
            }
            for (Review reviewListReview : book.getReviewList()) {
                Book oldIsbnOfReviewListReview = reviewListReview.getIsbn();
                reviewListReview.setIsbn(book);
                reviewListReview = em.merge(reviewListReview);
                if (oldIsbnOfReviewListReview != null) {
                    oldIsbnOfReviewListReview.getReviewList().remove(reviewListReview);
                    oldIsbnOfReviewListReview = em.merge(oldIsbnOfReviewListReview);
                }
            }
            for (Bookauthor bookAuthorListBookAuthor : book.getBookAuthorList()) {
                Book oldIsbnOfBookAuthorListBookAuthor = bookAuthorListBookAuthor.getIsbn();
                bookAuthorListBookAuthor.setIsbn(book);
                bookAuthorListBookAuthor = em.merge(bookAuthorListBookAuthor);
                if (oldIsbnOfBookAuthorListBookAuthor != null) {
                    oldIsbnOfBookAuthorListBookAuthor.getBookAuthorList().remove(bookAuthorListBookAuthor);
                    oldIsbnOfBookAuthorListBookAuthor = em.merge(oldIsbnOfBookAuthorListBookAuthor);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Book book) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            utx.begin();
            Book persistentBook = em.find(Book.class, book.getBookID());
            Publisher publisherIDOld = persistentBook.getPublisherID();
            Publisher publisherIDNew = book.getPublisherID();
            List<Booksale> bookSaleListOld = persistentBook.getBookSaleList();
            List<Booksale> bookSaleListNew = book.getBookSaleList();
            List<Review> reviewListOld = persistentBook.getReviewList();
            List<Review> reviewListNew = book.getReviewList();
            List<Bookauthor> bookAuthorListOld = persistentBook.getBookAuthorList();
            List<Bookauthor> bookAuthorListNew = book.getBookAuthorList();
            List<String> illegalOrphanMessages = null;
            for (Booksale bookSaleListOldBookSale : bookSaleListOld) {
                if (!bookSaleListNew.contains(bookSaleListOldBookSale)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BookSale " + bookSaleListOldBookSale + " since its isbn field is not nullable.");
                }
            }
            for (Review reviewListOldReview : reviewListOld) {
                if (!reviewListNew.contains(reviewListOldReview)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Review " + reviewListOldReview + " since its isbn field is not nullable.");
                }
            }
            for (Bookauthor bookAuthorListOldBookAuthor : bookAuthorListOld) {
                if (!bookAuthorListNew.contains(bookAuthorListOldBookAuthor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BookAuthor " + bookAuthorListOldBookAuthor + " since its isbn field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (publisherIDNew != null) {
                publisherIDNew = em.getReference(publisherIDNew.getClass(), publisherIDNew.getPublisherID());
                book.setPublisherID(publisherIDNew);
            }
            List<Booksale> attachedBookSaleListNew = new ArrayList<Booksale>();
            for (Booksale bookSaleListNewBookSaleToAttach : bookSaleListNew) {
                bookSaleListNewBookSaleToAttach = em.getReference(bookSaleListNewBookSaleToAttach.getClass(), bookSaleListNewBookSaleToAttach.getBookSaleID());
                attachedBookSaleListNew.add(bookSaleListNewBookSaleToAttach);
            }
            bookSaleListNew = attachedBookSaleListNew;
            book.setBookSaleList(bookSaleListNew);
            List<Review> attachedReviewListNew = new ArrayList<Review>();
            for (Review reviewListNewReviewToAttach : reviewListNew) {
                reviewListNewReviewToAttach = em.getReference(reviewListNewReviewToAttach.getClass(), reviewListNewReviewToAttach.getReviewID());
                attachedReviewListNew.add(reviewListNewReviewToAttach);
            }
            reviewListNew = attachedReviewListNew;
            book.setReviewList(reviewListNew);
            List<Bookauthor> attachedBookAuthorListNew = new ArrayList<Bookauthor>();
            for (Bookauthor bookAuthorListNewBookAuthorToAttach : bookAuthorListNew) {
                bookAuthorListNewBookAuthorToAttach = em.getReference(bookAuthorListNewBookAuthorToAttach.getClass(), bookAuthorListNewBookAuthorToAttach.getBookAuthorID());
                attachedBookAuthorListNew.add(bookAuthorListNewBookAuthorToAttach);
            }
            bookAuthorListNew = attachedBookAuthorListNew;
            book.setBookAuthorList(bookAuthorListNew);
            book = em.merge(book);
            if (publisherIDOld != null && !publisherIDOld.equals(publisherIDNew)) {
                publisherIDOld.getBookList().remove(book);
                publisherIDOld = em.merge(publisherIDOld);
            }
            if (publisherIDNew != null && !publisherIDNew.equals(publisherIDOld)) {
                publisherIDNew.getBookList().add(book);
                publisherIDNew = em.merge(publisherIDNew);
            }
            for (Booksale bookSaleListNewBookSale : bookSaleListNew) {
                if (!bookSaleListOld.contains(bookSaleListNewBookSale)) {
                    Book oldIsbnOfBookSaleListNewBookSale = bookSaleListNewBookSale.getIsbn();
                    bookSaleListNewBookSale.setIsbn(book);
                    bookSaleListNewBookSale = em.merge(bookSaleListNewBookSale);
                    if (oldIsbnOfBookSaleListNewBookSale != null && !oldIsbnOfBookSaleListNewBookSale.equals(book)) {
                        oldIsbnOfBookSaleListNewBookSale.getBookSaleList().remove(bookSaleListNewBookSale);
                        oldIsbnOfBookSaleListNewBookSale = em.merge(oldIsbnOfBookSaleListNewBookSale);
                    }
                }
            }
            for (Review reviewListNewReview : reviewListNew) {
                if (!reviewListOld.contains(reviewListNewReview)) {
                    Book oldIsbnOfReviewListNewReview = reviewListNewReview.getIsbn();
                    reviewListNewReview.setIsbn(book);
                    reviewListNewReview = em.merge(reviewListNewReview);
                    if (oldIsbnOfReviewListNewReview != null && !oldIsbnOfReviewListNewReview.equals(book)) {
                        oldIsbnOfReviewListNewReview.getReviewList().remove(reviewListNewReview);
                        oldIsbnOfReviewListNewReview = em.merge(oldIsbnOfReviewListNewReview);
                    }
                }
            }
            for (Bookauthor bookAuthorListNewBookAuthor : bookAuthorListNew) {
                if (!bookAuthorListOld.contains(bookAuthorListNewBookAuthor)) {
                    Book oldIsbnOfBookAuthorListNewBookAuthor = bookAuthorListNewBookAuthor.getIsbn();
                    bookAuthorListNewBookAuthor.setIsbn(book);
                    bookAuthorListNewBookAuthor = em.merge(bookAuthorListNewBookAuthor);
                    if (oldIsbnOfBookAuthorListNewBookAuthor != null && !oldIsbnOfBookAuthorListNewBookAuthor.equals(book)) {
                        oldIsbnOfBookAuthorListNewBookAuthor.getBookAuthorList().remove(bookAuthorListNewBookAuthor);
                        oldIsbnOfBookAuthorListNewBookAuthor = em.merge(oldIsbnOfBookAuthorListNewBookAuthor);
                    }
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        try {
            utx.begin();
            Book book;
            try {
                book = em.getReference(Book.class, id);
                book.getBookID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The book with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Booksale> bookSaleListOrphanCheck = book.getBookSaleList();
            for (Booksale bookSaleListOrphanCheckBookSale : bookSaleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Book (" + book + ") cannot be destroyed since the BookSale " + bookSaleListOrphanCheckBookSale + " in its bookSaleList field has a non-nullable isbn field.");
            }
            List<Review> reviewListOrphanCheck = book.getReviewList();
            for (Review reviewListOrphanCheckReview : reviewListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Book (" + book + ") cannot be destroyed since the Review " + reviewListOrphanCheckReview + " in its reviewList field has a non-nullable isbn field.");
            }
            List<Bookauthor> bookAuthorListOrphanCheck = book.getBookAuthorList();
            for (Bookauthor bookAuthorListOrphanCheckBookAuthor : bookAuthorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Book (" + book + ") cannot be destroyed since the BookAuthor " + bookAuthorListOrphanCheckBookAuthor + " in its bookAuthorList field has a non-nullable isbn field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Publisher publisherID = book.getPublisherID();
            if (publisherID != null) {
                publisherID.getBookList().remove(book);
                publisherID = em.merge(publisherID);
            }
            em.remove(book);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Book> findBookEntities() {
        return findBookEntities(true, -1, -1);
    }

    public List<Book> findBookEntities(int maxResults, int firstResult) {
        return findBookEntities(false, maxResults, firstResult);
    }

    private List<Book> findBookEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Book.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Book findBook(Integer id) {
        return em.find(Book.class, id);
    }

    public int getBookCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Book> rt = cq.from(Book.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Method used to get the 5 top sellers of a specific genre
     * @author Grant 
     * @param genre
     * @return
     */
    public List<Book> getBooksTopSellers(String genre) {
        final int LIMIT = 5;

        TypedQuery<Book> topSellers = em.createQuery("SELECT b FROM Book b INNER JOIN b.booksaleList s where b.genre = :genre GROUP BY s.isbn ORDER BY count(s.isbn) DESC", Book.class).setMaxResults(LIMIT);
        topSellers.setParameter("genre", genre);
        List<Book> topSellerBooks = topSellers.getResultList();
        return topSellerBooks;
    }

    /**
     * Method used to get the recently added books in the system (database)
     * 
     * @author Grant 
     * @return
     */
    public List<Book> getBooksRecentlyAdded() {
        final int LIMIT = 3;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        cq.orderBy(cb.desc(book.get("dateEntered")));
        cq.select(book);

        TypedQuery<Book> recentBooks = em.createQuery(cq);
        recentBooks.setMaxResults(LIMIT);

        List<Book> booksRecentlyAdded = recentBooks.getResultList();
        return booksRecentlyAdded;
    }

    /**
     * Method that truncates the synopsis of a Book and appends "View Book To
     * Read More!"
     * 
     * @author Grant 
     * @param synopsis
     * @return
     */
    public String displayReadMore(String synopsis) {
        if (synopsis.length() < 100) {
            return synopsis;
        }
        return synopsis.substring(0, 100).concat(" [View Book To Read More!]");
    }

    /**
     * Method used to get books from the database using a filter (author, isbn,
     * publisher or title) and a filterValue (user input for querying)
     * 
     * @author Grant 
     * @param filter
     * @param filterValue
     * @return
     */
    public List<Book> searchBarBooks(String filter, String filterValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        switch (filter) {
            case "isbn":
                cq.where(cb.like(book.get("isbn"), "%" + filterValue + "%"));
                break;
            case "title":
                cq.where(cb.like(book.get("title"), "%" + filterValue + "%"));
                break;
            case "author":
                Join bookauthor = book.join("bookauthorList");
                Join author = bookauthor.join("authorID");     
                Expression<String> fullName = cb.concat(cb.concat(author.get("fName"), " "), author.get("lName"));
                cq.where(cb.like(fullName, "%" + filterValue + "%"));
                break;
            case "publisher":
                cq.where(cb.like(book.get("publisherID").get("name"), "%" + filterValue + "%"));
                break;
            default:
                break;
        }
        
        cq.select(book);

        TypedQuery<Book> searchQuery = em.createQuery(cq);
        List<Book> searchedBooks = searchQuery.getResultList();
        return searchedBooks;
    }

    /**
     * Method used to get the author id given a certain book ISBN. Used to get
     * the author name in the AuthorJpaController
     *
     * @author Grant 
     * @param isbn
     * @return
     */
    public int getAuthorIDFromBook(String isbn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        Join bookauthor = book.join("bookauthorList");
        Join author = bookauthor.join("authorID");
        cq.where(cb.equal(bookauthor.get("isbn").get("isbn"), isbn));
        cq.select(author.get("authorID"));

        //Gets an author linked to the ISBN
        Query authorID = em.createQuery(cq);
        return ((int) authorID.getSingleResult());
    }

    /**
     * Method used to get all the books according to a specified genre
     *
     * @author Grant 
     * @param genre
     * @return
     */
    public List<Book> getBooksByGenre(String genre) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        cq.where(cb.equal(book.get("genre"), genre));
        cq.select(book);

        //List of books by a selected genre
        Query genreBooks = em.createQuery(cq);
        return genreBooks.getResultList();
    }

    /**
     * Used in the display of 3 books for the modal view, but ensures they're
     * not the same one that's on the modal
     *
     * @author Grant 
     * @param genre
     * @param bookID
     * @return
     */
    public List<Book> getThreeBooksByGenre(String genre, int bookID) {
        final int LIMIT = 3;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        cq.where(
                cb.equal(book.get("genre"), genre),
                cb.notEqual(book.get("bookID"), bookID)
        );
        cq.select(book);

        Query booksByGenre = em.createQuery(cq);
        booksByGenre.setMaxResults(LIMIT);
        return booksByGenre.getResultList();
    }

    /**
     * Method used to get three books from the same author of the book that the
     * user currently clicked on
     *
     * @author Grant 
     * @param fullName
     * @param bookID
     * @return
     */
    public List<Book> getBooksFromSameAuthor(String fullName, int bookID) {
        final int LIMIT = 3;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        Join bookauthor = book.join("bookauthorList");
        Join author = bookauthor.join("authorID");
        Expression<String> concatFullName = cb.concat(cb.concat(author.get("fName"), " "), author.get("lName"));
        cq.where(
                cb.like(concatFullName, fullName),
                cb.notEqual(book.get("bookID"), bookID)
        );
        cq.select(book);

        Query booksByAuthor = em.createQuery(cq);
        booksByAuthor.setMaxResults(LIMIT);

        return booksByAuthor.getResultList();
    }

    /**
     * Used to display three books of the same genre as the one stored in the
     * cookie from the previous click.
     *
     * @author Grant 
     * @param genre
     * @return
     */
    public List<Book> getThreeBooksByGenre(String genre) {
        final int LIMIT = 3;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        cq.where(cb.equal(book.get("genre"), genre));
        cq.select(book);

        Query booksByGenre = em.createQuery(cq);
        booksByGenre.setMaxResults(LIMIT);
        return booksByGenre.getResultList();
    }

    /**
     * Method that gets the books that a user bought
     * 
     * @author Grant 
     * @param accountID
     * @return 
     */
    public List<Book> getBoughtBooks(int accountID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        Join booksale = book.join("booksaleList");
        cq.select(book);
        cq.where(
                cb.equal(booksale.get("saleID").get("accountID").get("accountID"), accountID)
        );

        TypedQuery<Book> userBoughtBooks = em.createQuery(cq);
        return userBoughtBooks.getResultList();
    }
    
    
    /**
     * This method return all books with the specified status.
     * This is used to get all disactivated books or all 
     * activated books
     * @param status
     * @return list of books
     * @author Alex Bellerive
     */
    public List<Book> findAllBooks(char status){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        
        Root<Book> books = cq.from(Book.class);
        
        cq.where(cb.equal(books.get("removalStatus"), status));
        
        TypedQuery<Book> query = em.createQuery(cq);
        cq.select(books);
        List<Book> booksNotRemoved = query.getResultList();
        
        return booksNotRemoved;
    }
    /**
     * This method takes input the ISBN and the status the book will be turned into.
     * 1 means disactivated, 0 means Activated
     * @param isbn
     * @param status
     * @author Alex Bellerive
     * @return Book
     * @throws IllegalOrphanException 
     */
    public Book toggleBook(String isbn, char status) throws IllegalOrphanException{
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        
        Root<Book> books = cq.from(Book.class);
        
        cq.where(cb.equal(books.get("isbn"), isbn));
        
        TypedQuery<Book> query = em.createQuery(cq);
        cq.select(books);
        Book bookFromDb = query.getSingleResult();
        LOG.info("THE STATUS IS "+ status);
        bookFromDb.setRemovalStatus(status);
        
        return bookFromDb;
    }

    /**
     * Method that gets books on sale
     * 
     * @author Grant 
     * @return 
     */
    public List<Book> getBooksOnSale(){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        cq.where(
                cb.greaterThan(book.get("salePrice"), 0)
        );
        cq.select(book);
        
        TypedQuery<Book> booksOnSale = em.createQuery(cq);
        
        return booksOnSale.getResultList();
    }
    
    /**
     * Method that formats the price of a book
     * 
     * @author Grant 
     * @param price
     * @return 
     */
    public String priceFormat(double price){
        this.df.setRoundingMode(RoundingMode.CEILING);
        this.df.setMinimumFractionDigits(2);
        
        return this.df.format(price);
    }
    
    /**
     * Method that Find the book by isbn 
     * 
     * @author Lin Yang 
     * @param isbn
     * @return Book
     */
    
    public Book findBookByIsbn(String isbn){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        cq.where(
                cb.equal(book.get("isbn"), isbn)
        );
        cq.select(book);
        TypedQuery<Book> bookByisbn = em.createQuery(cq);
        
        return bookByisbn.getSingleResult();
    }
}

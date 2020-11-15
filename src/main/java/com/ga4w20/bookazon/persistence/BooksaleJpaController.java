package com.ga4w20.bookazon.persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Booksale;
import com.ga4w20.bookazon.entities.Sale;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate booksale data
 * @author Grant
 */
@Named
@SessionScoped
public class BooksaleJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BooksaleJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BookJpaController bookJpaController;

    public void create(Booksale booksale) {
        try {
            utx.begin();
            Book isbn = booksale.getIsbn();
            if (isbn != null) {
                isbn = em.getReference(isbn.getClass(), isbn.getBookID());
                booksale.setIsbn(isbn);
            }
            Sale saleID = booksale.getSaleID();
            if (saleID != null) {
                saleID = em.getReference(saleID.getClass(), saleID.getSaleID());
                booksale.setSaleID(saleID);
            }
            em.persist(booksale);
            if (isbn != null) {
                isbn.getBookSaleList().add(booksale);
                isbn = em.merge(isbn);
            }
            if (saleID != null) {
                saleID.getBooksaleList().add(booksale);
                saleID = em.merge(saleID);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BooksaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Booksale booksale) {
        try {
            utx.begin();
            Booksale persistentBooksale = em.find(Booksale.class, booksale.getBookSaleID());
            Book isbnOld = persistentBooksale.getIsbn();
            Book isbnNew = booksale.getIsbn();
            Sale saleIDOld = persistentBooksale.getSaleID();
            Sale saleIDNew = booksale.getSaleID();
            if (isbnNew != null) {
                isbnNew = em.getReference(isbnNew.getClass(), isbnNew.getBookID());
                booksale.setIsbn(isbnNew);
            }
            if (saleIDNew != null) {
                saleIDNew = em.getReference(saleIDNew.getClass(), saleIDNew.getSaleID());
                booksale.setSaleID(saleIDNew);
            }
            booksale = em.merge(booksale);
            if (isbnOld != null && !isbnOld.equals(isbnNew)) {
                isbnOld.getBookSaleList().remove(booksale);
                isbnOld = em.merge(isbnOld);
            }
            if (isbnNew != null && !isbnNew.equals(isbnOld)) {
                isbnNew.getBookSaleList().add(booksale);
                isbnNew = em.merge(isbnNew);
            }
            if (saleIDOld != null && !saleIDOld.equals(saleIDNew)) {
                saleIDOld.getBooksaleList().remove(booksale);
                saleIDOld = em.merge(saleIDOld);
            }
            if (saleIDNew != null && !saleIDNew.equals(saleIDOld)) {
                saleIDNew.getBooksaleList().add(booksale);
                saleIDNew = em.merge(saleIDNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BooksaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Booksale booksale;
            try {
                booksale = em.getReference(Booksale.class, id);
                booksale.getBookSaleID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The booksale with id " + id + " no longer exists.", enfe);
            }
            Book isbn = booksale.getIsbn();
            if (isbn != null) {
                isbn.getBookSaleList().remove(booksale);
                isbn = em.merge(isbn);
            }
            Sale saleID = booksale.getSaleID();
            if (saleID != null) {
                saleID.getBooksaleList().remove(booksale);
                saleID = em.merge(saleID);
            }
            em.remove(booksale);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BooksaleJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Booksale> findBooksaleEntities() {
        return findBooksaleEntities(true, -1, -1);
    }

    public List<Booksale> findBooksaleEntities(int maxResults, int firstResult) {
        return findBooksaleEntities(false, maxResults, firstResult);
    }

    private List<Booksale> findBooksaleEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Booksale.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Booksale findBooksale(Integer id) {
        return em.find(Booksale.class, id);

    }

    public int getBooksaleCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Booksale> rt = cq.from(Booksale.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * This method returns only books that have zero sales
     *
     * @param start
     * @param end
     * @return List of books that have never been bought
     * @author Alex Bellerive & Grant
     */
    public List<Book> zeroSales(Date start, Date end) {
        //Gets all books sold given a date range
        List<Booksale> bs = this.booksSold(start, end);
        
        //Gets all books in the database
        List<Book> zeroSaleBooks = this.bookJpaController.findBookEntities();
        if(!bs.isEmpty()){
            //If books were sold during the given range, that means those HAVE sales, but we want zero sales, meaning all the other books.
            for(int i=0; i<bs.size(); i++){
                zeroSaleBooks.remove(bs.get(i).getIsbn());
            }
        }
        return zeroSaleBooks;
    }

    public List<Booksale> booksSold(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Booksale.class);

        Root<Booksale> bs = cq.from(Booksale.class);
        if (start != null && end != null) {
            cq.where(
                    cb.between(bs.get("saleID").<Date>get("saleDate"), start, end)
            );
        }
        cq.distinct(true);

        cq.select(bs);

        TypedQuery<Booksale> booksSold = em.createQuery(cq);

        return booksSold.getResultList();
    }

    /**
     * This method takes input a books isbn and and selects all the books that
     * are equal and returns the size of the list to find all the books sold
     * with that specific isbn.
     *
     * @param isbn
     * @return
     * @author Alex Bellerive
     */
    public int getBookSales(String isbn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Booksale.class);

        Root<Booksale> bookSale = cq.from(Booksale.class);

        cq.where(cb.equal(bookSale.get("isbn").get("isbn"), isbn));
        cq.select(bookSale);

        TypedQuery<Booksale> bookSales = em.createQuery(cq);

        return bookSales.getResultList().size();
    }

    /**
     * Method that checks the books sold linked to a publisher
     * @param start
     * @param end
     * @return 
     */
    public List<Booksale> booksSoldPerPublisher(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Booksale.class);

        Root<Booksale> bs = cq.from(Booksale.class);
        if (start != null && end != null) {
            cq.where(
                    cb.between(bs.get("saleID").<Date>get("saleDate"), start, end)
            );
        }
        cq.distinct(true);
        cq.select(bs);

        TypedQuery<Booksale> booksSoldPerPublisher = em.createQuery(cq);

        return booksSoldPerPublisher.getResultList();
    }

    /**
     * This method counts the amount of books sold with that specified publisher
     * name.
     *
     * @param publisherName
     * @return int
     * @author Alex Bellerive
     */
    public int getPublisherCount(String publisherName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Booksale.class);

        Root<Booksale> bookSale = cq.from(Booksale.class);

        cq.where(cb.equal(bookSale.get("isbn").get("publisherID").get("name"), publisherName));
        cq.select(bookSale);

        TypedQuery<Booksale> listBookSales = em.createQuery(cq);

        return listBookSales.getResultList().size();
    }
}

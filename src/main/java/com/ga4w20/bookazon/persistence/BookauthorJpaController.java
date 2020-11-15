package com.ga4w20.bookazon.persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Author;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Bookauthor;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
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
import javax.persistence.criteria.Join;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate book author data
 * @author Grant
 */
@Named
@SessionScoped
public class BookauthorJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BookauthorJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Bookauthor bookauthor) {
        try {
            utx.begin();
            Author authorID = bookauthor.getAuthorID();
            if (authorID != null) {
                authorID = em.getReference(authorID.getClass(), authorID.getAuthorID());
                bookauthor.setAuthorID(authorID);
            }
            Book isbn = bookauthor.getIsbn();
            if (isbn != null) {
                isbn = em.getReference(isbn.getClass(), isbn.getBookID());
                bookauthor.setIsbn(isbn);
            }
            em.persist(bookauthor);
            if (authorID != null) {
                authorID.getBookauthorList().add(bookauthor);
                authorID = em.merge(authorID);
            }
            if (isbn != null) {
                isbn.getBookAuthorList().add(bookauthor);
                isbn = em.merge(isbn);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BookauthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Bookauthor bookauthor) {
        try {
            utx.begin();
            Bookauthor persistentBookauthor = em.find(Bookauthor.class, bookauthor.getBookAuthorID());
            Author authorIDOld = persistentBookauthor.getAuthorID();
            Author authorIDNew = bookauthor.getAuthorID();
            Book isbnOld = persistentBookauthor.getIsbn();
            Book isbnNew = bookauthor.getIsbn();
            if (authorIDNew != null) {
                authorIDNew = em.getReference(authorIDNew.getClass(), authorIDNew.getAuthorID());
                bookauthor.setAuthorID(authorIDNew);
            }
            if (isbnNew != null) {
                isbnNew = em.getReference(isbnNew.getClass(), isbnNew.getBookID());
                bookauthor.setIsbn(isbnNew);
            }
            bookauthor = em.merge(bookauthor);
            if (authorIDOld != null && !authorIDOld.equals(authorIDNew)) {
                authorIDOld.getBookauthorList().remove(bookauthor);
                authorIDOld = em.merge(authorIDOld);
            }
            if (authorIDNew != null && !authorIDNew.equals(authorIDOld)) {
                authorIDNew.getBookauthorList().add(bookauthor);
                authorIDNew = em.merge(authorIDNew);
            }
            if (isbnOld != null && !isbnOld.equals(isbnNew)) {
                isbnOld.getBookAuthorList().remove(bookauthor);
                isbnOld = em.merge(isbnOld);
            }
            if (isbnNew != null && !isbnNew.equals(isbnOld)) {
                isbnNew.getBookAuthorList().add(bookauthor);
                isbnNew = em.merge(isbnNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BookauthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Bookauthor bookauthor;
            try {
                bookauthor = em.getReference(Bookauthor.class, id);
                bookauthor.getBookAuthorID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bookauthor with id " + id + " no longer exists.", enfe);
            }
            Author authorID = bookauthor.getAuthorID();
            if (authorID != null) {
                authorID.getBookauthorList().remove(bookauthor);
                authorID = em.merge(authorID);
            }
            Book isbn = bookauthor.getIsbn();
            if (isbn != null) {
                isbn.getBookAuthorList().remove(bookauthor);
                isbn = em.merge(isbn);
            }
            em.remove(bookauthor);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(BookauthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Bookauthor> findBookauthorEntities() {
        return findBookauthorEntities(true, -1, -1);
    }

    public List<Bookauthor> findBookauthorEntities(int maxResults, int firstResult) {
        return findBookauthorEntities(false, maxResults, firstResult);
    }

    private List<Bookauthor> findBookauthorEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Bookauthor.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Bookauthor findBookauthor(Integer id) {
        return em.find(Bookauthor.class, id);
    }

    public int getBookauthorCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Bookauthor> rt = cq.from(Bookauthor.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Method that gets the authors linked to a book by ISBN
     * 
     * @author Grant 
     * @param ISBN
     * @return 
     */
    public List<Author> getAuthorOfBook(String ISBN){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Bookauthor.class);
        Root<Bookauthor> bookauthor = cq.from(Bookauthor.class);
        Join book = bookauthor.join("isbn");
        Join author = bookauthor.join("authorID");
        cq.where(cb.equal(book.get("isbn"), ISBN));
        
        cq.select(author);
        
        TypedQuery<Author> listOfAuthors = em.createQuery(cq);
        List<Author> authorObj = listOfAuthors.getResultList();
        
        return authorObj;

    }
    
    /**
     * This method creates the bookauthor entry in the bridging table.
     * It takes input the author and the book that needs to be added
     * @author Alex Bellerive
     * @param auth
     * @param book 
     */
    public void create (Author auth , Book book){
        List<Bookauthor> bookauthors = findBookauthorEntities();
        Integer nextId = bookauthors.get(bookauthors.size()-1).getBookAuthorID()+1;
        
        Bookauthor ba = new Bookauthor();
        ba.setIsbn(book);
        ba.setAuthorID(auth);
        ba.setBookAuthorID(nextId);
        
        create(ba);
    }
}

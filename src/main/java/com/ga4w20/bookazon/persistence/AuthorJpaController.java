package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.Author;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Bookauthor;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate author data.
 * @author Grant
 */
@Named
@SessionScoped
public class AuthorJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(AuthorJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Author author) {
        try {
            if (author.getBookauthorList() == null) {
                author.setBookauthorList(new ArrayList<Bookauthor>());
            }
            utx.begin();
            List<Bookauthor> attachedBookauthorList = new ArrayList<Bookauthor>();
            for (Bookauthor bookauthorListBookauthorToAttach : author.getBookauthorList()) {
                bookauthorListBookauthorToAttach = em.getReference(bookauthorListBookauthorToAttach.getClass(), bookauthorListBookauthorToAttach.getBookAuthorID());
                attachedBookauthorList.add(bookauthorListBookauthorToAttach);
            }
            author.setBookauthorList(attachedBookauthorList);
            em.persist(author);
            for (Bookauthor bookauthorListBookauthor : author.getBookauthorList()) {
                Author oldAuthorIDOfBookauthorListBookauthor = bookauthorListBookauthor.getAuthorID();
                bookauthorListBookauthor.setAuthorID(author);
                bookauthorListBookauthor = em.merge(bookauthorListBookauthor);
                if (oldAuthorIDOfBookauthorListBookauthor != null) {
                    oldAuthorIDOfBookauthorListBookauthor.getBookauthorList().remove(bookauthorListBookauthor);
                    oldAuthorIDOfBookauthorListBookauthor = em.merge(oldAuthorIDOfBookauthorListBookauthor);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Author author) throws IllegalOrphanException {
        try {
            utx.begin();
            Author persistentAuthor = em.find(Author.class, author.getAuthorID());
            List<Bookauthor> bookauthorListOld = persistentAuthor.getBookauthorList();
            List<Bookauthor> bookauthorListNew = author.getBookauthorList();
            List<String> illegalOrphanMessages = null;
            for (Bookauthor bookauthorListOldBookauthor : bookauthorListOld) {
                if (!bookauthorListNew.contains(bookauthorListOldBookauthor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Bookauthor " + bookauthorListOldBookauthor + " since its authorID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Bookauthor> attachedBookauthorListNew = new ArrayList<Bookauthor>();
            for (Bookauthor bookauthorListNewBookauthorToAttach : bookauthorListNew) {
                bookauthorListNewBookauthorToAttach = em.getReference(bookauthorListNewBookauthorToAttach.getClass(), bookauthorListNewBookauthorToAttach.getBookAuthorID());
                attachedBookauthorListNew.add(bookauthorListNewBookauthorToAttach);
            }
            bookauthorListNew = attachedBookauthorListNew;
            author.setBookauthorList(bookauthorListNew);
            author = em.merge(author);
            for (Bookauthor bookauthorListNewBookauthor : bookauthorListNew) {
                if (!bookauthorListOld.contains(bookauthorListNewBookauthor)) {
                    Author oldAuthorIDOfBookauthorListNewBookauthor = bookauthorListNewBookauthor.getAuthorID();
                    bookauthorListNewBookauthor.setAuthorID(author);
                    bookauthorListNewBookauthor = em.merge(bookauthorListNewBookauthor);
                    if (oldAuthorIDOfBookauthorListNewBookauthor != null && !oldAuthorIDOfBookauthorListNewBookauthor.equals(author)) {
                        oldAuthorIDOfBookauthorListNewBookauthor.getBookauthorList().remove(bookauthorListNewBookauthor);
                        oldAuthorIDOfBookauthorListNewBookauthor = em.merge(oldAuthorIDOfBookauthorListNewBookauthor);
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
            Author author;
            try {
                author = em.getReference(Author.class, id);
                author.getAuthorID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The author with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Bookauthor> bookauthorListOrphanCheck = author.getBookauthorList();
            for (Bookauthor bookauthorListOrphanCheckBookauthor : bookauthorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Author (" + author + ") cannot be destroyed since the Bookauthor " + bookauthorListOrphanCheckBookauthor + " in its bookauthorList field has a non-nullable authorID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(author);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Author> findAuthorEntities() {
        return findAuthorEntities(true, -1, -1);
    }

    public List<Author> findAuthorEntities(int maxResults, int firstResult) {
        return findAuthorEntities(false, maxResults, firstResult);
    }

    private List<Author> findAuthorEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Author.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Author findAuthor(Integer id) {
        return em.find(Author.class, id);
    }

    public int getAuthorCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Author> rt = cq.from(Author.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Method used to get author's full name given an authorID
     * @author grant
     * @param authorID
     * @return 
     */
    public String getAuthorName(int authorID) {
        Author author = this.findAuthor(authorID);
        return (author.getFName() + " " + author.getLName());
    }

    /**
     * This method searches all authors to find the first name and last name
     * of the author that is trying to be found. If it finds a author with the name
     * it will return that author, other wise, it will create author 
     * @param fname
     * @param lname
     * @return Author
     * @author Alex Bellerive
     */
    public Author findAuthorByName(String fname, String lname) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Author.class);

        Root<Author> auth = cq.from(Author.class);

        LOG.info(fname + " " + lname);

        cq.where(cb.equal(auth.get("fName"), fname), cb.equal(auth.get("lName"), lname));
        cq.select(auth);

        TypedQuery<Author> query = em.createQuery(cq);
        List<Author> authorList = query.getResultList();

        if (authorList.isEmpty()) {
            List<Author> allAuthors = findAuthorEntities();
            Integer newId = allAuthors.get(allAuthors.size() - 1).getAuthorID() + 1;

            Author newAuthor = new Author();
            newAuthor.setFName(fname);
            newAuthor.setLName(lname);
            newAuthor.setAuthorID(newId);

            create(newAuthor);
            return newAuthor;
        }

        return authorList.get(0);
    }
}

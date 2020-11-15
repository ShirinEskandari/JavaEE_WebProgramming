package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.*;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate publisher data
 * @author jason
 */
@Named
@SessionScoped
public class PublisherJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(PublisherJpaController.class);

    @Resource
    private UserTransaction utx;

    //container inserts references to the entitymanager (injection)
    @PersistenceContext
    private EntityManager em;

    public void create(Publisher publisher) {
        if (publisher.getBookList() == null) {
            publisher.setBookList(new ArrayList<Book>());
        }
        try {
            utx.begin();
            List<Book> attachedBookList = new ArrayList<Book>();
            for (Book bookListBookToAttach : publisher.getBookList()) {
                bookListBookToAttach = em.getReference(bookListBookToAttach.getClass(), bookListBookToAttach.getBookID());
                attachedBookList.add(bookListBookToAttach);
            }
            publisher.setBookList(attachedBookList);
            em.persist(publisher);
            for (Book bookListBook : publisher.getBookList()) {
                Publisher oldPublisherIDOfBookListBook = bookListBook.getPublisherID();
                bookListBook.setPublisherID(publisher);
                bookListBook = em.merge(bookListBook);
                if (oldPublisherIDOfBookListBook != null) {
                    oldPublisherIDOfBookListBook.getBookList().remove(bookListBook);
                    oldPublisherIDOfBookListBook = em.merge(oldPublisherIDOfBookListBook);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Publisher publisher) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            utx.begin();
            Publisher persistentPublisher = em.find(Publisher.class, publisher.getPublisherID());
            List<Book> bookListOld = persistentPublisher.getBookList();
            List<Book> bookListNew = publisher.getBookList();
            List<String> illegalOrphanMessages = null;
            for (Book bookListOldBook : bookListOld) {
                if (!bookListNew.contains(bookListOldBook)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Book " + bookListOldBook + " since its publisherID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Book> attachedBookListNew = new ArrayList<Book>();
            for (Book bookListNewBookToAttach : bookListNew) {
                bookListNewBookToAttach = em.getReference(bookListNewBookToAttach.getClass(), bookListNewBookToAttach.getBookID());
                attachedBookListNew.add(bookListNewBookToAttach);
            }
            bookListNew = attachedBookListNew;
            publisher.setBookList(bookListNew);
            publisher = em.merge(publisher);
            for (Book bookListNewBook : bookListNew) {
                if (!bookListOld.contains(bookListNewBook)) {
                    Publisher oldPublisherIDOfBookListNewBook = bookListNewBook.getPublisherID();
                    bookListNewBook.setPublisherID(publisher);
                    bookListNewBook = em.merge(bookListNewBook);
                    if (oldPublisherIDOfBookListNewBook != null && !oldPublisherIDOfBookListNewBook.equals(publisher)) {
                        oldPublisherIDOfBookListNewBook.getBookList().remove(bookListNewBook);
                        oldPublisherIDOfBookListNewBook = em.merge(oldPublisherIDOfBookListNewBook);
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
            Publisher publisher;
            try {
                publisher = em.getReference(Publisher.class, id);
                publisher.getPublisherID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The publisher with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Book> bookListOrphanCheck = publisher.getBookList();
            for (Book bookListOrphanCheckBook : bookListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Publisher (" + publisher + ") cannot be destroyed since the Book " + bookListOrphanCheckBook + " in its bookList field has a non-nullable publisherID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(publisher);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AuthorJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Publisher> findPublisherEntities() {
        return findPublisherEntities(true, -1, -1);
    }

    public List<Publisher> findPublisherEntities(int maxResults, int firstResult) {
        return findPublisherEntities(false, maxResults, firstResult);
    }

    private List<Publisher> findPublisherEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Publisher.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Publisher findPublisher(Integer id) {
        return em.find(Publisher.class, id);
    }

    public int getPublisherCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Publisher> rt = cq.from(Publisher.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    /**
     * Method used to get the publisher's name from the ID
     * 
     * @author Grant
     * @param publisherID
     * @return 
     */
    public String getPublisherName(int publisherID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Publisher.class);
        Root<Publisher> publisher = cq.from(Publisher.class);
        cq.where(cb.equal(publisher.get("publisherID"), publisherID));
        cq.select(publisher);

        TypedQuery<Publisher> publisherQuery = em.createQuery(cq);
        Publisher publisherObj = publisherQuery.getSingleResult();

        return (publisherObj.getName());
    }

    /**
     * This method takes input the publisher name and tries to find it 
     * already declared in the database, if it isn't in the database, it will 
     * create that new Publisher, if it finds it in the database it will
     * return that already made Publisher
     * @param publisherName
     * @return Publisher
     * @author Alex Bellerive
     */
    public Publisher findPublisherByName(String publisherName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Publisher.class);
        
        Root<Publisher> publisher = cq.from(Publisher.class);
        
        cq.where(cb.equal(publisher.get("name"), publisherName));
        cq.select(publisher);

        TypedQuery<Publisher> query = em.createQuery(cq);
        List<Publisher> publisherList = query.getResultList();
        
        if(publisherList.isEmpty()){
            List<Publisher> allPublishers = findPublisherEntities();
            
            Publisher newPub = new Publisher();
            newPub.setName(publisherName);
            Integer highestId = allPublishers.get(allPublishers.size()-1).getPublisherID();
            newPub.setPublisherID(highestId++);
            create(newPub);
            return newPub;
        }

        return publisherList.get(0);
    }
}

package com.ga4w20.bookazon.persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Review;
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
 *
 * @author Finley
 */
@Named
@SessionScoped
public class ReviewJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(ReviewJpaController.class);
    
    private final int resultsByBook = 3;

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;
    
    private List<Review> reviewList;
    public void create(Review review) {
        try {
            utx.begin();
            Account accountID = review.getAccountID();
            if (accountID != null) {
                accountID = em.getReference(accountID.getClass(), accountID.getAccountID());
                review.setAccountID(accountID);
            }
            Book isbn = review.getIsbn();
            if (isbn != null) {
                isbn = em.getReference(isbn.getClass(), isbn.getBookID());
                review.setIsbn(isbn);
            }
            em.persist(review);
            if (accountID != null) {
                accountID.getReviewList().add(review);
                accountID = em.merge(accountID);
            }
            if (isbn != null) {
                isbn.getReviewList().add(review);
                isbn = em.merge(isbn);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(ReviewJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Review review) {
        try {
            utx.begin();
            Review persistentReview = em.find(Review.class, review.getReviewID());
            Account accountIDOld = persistentReview.getAccountID();
            Account accountIDNew = review.getAccountID();
            Book isbnOld = persistentReview.getIsbn();
            Book isbnNew = review.getIsbn();
            if (accountIDNew != null) {
                accountIDNew = em.getReference(accountIDNew.getClass(), accountIDNew.getAccountID());
                review.setAccountID(accountIDNew);
            }
            if (isbnNew != null) {
                isbnNew = em.getReference(isbnNew.getClass(), isbnNew.getBookID());
                review.setIsbn(isbnNew);
            }
            review = em.merge(review);
            if (accountIDOld != null && !accountIDOld.equals(accountIDNew)) {
                accountIDOld.getReviewList().remove(review);
                accountIDOld = em.merge(accountIDOld);
            }
            if (accountIDNew != null && !accountIDNew.equals(accountIDOld)) {
                accountIDNew.getReviewList().add(review);
                accountIDNew = em.merge(accountIDNew);
            }
            if (isbnOld != null && !isbnOld.equals(isbnNew)) {
                isbnOld.getReviewList().remove(review);
                isbnOld = em.merge(isbnOld);
            }
            if (isbnNew != null && !isbnNew.equals(isbnOld)) {
                isbnNew.getReviewList().add(review);
                isbnNew = em.merge(isbnNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(ReviewJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Review review;
            try {
                review = em.getReference(Review.class, id);
                review.getReviewID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The review with id " + id + " no longer exists.", enfe);
            }
            Account accountID = review.getAccountID();
            if (accountID != null) {
                accountID.getReviewList().remove(review);
                accountID = em.merge(accountID);
            }
            Book isbn = review.getIsbn();
            if (isbn != null) {
                isbn.getReviewList().remove(review);
                isbn = em.merge(isbn);
            }
            em.remove(review);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(ReviewJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Review> findReviewEntities() {
        return findReviewEntities(true, -1, -1);
    }

    public List<Review> findReviewEntities(int maxResults, int firstResult) {
        return findReviewEntities(false, maxResults, firstResult);
    }

    private List<Review> findReviewEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Review.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }
    
    /**
     * A method that finds a list of three reviews for a given book
     * 
     * @author Finley
     * @param ISBN
     * @return 
     */
    public List<Review> findTopThreeByBook(String ISBN) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        Root<Review> root = cq.from(Review.class);
        cq.select(root).where(cb.and(cb.equal(root.get("isbn").get("isbn"), ISBN),cb.equal(root.get("approval"), '1')));
        Query q = em.createQuery(cq).setMaxResults(resultsByBook);
        List<Review> list = q.getResultList();
        return list;

    }

    public Review findReview(Integer id) {
        return em.find(Review.class, id);
    }

    public int getReviewCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Review> rt = cq.from(Review.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<Review> findReviewEntitiesNeedManage(){
        Query query = em.createNamedQuery("Review.findByApproval");
        reviewList = query.setParameter("approval", '0').getResultList();
        LOG.info("length of reviews need manage are: "+ reviewList.size());
       
        return reviewList;
    }
    
    /**
     * A method that publish the review to front page which change number from 0 to 1
     * 
     * @author Lin Yang
     * @param newReview
     */
    public void updateReviewApproval(Review newReview){
        newReview.setApproval('1');
        edit(newReview);
    }
    
    /**
     * A method that DELETE the review to front page which change number from 0 to 2
     * 
     * @author Lin Yang
     * @param newReview
     */
    public void hiddenReview(Review newReview){
        newReview.setApproval('2');
        edit(newReview);
    }

}

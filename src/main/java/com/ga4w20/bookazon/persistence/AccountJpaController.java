package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.Account;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Sale;
import java.util.ArrayList;
import java.util.List;
import com.ga4w20.bookazon.entities.Review;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
 * Controller use for Account
 *
 * @author Shirin
 */
@Named
@SessionScoped
public class AccountJpaController implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(AccountJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Account account) {
        try {
            if (account.getSaleList() == null) {
                account.setSaleList(new ArrayList<Sale>());
            }
            if (account.getReviewList() == null) {
                account.setReviewList(new ArrayList<Review>());
            }
            utx.begin();
            List<Sale> attachedSaleList = new ArrayList<Sale>();
            for (Sale saleListSaleToAttach : account.getSaleList()) {
                saleListSaleToAttach = em.getReference(saleListSaleToAttach.getClass(), saleListSaleToAttach.getSaleID());
                attachedSaleList.add(saleListSaleToAttach);
            }
            account.setSaleList(attachedSaleList);
            List<Review> attachedReviewList = new ArrayList<Review>();
            for (Review reviewListReviewToAttach : account.getReviewList()) {
                reviewListReviewToAttach = em.getReference(reviewListReviewToAttach.getClass(), reviewListReviewToAttach.getReviewID());
                attachedReviewList.add(reviewListReviewToAttach);
            }
            account.setReviewList(attachedReviewList);
            em.persist(account);
            for (Sale saleListSale : account.getSaleList()) {
                Account oldAccountIDOfSaleListSale = saleListSale.getAccountID();
                saleListSale.setAccountID(account);
                saleListSale = em.merge(saleListSale);
                if (oldAccountIDOfSaleListSale != null) {
                    oldAccountIDOfSaleListSale.getSaleList().remove(saleListSale);
                    oldAccountIDOfSaleListSale = em.merge(oldAccountIDOfSaleListSale);
                }
            }
            for (Review reviewListReview : account.getReviewList()) {
                Account oldAccountIDOfReviewListReview = reviewListReview.getAccountID();
                reviewListReview.setAccountID(account);
                reviewListReview = em.merge(reviewListReview);
                if (oldAccountIDOfReviewListReview != null) {
                    oldAccountIDOfReviewListReview.getReviewList().remove(reviewListReview);
                    oldAccountIDOfReviewListReview = em.merge(oldAccountIDOfReviewListReview);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AccountJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Account account) throws IllegalOrphanException {
        try {
            utx.begin();
            Account persistentAccount = em.find(Account.class, account.getAccountID());
            List<Sale> saleListOld = persistentAccount.getSaleList();
            List<Sale> saleListNew = account.getSaleList();
            List<Review> reviewListOld = persistentAccount.getReviewList();
            List<Review> reviewListNew = account.getReviewList();
            List<String> illegalOrphanMessages = null;
            for (Sale saleListOldSale : saleListOld) {
                if (!saleListNew.contains(saleListOldSale)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sale " + saleListOldSale + " since its accountID field is not nullable.");
                }
            }
            for (Review reviewListOldReview : reviewListOld) {
                if (!reviewListNew.contains(reviewListOldReview)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Review " + reviewListOldReview + " since its accountID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sale> attachedSaleListNew = new ArrayList<Sale>();
            for (Sale saleListNewSaleToAttach : saleListNew) {
                saleListNewSaleToAttach = em.getReference(saleListNewSaleToAttach.getClass(), saleListNewSaleToAttach.getSaleID());
                attachedSaleListNew.add(saleListNewSaleToAttach);
            }
            saleListNew = attachedSaleListNew;
            account.setSaleList(saleListNew);
            List<Review> attachedReviewListNew = new ArrayList<Review>();
            for (Review reviewListNewReviewToAttach : reviewListNew) {
                reviewListNewReviewToAttach = em.getReference(reviewListNewReviewToAttach.getClass(), reviewListNewReviewToAttach.getReviewID());
                attachedReviewListNew.add(reviewListNewReviewToAttach);
            }
            reviewListNew = attachedReviewListNew;
            account.setReviewList(reviewListNew);
            account = em.merge(account);
            for (Sale saleListNewSale : saleListNew) {
                if (!saleListOld.contains(saleListNewSale)) {
                    Account oldAccountIDOfSaleListNewSale = saleListNewSale.getAccountID();
                    saleListNewSale.setAccountID(account);
                    saleListNewSale = em.merge(saleListNewSale);
                    if (oldAccountIDOfSaleListNewSale != null && !oldAccountIDOfSaleListNewSale.equals(account)) {
                        oldAccountIDOfSaleListNewSale.getSaleList().remove(saleListNewSale);
                        oldAccountIDOfSaleListNewSale = em.merge(oldAccountIDOfSaleListNewSale);
                    }
                }
            }
            for (Review reviewListNewReview : reviewListNew) {
                if (!reviewListOld.contains(reviewListNewReview)) {
                    Account oldAccountIDOfReviewListNewReview = reviewListNewReview.getAccountID();
                    reviewListNewReview.setAccountID(account);
                    reviewListNewReview = em.merge(reviewListNewReview);
                    if (oldAccountIDOfReviewListNewReview != null && !oldAccountIDOfReviewListNewReview.equals(account)) {
                        oldAccountIDOfReviewListNewReview.getReviewList().remove(reviewListNewReview);
                        oldAccountIDOfReviewListNewReview = em.merge(oldAccountIDOfReviewListNewReview);
                    }
                }
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(AccountJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        try {
            utx.begin();
            Account account;
            try {
                account = em.getReference(Account.class, id);
                account.getAccountID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The account with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sale> saleListOrphanCheck = account.getSaleList();
            for (Sale saleListOrphanCheckSale : saleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Sale " + saleListOrphanCheckSale + " in its saleList field has a non-nullable accountID field.");
            }
            List<Review> reviewListOrphanCheck = account.getReviewList();
            for (Review reviewListOrphanCheckReview : reviewListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Review " + reviewListOrphanCheckReview + " in its reviewList field has a non-nullable accountID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(account);
            utx.commit();

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AccountJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Account> findAccountEntities() {
        return findAccountEntities(true, -1, -1);
    }

    public List<Account> findAccountEntities(int maxResults, int firstResult) {
        return findAccountEntities(false, maxResults, firstResult);
    }

    private List<Account> findAccountEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Account.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Account findAccount(Integer id) {
        return em.find(Account.class, id);

    }

    public int getAccountCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Account> rt = cq.from(Account.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Finds account with email address which is unique
     *
     * @author Shirin
     * @param account
     * @return
     */
    public Account findAccountByEmail(String account) {
        TypedQuery<Account> query = em.createNamedQuery("Account.findByEmail", Account.class);
        query.setParameter("email", account);
        List<Account> clients = query.getResultList();
        if (!clients.isEmpty()) {
            return clients.get(0);
        }
        return null;
    }

    /**
     * it is a helper method for compare a password with salt it helps the
     * methods inside AccountJpaController it gets a password, finds user with
     * the email address, then gets salt string from the database and again make
     * another hash password with help of this salt and just entered password
     * compares with hashed password that exist in the database, if there is a
     * match then login
     *
     * @author Shirin and I learned this method from Gordon class, he helped me
     * to underestand it completely
     * @param password
     * @param salt
     * @return
     * @throws InvalidKeySpecException
     */
    public byte[] generateHash(String password, String salt) throws InvalidKeySpecException {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            /* When defining the keyspec, in addition to passing in the password and salt, we also pass in
			a number of iterations (1024) and a key size (256). The number of iterations, 1024, is the
			number of times we perform our hashing function on the input. Normally, you could increase security
			further by using a different number of iterations for each user (in the same way you use a different
			salt for each user) and storing that number of iterations. Here, we just use a constant number of
			iterations. The key size is the number of bits we want in the output hash
             */
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);

            SecretKey key = skf.generateSecret(spec);
            byte[] hash = key.getEncoded();
            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a randomly generated String
     *
     * @author Shirin
     * @return
     */
    public String getSalt2() {
        SecureRandom random = new SecureRandom();

        return new BigInteger(140, random).toString(32);
    }

}

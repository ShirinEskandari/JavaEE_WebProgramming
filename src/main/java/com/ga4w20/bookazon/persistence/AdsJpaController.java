package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.Ads;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.LoggerFactory;

/**
 * Controller used to manipulate ads data.
 * @author Finley
 */
@Named
@SessionScoped
public class AdsJpaController implements Serializable {
    
    private static final long serialVersionUID = 12L;
    
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(AdsJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Ads ads) {
        try {
            utx.begin();
            em.persist(ads);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AdsJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Ads ads) {
        try {
            utx.begin();
            ads = em.merge(ads);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AdsJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Ads ads;
            try {
                ads = em.getReference(Ads.class, id);
                ads.getAdID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ads with id " + id + " no longer exists.", enfe);
            }
            em.remove(ads);
            utx.commit();

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AdsJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Ads> findAdsEntities() {
        return findAdsEntities(true, -1, -1);
    }

    public List<Ads> findAdsEntities(int maxResults, int firstResult) {
        return findAdsEntities(false, maxResults, firstResult);
    }

    private List<Ads> findAdsEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Ads.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Ads findAds(Integer id) {
        return em.find(Ads.class, id);
    }

    public int getAdsCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Ads> rt = cq.from(Ads.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    /**
     * Method that finds the ad that is horizontal and active
     * @author Finley
     * @return 
     */
    public List<Ads> findActiveHorizontalAd() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ads> cq = cb.createQuery(Ads.class);
        Root<Ads> root = cq.from(Ads.class);
        cq.select(root).where(cb.equal(root.get("adType"), 0), cb.equal(root.get("activeStatus"), 1));
        Query q = em.createQuery(cq);
        List<Ads> list = q.getResultList();
        
        LOG.info(list.toString());
        return list;
    }
    
    /**
     * Method that finds the ad that is square and active
     * @author Finley
     * @return 
     */
    public List<Ads> findActiveSquareAd() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ads> cq = cb.createQuery(Ads.class);
        Root<Ads> root = cq.from(Ads.class);
        cq.select(root).where(cb.equal(root.get("adType"), 1), cb.equal(root.get("activeStatus"), 1));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
    
    /**
     * Method used to change the activeStatus of an Ad in the manager side.
     * @param ad 
     */
    public void activate(Ads ad){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ads> cq = cb.createQuery(Ads.class);
        Root<Ads> root = cq.from(Ads.class);
        //Find the adType that is the same as the one we want to activate (the param) and that is active
        cq.select(root).where(cb.equal(root.get("adType"), ad.getAdType()), cb.equal(root.get("activeStatus"), 1));
        Query q = em.createQuery(cq);
        List<Ads> activeAds = q.getResultList();
        
        if(!activeAds.isEmpty()){
            Ads previousAd = activeAds.get(0);
            previousAd.setActiveStatus(0);
            this.edit(previousAd);
        }
        
        ad.setActiveStatus(1);
        this.edit(ad);
    }
}

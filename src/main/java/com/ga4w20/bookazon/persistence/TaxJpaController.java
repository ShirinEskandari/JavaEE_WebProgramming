package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.Tax;
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
import javax.persistence.TypedQuery;
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
 * Controller used to manipulate tax data.
 * @author Grant
 */
@Named
@SessionScoped
public class TaxJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(TaxJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Tax tax) {
        try {
            utx.begin();
            em.persist(tax);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(TaxJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Tax tax) {
        try {
            utx.begin();
            tax = em.merge(tax);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(TaxJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Tax tax;
            try {
                tax = em.getReference(Tax.class, id);
                tax.getTaxID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tax with id " + id + " no longer exists.", enfe);
            }
            em.remove(tax);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(TaxJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Tax> findTaxEntities() {
        return findTaxEntities(true, -1, -1);
    }

    public List<Tax> findTaxEntities(int maxResults, int firstResult) {
        return findTaxEntities(false, maxResults, firstResult);
    }

    private List<Tax> findTaxEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Tax.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Tax findTax(Integer id) {
        return em.find(Tax.class, id);
    }

    public int getTaxCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Tax> rt = cq.from(Tax.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    /**
     * Provided a string of the province acronym, it will look into the 
     * provinces in the database and return the proper Tax object
     * @param province
     * @return 
     * @author Alex Bellerive, Grant Manzano
     */
    public Tax getTaxes(String province){
        //Giving a default value (quebec) rather than having the application crash
        if(province == null || province.isEmpty()){
            LOG.info("Province is empty. Default to Quebec");
            province = "QC";
        }
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tax> cq = cb.createQuery(Tax.class);
        Root<Tax> tax = cq.from(Tax.class);
        
        cq.where(cb.equal(tax.get("province"), province));
        cq.select(tax);
        
        TypedQuery<Tax> query = em.createQuery(cq);
        Tax taxes = query.getSingleResult();
        
        return taxes;
    }

}

package com.ga4w20.bookazon.persistence;

import com.ga4w20.bookazon.entities.Answer;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Survey;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.persistence.PersistenceContext;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lin
 */
@Named
@SessionScoped
public class AnswerJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(AnswerJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Answer answer) {
        try {
            utx.begin();
            Survey serveyID = answer.getServeyID();
            if (serveyID != null) {
                serveyID = em.getReference(serveyID.getClass(), serveyID.getServeyID());
                answer.setServeyID(serveyID);
            }
            em.persist(answer);
            if (serveyID != null) {
                serveyID.getAnswerList().add(answer);
                serveyID = em.merge(serveyID);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AnswerJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void edit(Answer answer) {
        try {
            utx.begin();
            Answer persistentAnswer = em.find(Answer.class, answer.getAnswerID());
            Survey serveyIDOld = persistentAnswer.getServeyID();
            Survey serveyIDNew = answer.getServeyID();
            if (serveyIDNew != null) {
                serveyIDNew = em.getReference(serveyIDNew.getClass(), serveyIDNew.getServeyID());
                answer.setServeyID(serveyIDNew);
            }
            answer = em.merge(answer);
            if (serveyIDOld != null && !serveyIDOld.equals(serveyIDNew)) {
                serveyIDOld.getAnswerList().remove(answer);
                serveyIDOld = em.merge(serveyIDOld);
            }
            if (serveyIDNew != null && !serveyIDNew.equals(serveyIDOld)) {
                serveyIDNew.getAnswerList().add(answer);
                serveyIDNew = em.merge(serveyIDNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AnswerJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            utx.begin();
            Answer answer;
            try {
                answer = em.getReference(Answer.class, id);
                answer.getAnswerID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The answer with id " + id + " no longer exists.", enfe);
            }
            Survey serveyID = answer.getServeyID();
            if (serveyID != null) {
                serveyID.getAnswerList().remove(answer);
                serveyID = em.merge(serveyID);
            }
            em.remove(answer);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AnswerJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Answer> findAnswerEntities() {
        return findAnswerEntities(true, -1, -1);
    }

    public List<Answer> findAnswerEntities(int maxResults, int firstResult) {
        return findAnswerEntities(false, maxResults, firstResult);
    }

    private List<Answer> findAnswerEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Answer.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Answer findAnswer(Integer id) {
        return em.find(Answer.class, id);
    }

    public int getAnswerCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Answer> rt = cq.from(Answer.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }
    /**
     * this method will update the volt for given answer id
     * first have to find answer by giving id
     * @param ID 
     * @author Lin Yang
     */
    public void updateAnswerForVote(int ID){
        Answer answerForUpdate = findAnswer(ID);
        int answerNewVote = answerForUpdate.getVote()+1;
        answerForUpdate.setVote(answerNewVote);
        edit(answerForUpdate);
    }

}

package com.ga4w20.bookazon.persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ga4w20.bookazon.entities.Answer;
import com.ga4w20.bookazon.entities.Survey;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
 *
 * @author Grant
 */
@Named
@SessionScoped
public class SurveyJpaController implements Serializable {
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SurveyJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public void create(Survey survey) {
        try {
            if (survey.getAnswerList() == null) {
                survey.setAnswerList(new ArrayList<Answer>());
            }
            utx.begin();
            List<Answer> attachedAnswerList = new ArrayList<Answer>();
            for (Answer answerListAnswerToAttach : survey.getAnswerList()) {
                answerListAnswerToAttach = em.getReference(answerListAnswerToAttach.getClass(), answerListAnswerToAttach.getAnswerID());
                attachedAnswerList.add(answerListAnswerToAttach);
            }
            survey.setAnswerList(attachedAnswerList);
            em.persist(survey);
            for (Answer answerListAnswer : survey.getAnswerList()) {
                Survey oldServeyIDOfAnswerListAnswer = answerListAnswer.getServeyID();
                answerListAnswer.setServeyID(survey);
                answerListAnswer = em.merge(answerListAnswer);
                if (oldServeyIDOfAnswerListAnswer != null) {
                    oldServeyIDOfAnswerListAnswer.getAnswerList().remove(answerListAnswer);
                    oldServeyIDOfAnswerListAnswer = em.merge(oldServeyIDOfAnswerListAnswer);
                }
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(SurveyJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Survey survey) throws IllegalOrphanException {
        try {
            utx.begin();
            Survey persistentSurvey = em.find(Survey.class, survey.getServeyID());
            List<Answer> answerListOld = persistentSurvey.getAnswerList();
            List<Answer> answerListNew = survey.getAnswerList();
            List<String> illegalOrphanMessages = null;
            for (Answer answerListOldAnswer : answerListOld) {
                if (!answerListNew.contains(answerListOldAnswer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Answer " + answerListOldAnswer + " since its serveyID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Answer> attachedAnswerListNew = new ArrayList<Answer>();
            for (Answer answerListNewAnswerToAttach : answerListNew) {
                answerListNewAnswerToAttach = em.getReference(answerListNewAnswerToAttach.getClass(), answerListNewAnswerToAttach.getAnswerID());
                attachedAnswerListNew.add(answerListNewAnswerToAttach);
            }
            answerListNew = attachedAnswerListNew;
            survey.setAnswerList(answerListNew);
            survey = em.merge(survey);
            for (Answer answerListNewAnswer : answerListNew) {
                if (!answerListOld.contains(answerListNewAnswer)) {
                    Survey oldServeyIDOfAnswerListNewAnswer = answerListNewAnswer.getServeyID();
                    answerListNewAnswer.setServeyID(survey);
                    answerListNewAnswer = em.merge(answerListNewAnswer);
                    if (oldServeyIDOfAnswerListNewAnswer != null && !oldServeyIDOfAnswerListNewAnswer.equals(survey)) {
                        oldServeyIDOfAnswerListNewAnswer.getAnswerList().remove(answerListNewAnswer);
                        oldServeyIDOfAnswerListNewAnswer = em.merge(oldServeyIDOfAnswerListNewAnswer);
                    }
                }
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(SurveyJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, IllegalOrphanException {
        try {
            utx.begin();
            Survey survey;
            try {
                survey = em.getReference(Survey.class, id);
                survey.getServeyID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The survey with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Answer> answerListOrphanCheck = survey.getAnswerList();
            for (Answer answerListOrphanCheckAnswer : answerListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Survey (" + survey + ") cannot be destroyed since the Answer " + answerListOrphanCheckAnswer + " in its answerList field has a non-nullable serveyID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(survey);
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(SurveyJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Survey> findSurveyEntities() {
        return findSurveyEntities(true, -1, -1);
    }

    public List<Survey> findSurveyEntities(int maxResults, int firstResult) {
        return findSurveyEntities(false, maxResults, firstResult);
    }

    private List<Survey> findSurveyEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Survey.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Survey findSurvey(Integer id) {
        return em.find(Survey.class, id);
    }

    public int getSurveyCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Survey> rt = cq.from(Survey.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    /**
     * this method for randomly pick the survey from database to display on the client page
     * first find random number by using Random id
     * @param 
     * @return id
     * @author Lin Yang
     */
    
    public int findRandomSurveyId(){
        Random random = new Random();
        int id = random.nextInt(10)+1;
        return id;
    }
    
}

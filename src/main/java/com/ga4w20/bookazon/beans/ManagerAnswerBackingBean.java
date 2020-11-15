
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Answer;
import com.ga4w20.bookazon.entities.Survey;
import com.ga4w20.bookazon.persistence.AnswerJpaController;
import com.ga4w20.bookazon.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lin Yang
 */
@Named
@SessionScoped
public class ManagerAnswerBackingBean implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(ManagerAnswerBackingBean.class);
    
    @Inject
    private AnswerJpaController ajc;
    
    private Answer answerObj;
    private String answer; 
    private int vote;
    private int answerID;
    private Survey surveyID;



    public Answer getAnswerObj() {
        return answerObj;
    }

    public void setAnswerObj(Answer answerObj) {
        this.answerObj = answerObj;
    }

    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    public Survey getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(Survey surveyID) {
        this.surveyID = surveyID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
    
    /*
     * update the answer by user click on page
     * keep the vote as old one. 
     */
    
    public void updateanswer(){
        if (!this.answer.isEmpty() && !this.answer.isBlank()) {
            LOG.info("Changing " + this.answer);
            this.answerObj.setAnswer(this.answer.trim());
        }
        if (this.vote != 0) {
            LOG.info("Changing " + this.vote);
            this.answerObj.setVote(this.vote);
        }
        if (this.surveyID != null) {
            LOG.info("Changing " + this.surveyID.getServeyID());
            this.answerObj.setServeyID(this.surveyID);
        }
        
        ajc.edit(answerObj);
    }
    
    /*
     * Delete the answer by calling the destroy method! 
     * Not user anymore.
     */
    public void deleteAnswer(Answer answerForDelete) throws NonexistentEntityException{
        ajc.destroy(answerForDelete.getAnswerID());
    }
    
    /*
     * add the new answer to database by set the vote to 0 
     */
    public void addNewAnswer(){
        if (!this.answer.isEmpty() && !this.answer.isBlank()) {
            LOG.info("Adding " + this.answer);
            this.answerObj.setAnswer(this.answer.trim());
        }
        this.answerObj.setVote(0);
        
        if (this.surveyID != null) {
            LOG.info("Changing " + this.surveyID.getServeyID());
            this.answerObj.setServeyID(this.surveyID);
        }
        ajc.create(answerObj);
    }
    
}

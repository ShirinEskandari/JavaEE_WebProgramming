
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Answer;
import com.ga4w20.bookazon.entities.Survey;
import com.ga4w20.bookazon.persistence.AnswerJpaController;
import com.ga4w20.bookazon.persistence.SurveyJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
import java.io.Serializable;
import java.util.List;
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
public class ManagerSurveyBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerSurveyBackingBean.class);

    @Inject
    private SurveyJpaController sjc;
    
    @Inject
    private AnswerJpaController ajc;
    
    @Inject
    ManagerAnswerBackingBean mbm;
   
    private Survey newSurvey;
    private List<Survey> surveyList;
    
    private String question;

    
    public Survey getNewSurvey() {
        return newSurvey;
    }

    public void setNewSurvey(Survey newSurvey) {
        this.newSurvey = newSurvey;
    }
    

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
    
    public List<Survey> getSurveyList() {
        this.surveyList = sjc.findSurveyEntities();
        return this.surveyList;
    }

    public void setSurveyList(List<Survey> surveyList) {
        this.surveyList = surveyList;
    }
    
   /*
    * this method is working on creating a new question by manager
    * will set a default answer for thi question in database
    * default answer: default message + zero volt.
    */

    public void createQuestion(){
        if(!this.question.isBlank() && !this.question.isEmpty()){
          LOG.info("Adding " + this.question);
          this.newSurvey.setQuestion(this.question);
        }
        sjc.create(newSurvey);
        
        Answer defaultAnswer = ajc.findAnswer(1);
        defaultAnswer.setAnswer("default Answer, please change!");
        defaultAnswer.setVote(0);
        defaultAnswer.setServeyID(newSurvey);
        ajc.create(defaultAnswer);
        
    }
    
   /*
    * this method is working on update question by manager
    * save the new question to database.
    */
    public void updateQuestion() throws IllegalOrphanException {
        if(!this.question.isBlank() && !this.question.isEmpty()){
          LOG.info("Changing " + this.question);
          this.newSurvey.setQuestion(this.question);
        }
        sjc.edit(newSurvey);
    }
    
}

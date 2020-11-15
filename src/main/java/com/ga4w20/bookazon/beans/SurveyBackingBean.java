package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Answer;
import com.ga4w20.bookazon.entities.Survey;
import com.ga4w20.bookazon.persistence.AnswerJpaController;
import com.ga4w20.bookazon.persistence.SurveyJpaController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
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
public class SurveyBackingBean implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(BookBackingBean.class);
    

    @Inject
    private SurveyJpaController sjc;
    @Inject
    private AnswerJpaController ajc;
    private int randomId;
    private Survey survey;
    private List<Answer> answerList;
    private int answerIDFromUser;
    private boolean showingSurvey;
    private boolean showingMessage;
    private String userChoose;

    
    @PostConstruct
    public void init(){
        this.randomId = sjc.findRandomSurveyId();
        this.survey = sjc.findSurvey(randomId);
        this.answerList = this.survey.getAnswerList();
        this.showingSurvey = true;
        this.showingMessage = false;
    }
    
    
    public void setAnswerIDFromUser(int answer){
        this.answerIDFromUser = answer;
    }
    
    public int getAnswerIDFromUser(){
        return this.answerIDFromUser;
    }
    
    //showing the survey and answer part when first load page.
    public boolean getSurveyShowing() {
        return showingSurvey;
    }
    public void setSurveyShowing(boolean isShowing) {
        this.showingSurvey = isShowing;
    }
    
    //seeting the showing message after user choose the answer
    public boolean getShowingMessage(){
        return this.showingMessage;
    }
    public void setShowingMessage(boolean showing){
         this.showingMessage=showing;
    }

    
    public Survey getSurvey(){
        return this.survey;
    }
    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
    
    
    public List<Answer> getAnswersList(){
        return this.answerList;
    }
    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
    
   /*
    * this method is for a submit buttom event handler
    * hidden question, show the message and volt data to the user
    * update the volt from user to database.
    */
    public void submitClicked(AjaxBehaviorEvent event){
       setSurveyShowing(false);
       setShowingMessage(true);
       this.userChoose = ajc.findAnswer(this.answerIDFromUser).getAnswer();
       LOG.info("This is the output from ajax: "+this.userChoose);
       ajc.updateAnswerForVote(this.answerIDFromUser);
    }
    
    public String getUserChoose() {
        return userChoose;
    }

    public void setUserChoose(String userChoose) {
        this.userChoose = userChoose;
    }
   
}

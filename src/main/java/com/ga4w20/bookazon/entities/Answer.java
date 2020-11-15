/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "answer", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Answer.findAll", query = "SELECT a FROM Answer a"),
    @NamedQuery(name = "Answer.findByAnswerID", query = "SELECT a FROM Answer a WHERE a.answerID = :answerID"),
    @NamedQuery(name = "Answer.findByAnswer", query = "SELECT a FROM Answer a WHERE a.answer = :answer"),
    @NamedQuery(name = "Answer.findByVote", query = "SELECT a FROM Answer a WHERE a.vote = :vote")})
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "AnswerID")
    private Integer answerID;
    @Size(max = 250)
    @Column(name = "Answer")
    private String answer;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Vote")
    private int vote;
    @JoinColumn(name = "ServeyID", referencedColumnName = "ServeyID")
    @ManyToOne(optional = false)
    private Survey serveyID;

    public Answer() {
    }

    public Answer(Integer answerID) {
        this.answerID = answerID;
    }

    public Answer(Integer answerID, int vote) {
        this.answerID = answerID;
        this.vote = vote;
    }

    public Integer getAnswerID() {
        return answerID;
    }

    public void setAnswerID(Integer answerID) {
        this.answerID = answerID;
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

    public Survey getServeyID() {
        return serveyID;
    }

    public void setServeyID(Survey serveyID) {
        this.serveyID = serveyID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (answerID != null ? answerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Answer)) {
            return false;
        }
        Answer other = (Answer) object;
        if ((this.answerID == null && other.answerID != null) || (this.answerID != null && !this.answerID.equals(other.answerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Answer[ answerID=" + answerID + " ]";
    }
    
}

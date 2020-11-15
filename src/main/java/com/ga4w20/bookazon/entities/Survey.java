/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Grant
 */
@Entity
@Table(name = "survey", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findByServeyID", query = "SELECT s FROM Survey s WHERE s.serveyID = :serveyID"),
    @NamedQuery(name = "Survey.findByQuestion", query = "SELECT s FROM Survey s WHERE s.question = :question")})
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ServeyID")
    private Integer serveyID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "Question")
    private String question;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serveyID")
    private List<Answer> answerList;

    public Survey() {
    }

    public Survey(Integer serveyID) {
        this.serveyID = serveyID;
    }

    public Survey(Integer serveyID, String question) {
        this.serveyID = serveyID;
        this.question = question;
    }

    public Integer getServeyID() {
        return serveyID;
    }

    public void setServeyID(Integer serveyID) {
        this.serveyID = serveyID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serveyID != null ? serveyID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Survey)) {
            return false;
        }
        Survey other = (Survey) object;
        if ((this.serveyID == null && other.serveyID != null) || (this.serveyID != null && !this.serveyID.equals(other.serveyID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Survey[ serveyID=" + serveyID + " ]";
    }
    
}

package com.ga4w20.bookazon.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Alex Bellerive
 */
@Named("language")
@SessionScoped
public class LanguageConfiguration implements Serializable{
    
    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    
    public void setLocale(String newLang){
        this.locale= new Locale(newLang);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(this.locale);
    }
    
    public Locale getLocale(){
        return this.locale;
    }
}
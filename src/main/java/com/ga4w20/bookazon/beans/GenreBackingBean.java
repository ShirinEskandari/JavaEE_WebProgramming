package com.ga4w20.bookazon.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Backing Bean for Genre used to store the genre that was clicked for redirection towards the genre.xhtml (used in the header/mainHeader mainly)
 * @author Grant
 */
@Named
@SessionScoped
public class GenreBackingBean implements Serializable {
    private String selectedGenre;
    private boolean wasPreviouslyVisited = false;
    private boolean clicked = false;
    private String genrePreviouslyVisited;
    
    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
    
    public String getGenrePreviouslyVisited(){
        return this.genrePreviouslyVisited;
    }
    
    public void setGenrePreviouslyVisited(String genrePreviouslyVisited){
        this.genrePreviouslyVisited = genrePreviouslyVisited;
    }
    
    public String getSelectedGenre() {
        return this.selectedGenre;
    }

    public void setSelectedGenre(String selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    public boolean wasPreviouslyVisisted() {
        return this.wasPreviouslyVisited;
    }
    
    public void setWasPreviouslyVisited(boolean wasPreviouslyVisited){
        this.wasPreviouslyVisited = wasPreviouslyVisited;
    }
}

package com.ga4w20.bookazon.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Backing Bean for the Search bar, used to store the user input and the filter they've selected. Filter is defaulted to title.
 * @author Grant
 */
@Named
@SessionScoped
public class SearchBackingBean implements Serializable {
    private String searchFilterValue;
    private String searchFilter = "title";
    private boolean searched = false;
    
    public String getSearchFilterValue(){
        return this.searchFilterValue;
    }
    
    public void setSearchFilterValue(String searchFilterValue){
        this.searchFilterValue = searchFilterValue;
    }
    
    public String getSearchFilter(){
        return this.searchFilter;
    }
    
    public void setSearchFilter(String searchFilter){
        this.searchFilter = searchFilter;
    }
    
    public boolean isSearched(){
        return this.searched;
    }
    
    public void setSearched(boolean searched){
        this.searched = searched;
    }
}
package com.ga4w20.bookazon.beans;

/**
 *
 * @author Alex Bellerive
 */
public class RSSItem {
    
    private String link;
    private String title;
    private String description;
    
    public RSSItem (String givenLink, String givenTitle, String givenDesc){
        this.link = givenLink;
        this.title = givenTitle;
        this.description = givenDesc;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String desc){
        this.description = desc;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public String getLink() {
    	return this.link;
    }
    
    public void setLink(String link){
        this.link = link;
    }
}

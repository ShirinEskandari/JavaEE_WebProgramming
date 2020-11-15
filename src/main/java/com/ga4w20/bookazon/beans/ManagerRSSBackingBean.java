package com.ga4w20.bookazon.beans;


import com.ga4w20.bookazon.persistence.RssJpaController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bellerive
 */
@Named("rssBackingBean")
@SessionScoped
public class ManagerRSSBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerRSSBackingBean.class);
    
    @Inject
    private RssJpaController rssJpaController;
    
    private String label;
    private String newLink;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNewLink() {
        return newLink;
    }

    public void setNewLink(String newLink) {
        this.newLink = newLink;
    }

    /**
     * Depending on the status of the specific rss, it will return a string
     * that will say activate or currently activated in a button. 
     * @param status
     * @return String
     * @author Alex Bellerive
     */
    public Boolean statusTranslation(int status){
        if(status == 0){
            label = "Activate";
            return false;
        }
        label = "Currently Activated";
        return true;
    }
    
    /**
     * This method activates an rss link depending on the id given
     * @param rssId 
     * @author Alex Bellerive
     */
    public void activateLink (Integer rssId){
        rssJpaController.activateLink(rssId);
    }
    
    /**
     * This method creates the new rsslink depending on the manager input
     * @author Alex Bellerive
     */
    public void createRSS(){
        rssJpaController.addNewLink(newLink);
    }

}

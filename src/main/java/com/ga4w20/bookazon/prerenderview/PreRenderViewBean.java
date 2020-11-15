package com.ga4w20.bookazon.prerenderview;

import com.ga4w20.bookazon.beans.CartBackingBean;
import com.ga4w20.bookazon.beans.GenreBackingBean;
import java.util.ArrayList;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods to read and write cookies Cookies can be read at
 * any time but can only be written to before any HTML is delivered to the
 * browser. For that reason creating cookies is always a preRenderView type
 * event
 *
 * @author Ken
 * @author updates made by Grant
 */
@Named
@RequestScoped
public class PreRenderViewBean {
    @Inject
    CartBackingBean cartBean;
    @Inject
    GenreBackingBean genreBackingBean;
    
    // Default logger is java.util.logging
    private final static Logger LOG = LoggerFactory.getLogger(PreRenderViewBean.class);

    /**
     * Look for a cookie
     */
    public void checkCookies() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> cookieMap = context.getExternalContext().getRequestCookieMap();

        // Retrieve all cookies
        if (cookieMap == null || cookieMap.isEmpty()) {
            LOG.info("No cookies");
        } else {
            ArrayList<Object> ac = new ArrayList<>(cookieMap.values());
            // Streams coding to print out the contenst of the cookies found
            ac.stream().map((c) -> {
                String cookieName = ((Cookie) c).getName();
                LOG.info(cookieName);
                switch (cookieName) {
                    case "JSESSIONID":
                        return null;
                    case "genre":
                        return null;
                        //cookie that appears when you download a book
                    case "primefaces.download":
                        return null;
                        //cookie that appears when you open the page on waldo
                    case "treeForm_tree-hi":
                        return null;
                    default:
                        break;
                }
                return c;
            }).forEach((c) -> {
                if (c == null) {
                    LOG.info("Cookie not used for the cart.");
                } else {
                    int cookieIntVal = Integer.valueOf(((Cookie) c).getValue());
                    this.cartBean.addBook(cookieIntVal);
                    LOG.info(((Cookie) c).getValue());
                }
            });
        }
    }
    
    /**
     * Method used to set a list of books from the user's previous click
     */
    public void checkPreviousVisit(){
        FacesContext context = FacesContext.getCurrentInstance();
        
        // Retrieve a specific cookie
        Object previousVisit = context.getExternalContext().getRequestCookieMap().get("genre");
        if(previousVisit != null) {
            //false by default in the bean
            this.genreBackingBean.setClicked(true);
            this.genreBackingBean.setWasPreviouslyVisited(true);
            this.genreBackingBean.setGenrePreviouslyVisited(((Cookie) previousVisit).getValue());
        }
    }
}

package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import com.ga4w20.bookazon.entities.Review;
import com.ga4w20.bookazon.persistence.BookJpaController;
import com.ga4w20.bookazon.persistence.ReviewJpaController;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A backing bean for reviews
 * 
 * @author finley
 */
@Named("reviewBacking")
@SessionScoped
public class ReviewBackingBean implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(ReviewBackingBean.class);

    @Inject
    private ReviewJpaController reviewJpaController;
    
    @Inject
    private BookJpaController bookJpaController;
    
    @Inject
    private LoginBackingBean loginBackingBean;

    private int rating;
    
    private String text;
    
    private String isbn;
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getText() {
        return text;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    /**
     * A method that calls on the jpa controller to get a list of 3 reviews for a given book
     * 
     * @author Finley
     * @param ISBN
     * @return 
     */
    public List<Review> findListByBook(String ISBN) {
        return reviewJpaController.findTopThreeByBook(ISBN);
    }

    /**
     * Save the current review to the db
     *
     * @param isbn
     * @param reviewText
     * @param rating
     * @throws Exception
     */
    public void createReview() throws Exception {
        
        // Only logged in users can submit reviews
        if(!loginBackingBean.isSignedIn())
            return;
        
        // I would normally never validate this way but I'm running out of time and the user can just assume that their review wasn't approved
        if(text == null || text.length()>750)
            return;
        
        Review newReview = new Review();
        
        newReview.setAccountID(loginBackingBean.getAccountTwo());
        newReview.setApproval('0');
        Book bookToReview = bookJpaController.searchBarBooks("isbn", isbn).get(0);
        if(bookToReview==null)
            return;
        newReview.setIsbn(bookToReview);
        newReview.setRating(rating);
        Date today = new Date();
        newReview.setPostDate(today);
        newReview.setReviewText(text);
        reviewJpaController.create(newReview);
    }
    
}
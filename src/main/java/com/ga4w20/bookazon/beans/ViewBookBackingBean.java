package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Book;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Backing Bean for the selected book to view to display inside of book.xhtml
 * @author Grant
 */
@Named
@SessionScoped
public class ViewBookBackingBean implements Serializable{
    private Book selectedBook;
    private boolean onSale = false;

    public Book getSelectedBook(){
        return this.selectedBook;
    }
    
    public void setSelectedBook(Book selectedBook){
        this.selectedBook = selectedBook;
        if(this.selectedBook.getSalePrice() > 0){
            this.setOnSale(true);
        } else{
            this.setOnSale(false);
        }
    }
    
    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

}

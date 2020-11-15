package com.ga4w20.bookazon.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bellerive
 * @author Lin Yang
 */
@Named
@SessionScoped
public class ManagerViewBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerViewBackingBean.class);

    private Boolean activatedInventory = true;
    private Boolean deactivatedInventory = false;
    private Boolean addBook = false;
    private Boolean reviews = false;
    private Boolean rss = false;
    private Boolean zeroSales = false;
    private Boolean ads = false;
    private Boolean clients = false;
    private Boolean topClients = false;
    private Boolean topSellers = false;
    private Boolean salesByPubliser = false;
    private Boolean surveys = false;
    private Boolean answers = false;
    private Boolean order = false;
        
    public Boolean getSurveys() {
        return surveys;
    }

    public void setSurveys(Boolean surveys) {
        this.surveys = surveys;
    }

    public Boolean getAnswers() {
        return answers;
    }

    public void setAnswers(Boolean answers) {
        this.answers = answers;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }


    public Boolean getActivatedInventory() {
        return activatedInventory;
    }

    public void setActivatedInventory(Boolean activatedInventory) {
        this.activatedInventory = activatedInventory;
    }

    public Boolean getDeactivatedInventory() {
        return deactivatedInventory;
    }

    public void setDeactivatedInventory(Boolean deactivatedInventory) {
        this.deactivatedInventory = deactivatedInventory;
    }

    public Boolean getAddBook() {
        return addBook;
    }

    public void setAddBook(Boolean addBook) {
        this.addBook = addBook;
    }

    public Boolean getReviews() {
        return reviews;
    }

    public void setReviews(Boolean reviews) {
        this.reviews = reviews;
    }

    public Boolean getRss() {
        return rss;
    }

    public void setRss(Boolean rss) {
        this.rss = rss;
    }

    public Boolean getZeroSales() {
        return zeroSales;
    }

    public void setZeroSales(Boolean zeroSales) {
        this.zeroSales = zeroSales;
    }

    public Boolean getAds() {
        return ads;
    }

    public void setAds(Boolean ads) {
        this.ads = ads;
    }

    public Boolean getClients() {
        return clients;
    }

    public void setClients(Boolean clients) {
        this.clients = clients;
    }

    public Boolean getTopClients() {
        return topClients;
    }

    public void setTopClients(Boolean topClients) {
        this.topClients = topClients;
    }

    public Boolean getTopSellers() {
        return topSellers;
    }

    public void setTopSellers(Boolean topSellers) {
        this.topSellers = topSellers;
    }

    public Boolean getSalesByPubliser() {
        return salesByPubliser;
    }

    public void setSalesByPubliser(Boolean salesByPubliser) {
        this.salesByPubliser = salesByPubliser;
    }

    /**
     * All the methods below will toggle the boolean that manage which view
     * Is going to be displayed at a specific moment in the manager site
     * @author Alex Bellerive
     * @param event 
     */
    public void viewActivatedInventory(AjaxBehaviorEvent event) {
        activatedInventory = true;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        
        LOG.info("Showing activated inventroy");
    }

    public void viewDeactivatedInventory(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = true;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing deactivated inventory");

    }

    public void viewAddBook(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = true;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing add books");

    }

    public void viewReviews(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = true;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing reviews");

    }

    public void viewRss(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = true;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing rss");

    }

    public void viewZeroSales(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = true;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing zero sales");

    }

    public void viewAds(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = true;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing ads");

    }

    public void viewClients(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = true;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing clients");
    }

    public void viewTopClients(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = true;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing Top Clients ");
    }

    public void viewTopSales(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = true;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing Top Sales ");
    }

    public void viewSalesByPublisher(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = true;
        surveys = false;
        answers = false;
        order = false;
        LOG.info("Showing Sales By Publisher");
    }
    
    public void viewSurveys(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = true;
        answers = false;
        order = false;
        LOG.info("Showing Survey");
    }
    
    public void viewAnswers(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = true;
        order = false;
        LOG.info("Showing Answers");
    }
    
    public void viewOrder(AjaxBehaviorEvent event) {
        activatedInventory = false;
        deactivatedInventory = false;
        addBook = false;
        reviews = false;
        rss = false;
        zeroSales = false;
        ads = false;
        clients = false;
        topClients = false;
        topSellers = false;
        salesByPubliser = false;
        surveys = false;
        answers = false;
        order = true;
        LOG.info("Showing Orders");
    }

}

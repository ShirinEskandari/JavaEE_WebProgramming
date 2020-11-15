package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Ads;
import com.ga4w20.bookazon.persistence.AdsJpaController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean used to store ads from the database for display in index.xhtml
 * @author finley
 */
@Named
@RequestScoped
public class AdsBackingBean implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(AdsBackingBean.class);

    @Inject
    private AdsJpaController adsJc;
    private Ads horizontalAd;
    private Ads squareAd;
    private int adType;
    private String adImage;
    private String adRedirect;

    @PostConstruct
    public void init() {
        List<Ads> horizontalAds = this.adsJc.findActiveHorizontalAd();
        List<Ads> squareAds = this.adsJc.findActiveSquareAd();
        if (!horizontalAds.isEmpty()) {
            this.horizontalAd = horizontalAds.get(0);
        }
        if (!squareAds.isEmpty()) {
            this.squareAd = squareAds.get(0);
        }
    }

    public void createAd(){
        Ads ad = new Ads();
        //0 is rectangle, 1 is qaure
        ad.setAdType(this.adType);
        ad.setImage(this.adImage);
        ad.setRedirect(this.adRedirect);
        //Default to 0
        ad.setActiveStatus(0);
        
        this.adsJc.create(ad);
    }
    
    public Ads getHorizontalAd() {
        return horizontalAd;
    }

    public Ads getSquareAd() {
        return squareAd;
    }

    public void setHorizontalAd(Ads horizontalAd) {
        this.horizontalAd = horizontalAd;
    }

    public void setSquareAd(Ads squareAd) {
        this.squareAd = squareAd;
    }
    
    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public String getAdImage() {
        return adImage;
    }

    public void setAdImage(String adImage) {
        this.adImage = adImage;
    }

    public String getAdRedirect() {
        return adRedirect;
    }

    public void setAdRedirect(String adRedirect) {
        this.adRedirect = adRedirect;
    }
}

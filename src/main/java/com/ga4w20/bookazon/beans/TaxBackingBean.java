
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.persistence.TaxJpaController;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *  Backing Bean used for taxes given the user's province information once he is logged in.
 * @author Grant & Alex
 */
@Named
@SessionScoped
public class TaxBackingBean implements Serializable{
    
    @Inject
    private TaxJpaController tjc;
    private String province;
    private double gst;
    private double hst;
    private double pst;
    
    @PostConstruct
    public void init(){
        //Default province if ever a user is not logged in.
        this.province = "QC";
        //The following values are in %
        this.gst = 5;
        this.pst = 10;
        this.hst = 0;
    }
    
    public void setTaxes(String province){
        this.setProvince(province);
        this.setGST(this.tjc.getTaxes(this.province).getGst());
        this.setHST(this.tjc.getTaxes(this.province).getHst());
        this.setPST(this.tjc.getTaxes(this.province).getPst());
    }
    
    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getGST() {
        return this.gst;
    }

    public void setGST(int gst) {
        this.gst = gst;
    }

    public double getHST() {
        return this.hst;
    }

    public void setHST(int hst) {
        this.hst = hst;
    }

    public double getPST() {
        return this.pst;
    }

    public void setPST(double pst) {
        this.pst = pst;
    }
    
}

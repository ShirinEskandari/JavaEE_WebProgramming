/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.persistence.AccountJpaController;
import com.ga4w20.bookazon.persistence.exceptions.IllegalOrphanException;
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
@Named
@SessionScoped
public class EditUserBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(EditUserBackingBean.class);

    @Inject
    private AccountJpaController accountJpaController;

    private Account account;

    private String email;
    private String fname;
    private String lname;
    private String address;
    private String street;
    private String province;
    private String postalCode;
    private String city;
    private String telephone;
    private String cellphone;
    private String companyName;
    private String managerStatus;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getEmail() {
        return "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return "";
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return "";
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet() {
        return "";
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getProvince() {
        return "";
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return "";
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return "";
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return "";
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCellphone() {
        return "";
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCompanyName() {
        return "";
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getManagerStatus() {
        return managerStatus;
    }

    public void setManagerStatus(String managerStatus) {
        this.managerStatus = managerStatus;
    }

    public Boolean isManager() {
        return this.account.getManagerStatus() == '1';
    }

    /**
     * This method validates and logs all the info that the manager edited.
     * Once it checks all the fields the manager could've edited it calls
     * the controller and edits the entry
     * @throws IllegalOrphanException
     * @author Alex Bellerive
     */
    public void editAccount() throws IllegalOrphanException {
        if (!this.email.isEmpty() && !this.email.isBlank()) {
            LOG.info("Changing " + this.account.getEmail() + " to " + this.email);
            this.account.setEmail(this.email.trim());
        }
        if (!this.fname.isEmpty() && !this.fname.isBlank()) {
            LOG.info("Changing " + this.account.getFName() + " to " + this.fname);
            this.account.setFName(this.fname.trim());
        }
        if (!this.lname.isEmpty() && !this.lname.isBlank()) {
            LOG.info("Changing " + this.account.getLName() + " to " + this.lname);
            this.account.setLName(this.lname.trim());
        }
        if (!this.address.isEmpty() && !this.address.isBlank()) {
            LOG.info("Changing " + this.account.getAddress() + " to " + this.address);
            this.account.setAddress(this.address.trim());
        }
        if (!this.street.isEmpty() && !this.street.isBlank()) {
            LOG.info("Changing " + this.account.getStreet() + " to " + this.street);
            this.account.setStreet(this.street.trim());
        }
        if (!this.province.equals("default")) {
            LOG.info("Changing " + this.account.getProvince() + " to " + this.province);
            this.account.setProvince(this.province.trim());
        }
        if (!this.postalCode.isEmpty() && !this.postalCode.isBlank()) {
            LOG.info("Changing " + this.account.getPostalCode() + " to " + this.postalCode);
            this.account.setPostalCode(this.postalCode.trim());
        }
        if (!this.city.isEmpty() && !this.city.isBlank()) {
            LOG.info("Changing " + this.account.getCity() + " to " + this.city);
            this.account.setCity(this.city.trim());
        }
        if (!this.telephone.isEmpty() && !this.telephone.isBlank()) {
            LOG.info("Changing " + this.account.getTelephone() + " to " + this.telephone);
            this.account.setTelephone(this.telephone.trim());
        }
        if (!this.cellphone.isEmpty() && !this.cellphone.isBlank()) {
            LOG.info("Changing " + this.account.getCellphone() + " to " + this.cellphone);
            this.account.setCellphone(this.cellphone.trim());
        }
        if (!this.companyName.isEmpty() && !this.companyName.isBlank()) {
            LOG.info("Changing " + this.account.getCompanyName() + " to " + this.companyName);
            this.account.setCompanyName(this.companyName.trim());
        }
        if (!this.managerStatus.equals("default")) {
            LOG.info("Changing " + this.account.getManagerStatus() + " to " + this.managerStatus);
            this.account.setManagerStatus(this.managerStatus.toCharArray()[0]);
        }
        accountJpaController.edit(this.account);
    }

}

package com.ga4w20.bookazon.entities;

import com.ga4w20.bookazon.validators.ValidEmail;
import com.ga4w20.bookazon.validators.ValidPhone;
import com.ga4w20.bookazon.validators.ValidPostalCode;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Shirin
 */
@Entity
@Table(name = "account", catalog = "ga4w20", schema = "")
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
    @NamedQuery(name = "Account.findByAccountID", query = "SELECT a FROM Account a WHERE a.accountID = :accountID"),
    @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(name = "Account.findByFName", query = "SELECT a FROM Account a WHERE a.fName = :fName"),
    @NamedQuery(name = "Account.findByLName", query = "SELECT a FROM Account a WHERE a.lName = :lName"),
    @NamedQuery(name = "Account.findByHashedPassword", query = "SELECT a FROM Account a WHERE a.hashedPassword = :hashedPassword"),
    @NamedQuery(name = "Account.findBySalt", query = "SELECT a FROM Account a WHERE a.salt = :salt"),
    @NamedQuery(name = "Account.findByAddress", query = "SELECT a FROM Account a WHERE a.address = :address"),
    @NamedQuery(name = "Account.findByStreet", query = "SELECT a FROM Account a WHERE a.street = :street"),
    @NamedQuery(name = "Account.findByProvince", query = "SELECT a FROM Account a WHERE a.province = :province"),
    @NamedQuery(name = "Account.findByPostalCode", query = "SELECT a FROM Account a WHERE a.postalCode = :postalCode"),
    @NamedQuery(name = "Account.findByCity", query = "SELECT a FROM Account a WHERE a.city = :city"),
    @NamedQuery(name = "Account.findByTelephone", query = "SELECT a FROM Account a WHERE a.telephone = :telephone"),
    @NamedQuery(name = "Account.findByCellphone", query = "SELECT a FROM Account a WHERE a.cellphone = :cellphone"),
    @NamedQuery(name = "Account.findByCompanyName", query = "SELECT a FROM Account a WHERE a.companyName = :companyName"),
    @NamedQuery(name = "Account.findByManagerStatus", query = "SELECT a FROM Account a WHERE a.managerStatus = :managerStatus")})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "AccountID")
    private Integer accountID;

    @Basic(optional = false)
    @NotNull
    @Size
    @Column(name = "Email")
    @ValidEmail
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size( message = "{com.ga4w20.bookazon.nameMsg}")
    @Column(name = "FName")
    private String fName;
    @Basic(optional = false)
    @NotNull
    @Size( message = "{com.ga4w20.bookazon.lnameMsg}")
    @Column(name = "LName")
    private String lName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 5, message = "{com.ga4w20.bookazon.errorPass}")
    @Column(name = "HashedPassword")
    private String hashedPassword;
    @Basic(optional = false)
    @NotNull
    @Size
    @Column(name = "Salt")
    private String salt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100, message = "{com.ga4w20.bookazon.addressMsg}")
    @Column(name = "Address")
    private String address;
    @Basic(optional = true)
    @NotNull
    @Size( min = 1,message = "{com.ga4w20.bookazon.streetMsg}")
    @Column(name = "Street")
    private String street;
    @Basic(optional = false)
    @NotNull
    @Size( min = 1, max = 2,message = "{com.ga4w20.bookazon.provanceMsg}")
    @Column(name = "Province")
    private String province;
    @Basic(optional = false)
    @NotNull
    @Size( min = 6,message ="{com.ga4w20.bookazon.invalidpostal}")
    @Column(name = "PostalCode")
    @ValidPostalCode
    private String postalCode;
    @Basic(optional = false)
    @NotNull
    @Size( min = 1, max = 40, message = "{com.ga4w20.bookazon.cityMsg}")
    @Column(name = "City")
    private String city;
    @Basic(optional = false)
    @NotNull

    @Size
    @Column(name = "Telephone")
    @ValidPhone
    private String telephone;
    @Basic(optional = false)
    @NotNull
    @Size( min = 10, max = 12,message = "{com.ga4w20.bookazon.telephoneeMsg}")
    @Column(name = "Cellphone")
    private String cellphone;
    @Basic(optional = false)
    @NotNull
    @Size( min = 1, max = 40,message = "{com.ga4w20.bookazon.companyyMsg}")
    @Column(name = "CompanyName")
    private String companyName;
    @Column(name = "ManagerStatus")
    private Character managerStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountID")
    private List<Sale> saleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountID")
    private List<Review> reviewList;

    public Account() {
    }

    public Account(Integer accountID) {
        this.accountID = accountID;
    }

    public Account(Integer accountID, String email, String fName, String lName, String hashedPassword, String salt, String address, String street, String province, String postalCode, String city, String telephone, String cellphone, String companyName) {
        this.accountID = accountID;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.address = address;
        this.street = street;
        this.province = province;
        this.postalCode = postalCode;
        this.city = city;
        this.telephone = telephone;
        this.cellphone = cellphone;
        this.companyName = companyName;
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Character getManagerStatus() {
        return managerStatus;
    }

    public void setManagerStatus(Character managerStatus) {
        this.managerStatus = managerStatus;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountID != null ? accountID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.accountID == null && other.accountID != null) || (this.accountID != null && !this.accountID.equals(other.accountID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ga4w20.bookazon.persistence.Account[ accountID=" + accountID + " ]";
    }

}

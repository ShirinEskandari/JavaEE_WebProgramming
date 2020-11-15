package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.persistence.AccountJpaController;
import java.io.Serializable;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class helps for the precess of login of manager or Client
 *
 * @author Shirin 04/2020
 */
@Named("loginBackingBean")
@SessionScoped
public class LoginBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(LoginBackingBean.class);

    @Inject
    private AccountJpaController accountJpaController;
    private Account account;
    private Account account2;

    public Account getAccount() {
        if (account == null) {
            account = new Account();
        }
        return account;
    }

    public Account getAccountTwo() {
        return this.account2;
    }

    /**
     * This method uses for login of already exist account(client) checks if
     * email exist in the database then checks if password is equal of hash+salt
     * on base of the user if he is a manager redirects to manager page, if he
     * is a client redirects to index page if he is not registered yet,
     * redirects to registration page
     *
     * @author Shirin
     * @return
     * @throws InvalidKeySpecException
     */
    public String submit() throws InvalidKeySpecException {
        account2 = accountJpaController.findAccountByEmail(account.getEmail());
        if (accountJpaController.findAccountByEmail(account.getEmail()) != null && login(account.getHashedPassword(), account2) == true) {
            account = account2;
            if (account.getManagerStatus() == '0' || account.getManagerStatus() == null) {
                return "index?faces-redirect=true";

            } else {
                return "manager?faces-redirect=true";

            }
        } else {
            account = null;
            return "registration?faces-redirect=true";

        }
    }

    /**
     * checks if an account is a manager or normal client
     *
     * @author Shirin
     * @return
     */
    public boolean isManager() {
        return this.account2.getManagerStatus().equals('1');
    }

    /**
     * checks if client signed in or not
     *
     * @author Shirin
     * @return
     */
    public boolean isSignedIn() {
        return account2 != null;
    }

    /**
     * takes out all sessions then no page would recognize this user
     *
     * @author Shirin
     * @return
     */
    public String signOut() {
        // End the session, removing any session state
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // Redirect is necessary to let the browser make a new GET request
        return "index?faces-redirect=true";
    }

    /**
     * it is a helper method for compare a password with salt it helps the
     * methods inside AccountJpaController it gets a password, finds user with
     * the email address, then gets salt string from the database and again make
     * another hash password with help of this salt and just entered password
     * compares with hashed password that exist in the database, if there is a
     * match then login
     *
     * @author Shirin
     * @param inputpass
     *  * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeySpecException
     * @throws InvalidKeySpecException
     */
    public boolean login(String inputpass, Account account2) throws InvalidKeySpecException, InvalidKeySpecException, InvalidKeySpecException {

        String salt = "";
        String password_hash = null;
        boolean valid = true;

        salt = account2.getSalt();
        password_hash = account2.getHashedPassword();
        byte[] comparehash = accountJpaController.generateHash(inputpass, salt);
        String s = Base64.getEncoder().encodeToString(comparehash);
        if (s.equals(password_hash)) {
            valid = true;
        } else {
            valid = false;
        }

        return valid;
    }

}

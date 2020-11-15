package com.ga4w20.bookazon.beans;

import com.ga4w20.bookazon.entities.Account;
import com.ga4w20.bookazon.persistence.AccountJpaController;
import java.io.Serializable;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean for Account that create an Account for the client or manager,
 * make hash and salt password use for registration and Login pages
 * (registration.xhtml , login.xhtml)
 *
 * @author Shirin
 */
@Named("accountBackingBean")
@SessionScoped
public class AccountBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AccountBackingBean.class);

    @Inject
    private AccountJpaController accountJpaController;

    private Account account;

    public Account getAccount() {
        if (account == null) {
            account = new Account();
        }
        return account;
    }

    /**
     * This method checks if email address does not exist inside the database,
     * it allows to registration and create an account
     *
     * @author shirin
     * @return
     * @throws InvalidKeySpecException
     */
    public String save() throws InvalidKeySpecException {
        if (accountJpaController.findAccountByEmail(account.getEmail()) != null) {
            return "registration?faces-redirect=true";
        }
        signup();
        accountJpaController.create(account);

        return "login?faces-redirect-true";
    }

    /**
     * This method uses methods inside AccountJpaController for make hash and
     * salt for the password that client entered
     *
     * @author Shirin
     * @throws InvalidKeySpecException
     */
    public void signup() throws InvalidKeySpecException {
        String saltedPassword = accountJpaController.getSalt2();
        byte[] hashedPassword2 = accountJpaController.generateHash(account.getHashedPassword(), saltedPassword);
        account.setSalt(saltedPassword);
        String s = Base64.getEncoder().encodeToString(hashedPassword2);
        account.setHashedPassword(s);
    }

}

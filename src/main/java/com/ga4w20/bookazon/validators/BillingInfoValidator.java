package com.ga4w20.bookazon.validators;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

/**
 * Validator used to validate the billing form in the checkout.xhtml
 *
 * @author Grant
 */
@Named("payment")
@RequestScoped
public class BillingInfoValidator implements Serializable {

    @Size(min = 16, max = 16)
    private String cardNumber = "";
    private String cardType = "";
    @Size(min = 3, max = 3)
    private String cardCVC = "";
    @Future
    private Date cardExp = new Date();
    private String firstNameOnCard;
    private String lastNameOnCard;
    private String billAddress;

    public void validateName(FacesContext context, UIComponent component, Object value) {
        UIInput cnInput = (UIInput) component.findComponent("fNameOnCard");
        String fName = String.valueOf(cnInput.getSubmittedValue());
        
        cnInput = (UIInput) component.findComponent("lNameOnCard");
        String lName = String.valueOf(cnInput.getSubmittedValue());
        
        cnInput = (UIInput) component.findComponent("billAddress");
        String billAdd = String.valueOf(cnInput.getSubmittedValue());
        
        if(fName.isEmpty() || lName.isEmpty() || billAdd.isEmpty()){
            FacesMessage message = com.ga4w20.util.Messages.getMessage(
                    "com.ga4w20.bookazon.bundles.messages", "valueIsRequired", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        if (fName.matches(".*[0-9].*") || lName.matches(".*[0-9].*")) {
            FacesMessage message = com.ga4w20.util.Messages.getMessage(
                    "com.ga4w20.bookazon.bundles.messages", "invalidName", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void validateCardNum(FacesContext context, UIComponent component, Object value) {
        UIInput cnInput = (UIInput) component.findComponent("cardNumber");
        String cardNum = String.valueOf(cnInput.getSubmittedValue());
        if (cardNum == null || cardNum.length() < 16 || cardNum.length() > 16) {
            FacesMessage message = com.ga4w20.util.Messages.getMessage(
                    "com.ga4w20.bookazon.bundles.messages", "invalidCardNumLength", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        } else if (cardNum.length() == 16) {
            char firstNumOnCard = cardNum.charAt(0);
            UIInput ctInput = (UIInput) component.findComponent("cardType");
            String cType = String.valueOf(ctInput.getValue());

            if (cardNum.matches(".*[a-zA-Z].*")) {
                FacesMessage message = com.ga4w20.util.Messages.getMessage(
                        "com.ga4w20.bookazon.bundles.messages", "invalidCardNum", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }

            if ((cType.equals("Visa") && firstNumOnCard != '4') || (cType.equals("Mastercard") && firstNumOnCard != '5')) {
                FacesMessage message = com.ga4w20.util.Messages.getMessage(
                        "com.ga4w20.bookazon.bundles.messages", "invalidFirstChar", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public void validateCardType(FacesContext context, UIComponent component, Object value) {
        UIInput ctInput = (UIInput) component.findComponent("cardType");

        String cType = String.valueOf(ctInput.getSubmittedValue());

        if (cType == null) {
            FacesMessage message = com.ga4w20.util.Messages.getMessage(
                    "com.ga4w20.bookazon.bundles.messages", "invalidCardTypeError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        } else {
            if (!cType.equals("Visa") && !cType.equals("Mastercard")) {
                FacesMessage message = com.ga4w20.util.Messages.getMessage(
                        "com.ga4w20.bookazon.bundles.messages", "invalidCardType", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public void validateCardCVC(FacesContext context, UIComponent component, Object value) {
        UIInput cardcvcInput = (UIInput) component.findComponent("cardCVC");

        String cvc = String.valueOf(cardcvcInput.getSubmittedValue());
        if (cvc == null) {
            FacesMessage message = com.ga4w20.util.Messages.getMessage(
                    "com.ga4w20.bookazon.bundles.messages", "invalidCardCVC", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        } else {
            if (cvc.matches(".*[a-zA-Z].*")) {
                FacesMessage message = com.ga4w20.util.Messages.getMessage(
                        "com.ga4w20.bookazon.bundles.messages", "invalidCardCVCDigits", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Date getCardExp() {
        return this.cardExp;
    }

    public void setCardExp(Date date) {
        this.cardExp = date;
    }

    public String getCardCVC() {
        return cardCVC;
    }

    public void setCardCVC(String cardCVC) {
        this.cardCVC = cardCVC;
    }

    public String getFirstNameOnCard() {
        return firstNameOnCard;
    }

    public void setFirstNameOnCard(String fNameOnCard) {
        this.firstNameOnCard = fNameOnCard;
    }

    public String getLastNameOnCard() {
        return lastNameOnCard;
    }

    public void setLastNameOnCard(String lNameOnCard) {
        this.lastNameOnCard = lNameOnCard;
    }

    public String getBillAddress() {
        return this.billAddress;
    }

    public void setBillAddress(String billAddress) {
        this.billAddress = billAddress;
    }

}

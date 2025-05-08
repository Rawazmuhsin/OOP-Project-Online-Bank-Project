package bank.pr;

import java.awt.Color;

public class PhoneCard extends CardBase {
    private String operator;
    
    public PhoneCard(int cardId, String operator, double balance, String holderName, String status) {
        super(cardId, operator, balance, holderName, status);
        this.operator = operator;
    }
    
    @Override
    public String getTypeDescription() {
        return "Phone Card - " + operator;
    }
    
    @Override
    public Color getCardColor() {
        return new Color(178, 34, 34); // Red
    }
    
    @Override
    public String getCardType() {
        return "PHONE";
    }
    
    public String getOperator() {
        return operator;
    }
}
package bank.pr;

import java.awt.Color;

public abstract class CardBase {
    protected int cardId;
    protected String cardName;
    protected double balance;
    protected String holderName;
    protected String status;
    
    public CardBase(int cardId, String cardName, double balance, String holderName, String status) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.balance = balance;
        this.holderName = holderName;
        this.status = status;
    }
    
    public abstract String getTypeDescription();
    public abstract Color getCardColor();
    public abstract String getCardType();
    
    public int getCardId() {
        return cardId;
    }
    
    public String getCardName() {
        return cardName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public String getHolderName() {
        return holderName;
    }
    
    public String getStatus() {
        return status;
    }
}





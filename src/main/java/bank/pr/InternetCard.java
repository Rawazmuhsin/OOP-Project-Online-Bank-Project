package bank.pr;

import java.awt.Color;

public class InternetCard extends CardBase {
    private String provider;
    
    public InternetCard(int cardId, String provider, double balance, String holderName, String status) {
        super(cardId, provider, balance, holderName, status);
        this.provider = provider;
    }
    
    @Override
    public String getTypeDescription() {
        return "Internet Card - " + provider;
    }
    
    @Override
    public Color getCardColor() {
        return new Color(72, 61, 139); // Purple
    }
    
    @Override
    public String getCardType() {
        return "INTERNET";
    }
    
    public String getProvider() {
        return provider;
    }
}
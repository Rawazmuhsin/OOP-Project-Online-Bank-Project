package bank.pr;

import java.awt.Color;

public class OnlineCard extends CardBase {
    private String provider;
    
    public OnlineCard(int cardId, String provider, double balance, String holderName, String status) {
        super(cardId, provider, balance, holderName, status);
        this.provider = provider;
    }
    
    @Override
    public String getTypeDescription() {
        return "Online Card - " + provider;
    }
    
    @Override
    public Color getCardColor() {
        return new Color(46, 139, 87); // Green
    }
    
    @Override
    public String getCardType() {
        return "ONLINE";
    }
    
    public String getProvider() {
        return provider;
    }
}

package sma;

/**
 *
 * @author Mélanie
 * @author Ophélie
 */
public abstract class Message {

    protected Agent emetteur;
    protected Agent destinataire;
    
    /**
     * Inform, request, ...
     */
    protected String performatif;
    
    /**
     * Accept, Refuse, 
     */
    protected String action;

    public String getPerformatif() {
        return performatif;
    }

    public void setPerformatif(String performatif) {
        this.performatif = performatif;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Agent getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Agent emetteur) {
        this.emetteur = emetteur;
    }

    public Agent getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Agent destinataire) {
        this.destinataire = destinataire;
    }
}

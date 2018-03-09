
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
    protected IPerformatif performatif;
    
    /**
     * Accept, Refuse, ...
     */
    protected IAction action;

    public IPerformatif getPerformatif() {
        return performatif;
    }

    public void setPerformatif(IPerformatif performatif) {
        this.performatif = performatif;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
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

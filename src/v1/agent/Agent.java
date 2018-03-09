
package v1.agent;

import java.util.HashMap;
import java.util.Map;
import v1.message.Message;
import v1.model.Negociation;
import v1.message.Action;
import v1.view.Dialog;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public abstract class Agent extends sma.Agent {
    protected Map<Integer, Negociation> negociations;
    protected Dialog dialogView;
    protected int id;
    
    /**
     * Constructeur Agent
     * @param id
     */
    public Agent(int id) {
        super();
        this.negociations = new HashMap<>();
        this.id = id;
    }

    public int getIdAgent() {
        return id;
    }

    public void setIdAgent(int id) {
        this.id = id;
    }
    
    @Override
    protected void sendMessage(sma.Message message) {
        if (message instanceof Message) {
            message = (Message) message;
            Agent destinataire = (Agent) message.getDestinataire();
            Agent emetteur = (Agent) message.getEmetteur();
            destinataire.addMessage(message);
            emetteur.dialogView.addDialogLine(emetteur.getName(), "Envoi du message: \n" + message.toString());
        }
    }
    
    /**
     * Soumission d'un nouveau prix.
     *
     * @param prix
     * @param emetteur
     * @param destinataire
     * @param negociation 
     */
    protected void reponsePropositionPrix(float prix, Agent emetteur, Agent destinataire, Negociation negociation) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, emetteur, destinataire, negociation);
        negociation.getVoeu().setPrix(prix);
        negociation.incrementeNbEchanges();

        sendMessage(message);
    }
    
    /**
     * Acceptation ou refus à un appel d'offre.
     *
     * @param ok
     * @param destinataire
     * @param negociation 
     */
    protected void reponseProposition(boolean ok, Agent destinataire, Negociation negociation) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
 
        if (ok) {
            message.setAction(Action.ACCEPTATION);
        } else {
            message.setAction(Action.REFUS);
        }

        sendMessage(message);
    }
}

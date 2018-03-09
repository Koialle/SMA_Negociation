
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
            ((Message) message).getNegociation().incrementeStep();
            Agent destinataire = (Agent) message.getDestinataire();
            destinataire.addMessage(message);
            displayMessageEnvoye(message);
        }
    }
    
//    /**
//     * Soumission d'un nouveau prix.
//     *
//     * @param prix
//     * @param emetteur
//     * @param destinataire
//     * @param negociation 
//     */
//    protected void reponsePropositionPrix(float prix, Agent emetteur, Agent destinataire, Negociation negociation) {
//        Message message = new Message(Message.Type.PROPOSITION_PRIX, emetteur, destinataire, negociation);
//        negociation.getVoeu().setPrix(prix);
//        negociation.incrementeNbEchanges();
//
//        sendMessage(message);
//    }
    
//    /**
//     * Acceptation ou refus à un appel d'offre.
//     *
//     * @param ok
//     * @param destinataire
//     * @param negociation 
//     */
//    protected void reponseProposition(boolean ok, Agent destinataire, Negociation negociation) {
//        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
// 
//        if (ok) {
//            message.setAction(Action.ACCEPTATION);
//        } else {
//            message.setAction(Action.REFUS);
//        }
//        
//        if (negociation.getProposition() == null) {
//            message.setMessage("Plus de proposition de disponible pour ce service");
//        }
//
//        sendMessage(message);
//    }
    
    protected void envoieAccordProposition(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.APPEL_OFFRE, this, destinataire, negociation);
        message.setAction(Action.ACCEPTATION);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieRefusProposition(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.APPEL_OFFRE, this, destinataire, negociation);
        message.setAction(Action.REFUS);
        message.setMessage(raison);

        sendMessage(message);
    }

    protected void envoieAccordPrix(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        message.setAction(Action.ACCEPTATION);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieRefusPrix(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        message.setAction(Action.REFUS);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieNouveauPrix(float prix, Agent destinataire, Negociation negociation, String detailPrix) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        
        if (destinataire instanceof Negociateur) {
            negociation.getProposition().setPrix(prix);
        } else if (destinataire instanceof Fournisseur) {
            negociation.getVoeu().setPrix(prix);
        }

        negociation.incrementeNbEchanges();
        message.setMessage(detailPrix);

        sendMessage(message);
    }

    protected void displayMessageReçu(sma.Message message) {
        dialogView.addDialogLine(message.getEmetteur().getName(), message.toString());
    }

    protected void displayMessageEnvoye(sma.Message message) {
        Agent emetteur = (Agent) message.getEmetteur();
        emetteur.dialogView.addDialogLine(emetteur.getName(), message.toString());
    }
}

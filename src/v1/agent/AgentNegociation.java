
package v1.agent;

import java.util.HashMap;
import java.util.Map;
import sma.Agent;
import sma.Message;
import v1.MessageNegociation;
import v1.Negociation;
import v1.view.Dialog;

/**
 *
 * @author Ophélie EOUZAN
 */
public abstract class AgentNegociation extends Agent {
    protected Map<Integer, Negociation> negociations;
    protected Dialog dialogView;
    protected int id;
    
    /**
     * Constructeur Agent
     * @param id
     */
    public AgentNegociation(int id) {
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
    protected void sendMessage(Message message) {
        if (message instanceof MessageNegociation) {
            message = (MessageNegociation) message;
            AgentNegociation destinataire = (AgentNegociation) message.getDestinataire();
            AgentNegociation emetteur = (AgentNegociation) message.getEmetteur();
            destinataire.addMessage(message);
            emetteur.dialogView.addDialogLine(emetteur.getName(), "Envoi du message: \n" + message.toString());
        }
    }
    
    protected void reponsePropositionPrix(float prix, AgentNegociation emetteur, AgentNegociation destinataire, Negociation negociation) {
        MessageNegociation message;
        if (emetteur instanceof Negociateur) {
            message = new MessageNegociation(MessageNegociation.PERFORMATIF_PROPOSITION, MessageNegociation.ACTION_PRIX_NEGOCIATEUR);
        } else {
            message = new MessageNegociation(MessageNegociation.PERFORMATIF_PROPOSITION, MessageNegociation.ACTION_PRIX_FOURNISSEUR);
        }

        message.setEmetteur(emetteur);
        message.setDestinataire(destinataire);
        message.setNegociation(negociation);

        negociation.getVoeu().setPrix(prix);
        negociation.incrementeNbEchanges();

        sendMessage(message);
    }
    
    protected void reponseProposition(boolean ok, AgentNegociation destinataire, Negociation negociation) {
        MessageNegociation reponse;

        if (ok) {
            reponse = new MessageNegociation(MessageNegociation.PERFORMATIF_PROPOSITION, MessageNegociation.ACTION_ACCEPTED_NEGOCIATION);
        } else {
            reponse = new MessageNegociation(MessageNegociation.PERFORMATIF_PROPOSITION, MessageNegociation.ACTION_REFUSED_NEGOCIATION);
        }
        reponse.setEmetteur(this);
        reponse.setDestinataire(destinataire);
        reponse.setNegociation(negociation);

        sendMessage(reponse);
    }
}

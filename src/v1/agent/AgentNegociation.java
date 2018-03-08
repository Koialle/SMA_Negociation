
package v1.agent;

import java.util.HashMap;
import java.util.LinkedList;
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
    //protected String name;
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
    
//    public String getNameAgent() {
//        return name;
//    }
//
//    public void setNameAgent(String name) {
//        this.name = name;
//    }

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
            destinataire.dialogView.addDialogLine(destinataire.getName(), "Reçu message: \n"+message.toString());
            emetteur.dialogView.addDialogLine(emetteur.getName(), "Envoi du message: \n"+message.toString());
        }
    }
    
//    protected Negociation getNegociationByVoeu(Voeu voeu) {
//        for (Negociation negociation: negociations) {
//            if (voeu.equals(negociation.getVoeu())) {
//                return negociation;
//            }
//        }
//
//        return null;
//    }
//
//    protected Negociation getNegociationByProposition(Proposition proposition) {
//        for (Negociation negociation: negociations) {
//            if (proposition.equals(negociation.getProposition())) {
//                return negociation;
//            }
//        }
//
//        return null;
//    }
}

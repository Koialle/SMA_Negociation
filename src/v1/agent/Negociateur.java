
package v1.agent;

import sma.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import v1.MessageNegociation;
import v1.Negociation;
import v1.view.Dialog;
import v1.Voeu;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Negociateur extends AgentNegociation {
    private static int cptNegociateurs = 0;
    protected List<Voeu> voeux;
    protected List<Fournisseur> fournisseurs;

    public Negociateur() {
        super(cptNegociateurs++);
        setName("Negociateur n°"+id);
        voeux = new ArrayList();
        fournisseurs = new ArrayList();
        //name = "Negociateur n°"+id;
        dialogView = new Dialog(this);
    }

    @Override
    public void runAgent() {
        try {
            while (voeuxNonTraites() || getMessages().size() > 0) { // while (true)
                checkVoeux();
                checkAllMessages();
            }
        } catch (Exception ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean voeuxNonTraites() {
        for (Voeu voeu: voeux) {
            if (!voeu.isTraite()) {
                return true;
            }
        }

        return false;
    }

    private void checkVoeux() {
        for (Voeu voeu: voeux) {
            if (!voeu.isTraite()) {
                // Appel d'offre: à implémenter (2)
                
                // Creation de négociation
                for (Fournisseur fournisseur: fournisseurs) {
                    Negociation negociation = new Negociation(this, fournisseur, voeu);
                    MessageNegociation message = new MessageNegociation(this, fournisseur, negociation); 
                    sendMessage(message);

                    this.negociations.put(negociation.getIdentifiantNegociation(), negociation);
                }

                voeu.setTraite(true);
            }
        }
    }
    
    @Override
    protected void checkAllMessages() {
        MessageNegociation message = null;
        while (messages.size() > 0) {
            message = (MessageNegociation) messages.poll();
            if (message.getPerformatif().equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)) { // Appel d'offre
                if (message.getAction().equals(MessageNegociation.ACTION_ACCEPTED_OFFRE)) {
                    this.dialogView.addDialogLine(getName(), String.format("Début de la négociation(%d) avec le fournisseur %s", message.getNegociation().getIdentifiantNegociation(), message.getEmetteur().getName()));
                    
                } else if (message.getAction().equals(MessageNegociation.ACTION_REFUSED_OFFRE)) {
                    negociations.remove(message.getNegociation().getIdentifiantNegociation());
                    this.dialogView.addDialogLine(getName(), String.format("Fin de la conversation avec %s", message.getEmetteur().getName()));
                }
            } else if (message.getPerformatif().equals(MessageNegociation.PERFORMATIF_PROPOSITION)
                    && message.getAction().equals(MessageNegociation.ACTION_REQUEST)) {
                // @TODO
            }
        }
    }

    public List<Voeu> getVoeux() {
        return voeux;
    }

    public void setVoeux(List<Voeu> voeux) {
        this.voeux = voeux;
    }

    public void addVoeu(Voeu voeu) {
        this.voeux.add(voeu);
    }

    public List<Fournisseur> getFournisseurs() {
        return fournisseurs;
    }

    public void setFournisseurs(List<Fournisseur> fournisseurs) {
        this.fournisseurs = fournisseurs;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v1.agent;

import sma.Agent;
import java.util.ArrayList;
import java.util.List;
import sma.Message;
import v1.MessageNegociation;
import v1.Negociation;
import v1.Proposition;
import v1.Voeu;
import v1.view.Dialog;

/**
 *
 * @author Epulapp
 */
public class Fournisseur extends AgentNegociation {
    private static int cptFournisseurs = 0;
    protected List<Proposition> propositions;
    protected List<Negociateur> negociateurs;
    
    public Fournisseur() {
        super(cptFournisseurs++);
        setName("Fournisseur n°"+id);
        //name = "Fournisseur n°"+id;
        dialogView = new Dialog(this);
        negociateurs = new ArrayList<>();
    }

    @Override
    public void runAgent() {
        this.dialogView.addDialogLine(getName(), "Test");
        
        checkAllMessages();
    }

    @Override
    protected void checkAllMessages() {
        MessageNegociation message = null;
        while (messages.size() > 0) {
            message = (MessageNegociation) messages.poll();
            // Appel d'offre
            if (message.getPerformatif().equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)
                    && message.getAction().equals(MessageNegociation.ACTION_REQUEST)) {
                this.dialogView.addDialogLine(getName(), "Traitement de l'appel d'offre");
                Negociation negociation = ((MessageNegociation) message).getNegociation();
                List<Proposition> listePropositions = getPropositionsPossibles(negociation.getVoeu());
                this.dialogView.addDialogLine(getName(), String.format("%d offres trouvées", listePropositions.size()));

                reponseAppelOffre(listePropositions.size() > 0, negociation);
            } else if (message.getPerformatif().equals(MessageNegociation.PERFORMATIF_PROPOSITION)
                    && message.getAction().equals(MessageNegociation.ACTION_REQUEST)) {
                // @TODO
            }
        }
    }
    
    private void reponseAppelOffre(boolean ok, Negociation negociation) {
        MessageNegociation reponse;
        
        if (ok) {
            reponse = new MessageNegociation(MessageNegociation.PERFORMATIF_APPEL_OFFRE, MessageNegociation.ACTION_ACCEPTED_OFFRE);
        } else {
            reponse = new MessageNegociation(MessageNegociation.PERFORMATIF_APPEL_OFFRE, MessageNegociation.ACTION_REFUSED_OFFRE);
        }
        reponse.setEmetteur(this);
        reponse.setDestinataire(negociation.getNegociateur());
        reponse.setNegociation(negociation);

        sendMessage(reponse);
    }
    
    public void addProposition(Proposition proposition) {
        this.propositions.add(proposition);
    }

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<Proposition> propositions) {
        this.propositions = propositions;
    }

    public List<Negociateur> getNegociateurs() {
        return negociateurs;
    }

    public void setNegociateurs(List<Negociateur> negociateurs) {
        this.negociateurs = negociateurs;
    }

    private List<Proposition> getPropositionsPossibles(Voeu voeu) {
        List<Proposition> liste = new ArrayList();

        for (Proposition proposition: propositions) {
            //@TODO this in method equals of subclass Billet to create in Service
            if (voeu.getDepart() == null || voeu.getDepart().equals(proposition.getDepart())) {
                if (voeu.getArrivee() == null || voeu.getArrivee().equals(proposition.getArrivee())) {
                    if (voeu.getType() == null || voeu.getType().equals(proposition.getType())) {
                        if (voeu.getDateDepart() == 0 || voeu.getDateDepart() == proposition.getDateDepart()) {
                            if (voeu.getDateArrivee() == 0 || voeu.getDateArrivee() == proposition.getDateArrivee()) {
                                liste.add(proposition);
                            }
                        }
                    }
                }
            } 
        }
        
        return liste;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v1.agent;

import java.util.ArrayList;
import java.util.List;
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

    // @TODO gérer cas où fournisseur refuse, donc négociateur remonte son prix si possible
    // @TODO gérer cas où plusieurs fournisseurs proposer le même produit, choisir le prix minimal

    public Fournisseur() {
        super(cptFournisseurs++);
        setName("Fournisseur n°"+id);
        //name = "Fournisseur n°"+id;
        dialogView = new Dialog(this);
        negociateurs = new ArrayList();
        propositions = new ArrayList();
    }

    @Override
    public void runAgent() {        
        checkAllMessages();
        while (propositions.size() > 0 || getMessages().size() > 0) { // while (true)
            checkAllMessages();
        }
    }

    @Override
    protected void checkAllMessages() {
        MessageNegociation message = null;
        String action, performatif;
        while (messages.size() > 0) {
            message = (MessageNegociation) messages.poll();
            performatif = message.getPerformatif();
            action = message.getAction();
            Negociation negociation = message.getNegociation();
            dialogView.addDialogLine(getName(), "Reçu message: \n" + message.toString());

            // Appel d'offre
            if (performatif.equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)
                    && action.equals(MessageNegociation.ACTION_REQUEST)) {
                dialogView.addDialogLine(getName(), "Traitement de l'appel d'offre");
                List<Proposition> listePropositions = getPropositionsPossibles(negociation.getVoeu());
                dialogView.addDialogLine(getName(), String.format("%d offres trouvées", listePropositions.size()));

                reponseAppelOffre(listePropositions.size() > 0, negociation);
            } else if (performatif.equals(MessageNegociation.PERFORMATIF_PROPOSITION)) {
                switch (action) {
                    case MessageNegociation.ACTION_PRIX_NEGOCIATEUR:
                        float prixNegociateur = negociation.getVoeu().getPrix();
                        Proposition proposition = negociation.getProposition();
                        if (proposition == null) { // Première proposition
                            List<Proposition> listePropositions = getPropositionsPossibles(negociation.getVoeu());

                            if (!listePropositions.isEmpty()) {
                                // Plus d'offres de disponibles
                                if (listePropositions.size() > 1) {
                                    // On va proposer l'offre qui a le plus de chance d'être acceptée en se basant sur le prix de départ                            
                                    float minAbsolue = -1;
                                    for (Proposition prop: listePropositions) {
                                        if (Math.abs(prixNegociateur) < minAbsolue || minAbsolue < 0) {
                                            minAbsolue = Math.abs(prixNegociateur);
                                            proposition = prop;
                                        }
                                    }
                                } else {
                                    proposition = listePropositions.get(0);
                                }
                            }
                        }

                        if (proposition == null) {
                            reponseProposition(false, negociation.getNegociateur(), negociation);
                        } else {
                            // Pas de stratégie de croissance
                            negociation.setProposition(proposition);
                            if (prixNegociateur > proposition.getTarifMinimal()) {
                                reponseProposition(true, negociation.getNegociateur(), negociation);
                            } else {
                                reponseProposition(false, negociation.getNegociateur(), negociation);
                            }
                        }
                        break;
                    case MessageNegociation.ACTION_REFUSED_NEGOCIATION:
                        negociation.setRefused(true);
                        dialogView.addDialogLine(getName(), String.format("Refus de la négociation avec %s", message.getEmetteur().getName()));
                        break;
                    default:
                        dialogView.addDialogLine(getName(), "Default");
                }
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

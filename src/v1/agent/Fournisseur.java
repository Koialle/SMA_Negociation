
package v1.agent;

import java.util.ArrayList;
import java.util.List;
import v1.message.Action;
import v1.message.Message;
import v1.message.Performatif;
import v1.model.Negociation;
import v1.model.Proposition;
import v1.model.Voeu;
import v1.view.Dialog;

/**
 * Fournisseur : aggrège des offres de compagnies arériennes ou ferroviaire et les soumets au Négociateur.
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Fournisseur extends Agent {
    private static int cptFournisseurs = 0;
    protected List<Proposition> propositions;
    protected List<Negociateur> negociateurs;

    // @TODO gérer cas où fournisseur refuse, donc négociateur remonte son prix si possible
    // @TODO gérer cas où plusieurs fournisseurs proposer le même produit, choisir le prix minimal

    public Fournisseur() {
        super(cptFournisseurs++);
        setName("Fournisseur n°"+id);
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
        Message message = null;
        while (messages.size() > 0) {
            message = (Message) messages.poll();
            Performatif performatif = (Performatif) message.getPerformatif();
            Action action = (Action) message.getAction();
            Negociation negociation = message.getNegociation();
            dialogView.addDialogLine(getName(), "Reçu message: \n" + message.toString());

            // Appel d'offre
            if (performatif == Performatif.APPEL_OFFRE && action == Action.SOUMISSION) {
                dialogView.addDialogLine(getName(), "Traitement de l'appel d'offre");
                List<Proposition> listePropositions = getPropositionsPossibles(negociation.getVoeu());
                dialogView.addDialogLine(getName(), String.format("%d offres trouvées", listePropositions.size()));

                reponseAppelOffre(listePropositions.size() > 0, negociation);
            } else if (performatif == Performatif.PROPOSITION) {
                switch (action) {
                    case SOUMISSION:
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
                                proposition.setPrix(prixNegociateur);
                                reponseProposition(true, negociation.getNegociateur(), negociation);
                            } else {
                                reponseProposition(false, negociation.getNegociateur(), negociation);
                            }
                        }
                        break;
                    case REFUS:
                        negociation.setRefused(true);
                        dialogView.addDialogLine(getName(), String.format("Refus de la négociation avec %s", message.getEmetteur().getName()));
                        break;
                    case ACCEPTATION:
                        //@TODO
                        dialogView.addDialogLine(getName(), String.format("Acceptation de %s", message.getEmetteur().getName()));
                }
            }
        }
    }

    private void reponseAppelOffre(boolean ok, Negociation negociation) {
        Message reponse = new Message(Message.Type.APPEL_OFFRE, this, negociation.getNegociateur(), negociation);

        if (ok) {
            reponse.setAction(Action.ACCEPTATION);
        } else {
            reponse.setAction(Action.REFUS);
        }

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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //@TODO
    }
}

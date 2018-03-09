
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
    protected List<Negociateur> negociateurs; // Est-ce nécéssaire finalement ?

    // @TODO gérer cas où fournisseur refuse, donc négociateur remonte son prix si possible
    // @TODO gérer cas où plusieurs fournisseurs proposer le même produit, choisir le prix minimal

    public Fournisseur() {
        super(cptFournisseurs++);
        setName("Fournisseur n°" + id);
        dialogView = new Dialog(this);
        negociateurs = new ArrayList();
        propositions = new ArrayList();
    }

    @Override
    public void runAgent() {
        // Affichage des offres disponibles du fournisseur
        dialogView.addDialogLine(getName(), "Propositions disponibles:");
        for (Proposition proposition: propositions) {
            dialogView.addDialogLine(getName(), proposition.toString());
        }

        // Début interaction
        while (propositions.size() > 0 || getMessages().size() > 0) { // while (true)
            checkAllMessages();
        }
    }

    @Override
    protected void checkAllMessages() {
        Message message;
        while (messages.size() > 0) {
            message = (Message) messages.poll();
            Performatif performatif = (Performatif) message.getPerformatif();
            Action action = (Action) message.getAction();
            Negociation negociation = message.getNegociation();
            displayMessageReçu(message);

            // Appel d'offre
            if (performatif == Performatif.APPEL_OFFRE && action == Action.SOUMISSION) {
                List<Proposition> listePropositions = getPropositionsPossibles(negociation.getVoeu());
                String nbOffres = String.format("%d offres trouvées", listePropositions.size());
                if (listePropositions.size() > 0) {
                    envoieAccordProposition(negociation.getNegociateur(), negociation, nbOffres);
                } else {
                    envoieRefusProposition(negociation.getNegociateur(), negociation, nbOffres);
                }
            } else if (performatif == Performatif.PROPOSITION) {
                switch (action) {
                    case SOUMISSION:
                        float prixNegociateur = negociation.getVoeu().getPrix();
                        Proposition proposition = negociation.getProposition();
                        
                        // Choix de la proposition à lier à la néociation (étape = 4)
                        if (proposition == null) {
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

                        // Traitement de la proposition (étape > 4)
                        if (proposition == null) {
                            envoieRefusPrix(negociation.getNegociateur(), negociation, "Plus d'offres de disponibles pour ce service");
                        } else {
                            negociation.setProposition(proposition);
                            String explicationPrix;
                            float prixFournisseur = proposition.getPrix();

                            if (proposition.getPrix() == 0) {
                                prixFournisseur = proposition.getPrixDepart();
                                explicationPrix = "Pris de départ fournisseur";
                            } else {                                
                                if (prixNegociateur >= prixFournisseur) {
                                    prixFournisseur = prixNegociateur;
                                    explicationPrix = "Prix du négociateur accepté";
                                } else {
                                    prixFournisseur = proposition.getPrix() - proposition.getPrix() * (float) 0.2;
                                    explicationPrix = String.format("%.2f à la baisse avec taux de %.2f %%\nRemarque: Taux en dur pour le fournisseur", proposition.getPrix(), 0.2 * 100);
                                }

                                if (prixFournisseur < proposition.getTarifMinimal()) {
                                    explicationPrix = String.format("Nouveau prix calculé %.2f trop bas -> Envoi du tarif minimal", prixFournisseur, proposition.getTarifMinimal());
                                    prixFournisseur = proposition.getTarifMinimal();
                                }
                            }

                            envoieNouveauPrix(prixFournisseur, negociation.getNegociateur(), negociation, explicationPrix);
                        }
                        break;
                    case ACCEPTATION:
                        negociationAcceptee(negociation);
                        break;
                    case REFUS:
                        negociationRefusee(negociation);
                        break;
                    default:
                        dialogView.addDialogLine(getName(), "non géré");
                }
            }
        }
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

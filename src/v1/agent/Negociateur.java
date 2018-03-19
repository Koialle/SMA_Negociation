
package v1.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import v1.message.Action;
import v1.message.Message;
import v1.message.Performatif;
import v1.model.Negociation;
import v1.view.Dialog;
import v1.model.Voeu;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Negociateur extends Agent {
    private static int cptNegociateurs = 0;
    protected List<Voeu> voeux;
    protected List<Fournisseur> fournisseurs;

    public Negociateur() {
        super(cptNegociateurs++);
        setName("Negociateur n°" + id);
        voeux = new ArrayList();
        fournisseurs = new ArrayList();
        dialogView = new Dialog(this);
    }

    @Override
    public void runAgent() {
        try {
            while (true) {
                checkVoeux();
                checkAllMessages();
            }
        } catch (Exception ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkVoeux() {
        for (Voeu voeu: voeux) {
            if (voeu.getEtat() == Voeu.Etat.NON_TRAITE) {
                // Creation de négociation avec chaque fournisseurs connus
                for (Fournisseur fournisseur: fournisseurs) {
                    // Filtrages des fournisseurs préférés
                    if (voeu.getFournisseursPreferes().size() > 0) {
                        if (!voeu.getFournisseursPreferes().contains(fournisseur)) {
                            continue;
                        }
                    }

                    Negociation negociation = new Negociation(this, fournisseur, voeu);
                    dialogView.addDialogLine(getName(), String.format("Début de la négociation %d avec %s", negociation.getId(), fournisseur.getName()));
                    sendMessage(new Message(Message.Type.APPEL_OFFRE, this, fournisseur, negociation));
                    voeu.addNegociation(negociation);
                    // negociations.put(negociation.getId(), negociation);
                }
            } else if (voeu.getEtat() == Voeu.Etat.EN_TRAITEMENT) {
                boolean finished = true;
                for (Negociation negociation: voeu.getNegociations()) {
                    if (negociation.getEtat() == Negociation.Etat.ACCEPTEE) {
                        finished = false;
                    }
                }
                
                // Toutes les négociations ont été conclues
                if (finished) {
                    // Choix du meilleur prix et de la meilleure offre
                    String voteNegociation = String.format("Choix de la meilleure proposition fournisseur reçue pour le voeu n°%d\n", voeu.getId());
                    Negociation bestOffer = null;
                    float minPrice = -1;
                    for (Negociation negociation: voeu.getNegociations()) {
                        if (negociation.getEtat() == Negociation.Etat.ACCEPTEE) {
                            voteNegociation += String.format("- Proposition n°%d avec accord sur le prix %.2f\n", negociation.getProposition().getId(), negociation.getProposition().getPrix());
                            if (minPrice < 0 || negociation.getProposition().getPrix() < minPrice) {
                                minPrice = negociation.getProposition().getPrix();
                                bestOffer = negociation;
                            }
                        }
                    }

                    // Remise de la demande sur le "marché"
                    if (bestOffer == null) {
                        // @TODO reset negociations ?
                        // dialogView.addDialogLine(getName(), "Pas de proposition trouvée pour cette offre.\nRebouclage de la demande.");
                        voeu.setEtat(Voeu.Etat.A_REBOUCLER);
                    } else {
                        if (voeu.getNegociations().size() > 1) {
                            voteNegociation += String.format("Proposition n°%d retenue avec prix de %.2f €", bestOffer.getProposition().getId(), bestOffer.getProposition().getPrix());
                            dialogView.addDialogLine(getName(), voteNegociation);

                            // Fin de la négociation
                            for (Negociation negociation: voeu.getNegociations()) {
                                if (negociation.equals(bestOffer)) {
                                    envoieAccordProposition(bestOffer.getFournisseur(), bestOffer, "Acceptation car meilleure offre reçue."); // Send acceptance at best offer
                                } else {
                                    String message = String.format("Refus de la négociation avec %s car meilleure offre trouvée (%.2f€ vs %.2f€)", negociation.getId(), negociation.getFournisseur(), bestOffer.getProposition().getPrix(), negociation.getProposition().getPrix());
                                    envoieRefusProposition(negociation.getFournisseur(), negociation, message); // Send declination at other offers
                                }
                            }
                        } else {
                            envoieAccordProposition(bestOffer.getFournisseur(), bestOffer, "Confirmation de la transaction de la négociation n°" + bestOffer.getProposition().getId()); // Send acceptance at best offer
                        }
                    }
                }
            }
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

            if (performatif == Performatif.APPEL_OFFRE) { // Appel d'offre
                if (action == Action.ACCEPTATION) {
                    envoieNouveauPrix(negociation.getVoeu().getPrixDepart(), negociation.getFournisseur(), negociation, "Prix de départ négociateur");
                } else if (action == Action.REFUS) {
                    negociation.setEtat(Negociation.Etat.REFUSEE);
                    dialogView.addDialogLine(getName(), String.format("Fin de la négociation %d avec %s", negociation.getId(), message.getEmetteur().getName()));
                }
            } else if (performatif == Performatif.PROPOSITION) { // Proposition prix
                switch (action) {
                    case SOUMISSION:
                        if (negociation.isActive()) {
                            envoieNouveauPrixACalculer(negociation);
                        } else {
                            System.out.println("Negociation non active => debug");
//                            if (negociation.getEtat() == Negociation.Etat.ACCEPTEE || negociation.getVoeu().getEtat() == Voeu.Etat.RESOLU) {
//                                // Scénario 2: Normalement on ne devrait pas passer dans cette condition                                
//                                //@TODO (scénario 3) distinuer les deux conditions ci-dessus car pas forcément la même négociation qui a résolu le voeu
//
//                                String error = String.format("La négociation %d a déjà été acceptée entre %s et %s", negociation.getId(), getName(), negociation.getFournisseur().getName());
//                                dialogView.addDialogLine(getName(), error);
//                                
//                                Message reponse = new Message(Message.Type.PROPOSITION_PRIX, this, negociation.getFournisseur(), negociation);
//                                reponse.setAction(Action.REFUS);
//                                reponse.setMessage(error);
//                                sendMessage(reponse);
//                            } else {
//                                System.err.printf("La négociation(%d) a déjà été cloturée par le fournisseur\n", negociation.getId());
//                            }
                        }   break;
                    // Seul le négociateur prend la décision finale
                    case ACCEPTATION:
                        traitementAcceptationPrix(negociation);
                        break;
                    case REFUS:
                        negociationRefusee(negociation);
                        break;
//                    default:
//                        dialogView.addDialogLine(getName(), "non géré");
                }
            }
        }
    }

    private void envoieNouveauPrixACalculer(Negociation negociation) {
        float prixFournisseur = negociation.getProposition().getPrix();
        Voeu voeu = negociation.getVoeu();
        if (negociation.getNombreEchanges() > 0 && negociation.getNombreEchanges() < voeu.getFrequence()) {
            if (prixFournisseur > voeu.getPrix()) {
                // Calcule du nouveau prix
                float prixNegociateur = voeu.getPrix(); // Ne sera pas modifié si la stratégie de négociation est nulle
                String explicationPrix = "";
                if (voeu.getTypeCroissance().equals(Voeu.CROISSANCE_LINEAIRE)) {
                    prixNegociateur = voeu.getPrix() + voeu.getPrix() * voeu.getCroissance();
                    explicationPrix = String.format("%.2f à la hausse avec taux de %.2f %%", voeu.getPrix(), voeu.getCroissance() * 100);
                }
                
                if (prixFournisseur < prixNegociateur && prixFournisseur < voeu.getTarifMaximum()) {
                    voeu.setPrix(prixFournisseur);
                    envoieAcceptationPrix(negociation.getFournisseur(), negociation, String.format("Prix acceptabe et meilleur que nouveau prix calculé %.2f", prixNegociateur));
                    
                    return;
                }
                
                if (prixNegociateur > voeu.getTarifMaximum()) {
                    explicationPrix = String.format("Nouveau prix calculé %.2f trop haut -> Envoi du tarif maximal", prixNegociateur);
                    prixNegociateur = voeu.getTarifMaximum();
                }

                envoieNouveauPrix(prixNegociateur, negociation.getFournisseur(), negociation, explicationPrix);
            } else {
                // Acceptation du prix proposé
                if (prixFournisseur <= voeu.getPrixDepart()) {
                    envoieAcceptationPrix(negociation.getFournisseur(), negociation, "Prix inférieur ou égal à mon prix de départ");
                } else {
                    envoieAcceptationPrix(negociation.getFournisseur(), negociation, "Prix inférieur ou égal à mon prix");
                }
            }
        } else {
            // Nombre d'échanges dépassés
            if (negociation.getNombreEchanges() >= negociation.getVoeu().getFrequence()) {
                // Fin de la négociation => répond oui si prixFournisseur dans le budget, non sinon
                if (prixFournisseur <= negociation.getVoeu().getTarifMaximum()) {
                    envoieAcceptationPrix(negociation.getFournisseur(), negociation, "Prix dans le budget");
                } else {
                    envoieRefusPrix(negociation.getFournisseur(), negociation, "Prix hors budget");
                }
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

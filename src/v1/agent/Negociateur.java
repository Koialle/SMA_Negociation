
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
                // Creation de négociation
                for (Fournisseur fournisseur: fournisseurs) {
                    if (voeu.getFournisseursPreferes().size() > 0) {
                        if (!voeu.getFournisseursPreferes().contains(fournisseur)) {
                            continue;
                        }
                    }

                    Negociation negociation = new Negociation(this, fournisseur, voeu);
                    dialogView.addDialogLine(getName(), String.format("Début de la négociation %d avec %s", negociation.getId(), fournisseur.getName()));
                    sendMessage(new Message(Message.Type.APPEL_OFFRE, this, fournisseur, negociation));
                    negociations.put(negociation.getId(), negociation);
                }
            }
            // @TODO scénario 3
//            else if (voeu.getEtat() == Voeu.Etat.EN_TRAITEMENT ) { // Cas d'un voeu en traitement dont toutes les négociations ont été en échec.
//                for (Fournisseur fournisseur: fournisseurs) {
//                    boolean nouveauFournisseur = true;
//                    for (Negociation negociation: negociations.values()) {
//                        if (fournisseur.equals(negociation.getFournisseur())) {
//                            nouveauFournisseur = false;
//                            continue;
//                        }
//                    }
//                    
//                    if (nouveauFournisseur) {
//                        Negociation negociation = new Negociation(this, fournisseur, voeu);
//                        Message message = new Message(this, fournisseur, negociation); 
//                        sendMessage(message);
//
//                        this.negociations.put(negociation.getIdentifiantNegociation(), negociation);
//                    }
//                }
//            }
        }
    }

    @Override
    protected void checkAllMessages() {
        Message message = null;
        while (messages.size() > 0) {
            message = (Message) messages.poll();
            Performatif performatif = (Performatif) message.getPerformatif();
            Action action = (Action) message.getAction();
            Negociation negociation = negociations.get(message.getNegociation().getId());
            displayMessageReçu(message);

            if (performatif == Performatif.APPEL_OFFRE) { // Appel d'offre
                if (action == Action.ACCEPTATION) {
                    envoieNouveauPrix(negociation.getVoeu().getPrixDepart(), negociation.getFournisseur(), negociation, "Prix de départ négociateur");
                } else if (action == Action.REFUS) {
                    negociations.get(negociation.getId()).setEtat(Negociation.Etat.REFUSEE);
                    dialogView.addDialogLine(getName(), String.format("Fin de la négociation %d avec %s", negociation.getId(), message.getEmetteur().getName()));
                }
            } else if (performatif == Performatif.PROPOSITION) { // Proposition prix
                switch (action) {
                    case SOUMISSION:
                        if (negociation.isActive()) {
                            envoieNouveauPrixACalculer(negociation);
                        } else {
                            if (negociation.getEtat() == Negociation.Etat.ACCEPTEE || negociation.getVoeu().getEtat() == Voeu.Etat.RESOLU) {
                                // Scénario 2: Normalement on ne devrait pas passer dans cette condition                                
                                //@TODO (scénario 3) distinuer les deux conditions ci-dessus car pas forcément la même négociation qui a résolu le voeu

                                String error = String.format("La négociation %d a déjà été acceptée entre %s et %s", negociation.getId(), getName(), negociation.getFournisseur().getName());
                                dialogView.addDialogLine(getName(), error);
                                
                                Message reponse = new Message(Message.Type.PROPOSITION_PRIX, this, negociation.getFournisseur(), negociation);
                                reponse.setAction(Action.REFUS);
                                reponse.setMessage(error);
                                sendMessage(reponse);
                            } else {
                                System.err.printf("La négociation(%d) a déjà été cloturée par le fournisseur\n", negociation.getId());
                            }
                        }   break;
                        // Seul le négociateur prend la décision finale
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

    private void envoieNouveauPrixACalculer(Negociation negociation) {
        float prixFournisseur = negociation.getProposition().getPrix();
        Voeu voeu = negociation.getVoeu();
        if (negociation.getNombreEchanges() > 0 && negociation.getNombreEchanges() < voeu.getFrequence()) {
            if (prixFournisseur > voeu.getPrix()) {
                // Calcule du nouveau prix
                float prix = voeu.getPrix(); // Ne sera pas modifié si la stratégie de négociation est nulle
                if (voeu.getTypeCroissance().equals(Voeu.CROISSANCE_LINEAIRE)) {
                    prix = voeu.getPrix() + voeu.getPrix() * voeu.getCroissance();
                }
                envoieNouveauPrix(prix, negociation.getFournisseur(), negociation, String.format("%.2f à la hausse avec taux de %.2f %%", voeu.getPrix(), voeu.getCroissance() * 100));
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


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
        setName("Negociateur n°"+id);
        voeux = new ArrayList();
        fournisseurs = new ArrayList();
        voeux = new ArrayList();
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

                voeu.setEtat(Voeu.Etat.TRAITE);
            } 
//            else if (voeu.getEtat() != Voeu.Etat.TERMINE ) { // Cas d'un voeu traité mais qui n'a pas trouvé de proposition
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
                    envoieNouveauPrix(negociation.getVoeu().getPrixDepart(), negociation.getFournisseur(), negociation, "Prix de départ");
                } else if (action == Action.REFUS) {
                    negociations.get(negociation.getId()).setRefused(true);
                    dialogView.addDialogLine(getName(), String.format("Fin de la négociation %d avec %s", negociation.getId(), message.getEmetteur().getName()));
                }
            } else if (performatif == Performatif.PROPOSITION) { // Proposition prix
                switch (action) {
                    case SOUMISSION:
                        if (negociation.isActive()) {
                            reponsePrixNegociateur(negociation);
                        } else {
                            if (negociation.isAccepted()) {
                                String error = String.format("La négociation (%d) a déjà été acceptée entre %s et %s", negociation.getId(), getName(), negociation.getFournisseur().getName());
                                dialogView.addDialogLine(getName(), error);
                                
                                Message reponse = new Message(Message.Type.PROPOSITION_PRIX, this, negociation.getFournisseur(), negociation);
                                reponse.setAction(Action.REFUS);
                                reponse.setMessage(error);
                                sendMessage(reponse);
                            } else {
                                System.err.printf("La négociation(%d) a déjà été cloturée\n", negociation.getId());
                            }
                        }   break;
                        // Seul le négociateur prend la décision finale
//                    case ACCEPTATION:
//                        negociation.setAccepted(true);
//                        dialogView.addDialogLine(getName(), String.format("Accord de la négociation %d entre %s et %s", negociation.getId(), getName(), negociation.getFournisseur().getName()));
//                        break;
//                    case REFUS:
//                        if (negociation.getProposition() != null) {
//                            reponsePrixNegociateur(negociation);
//                        } else {
//                            negociation.setRefused(true);
//                            dialogView.addDialogLine(getName(), String.format("Refus de la négociation avec %s", message.getEmetteur().getName()));
//                        }   break;
                    default:
                        break;
                }
            }
        }
    }
    
    private void reponsePrixNegociateur(Negociation negociation) {
        float prixFournisseur = negociation.getProposition().getPrix();
        if (negociation.getNombreEchanges() > 0 && negociation.getNombreEchanges() < negociation.getVoeu().getFrequence()) {
            // Calcule du prix à proposer
            if (prixFournisseur <= negociation.getVoeu().getPrixDepart()) {
                envoieAccordPrix(negociation.getFournisseur(), negociation, "Prix inférieur à mon prix de départ");
            } else {
                // Calcule du nouveau prix
                Voeu voeu = negociation.getVoeu();
                float prix = voeu.getPrix() > 0 ? voeu.getPrix(): voeu.getPrixDepart(); // Ne sera pas modifié si la stratégie de négociation est nulle
                if (voeu.getTypeCroissance().equals(Voeu.CROISSANCE_LINEAIRE)) {
                    prix = voeu.getPrix() + voeu.getPrix() * voeu.getCroissance();
                }
                envoieNouveauPrix(prix, negociation.getFournisseur(), negociation, String.format("%.2f à la hausse avec taux de %.2f %", voeu.getPrix(), voeu.getCroissance() * 100));
            }

        } else {
            if (negociation.getNombreEchanges() == negociation.getVoeu().getFrequence()) {
                // Fin de la négociation => répond oui si prixFournisseur dans le budget, non sinon
                if (prixFournisseur <= negociation.getVoeu().getTarifMaximum()) {
                    envoieAccordPrix(negociation.getFournisseur(), negociation, "Prix dans le budget");
                } else {
                    envoieRefusPrix(negociation.getFournisseur(), negociation, "Prix hors budget");
                }
            } else {
                dialogView.addDialogLine(getName(), String.format("Fin de la négociation %d avec %s", negociation.getId(), negociation.getFournisseur().getName()));
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

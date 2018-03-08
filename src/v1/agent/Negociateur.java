
package v1.agent;

import sma.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
                    if (voeu.getFournisseursPreferes().size() > 0) {
                        if (!voeu.getFournisseursPreferes().contains(fournisseur)) {
                            continue;
                        }
                    }

                    Negociation negociation = new Negociation(this, fournisseur, voeu);
                    MessageNegociation message = new MessageNegociation(this, fournisseur, negociation); 
                    sendMessage(message);

                    this.negociations.put(negociation.getIdentifiantNegociation(), negociation);
                }

                voeu.setTraite(true);
            }
        }
    }
    
    private void checkNegociations() {
        //@TODO
    }
    
    @Override
    protected void checkAllMessages() {
        MessageNegociation message = null;
        String action, performatif;
        while (messages.size() > 0) {
            message = (MessageNegociation) messages.poll();
            action = message.getAction();
            performatif = message.getPerformatif();

            Negociation negociation = negociations.get(message.getNegociation().getIdentifiantNegociation());
            if (performatif.equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)) { // Appel d'offre
                if (action.equals(MessageNegociation.ACTION_ACCEPTED_OFFRE)) {
                    this.dialogView.addDialogLine(getName(), String.format("Début de la négociation(%d) avec le fournisseur %s", negociation.getIdentifiantNegociation(), message.getEmetteur().getName()));
                    reponsePropositionPrix(negociation.getVoeu().getPrixDepart(), this, negociation.getFournisseur(), negociation);
                } else if (action.equals(MessageNegociation.ACTION_REFUSED_OFFRE)) {
                    negociations.get(negociation.getIdentifiantNegociation()).setRefused(true);
                    this.dialogView.addDialogLine(getName(), String.format("Fin de la conversation avec %s", message.getEmetteur().getName()));
                }
            } else if (performatif.equals(MessageNegociation.PERFORMATIF_PROPOSITION)) {
                if (action.equals(MessageNegociation.ACTION_PRIX_FOURNISSEUR)) {
                    if (negociation.isActive()) {
                        float prixFournisseur = negociation.getProposition().getPrix();
                        if (negociation.getNombreEchanges() > 0 && negociation.getNombreEchanges() < negociation.getVoeu().getFrequence()) {
                            // Calcule du prix à proposer
                            if (prixFournisseur <= negociation.getVoeu().getPrixDepart()) {
                                reponseProposition(true, negociation.getFournisseur(), negociation);
                            } else {
                                // Calcule du nouveau prix
                                Voeu voeu = negociation.getVoeu();
                                float prix = voeu.getPrix(); // Ne sera pas modifié si la stratégie de négociation est nulle
                                if (voeu.getTypeCroissance().equals(Voeu.CROISSANCE_LINEAIRE)) {
                                    prix = voeu.getPrix() + voeu.getPrix() * voeu.getCroissance();
                                }
                                reponsePropositionPrix(prix, this, negociation.getFournisseur(), negociation);
                            }
                            
                        } else {
                            // Fin de la négociation => répond oui si prixFournisseur dans le budget, non sinon
                            reponseProposition(prixFournisseur <= negociation.getVoeu().getTarifMaximum(), negociation.getFournisseur(), negociation);
                        }
                    } else {
                        if (negociation.isAccepted()) {
                            this.dialogView.addDialogLine(getName(), String.format("La négociation(%d) a déjà été  entre %s et %s"));
                            
                            MessageNegociation reponse = new MessageNegociation(MessageNegociation.PERFORMATIF_PROPOSITION, MessageNegociation.ACTION_REFUSED_NEGOCIATION);
                            reponse.setEmetteur(this);
                            reponse.setDestinataire(negociation.getFournisseur());
                            reponse.setNegociation(negociation);

                            sendMessage(reponse);
                        } else {
                            System.err.printf("La négociation(%d) a déjà été cloturée\n", negociation.getIdentifiantNegociation());
                        }
                    }
                } else if (action.equals(MessageNegociation.ACTION_ACCEPTED_NEGOCIATION)) {
                    negociation.setAccepted(true);
                    this.dialogView.addDialogLine(getName(), String.format("Accord de la négociation %d entre %s et %s"));
                } else if (action.equals(MessageNegociation.ACTION_REFUSED_NEGOCIATION)) {
                    negociation.setRefused(true);
                    this.dialogView.addDialogLine(getName(), String.format("Refus de la négociation avec %s", message.getEmetteur().getName()));
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

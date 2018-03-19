
package v1.agent;

import java.util.HashMap;
import java.util.Map;
import v1.message.Message;
import v1.model.Negociation;
import v1.message.Action;
import v1.view.Dialog;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public abstract class Agent extends sma.Agent {
    // protected Map<Integer, Negociation> negociations;
    protected Dialog dialogView;
    protected int id;
    
    /**
     * Constructeur Agent
     * @param id
     */
    public Agent(int id) {
        super();
        // this.negociations = new HashMap<>();
        this.id = id;
    }

    public int getIdAgent() {
        return id;
    }

    public void setIdAgent(int id) {
        this.id = id;
    }
    
    @Override
    protected void sendMessage(sma.Message message) {
        if (message instanceof Message) {
            ((Message) message).getNegociation().incrementeStep();
            Agent destinataire = (Agent) message.getDestinataire();
            destinataire.addMessage(message);
            displayMessageEnvoye(message);
        }
    }
    
    protected void envoieAccordProposition(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.APPEL_OFFRE, this, destinataire, negociation);
        message.setAction(Action.ACCEPTATION);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieRefusProposition(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.APPEL_OFFRE, this, destinataire, negociation);
        message.setAction(Action.REFUS);
        message.setMessage(raison);

        sendMessage(message);
    }

    protected void envoieAcceptationPrix(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        message.setAction(Action.ACCEPTATION);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieRefusPrix(Agent destinataire, Negociation negociation, String raison) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        message.setAction(Action.REFUS);
        message.setMessage(raison);

        sendMessage(message);
    }
    
    protected void envoieNouveauPrix(float prix, Agent destinataire, Negociation negociation, String detailPrix) {
        Message message = new Message(Message.Type.PROPOSITION_PRIX, this, destinataire, negociation);
        
        if (destinataire instanceof Negociateur) {
            negociation.getProposition().setPrix(prix);
        } else if (destinataire instanceof Fournisseur) {
            negociation.getVoeu().setPrix(prix);
        }

        negociation.incrementeNbEchanges();
        message.setMessage(detailPrix);

        sendMessage(message);
    }
    
//    protected void envoieMeilleureOffreTrouvee(Negociation negociation, Negociation meilleureOffre) {
//        negociation.setEtat(Negociation.Etat.REFUSEE);
//        float meilleurPrix = meilleureOffre.getProposition().getPrix();
//        float prixCourant = negociation.getProposition().getPrix();
//
//        Agent F = negociation.getFournisseur();
//        //Agent N = negociation.getNegociateur();
//        String message = "Refus de la négociation avec %s car meilleure offre trouvée (%.2f€ vs %.2f€)";
//        F.dialogView.addDialogLine(getName(), String.format(message, F.getName(), prixCourant, meilleurPrix));
//        //N.dialogView.addDialogLine(getName(), String.format(message, N.getName(), prixCourant, meilleurPrix));
//    }
    
    
    // Helpers d'affichage
    protected void negociationAcceptee(Negociation negociation) {
        negociation.accepter();
        float prix = negociation.getVoeu().getProposition().getPrix();
        Agent F = negociation.getFournisseur();
        Agent N = negociation.getNegociateur();
        String message = "Accord définitif de la négociation %d entre %s et %s -> %.2f€";
        F.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), F.getName(), N.getName(), prix));
        N.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), F.getName(), N.getName(), prix));
    }

    protected void negociationRefusee(Negociation negociation) {
        negociation.setEtat(Negociation.Etat.REFUSEE);
        float prixN = negociation.getVoeu().getPrix();
        float prixF = negociation.getProposition().getPrix();

        Agent F = negociation.getFournisseur();
        Agent N = negociation.getNegociateur();
        String message = "Refus définitif de la négociation avec %s (%.2f€ vs %.2f€)";
        F.dialogView.addDialogLine(getName(), String.format(message, F.getName(), prixN, prixF));
        N.dialogView.addDialogLine(getName(), String.format(message, N.getName(), prixN, prixF));
    }

    protected void traitementAcceptationPrix(Negociation negociation) {
        negociation.setEtat(Negociation.Etat.ACCEPTEE);
        float prix = negociation.getProposition().getPrix();
        Agent F = negociation.getFournisseur();
        Agent N = negociation.getNegociateur();
        String message = "Accord du prix de la négociation n°%d entre %s et %s -> %.2f€";
        N.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), F.getName(), N.getName(), prix));
        F.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), F.getName(), N.getName(), prix));
    }
    
//    protected void traitementRefusPrix(Negociation negociation) {
//        negociation.setEtat(Negociation.Etat.REFUSEE);
//        float prixN = negociation.getVoeu().getPrix();
//        float prixF = negociation.getProposition().getPrix();
//
//        Agent F = negociation.getFournisseur();
//        Agent N = negociation.getNegociateur();
//        String message = "Refus du prix de la négociation %d avec %s (%.2f€ vs %.2f€)";
//        F.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), F.getName(), prixN, prixF));
//        N.dialogView.addDialogLine(getName(), String.format(message, negociation.getId(), N.getName(), prixN, prixF));
//    }

    protected void displayMessageReçu(sma.Message message) {
        dialogView.addDialogLine(message.getEmetteur().getName(), message.toString());
    }

    protected void displayMessageEnvoye(sma.Message message) {
        Agent emetteur = (Agent) message.getEmetteur();
        emetteur.dialogView.addDialogLine(emetteur.getName(), message.toString());
    }
}

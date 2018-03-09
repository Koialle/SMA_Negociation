
package v1.message;

import v1.model.Negociation;
import v1.agent.Agent;

/**
 * Classe Message spécifique à Negociation.
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Message extends sma.Message {
//    public static final String PERFORMATIF_APPEL_OFFRE = "Appel_offre";
//    public static final String PERFORMATIF_PROPOSITION = "Proposition";
//
//    public static final String ACTION_REQUEST = "Request";
//    public static final String ACTION_PRIX_NEGOCIATEUR = "Prix_Négociateur";
//    public static final String ACTION_PRIX_FOURNISSEUR = "Prix_Fournisseur";
//    public static final String ACTION_REFUSED_NEGOCIATION = "Refused_negociation"; // Proposition
//    public static final String ACTION_ACCEPTED_NEGOCIATION = "Accepted_negociation"; // Proposition
//    public static final String ACTION_REFUSED_OFFRE = "Refused_offre";
//    public static final String ACTION_ACCEPTED_OFFRE = "Accepted_offre";
    
    public enum Type {
        APPEL_OFFRE,
        PROPOSITION_PRIX
    }
    
    protected Negociation negociation;
    protected String message;

    /**
     * @param performatif
     * @param action 
     */
    public Message(Performatif performatif, Action action) {
        this.performatif = performatif;
        this.action = action;
    }

    /**
     * Soumission appel d'offre | proposition prix
     *
     * @param type
     * @param emetteur
     * @param destinataire
     * @param negociation
     */
    public Message(Type type, Agent emetteur, Agent destinataire, Negociation negociation) {
        this.emetteur = emetteur;
        this.destinataire = destinataire;
        this.negociation = negociation;  

        performatif = (type == Type.APPEL_OFFRE) ? Performatif.APPEL_OFFRE : Performatif.PROPOSITION;
        action = Action.SOUMISSION;
    }

    public Negociation getNegociation() {
        return negociation;
    }

    public void setNegociation(Negociation negociation) {
        this.negociation = negociation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        String texte = "Negociation n°" + negociation.getId();
        texte += "\nEmetteur: " + emetteur.getName();
        texte += "\nDestinataire: " + destinataire.getName();
        if (message != null) {
            texte += "\nMessage: " + message;
        }

        if (performatif == Performatif.APPEL_OFFRE) {
            if (action == Action.SOUMISSION) {
                texte += "\nRequète d'appel d'offre\n";
                if (negociation.getVoeu() != null) {
                    texte += "Demande :\n" + negociation.getVoeu().toString();
                }
            } else {
                texte += "\nAppel d'offre : " + action;
            }
        } else if (performatif == Performatif.PROPOSITION) {
            if (negociation.getNombreEchanges() > 0) texte += "\nOccurence: " + negociation.getNombreEchanges();
            switch ((Action) action) {
                case SOUMISSION:
                    if (emetteur.getId() == negociation.getFournisseur().getId()) {
                        texte += "\nSoumission prix fournisseur: " + negociation.getProposition().getPrix();
                    } else {
                        texte += "\nSoumission prix negociateur: " + negociation.getVoeu().getPrix();
                    }
//                    if (negociation.getProposition() != null) {
//                        texte += "\nProposition: \n" + negociation.getProposition();
//                    }
                    break;
                case ACCEPTATION:
                    if (emetteur.getId() == negociation.getFournisseur().getId()) {
                        texte += "\nAccord fournisseur: \n" + negociation.getProposition();
                    } else {
                        texte += "\nAccord negociateur: \n" + negociation.getVoeu();
                    }
                    break;
                case REFUS:
                    if (emetteur.getId() == negociation.getFournisseur().getId()) {
                        texte += "\nRefus fournisseur: " + negociation.getProposition().getPrix();
                    } else {
                        texte += "\nRefus negociateur: " + negociation.getVoeu().getPrix();
                    }
                    break;
            }
        }

        return texte += "\n";
    }
}


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
        String texte = String.format("\nNegociation n°%d, étape %d:\n%s -> %s", negociation.getId(), negociation.getStep(), emetteur.getName(), destinataire.getName());
        if (negociation.getProposition() != null) {
            texte += "\nRéférence proposition: " + negociation.getProposition().getId();
        }
        if (negociation.getVoeu()!= null) {
            texte += "\nRéférence voeu: " + negociation.getVoeu().getId();
        }
        if (message != null && message.length() > 0) {
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
            texte += "\nProposition prix : " + action;
            switch ((Action) action) {
                case SOUMISSION:
                    if (negociation.getNombreEchanges() > 0) texte += String.format("\nOccurence: %d/%d", negociation.getNombreEchanges(), negociation.getVoeu().getFrequence());
                    if (emetteur.getId() == negociation.getFournisseur().getId()) {
                        texte += "\nSoumission prix fournisseur: " + negociation.getProposition().getPrix();
                    } else {
                        texte += "\nSoumission prix negociateur: " + negociation.getVoeu().getPrix();
                    }
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
                        texte += "\nRefus fournisseur (conflit tarif minimal): " + negociation.getProposition().getTarifMinimal();
                    } else {
                        texte += "\nRefus negociateur (conflit budget): " + negociation.getVoeu().getTarifMaximum();
                    }
                    break;
            }
        }

        return texte += "\n";
    }
}

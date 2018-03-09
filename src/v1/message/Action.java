
package v1.message;

import sma.IAction;

/**
 * Actions spécifiques à Négociation.
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public enum Action implements IAction {
    SOUMISSION("Soumission"), // Proposition => prix ; Appel offre => appel offre
    REFUS("Refused_offre"), // Prix | Appel offre
    ACCEPTATION("Accepted_offre"); // Prix | Appel offre

    private final String texte;
    
    Action(String texte) {
        this.texte = texte;
    }
    
    @Override
    public String getTexte() {
        return texte;
    }
}

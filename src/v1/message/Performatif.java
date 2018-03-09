
package v1.message;

import sma.IPerformatif;

/**
 * Performatives spécifiques à Negociation.
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public enum Performatif implements IPerformatif {
    APPEL_OFFRE("Appel d'offre"), // Appel_offre
    PROPOSITION("Proposition");

    private final String texte;
    
    Performatif(String texte) {
        this.texte = texte;
    }

    @Override
    public String getTexte() {
        return texte;
    }
}

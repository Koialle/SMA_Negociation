
package sma;

/**
 * Preformatifs ACL - KQML
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public enum Performatif implements IPerformatif {
    REQUEST("Demande de faire"),
    REPLY("Répond"),
    INFORM("Informe"),
    ASK("Demande"),
    TELL("Indique"),
    SUBSCRIBE("Ecoute");

    private final String texte;
    
    Performatif(String texte) {
        this.texte = texte;
    }

    @Override
    public String getTexte() {
        return texte;
    }
}

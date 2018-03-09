
package sma;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public enum Action implements IAction {
    ACTION_REQUEST("Request"),
    ACTION_PRIX_NEGOCIATEUR("Prix_Négociateur"),
    ACTION_PRIX_FOURNISSEUR("Prix_Fournisseur"),
    ACTION_REFUSED_NEGOCIATION("Refused_negociation"), // Proposition
    ACTION_ACCEPTED_NEGOCIATION("Accepted_negociation"), // Proposition
    ACTION_REFUSED_OFFRE("Refused_offre"),
    ACTION_ACCEPTED_OFFRE("Accepted_offre");
    
    private final String texte;
    
    Action(String texte) {
        this.texte = texte;
    }
    
    @Override
    public String getTexte() {
        return texte;
    }
}

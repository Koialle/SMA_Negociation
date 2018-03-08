
package v1;

import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie
 * @author Mélanie
 */
public class Voeu extends Service {
    private static int cpt = 0;
    
    private int id;
    private boolean traite;
    private Proposition proposition;
    private Fournisseur fournisseur;

    public Voeu(String type, String arrivee, String depart, int dateDepart, int dateArrivee,float prixDepart, float tarifMaximum) {
        super();
        this.id = cpt++;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.arrivee = arrivee;
        this.depart = depart;
        this.type = type;
        this.prixDepart = prixDepart;
        this.prix = prixDepart;
        this.tarifMaximum = tarifMaximum;
    }

    public boolean isTraite() {
        return traite;
    }

    public void setTraite(boolean traite) {
        this.traite = traite;
    }

    public Proposition getProposition() {
        return proposition;
    }

    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof Voeu) {
                return ((Voeu) obj).id == this.id;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        String message = super.toString();
        //@TODO traité
        return message;
    }
}


package v1.model;

import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie
 * @author Mélanie
 */
public class Voeu extends Service {
    private static int cpt = 0;

    public enum Etat {
        NON_TRAITE,
        TRAITE,
        TERMINE
    }

    private int id;
    private Etat etat = Etat.NON_TRAITE;
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
        this.tarifMaximum = tarifMaximum;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
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

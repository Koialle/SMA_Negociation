
package v1.model;

import java.util.ArrayList;
import java.util.List;
import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie
 * @author Mélanie
 */
public class Voeu extends Service {
    public enum Etat {
        NON_TRAITE,
        EN_TRAITEMENT,
        A_REBOUCLER,
        RESOLU
    }

    private Etat etat = Etat.NON_TRAITE;
    private Proposition proposition;
    private Fournisseur fournisseur;
    private List<Negociation> negociations;

    /**
     * Billet recherché
     * @param type
     * @param depart
     * @param arrivee
     * @param dateDepart
     * @param dateArrivee
     * @param prixDepart
     * @param tarifMaximum Budget à ne pas dépasser
     */
    public Voeu(String type, String depart, String arrivee, int dateDepart, int dateArrivee,float prixDepart, float tarifMaximum) {
        super();
        this.id = cpt++;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.arrivee = arrivee;
        this.depart = depart;
        this.type = type;
        this.prixDepart = prixDepart;
        this.tarifMaximum = tarifMaximum;
        this.negociations = new ArrayList();
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
    
    public void addNegociation(Negociation negociation) {
        this.negociations.add(negociation);
    }
    
    public List<Negociation> getNegociations() {
        return this.negociations;
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

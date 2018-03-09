
package v1.model;

import java.util.ArrayList;
import java.util.List;
import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public abstract class Service {
    protected static int cpt = 0;

    public static String BILLET_AVION = "Avion";
    public static String BILLET_TRAIN = "Train";
    
    public static String CROISSANCE_LINEAIRE = "Lineaire";
    public static String CROISSANCE_NULLE = "None";

    protected int id; // Service
    protected int dateDepart; // Billet
    protected int dateArrivee; // Billet
    protected String arrivee; // Billet
    protected String depart; // Billet
    protected String type; // Billet
    protected float prixDepart; // Service: Paramètre stratégique Négociateur
    protected float prix; // Service
    protected float tarifMinimal; // Service: Contrainte Fournisseur
    protected float tarifMaximum; // Service: Budget, Contrainte Négociateur
    protected int dateButoire; // Service: Date de ventre|achat au plus tard, Contrainte Fournisseur|Negociateur
    protected int dateMVS; // Service: Date de Mise en Vente Souhaitée, Préférence Fournisseur
    protected List<Fournisseur> fournisseursPreferes; // Service: Préférences Negociateur
    protected int frequence = 6; // Service: Nb propositions dans une négociation, Paramètre stratégique Négociateur
    protected String typeCroissance = CROISSANCE_LINEAIRE; // Service: Stratégie de croissance, Paramètre stratégique Négociateur
    protected float croissance; // Service: < %10 par itération, Paramètre stratégique Négociateur

    public Service(){
        this.fournisseursPreferes = new ArrayList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(int dateDepart) {
        this.dateDepart = dateDepart;
    }

    public int getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(int dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public String getArrivee() {
        return arrivee;
    }

    public void setArrivee(String arrivee) {
        this.arrivee = arrivee;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public float getPrixDepart() {
        return prixDepart;
    }

    public void setPrixDepart(float prixDepart) {
        this.prixDepart = prixDepart;
    }

    public float getTarifMinimal() {
        return tarifMinimal;
    }

    public void setTarifMinimal(float tarifMinimal) {
        this.tarifMinimal = tarifMinimal;
    }

    public float getTarifMaximum() {
        return tarifMaximum;
    }

    public void setTarifMaximum(float tarifMaximum) {
        this.tarifMaximum = tarifMaximum;
    }

    public int getDateButoire() {
        return dateButoire;
    }

    public void setDateButoire(int dateButoire) {
        this.dateButoire = dateButoire;
    }

    public int getDateMVS() {
        return dateMVS;
    }

    public void setDateMVS(int dateMVS) {
        this.dateMVS = dateMVS;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public List<Fournisseur> getFournisseursPreferes() {
        return fournisseursPreferes;
    }

    public void setFournisseursPreferes(List<Fournisseur> fournisseursPreferes) {
        this.fournisseursPreferes = fournisseursPreferes;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public String getTypeCroissance() {
        return typeCroissance;
    }

    public void setTypeCroissance(String typeCroissance) {
        this.typeCroissance = typeCroissance;
    }

    public float getCroissance() {
        return croissance;
    }

    public void setCroissance(float croissance) {
        if (croissance > 0) {
            if (croissance > 1) {
                this.croissance = 1;
                System.err.printf("Le taux ne croissance (%.2f) ne peut pas être supérieur à 100%, soit 1.\n", croissance);
            } else {
                this.croissance = croissance;
            }
            this.typeCroissance = CROISSANCE_LINEAIRE;
        } else {
            this.croissance = croissance;
            this.typeCroissance = CROISSANCE_NULLE;
        }
    }

    @Override
    public String toString() {
        String message = "\n\tRéférence : " + id;

        if (type != null) message += "\n\tType billet : " + type;
        if (depart != null) message += "\n\tDépart : " + depart;
        if (arrivee != null) message += "\n\tArrivée : " + arrivee;
        if (dateDepart != 0) message += "\n\tDate départ : " + dateDepart;
        if (dateArrivee != 0) message += "\n\tDate arrivée : " + dateArrivee;
        if (prix != 0) message += "\n\tPrix : " + prix;
        
        message += "\nService (privé):";
        if (prixDepart != 0) message += "\n\tPrix depart (N) : " + prixDepart;
        if (tarifMinimal != 0) message += "\n\tTarif minimal (F) : " + tarifMinimal;
        if (tarifMaximum != 0) message += "\n\tTarif maximal (N) : " + tarifMaximum;
        if (dateButoire != 0) message += "\n\tDate butoire (F|N) : " + dateButoire;
        if (dateMVS != 0) message += "\n\tDate départ (F) : " + dateMVS;
        if (frequence != 0) message += "\n\tFréquence : " + frequence;
        if (typeCroissance != null) message += "\n\tType de croissance : " + typeCroissance;
        if (!typeCroissance.equals(CROISSANCE_NULLE)) message += "\n\tTaux de croissance : " + croissance;
        if (fournisseursPreferes.size() > 0) message += "\n\tFrounisseurs préférés : " + fournisseursPreferes.size(); //@TODO liste des fournisseurs

        return message;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //@TODO
    }
}

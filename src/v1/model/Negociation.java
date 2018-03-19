package v1.model;

import v1.agent.Negociateur;
import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Negociation {
    public enum Etat {
        REFUSEE,
        ACCEPTEE,
        EN_COURS
    }

    private static int cptNegociation = 0;
    private int nombreEchanges;
    private int id;
    private Negociateur negociateur;
    private Fournisseur fournisseur;
    private Voeu voeu;
    private Proposition proposition;
    private Etat etat= Etat.EN_COURS;
    private int step = 0;

    public Negociation(Negociateur negociateur, Fournisseur fournisseur, Voeu voeu) {
        this.id = cptNegociation++;
        this.nombreEchanges = 0;
        this.negociateur = negociateur;
        this.fournisseur = fournisseur;
        this.voeu = voeu;
        this.voeu.setEtat(Voeu.Etat.EN_TRAITEMENT);
    }

    public int getNombreEchanges() {
        return nombreEchanges;
    }

    public void setNombreEchanges(int nombreEchanges) {
        this.nombreEchanges = nombreEchanges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Negociateur getNegociateur() {
        return negociateur;
    }

    public void setNegociateur(Negociateur negociateur) {
        this.negociateur = negociateur;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Voeu getVoeu() {
        return voeu;
    }

    public void setVoeu(Voeu voeu) {
        this.voeu = voeu;
    }

    public Proposition getProposition() {
        return proposition;
    }

    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public void accepter() {
        System.out.println("Acceptation voeu n°" + voeu.getId());
        setEtat(Etat.ACCEPTEE);
        voeu.setEtat(Voeu.Etat.RESOLU);
        voeu.setProposition(proposition);
        voeu.setFournisseur(fournisseur);
        fournisseur.removeProposition(proposition);
    }
    
    public boolean isActive() {
        return etat == Etat.EN_COURS;
    }

    public void incrementeNbEchanges() {
        nombreEchanges++;
    }
    
    public void incrementeStep() {
        step++;
    }

    public int getStep() {
        return step;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //@TODO
    }
}

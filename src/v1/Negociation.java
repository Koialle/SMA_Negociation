package v1;

import v1.agent.Negociateur;
import v1.agent.Fournisseur;

/**
 *
 * @author Ophélie EOUZAN
 */
public class Negociation {
    private static int cptNegociation = 0;
    private int nombreEchanges;
    private int identifiantNegociation;
    private Negociateur negociateur;
    private Fournisseur fournisseur;
    private Voeu voeu;
    private Proposition proposition;
    private boolean refused = false; //@TODO Etat REFUSED/ACCEPTED
    private boolean accepted = false;

    public Negociation(Negociateur negociateur, Fournisseur fournisseur, Voeu voeu) {
        this.identifiantNegociation = cptNegociation++;
        this.nombreEchanges = 0;
        this.negociateur = negociateur;
        this.fournisseur = fournisseur;
        this.voeu = voeu;
    }

    public int getNombreEchanges() {
        return nombreEchanges;
    }

    public void setNombreEchanges(int nombreEchanges) {
        this.nombreEchanges = nombreEchanges;
    }

    public int getIdentifiantNegociation() {
        return identifiantNegociation;
    }

    public void setIdentifiantNegociation(int identifiantNegociation) {
        this.identifiantNegociation = identifiantNegociation;
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

    public boolean isRefused() {
        return refused;
    }

    public void setRefused(boolean refused) {
        this.refused = refused;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
        
        if (accepted) {
            voeu.setProposition(proposition);
            voeu.setFournisseur(fournisseur);
            voeu.setEtat(Voeu.Etat.TERMINE);
            // Faut-il aussi rattacher le voeu à la proposition ? Relation de 1-1 entre voeu et proposition
        }
    }
    
    public boolean isActive() {
        return !(accepted || refused);
    }

    public void incrementeNbEchanges() {
        nombreEchanges++;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v1;

import sma.Message;
import v1.agent.AgentNegociation;

/**
 *
 * @author Epulapp
 */
public class MessageNegociation extends Message {
    public static final String PERFORMATIF_APPEL_OFFRE = "Appel_offre";
    public static final String PERFORMATIF_PROPOSITION = "Proposition";

    public static final String ACTION_REQUEST = "Request";
    public static final String ACTION_PRIX_NEGOCIATEUR = "Prix_Négociateur";
    public static final String ACTION_PRIX_FOURNISSEUR = "Prix_Fournisseur";
    public static final String ACTION_REFUSED_NEGOCIATION = "Refused_negociation";
    public static final String ACTION_ACCEPTED_NEGOCIATION = "Accepted_negociation";
    public static final String ACTION_REFUSED_OFFRE = "Refused_offre";
    public static final String ACTION_ACCEPTED_OFFRE = "Accepted_offre";
    
    protected Negociation negociation;

    /**
     * @param performatif
     * @param action 
     */
    public MessageNegociation(String performatif, String action) {
        this.performatif = performatif;
        this.action = action;
    }

    /**
     * Appel d'offre du Negociateur
     *
     * @param emetteur
     * @param destinataire
     * @param negociation
     */
    public MessageNegociation(AgentNegociation emetteur, AgentNegociation destinataire, Negociation negociation) {
        this.emetteur = emetteur;
        this.destinataire = destinataire;
        this.negociation = negociation;
        this.performatif = MessageNegociation.PERFORMATIF_APPEL_OFFRE;
        this.action = MessageNegociation.ACTION_REQUEST;
    }

    public Negociation getNegociation() {
        return negociation;
    }

    public void setNegociation(Negociation negociation) {
        this.negociation = negociation;
    }

    @Override
    public String toString() {
        String message = "";
        if (performatif.equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)) {
            if (action.equals(MessageNegociation.ACTION_REQUEST)) {
                message += "Requète d'appel d'offre\n";
                if (this.negociation.getVoeu() != null) {
                    Voeu voeu = this.negociation.getVoeu();
                    message += "Demande :\n" + voeu.toString();
                }
            } else {
                message += "Appel d'offre : " + action;
            }
        } else {
            message += this.performatif + " - " + this.action;
            if (this.negociation.getProposition() != null) {
                Proposition proposition = this.negociation.getProposition();
                message += "Proposition:\n" + proposition.toString();
            }
        }
        
        message += "\nEmetteur: " + emetteur.getName();
        message += "\nDestinataire: " + destinataire.getName();
        
        message += "\n";

        return message;
    }
}

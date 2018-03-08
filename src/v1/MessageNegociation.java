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
    public static final String ACTION_REFUSED_NEGOCIATION = "Refused_negociation"; // Proposition
    public static final String ACTION_ACCEPTED_NEGOCIATION = "Accepted_negociation"; // Proposition
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
        String message = "Negociation n°" + negociation.getIdentifiantNegociation();
        if (performatif.equals(MessageNegociation.PERFORMATIF_APPEL_OFFRE)) {
            if (action.equals(MessageNegociation.ACTION_REQUEST)) {
                message += "\nRequète d'appel d'offre\n";
                if (negociation.getVoeu() != null) {
                    message += "Demande :\n" + negociation.getVoeu().toString();
                }
            } else {
                message += "\nAppel d'offre : " + action;
            }
        } else if (performatif.equals(MessageNegociation.PERFORMATIF_PROPOSITION)) {
            if (negociation.getNombreEchanges() > 0) message += "\nOccurence: " + negociation.getNombreEchanges();
            switch (action) {
                case ACTION_PRIX_FOURNISSEUR:
                    if (negociation.getProposition() != null) {
                        message += "\nProposition: \n" + negociation.getProposition();
                    }
                    break;
                case ACTION_PRIX_NEGOCIATEUR:
                    if (negociation.getVoeu()!= null) {
                        message += "\nVoeu: \n" + negociation.getVoeu();
                    }
                    break;
                default:
                    message += "\n" + this.performatif + " - " + this.action;
                    if (negociation.getProposition() != null) {
                        message += "\nProposition fournisseur: " + negociation.getProposition().getPrix();
                    }
                    if (negociation.getVoeu()!= null) {
                        message += "\nProposition negociateur: " + negociation.getVoeu().getPrix();
                    }
            }
        }
        
        message += "\nEmetteur: " + emetteur.getName();
        message += "\nDestinataire: " + destinataire.getName();
        
        message += "\n";

        return message;
    }
}

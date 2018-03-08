
package v1;

import v1.agent.Negociateur;
import v1.agent.Fournisseur;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import v1.view.Dialog;

/**
 *
 * @author Epulapp
 */
public class Main {
    public static void main(String args[])
    {
        // Schénario 1
        // - 1 fournisseur avec 1 proposition
        // - 1 negociateur avec 1 appel d'offre

        List<Fournisseur> fournisseurs = new ArrayList<>();
        List<Negociateur> negociateurs = new ArrayList<>();

        // Create fournisseur
        Fournisseur f1 = new Fournisseur();
        f1.setPropositions(getListePropositions());
        fournisseurs.add(f1);

        // Create negociateur
        Negociateur n1 = new Negociateur();
        n1.setVoeux(getListeVoeux());
        negociateurs.add(n1);

        for (Negociateur negociateur: negociateurs) {
            negociateur.setFournisseurs(fournisseurs);
        }
        
        for (Fournisseur fournisseur: fournisseurs) {
            fournisseur.setNegociateurs(negociateurs);
        }
        
        for (Negociateur negociateur: negociateurs) {
            negociateur.start();
        }
        
        for (Fournisseur fournisseur: fournisseurs) {
            fournisseur.start();
        }

        // Scénario 2
        // Create fournisseur avec N propositions
        // Create negociateur avec M appels d'offre
        
        // Scénario 3
        // Create X fournisseurs avec N propositions
        // Create Y negociateurs avec M appels d'offre
    }
    
    private static List<Voeu> getListeVoeux()
    {
        List<Voeu> voeux = new ArrayList();
        
        Voeu v1 = new Voeu();
        v1.setDateDepart(12);
        v1.setDateArrivee(14);
        v1.setDepart("Lyon");
        v1.setArrivee("Paris");
        v1.setType(Service.BILLET_TRAIN);
        voeux.add(v1);
        
        Voeu v2 = new Voeu();
        v2.setDateDepart(12);
        v2.setDateArrivee(15);
        v2.setDepart("Lyon");
        v2.setArrivee("Paris");
        v2.setType(Service.BILLET_AVION);
        voeux.add(v2);
        
//        Voeu v3 = new Voeu();
//        v2.setDateDepart(12);
//        v2.setDateArrivee(14);
//        v2.setDepart("France");
//        v2.setArrivee("Japon");
//        v2.setType(Service.BILLET_AVION);
//        v2.setPrixDepart(90);
//        v2.setTarifMaximum(120);
//        v2.setCroissance(10);
//        voeux.add(v3);
        
        return voeux;
    }
    
    private static List<Proposition> getListePropositions()
    {
        List<Proposition> propositions = new ArrayList();
        
        Proposition p1 = new Proposition();
        p1.setDateDepart(12);
        p1.setDateArrivee(14);
        p1.setDepart("Lyon");
        p1.setArrivee("Paris");
        p1.setType(Service.BILLET_TRAIN);
        p1.setTarifMinimal(10);
        propositions.add(p1);
        
//        Proposition p2 = new Proposition();
//        p2.setDateDepart(10);
//        p2.setDateArrivee(21);
//        p2.setDepart("France");
//        p2.setArrivee("Japon");
//        p2.setType(Service.BILLET_TRAIN);
//        p2.setDateButoire(5);
//        p2.setTarifMinimal(100);
//        propositions.add(p2);
        
        return propositions;
    }
}

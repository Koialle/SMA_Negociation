
package v1;

import v1.model.Voeu;
import v1.model.Service;
import v1.model.Proposition;
import v1.agent.Negociateur;
import v1.agent.Fournisseur;
import java.util.ArrayList;
import java.util.List;

/**
 * Lancement de la négociation.
 *
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 *
 * Scénatrio 2:
 * - 1 fournisseur
 * - 1 négociateur
 * - N voeux
 * - M offres dont 2 compatibles avec un même voeu => Permet de voir la stratégie de proposition d'1 fournisseur
 * 
 * Améliorations possibles:
 * - Interface d'ajout de fournisseurs/négociateurs
 * - Interface d'ajout d'offres/voeux
 *
 * Ce programme est de type déterministe si les offres et propositions 
 * des fournisseurs/négociateurs sont les mêmes à chaque execution.
 * 
 * La stochasticité du programme réside dans l'aléatoire des offres et 
 * voeux effectués par les acteurs externes au système.
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
        
        Voeu v1 = new Voeu(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 0, 5, 25); // Date d'arrivée indéfinie
        v1.setFrequence(5);
        v1.setTypeCroissance(Voeu.CROISSANCE_LINEAIRE);
        v1.setCroissance(1);
        voeux.add(v1);
        
        Voeu v2 = new Voeu(Service.BILLET_AVION, "Lyon", "Paris", 12, 14, 15, 50);
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
        
        Proposition p1 = new Proposition(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 14, 30, 20);
        propositions.add(p1);

        Proposition p2 = new Proposition(Service.BILLET_AVION, "France", "Japon", 10, 21, 120, 100);
        p2.setDateButoire(5);
        propositions.add(p2);
        
        //@TODO tester la stratégie du fournisseurs lorsque plusieurs propositions possibles
//        Proposition p3 = new Proposition(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 30, 10, 10);
//        propositions.add(p3);
        
        return propositions;
    }
}

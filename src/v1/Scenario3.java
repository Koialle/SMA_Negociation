
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
 * @TODO Mettre ci-dessous dans README GitHub.
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
 * - Gestion des préférences et contraintes manquantes : fournisseurs préférés, dates de vente|achat au plus tard, ...
 * - Scénario 3: 
 *      - Négociations concourrantes
 *      - Affectation de voeux et propositions aléatoirement
 * 
 * Paramètres:
 * - Nb fournisseurs|négociateurs
 * - Nb voeux|propositions
 * - Voeux: fréquence, type croissance, taux croissance, départ, arrivée, prix, départ, tarif max, (date départ), (date arrivée), ...
 * - Propositions : prix départ, tarif min, ...
 * /!\ Les valeurs entre parenthèses sont optionnelles. 
 * Bien sûr, il faut donner le plus de renseinements possibles sur le service recherché pour faire fonctionner le système...
 *
 * Remarques :
 * - Ce programme est de type déterministe si les offres et propositions 
 * des fournisseurs/négociateurs sont les mêmes à chaque execution.
 * - La stochasticité du programme réside dans l'aléatoire des offres et 
 * voeux effectués par les acteurs externes au système.
 * - /!\ On considère que les agents ne connaissent pas les contraintes des 
 * autres agents (tarif minimal, maximal, etc)
 */
public class Scenario3 {
    public static void main(String args[])
    {
        // Scénario 3
        
        List<Fournisseur> fournisseurs = new ArrayList<>();
        List<Negociateur> negociateurs = new ArrayList<>();

        // Version 1
        // Create 1 negociateur avec 1 voeu
        // Create 3 fournisseurs avec 1 proposition chacun correspondant au voeu du négociateur

        // Create 1 negociateur
        Negociateur n1 = new Negociateur();        
        Voeu v1 = new Voeu(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 0, 5, 25); // Date d'arrivée indéfinie
        v1.setFrequence(5);
        v1.setTypeCroissance(Voeu.CROISSANCE_LINEAIRE);
        v1.setCroissance(1); // 100%
        n1.addVoeu(v1);
        negociateurs.add(n1);

        // Create 3 fournisseurs
        Fournisseur f1 = new Fournisseur();
        f1.addProposition(new Proposition(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 14, 30, 20));
        fournisseurs.add(f1);
        
        Fournisseur f2 = new Fournisseur();
        f2.addProposition(new Proposition(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 16, 40, 15));
        fournisseurs.add(f2);

        Fournisseur f3 = new Fournisseur();
        f3.addProposition(new Proposition(Service.BILLET_TRAIN, "Lyon", "Paris", 12, 20, 15, 10));
        fournisseurs.add(f3);

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
        
        // Version 2
        // Plusieurs stratégies forunisseurs :
        // - Prix le plus élevé dès le départ
        // - Prix le plus bas avec croissance à définir (cf. stratégie négociateur)
        // @TODO

        // Version 3
        // Create X fournisseurs avec N propositions
        // Create Y negociateurs avec M appels d'offre
        // @TODO
    }
}


package v1;

/**
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Proposition extends Service {
    private static int cpt = 0;

    public Proposition(String type, String arrivee, String depart, int dateDepart, int dateArrivee, float prixDepart, float tarifMinimal) {
        super();
        this.id = cpt++;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.arrivee = arrivee;
        this.depart = depart;
        this.type = type;
        this.prixDepart = prixDepart;
        this.prix = prixDepart;
        this.tarifMinimal = tarifMinimal;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof Proposition) {
                return ((Proposition) obj).id == this.id;
            }
        }

        return false;
    }
}

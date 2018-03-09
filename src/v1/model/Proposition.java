
package v1.model;

/**
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Proposition extends Service {
    public Proposition(String type, String depart, String arrivee, int dateDepart, int dateArrivee, float prixDepart, float tarifMinimal) {
        super();
        this.id = cpt++;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.arrivee = arrivee;
        this.depart = depart;
        this.type = type;
        this.prixDepart = prixDepart;
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

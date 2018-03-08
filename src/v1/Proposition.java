
package v1;

/**
 * @author Ophélie EOUZAN
 * @author Mélanie DUBREUIL
 */
public class Proposition extends Service {
    private static int cpt = 0;

    public Proposition() {
        super();
        this.id = cpt++;
    }

//    public Proposition(Service service) {
//        super(service);
//        this.id = cpt++;
//    }

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

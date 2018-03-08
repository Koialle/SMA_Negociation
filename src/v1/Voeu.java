
package v1;

/**
 *
 * @author Ophélie
 * @author Mélanie
 */
public class Voeu extends Service {
    private static int cpt = 0;
    
    private int id;
    private boolean traite;

    public Voeu() {
        this.id = cpt++;
    }
    
//    public Voeu(Service service) {
//        super(service);
//        this.id = cpt++;
//    }

    public boolean isTraite() {
        return traite;
    }

    public void setTraite(boolean traite) {
        this.traite = traite;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof Voeu) {
                return ((Voeu) obj).id == this.id;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        String message = super.toString();
        //@TODO traité
        return message;
    }
}


package sma;

import sma.Message;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import v1.MessageNegociation;
import v1.Negociation;
import v1.Proposition;
import v1.view.Dialog;

/**
 * Agent cognitif évoluant dans le jeu du taquin, communiquant avec les autres agents :
 *  - Objectif local: atteindre une position objectif
 *  - Objectif global: tous les agents doivent atteindre leur position objectif
 * 
 * 
 * @author DUBREUIL Mélanie
 * @author EOUZAN Ophélie
 */
public abstract class Agent extends Thread {

    /**
     * File de messages reçus par l'agent, dépilés suivant une logique FIFO (First In, First Out).
     */
    protected Queue<Message> messages;

    public Agent() {
        this.messages = new LinkedList<>();
    }

    /**
     * Execute la logique de l'agent.
     */
    @Override
    public void run() {
        runAgent();
    }
    
    abstract public void runAgent();
    
    abstract protected void sendMessage(Message message);

    /**
     * Dépile les messages reçus par les autres agents.
     */
    abstract protected void checkAllMessages();
//     throws Exception {
        //@TODO handle blocking behaviour : when two agents want to exchange position
//        Message message = null;
//        while (messages.size() > 0) {
//            message = messages.poll();
//            if (message != null) {
//                if (message.getPerformatif().equals(Message.PERFORMATIVE_REQUEST) && message.getAction().equals(Message.ACTION_MOVE)) {
//                    Agent emetteur = message.getEmetteur();
//                    System.out.printf("Agent %s (%d, %d) : Message reçu de %s pour la position (%d, %d)\n", symbole, position.getX(), position.getY(), emetteur.getSymbole(), message.getPosition().getX(), message.getPosition().getY());
//
//                    if (this.position.equals(message.getPosition())) {
//                        Position newPosition = Grille.move(this, true); // get position
//
//                        if (newPosition != null) {
//                            System.out.printf("Agent %s (%d, %d) : Tentative de déplacement vers (%d, %d) à la demande de %s\n", symbole, position.getX(), position.getY(), newPosition.getX(), newPosition.getY(), emetteur.getSymbole());
//                        }
//
//                        this.processMovement(newPosition);
//                    } else {
//                        System.out.printf("Agent %s (%d, %d) : Demande de déplacement par %s expirée\n", symbole, position.getX(), position.getY(), emetteur.getSymbole());
//                    }
//                }
//            }
//        }
//    }

    protected synchronized void addMessage(Message message) {
        this.messages.add(message);
    }

    protected synchronized void removeMessage(Message message) {
        this.messages.remove(message);
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public synchronized void setMessages(Queue<Message> messages) {
        this.messages = messages;
    }
}

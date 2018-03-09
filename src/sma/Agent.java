
package sma;

import java.util.LinkedList;
import java.util.Queue;

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

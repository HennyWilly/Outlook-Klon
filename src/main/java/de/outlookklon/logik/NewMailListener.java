package de.outlookklon.logik;

import java.util.EventListener;

/**
 * Interface, das die Signatur einer Methode zum Behandeln einer neuen Mail
 * bereitstellt
 *
 * @author Hendrik Karwanni
 */
public interface NewMailListener extends EventListener {

    /**
     * Diese Methode wird aufgerufen, wenn eine neue Mail empfangen wurde
     *
     * @param e Enth�llt Informationen �ber die erhaltene Mail
     */
    public void newMessage(NewMailEvent e);
}

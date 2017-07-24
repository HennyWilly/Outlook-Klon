package de.outlookklon.application.mailclient;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.StringTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bei manchen Anbietern, z.B. Hotmail oder Yahoo, kann die MessageID nicht auf
 * normalem Wege mit dem standardmäßigen <code>MessageIDTerm</code> abgerufen
 * werden. Daher wird hier ein neuer <code>SeachTerm</code> implementiert, der
 * die Mails zuerst öffnet und dann erst die ID ausliest. Sollte nur verwendet
 * werden, wenn keine Mail gefunden wurde, da dieses Suchverfahren langsamer
 * ist, als der ursprüngliche <code>MessageIDTerm</code>.
 *
 * @author Hendrik Karwanni
 */
public class EnhancedMessageIDTerm extends StringTerm {

    private static final long serialVersionUID = -298319831328120350L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnhancedMessageIDTerm.class);

    // TODO Redundant
    private static final String MESSAGE_ID_HEADER_NAME = "Message-Id";

    /**
     * Erstellt eine neue Instanz der Klasse MyMessageIDTerm mit der zu
     * suchenden ID
     *
     * @param messageID ID der zu suchenden Mail
     */
    public EnhancedMessageIDTerm(final String messageID) {
        super(messageID);
    }

    @Override
    public boolean match(final Message message) {
        boolean result = false;

        try {
            String[] tmpId = message.getHeader(MESSAGE_ID_HEADER_NAME);
            String id = null;

            if (tmpId != null && tmpId.length == 1) {
                id = tmpId[0];
            } else if (message instanceof MimeMessage) {
                id = ((MimeMessage) message).getMessageID();
            }

            if (id != null) {
                result = super.match(id);
            }
        } catch (MessagingException ex) {
            LOGGER.warn("Error while getting message properies", ex);
        }

        return result;
    }
}

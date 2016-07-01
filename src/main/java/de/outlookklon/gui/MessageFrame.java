package de.outlookklon.gui;

import de.outlookklon.gui.helpers.Buttons;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextPane;

/**
 * Diese Frame dient zum Anzeigen und Bearbeiten von Krankheits- und
 * Abwesenheitsmeldungen.
 *
 * @author Hendrik Karwanni
 */
public class MessageFrame extends ExtendedDialog<String> {

    private static final long serialVersionUID = -426579552451278615L;

    private JTextPane textMessage;
    private JButton btnOk;
    private JButton btnAbort;
    private String info;

    /**
     * Erstellt ein neues {@link MeldungsFrame}.
     *
     * @param text Initialer Text des Fensters.
     * @param title Titel des Fensters.
     */
    public MessageFrame(String text, String title) {
        super(400, 365);

        initGui(text, title);
    }

    private void initGui(String text, String title) {
        setTitle(title);
        getContentPane().setLayout(null);

        textMessage = new JTextPane();
        textMessage.setBounds(0, 0, 394, 292);
        textMessage.setText(text);
        getContentPane().add(textMessage);

        btnOk = Buttons.getOkButton();
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                info = textMessage.getText();
                close();
            }
        });
        btnOk.setBounds(10, 303, 100, 23);
        getContentPane().add(btnOk);

        btnAbort = Buttons.getAbortButton();
        btnAbort.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnAbort.setBounds(120, 303, 100, 23);
        getContentPane().add(btnAbort);
    }

    @Override
    protected String getDialogResult() {
        return info;
    }
}

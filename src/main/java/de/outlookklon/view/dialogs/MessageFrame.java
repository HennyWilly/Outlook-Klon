package de.outlookklon.view.dialogs;

import de.outlookklon.localization.Localization;
import java.awt.Window;
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

    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 365;

    private final JTextPane textMessage;
    private final JButton btnOk;
    private final JButton btnAbort;

    private String info;

    /**
     * Erstellt ein neues {@link MeldungsFrame}.
     *
     * @param parent Das Vaterfenster des Dialogs
     * @param text Initialer Text des Fensters.
     * @param title Titel des Fensters.
     */
    public MessageFrame(Window parent, String text, String title) {
        super(parent, DIALOG_WIDTH, DIALOG_HEIGHT);

        textMessage = new JTextPane();
        btnOk = new JButton();
        btnAbort = new JButton();

        initGui(text, title);
        updateTexts();
    }

    @Override
    public void updateTexts() {
        btnOk.setText(Localization.getString("Button_Ok"));
        btnAbort.setText(Localization.getString("Button_Abort"));
    }

    private void initGui(String text, String title) {
        setTitle(title);
        getContentPane().setLayout(null);

        textMessage.setBounds(0, 0, 394, 292);
        textMessage.setText(text);
        getContentPane().add(textMessage);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                info = textMessage.getText();
                close();
            }
        });
        btnOk.setBounds(10, 303, 100, 23);
        getContentPane().add(btnOk);

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

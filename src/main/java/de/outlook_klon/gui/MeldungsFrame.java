package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextPane;

public class MeldungsFrame extends ExtendedDialog<String> {

    private static final long serialVersionUID = -426579552451278615L;

    private JTextPane textAbwes;
    private JButton btnOk;
    private JButton btnAbbrechen;
    private String info;

    private void initGui(String text, String titel) {
        setTitle(titel);
        getContentPane().setLayout(null);

        textAbwes = new JTextPane();
        textAbwes.setBounds(0, 0, 394, 292);
        textAbwes.setText(text);
        getContentPane().add(textAbwes);

        btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                info = textAbwes.getText();
                close();
            }
        });
        btnOk.setBounds(10, 303, 100, 23);
        getContentPane().add(btnOk);

        btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnAbbrechen.setBounds(120, 303, 100, 23);
        getContentPane().add(btnAbbrechen);
    }

    public MeldungsFrame(String text, String titel) {
        super(400, 365);

        initGui(text, titel);
    }

    protected String getDialogResult() {
        return info;
    }

    public void setText(String text) {
        textAbwes.setText(text);
    }

    public String getText() {
        return getText();
    }
}

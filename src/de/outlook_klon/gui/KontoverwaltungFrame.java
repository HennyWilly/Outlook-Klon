package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.mailclient.MailAccount;

public class KontoverwaltungFrame extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5036893845172118794L;
	
	private JButton btnNeuesKonto;
	private JList<MailAccount> lstKonten;

	public KontoverwaltungFrame(Benutzer benutzer) {
		setSize(711, 695);
		setResizable(false);
		
		setModal(true);
		setTitle("Konten-Einstellungen");
		getContentPane().setLayout(null);
		
		DefaultListModel<MailAccount> listModel = new DefaultListModel<MailAccount>();
		for(MailAccount acc : benutzer) {
			listModel.addElement(acc);
		}
		
		lstKonten = new JList<MailAccount>(listModel);
		lstKonten.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstKonten.setBounds(10, 11, 239, 570);
		getContentPane().add(lstKonten);
		
		btnNeuesKonto = new JButton("Neues E-Mail-Konto");
		btnNeuesKonto.setBounds(10, 592, 169, 23);
		btnNeuesKonto.addActionListener(this);
		getContentPane().add(btnNeuesKonto);
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBounds(583, 633, 112, 23);
		getContentPane().add(btnAbbrechen);
		
		JButton btnOK = new JButton("OK");
		btnOK.setBounds(484, 633, 89, 23);
		getContentPane().add(btnOK);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg) {
		Object sender = arg.getSource();
		
		if(sender == btnNeuesKonto) {
			KontoFrame kf = new KontoFrame();
			MailAccount acc = kf.showDialog();
			if(acc != null) {
				DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>)lstKonten.getModel();
				model.addElement(acc);
			}
		}
	}
}

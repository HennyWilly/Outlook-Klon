package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.mailclient.MailAccount;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class KontoverwaltungFrame extends ExtendedDialog<MailAccount[]> {
	private static final long serialVersionUID = -5036893845172118794L;
	
	private MailAccount[] meineAccounts;
	
	private JButton btnNeuesKonto;
	private JList<MailAccount> lstKonten;
	private JButton btnAbbrechen;
	private JButton btnOK;

	public KontoverwaltungFrame() {
		meineAccounts = null;
		
		setSize(711, 695);
		setTitle("Konten-Einstellungen");
		getContentPane().setLayout(null);
		
		DefaultListModel<MailAccount> listModel = new DefaultListModel<MailAccount>();
		for(MailAccount acc : Benutzer.getInstanz()) {
			listModel.addElement(acc);
		}
		
		lstKonten = new JList<MailAccount>(listModel);
		lstKonten.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				//TODO ListSelection Handler
			}
		});
		lstKonten.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstKonten.setBounds(10, 11, 239, 570);
		
		JScrollPane kontenScroller = new JScrollPane(lstKonten);		
		kontenScroller.setBounds(lstKonten.getBounds());
		getContentPane().add(kontenScroller);
		
		btnNeuesKonto = new JButton("Neues E-Mail-Konto");
		btnNeuesKonto.setBounds(10, 592, 169, 23);
		btnNeuesKonto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				KontoFrame kf = new KontoFrame();
				MailAccount acc = kf.showDialog();
				if(acc != null) {
					DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>)lstKonten.getModel();
					model.addElement(acc);
				}
			}
		});
		getContentPane().add(btnNeuesKonto);
		
		btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBounds(583, 633, 112, 23);
		btnAbbrechen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				meineAccounts = null;
				close();
			}
		});
		getContentPane().add(btnAbbrechen);
		
		btnOK = new JButton("OK");
		btnOK.setBounds(484, 633, 89, 23);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>)lstKonten.getModel();	
				meineAccounts = new MailAccount[model.getSize()];
				for(int i = 0; i< meineAccounts.length; i++) {
					meineAccounts[i] = model.get(i);
				}
				close();
			}
		});
		getContentPane().add(btnOK);
	}

	@Override
	protected MailAccount[] getDialogResult() {
		return meineAccounts;
	}
}

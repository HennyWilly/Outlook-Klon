package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.JList;

import de.outlook_klon.logik.kontakte.Kontakt;

public class AdressbuchFrame extends JFrame implements ActionListener {
	public AdressbuchFrame() {
		
		JSplitPane horizontalSplit = new JSplitPane();
		getContentPane().add(horizontalSplit, BorderLayout.CENTER);
		
		JSplitPane verticalSplit = new JSplitPane();
		verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplit.setRightComponent(verticalSplit);
		
		tblKontakte = new JTable();
		tblKontakte.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
			},
			new String[] {
				"Name", "E-Mail-Adresse"
			}
		) {
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class<?>[] {
				String.class, String.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tblKontakte.getColumnModel().getColumn(1).setPreferredWidth(91);
		verticalSplit.setLeftComponent(tblKontakte);
		
		JTextPane txtDetails = new JTextPane();
		verticalSplit.setRightComponent(txtDetails);
		
		JList<ArrayList<Kontakt>> lstListen = new JList<ArrayList<Kontakt>>();
		horizontalSplit.setLeftComponent(lstListen);
	}
	private static final long serialVersionUID = 2142631007771154882L;
	private JTable tblKontakte;

	@Override
	public void actionPerformed(ActionEvent arg) {
		// TODO Auto-generated method stub

	}

}

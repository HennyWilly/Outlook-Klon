package de.outlook_klon.gui;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JButton;

import de.outlook_klon.logik.kalendar.Termin;

public class TerminFrame extends JDialog {
	

	private static final long serialVersionUID = 8451017422297429822L;
	private JTextField textBetreff;
	private JTextField textBeschreibung;
	private JTextField textOrt;
	
	private JSpinner date1;
	private JSpinner date2;
	
	private Termin mTermin;
	
	private void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	
	private void initFrame() {
		setModal(true);
		setTitle("Termin");
		JLabel lblNBetreff = new JLabel("Betreff:");
		JLabel lblOrt = new JLabel("Ort:");
		JLabel lblNewLabel = new JLabel("Startzeit:");
		JLabel lblEndzeit = new JLabel("Endzeit:");
		JLabel lblBeschreibung = new JLabel("Beschreibung:");
		
		textBetreff = new JTextField();
		textBetreff.setColumns(10);
		
		textBeschreibung = new JTextField();
		textBeschreibung.setColumns(10);
		
		textOrt = new JTextField();
		textOrt.setColumns(10);
		
		date1 = new JSpinner();
		date1.setModel(new SpinnerDateModel(new Date(1388271600000L), null, null, Calendar.DAY_OF_YEAR));
		
		date2 = new JSpinner();
		date2.setModel(new SpinnerDateModel(new Date(1388271600000L), null, null, Calendar.DAY_OF_YEAR));
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SpinnerDateModel model1 = (SpinnerDateModel)date1.getModel();
				SpinnerDateModel model2 = (SpinnerDateModel)date2.getModel();
				
				if(mTermin == null) {
					mTermin = new Termin(textBetreff.getText(),textOrt.getText(), model1.getDate(), model2.getDate(), textBeschreibung.getText());
				}
				else {

					mTermin.setBetreff(textBetreff.getText());
					mTermin.setOrt(textOrt.getText());
					mTermin.setText(textBeschreibung.getText());
					mTermin.setStartUndEnde(model1.getDate(), model2.getDate());
				}
				
				close();
			}
		});
		
		
		
		
		
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(23)
							.addComponent(btnOk)
							.addGap(102)
							.addComponent(btnAbbrechen))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblBeschreibung, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addGap(41)
							.addComponent(textBeschreibung))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(lblEndzeit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblOrt, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNBetreff, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE))
							.addGap(77)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(date1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(date2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(textBetreff, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
								.addComponent(textOrt))))
					.addContainerGap(8, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(42)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNBetreff)
						.addComponent(textBetreff, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOrt, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
						.addComponent(textOrt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblEndzeit)
								.addComponent(date2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(date1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBeschreibung)
						.addComponent(textBeschreibung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnAbbrechen))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
	}
	
	
	public TerminFrame(){
		mTermin = null;
		initFrame();
		
	}
	
	public TerminFrame(Termin t){
		mTermin = t;
		initFrame();
		textBetreff.setText(t.getBetreff());
		textOrt.setText(t.getOrt());
		textBeschreibung.setText(t.getText());
	
		
		
	}
	
	
	
	
	
	public Termin showDialog() {
		setVisible(true);
		
		return mTermin;
	}
	
	
	
	
}

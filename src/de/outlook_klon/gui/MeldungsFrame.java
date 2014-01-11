package de.outlook_klon.gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;
import javax.swing.JButton;


public class MeldungsFrame extends ExtendedDialog<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -426579552451278615L;
	private JTextPane textAbwes;
	private JButton btnOk;	
	private JButton btnAbbrechen;
	private String info;
		
		
		public MeldungsFrame(){
			super(400, 400);
			setTitle("Abwesenheitsmeldung festlegen");
			getContentPane().setLayout(null);
			
			textAbwes = new JTextPane();
			textAbwes.setBounds(0, 0, 394, 292);
			getContentPane().add(textAbwes);
			
			btnOk = new JButton("OK");
			btnOk.setBounds(40, 323, 89, 23);
			getContentPane().add(btnOk);
			
			btnAbbrechen = new JButton("Abbrechen");
			btnAbbrechen.setBounds(242, 323, 89, 23);
			getContentPane().add(btnAbbrechen);
		}
		
		public MeldungsFrame(String s, String status) {
			super(400, 400);
			setTitle(status);
			getContentPane().setLayout(null);
			
			textAbwes = new JTextPane();
			textAbwes.setBounds(0, 0, 394, 292);
			textAbwes.setText(s);
			getContentPane().add(textAbwes);
			
			btnOk = new JButton("OK");
			btnOk.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					info = textAbwes.getText();					
					close();
				}
			});
			btnOk.setBounds(40, 323, 89, 23);
			getContentPane().add(btnOk);
			
			btnAbbrechen = new JButton("Abbrechen");
			btnAbbrechen.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			btnAbbrechen.setBounds(242, 323, 89, 23);
			getContentPane().add(btnAbbrechen);
		}
	
		protected String getDialogResult() {
			return info;
		}
		
		
		public void setText(String text){
			textAbwes.setText(text);
		}
		
		public String getText(){
			return getText();
		}
		
		
}



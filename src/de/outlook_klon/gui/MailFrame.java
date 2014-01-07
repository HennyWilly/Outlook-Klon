package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JList;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ParseException;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;

public class MailFrame extends ExtendedFrame {	
	private static final long serialVersionUID = 5976953616015664148L;
	
	/**
	 * Interne Aufzählung, welche die verschiedenen Arten definiert, 
	 * in welchem Kontext das Frame geöffnet werden kann
	 */
	private enum MailModus {
		/**
		 * Es wird eine neue Mail geschieben
		 */
		NEU,
		
		/**
		 * Es wird eine existierende Mail geöffnet
		 */
		OEFFNEN,
		
		/**
		 * Es wird auf eine existierende Mail geantwortet
		 */
		ANTWORT,
		
		/**
		 * Es wird eine existierende Mail weitergeleitet
		 */
		WEITERLEITEN
	}
	
	private MailModus modus;
	private MailInfo info;
	private String relPfad;
	
	private JComboBox<MailAccount> cBSender;
	private JTextField tSender;
	
	private JTextField tTo;
	private JTextField tCC;
	private JTextField tSubject;
	private HtmlEditorPane tpMailtext;
	
	private JButton btnSenden;
	private JButton btnAnhang;
	
	private JMenuItem mntmDateiAnhaengen;
	private JMenuItem mntmSchliessen;
	
	private JRadioButtonMenuItem rdbtnmntmReintext;
	private JRadioButtonMenuItem rdbtnmntmHtml;
	
	private JList<File> lstAnhang;
	
	private String charset;
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnAnhaengen = new JMenu("Anh\u00E4ngen");
		mnAnhaengen.setVisible(modus == MailModus.NEU);
		mnDatei.add(mnAnhaengen);
		
		mntmDateiAnhaengen = new JMenuItem("Datei anh\u00E4ngen");
		mntmDateiAnhaengen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				anhangHinzufügen();
			}
		});
		mnAnhaengen.add(mntmDateiAnhaengen);
		
		mntmSchliessen = new JMenuItem("Schlie\u00DFen");
		mntmSchliessen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		mnDatei.add(mntmSchliessen);
		
		JMenu mnOptionen = new JMenu("Optionen");
		menuBar.add(mnOptionen);
		
		JMenu mnEmailFormat = new JMenu("E-Mail Format");
		mnOptionen.add(mnEmailFormat);
		
		ItemListener radioMenu = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JRadioButtonMenuItem sender = (JRadioButtonMenuItem)arg0.getSource();		
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					boolean editable = tpMailtext.isEditable();

					String text = tpMailtext.getText();
					String contentType = tpMailtext.getContentType();
					
					if(sender == rdbtnmntmReintext) {
						contentType = "TEXT/plain; " + charset;
					}
					else if(sender == rdbtnmntmHtml) {
						contentType = "TEXT/html; " + charset;
					}
					
					tpMailtext.setEditable(true);
					tpMailtext.setText(text, contentType, false);
					tpMailtext.setEditable(editable);
					tpMailtext.setCaretPosition(0);
				}
			}
		};
		
		rdbtnmntmReintext = new JRadioButtonMenuItem("Reintext");
		rdbtnmntmReintext.setSelected(true);
		rdbtnmntmReintext.addItemListener(radioMenu);
		mnEmailFormat.add(rdbtnmntmReintext);
		
		rdbtnmntmHtml = new JRadioButtonMenuItem("Html");
		rdbtnmntmHtml.addItemListener(radioMenu);
		mnEmailFormat.add(rdbtnmntmHtml);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnmntmReintext);
		group.add(rdbtnmntmHtml);
	}
	
	private void initListe(JSplitPane splitHead) {
		lstAnhang = new JList<File>(new DefaultListModel<File>());
		
		JScrollPane anhangScroller = new JScrollPane(lstAnhang);
		splitHead.setRightComponent(anhangScroller);
		
		if(modus == MailModus.OEFFNEN) {
			lstAnhang.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						File selected = lstAnhang.getSelectedValue();
						anhangSpeichern(selected.getName());
					}
				}
			});
		}
	}
	
	private void initGui() {
		JToolBar toolBar = new JToolBar();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		JSplitPane splitHead = new JSplitPane();
		
		final GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(splitHead, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(splitHead, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
		);
		
		final JPanel panel_1 = new JPanel();
		splitHead.setLeftComponent(panel_1);
		
		JLabel lSender = new JLabel("Von:");
		JLabel lTo = new JLabel("An:");
		JLabel lCC = new JLabel("CC:");
		JLabel lSubject = new JLabel("Betreff:");
		
		tTo = new JTextField();
		tTo.setColumns(10);
		
		tCC = new JTextField();
		tCC.setColumns(10);
		
		tSubject = new JTextField();
		tSubject.setColumns(10);
		tSubject.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateCaption();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateCaption();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateCaption();
			}
		});
		
		cBSender = new JComboBox<MailAccount>();
		tSender = new JTextField();
		
		final GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lSender, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(lTo, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(lCC, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(lSubject, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(modus != MailModus.OEFFNEN ? cBSender : tSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(tTo, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 6, Short.MAX_VALUE)
						.addComponent(tCC, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 6, Short.MAX_VALUE)
						.addComponent(tSubject, GroupLayout.DEFAULT_SIZE, 6, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lSender)
						.addComponent(modus != MailModus.OEFFNEN ? cBSender : tSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lTo)
						.addComponent(tTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lCC)
						.addComponent(tCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(3)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lSubject)
						.addComponent(tSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
		
		
		initListe(splitHead);
		
		panel.setLayout(gl_panel);
		
		tpMailtext = new HtmlEditorPane();
		
		JScrollPane textScroller = new JScrollPane(tpMailtext);
		splitPane.setRightComponent(textScroller);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
						.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
					.addGap(0))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
		);
		
		btnSenden = new JButton("Senden");
		toolBar.add(btnSenden);
		btnSenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendeMail();
			}
		});
		
		btnAnhang = new JButton("Anhang");
		btnAnhang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				anhangHinzufügen();
			}
		});
		toolBar.add(btnAnhang);
		toolBar.setVisible(modus != MailModus.OEFFNEN);
		
		getContentPane().setLayout(groupLayout);
		
		initMenu();
	}
	
	private String appendAddresses(Address[] addr) {
		if(addr == null || addr.length == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < addr.length; i++) {
			InternetAddress inet = (InternetAddress)addr[i];
			
			sb.append(inet.toUnicodeString());
			
			if(i < addr.length - 1)
				sb.append("; ");
		}
		
		return sb.toString();
	}
	
	public MailFrame() {
		modus = MailModus.NEU;
		charset = "";
		
		setTitle("<Kein Betreff>");
		initGui();
		
		for(MailAccount acc : Benutzer.getInstanz()) {
			addMailAccount(acc);
		}
	}
	
	public MailFrame(Kontakt[] kontakte) {
		modus = MailModus.NEU;
		charset = "";
		
		setTitle("<Kein Betreff>");
		initGui();
		
		for(MailAccount ac : Benutzer.getInstanz()) {
			addMailAccount(ac);
		}
		
		ArrayList<InternetAddress> adressen = new ArrayList<InternetAddress>();
		for(Kontakt k : kontakte) {
			if(k.getMail1() == null)
				continue;
			adressen.add(k.getMail1());
		}
		
		String adressString = appendAddresses(adressen.toArray(new InternetAddress[adressen.size()]));
		tTo.setText(adressString);
	}

	public MailFrame(MailInfo mail, String pfad, MailAccount parent) throws MessagingException {
		modus = MailModus.OEFFNEN;
		
		initGui();
		
		info = mail;
		relPfad = pfad;
		
		addMailAccount(parent);
		cBSender.setSelectedItem(parent);
		
		parent.getWholeMessage(pfad, mail);
		charset = mail.getContentType().split("; ")[1];
		
		tSender.setText(((InternetAddress)mail.getSender()).toUnicodeString());
		tSender.setEditable(false);
		
		tSubject.setText(mail.getSubject());
		tSubject.setEditable(false);
		
		tTo.setText(appendAddresses(mail.getTo()));
		tTo.setEditable(false);
		
		tCC.setText(appendAddresses(mail.getCc()));
		tCC.setEditable(false);

		String text = info.getText();
		String contentType = info.getContentType();
		String tmpText = text.replace("\r\n", "<br/>");
		
		if(!contentType.startsWith("TEXT/html") && HtmlEditorPane.istHtml(tmpText))
			contentType = contentType.replace("plain", "html");
		
		if(contentType.startsWith("TEXT/plain")) {
			rdbtnmntmReintext.setSelected(true);
			tpMailtext.setText(text);
		}
		else {
			tpMailtext.setText(text);
			rdbtnmntmHtml.setSelected(true);
		}
		
		tpMailtext.setEditable(false);
		
		DefaultListModel<File> model = (DefaultListModel<File>)lstAnhang.getModel();
		String[] attachments = mail.getAttachment();
		for(int i = 0; i < attachments.length; i++) {
			model.addElement(new File(attachments[i]));
		}
	}
	
	/**
	 * @param weiterleiten Weiterleiten -> true; Antworten -> false
	 */
	public MailFrame(MailInfo mail, String pfad, MailAccount parent, boolean weiterleiten) throws MessagingException {
		modus = weiterleiten ? MailModus.WEITERLEITEN : MailModus.ANTWORT;
		
		initGui();
		
		for(MailAccount acc : Benutzer.getInstanz()) {
			addMailAccount(acc);
		}
		cBSender.setSelectedItem(parent);
		
		info = mail;
		relPfad = pfad;
		
		parent.getWholeMessage(pfad, mail);
		charset = mail.getContentType().split("; ")[1];

		String subject = (weiterleiten ? "Fwd: " : "Re: ") + mail.getSubject();
		
		tSubject.setText(subject);
		
		if(weiterleiten == false)
			tTo.setText(((InternetAddress)mail.getSender()).toUnicodeString());
		tCC.setText(appendAddresses(mail.getCc()));

		String text = info.getText();
		String contentType = info.getContentType();
		String tmpText = text.replace("\r\n", "<br/>");
		
		Pattern pattern = Pattern.compile(".*?<(\"[^\"]*\"|'[^']*'|[^'\">])*>.*?");
		Matcher matcher = pattern.matcher(tmpText);
		if(matcher.matches())
			contentType = contentType.replace("plain", "html");
		
		if(contentType.startsWith("TEXT/plain")) {
			rdbtnmntmReintext.setSelected(true);
			tpMailtext.setText(text);
		}
		else {
			tpMailtext.setText(text);
			rdbtnmntmHtml.setSelected(true);
		}
	}
	
	private void addMailAccount(MailAccount ac) {
		DefaultComboBoxModel<MailAccount> model = (DefaultComboBoxModel<MailAccount>) cBSender.getModel();
		
		if(model.getIndexOf(ac) == -1 ) {
			cBSender.addItem(ac);
		}
		
		if(cBSender.getSelectedIndex() == -1)
			cBSender.setSelectedIndex(0);
	}
	
	InternetAddress[] unicodifyAddresses(String addresses) throws ParseException {
		addresses = addresses.replace(';', ',');
		
	    InternetAddress[] recips = InternetAddress.parse(addresses, true);
	    
	    for(int i=0; i<recips.length; i++) {
	        try {
	            recips[i] = new InternetAddress(recips[i].getAddress(), recips[i].getPersonal(), "utf-8");
	        } catch(UnsupportedEncodingException uee) { }
	    }
		    
	    return recips;
	}
	
	private void sendeMail() {
		InternetAddress[] to = null;
		InternetAddress[] cc = null;
		
		try {
			to = unicodifyAddresses(tTo.getText());
			cc = unicodifyAddresses(tCC.getText());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String subject = tSubject.getText();
		String text = tpMailtext.getText();
		
		MailAccount acc = (MailAccount)cBSender.getSelectedItem();
		if(acc == null) {
			JOptionPane.showMessageDialog(this, "Es wurde keine Mailadresse angegeben, über die die Mail gesendet werden soll",
					"Fehler", JOptionPane.OK_OPTION);
		}
		else {
			DefaultListModel<File> model = (DefaultListModel<File>)lstAnhang.getModel();
			File[] anhänge = new File[model.getSize()];
			for(int i = 0; i < anhänge.length; i++) {
				anhänge[i] = model.get(i);
			}
			
			try {
				acc.sendeMail(to, cc, subject, text, tpMailtext.getContentType(), anhänge);
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			} catch(MessagingException ex) {
				JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Senden der Mail aufgetreten:\n" + ex.getMessage(),
						"Fehler", JOptionPane.OK_OPTION);
			}
		}
	}
	
	private void anhangHinzufügen() {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			DefaultListModel<File> model = (DefaultListModel<File>)lstAnhang.getModel();
			
			for(File file : files) {
				if(file.exists())
					model.addElement(file);
			}
		}
	}
	
	private void anhangSpeichern(String name) {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(name));
		
		if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String pfad = fc.getSelectedFile().getAbsolutePath();
			MailAccount acc = (MailAccount)cBSender.getSelectedItem();
			
			try {
				acc.anhangSpeichern(info, relPfad, name, pfad);
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Speichern des Anhangs aufgetreten: \n" + ex.getMessage(),
						"Fehler", JOptionPane.OK_OPTION);
			}
		}
	}
	
	private void updateCaption() {
		String text = tSubject.getText();
		
		if(text.trim().isEmpty())
			this.setTitle("<Kein Betreff>");
		else
			this.setTitle(text);
	}
}

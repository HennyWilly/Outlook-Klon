package de.outlook_klon.logik;

import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;

public class NewMailEvent {
	private MailAccount account;
	private String folder;
	private MailInfo info;
	
	public NewMailEvent(MailAccount account, String folder, MailInfo info) {
		this.setAccount(account);
		this.setFolder(folder);
		this.setInfo(info);
	}

	public MailAccount getAccount() {
		return account;
	}

	private void setAccount(MailAccount account) {
		this.account = account;
	}

	public String getFolder() {
		return folder;
	}

	private void setFolder(String folder) {
		this.folder = folder;
	}

	public MailInfo getInfo() {
		return info;
	}

	private void setInfo(MailInfo info) {
		this.info = info;
	}
}

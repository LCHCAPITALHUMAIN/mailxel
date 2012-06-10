/*
 * Copyright (C) 2008-2011 by Simon Hefti.
 * All rights reserved.
 * 
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 */
package ch.heftix.mailxel.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.heftix.mailxel.client.ListUtil;
import ch.heftix.mailxel.client.MailService;
import ch.heftix.mailxel.client.MailxelServiceException;
import ch.heftix.mailxel.client.MimeTypeDescriptor;
import ch.heftix.mailxel.client.MimeTypeUtil;
import ch.heftix.mailxel.client.Version;
import ch.heftix.mailxel.client.to.AccountConfigTO;
import ch.heftix.mailxel.client.to.AddressTO;
import ch.heftix.mailxel.client.to.AttachedCategoryTO;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.AttachmentWithEnvelopes;
import ch.heftix.mailxel.client.to.Category;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.Constants;
import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.mailxel.client.to.IconTO;
import ch.heftix.mailxel.client.to.MailTO;
import ch.heftix.mailxel.client.to.MessageQueryTO;
import ch.heftix.mailxel.client.to.MessageSearchTO;
import ch.heftix.mailxel.client.to.ReorgRuleTO;
import ch.heftix.mailxel.client.to.StatisticsTO;
import ch.heftix.xel.db.TransactionBody;
import ch.heftix.xel.log.LOG;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MailServiceImpl extends RemoteServiceServlet implements MailService {

	/** Comment for <code>serialVersionUID</code>. */
	private static final long serialVersionUID = 416806568375332808L;
	MailDB db = null;
	ConfigTO configTO = new ConfigTO();

	public void init() throws ServletException {

		LOG.debug("MailServiceImpl - Starting (" + Version.getVersion() + ")");

		configTO = ConfigManager.readConfig();

		switch (configTO.logConfiguration) {
		case 1:
			LOG.setMode("P");
			break;
		default:
			LOG.setMode("D");
			break;
		}

		String zOverWriteLogLevel = System.getProperty("mailxel.loglevel");
		if (null != zOverWriteLogLevel) {
			LOG.setMode(zOverWriteLogLevel);
		}

		String zOverWriteDB = System.getProperty("mailxel.localstore");
		if (null != zOverWriteDB) {
			LOG.info("DB localstore location overwritten to: %s", zOverWriteDB);
			configTO.localstore = zOverWriteDB;
		}

		LOG.debug("  MailServiceImpl - starting DB");
		String status = initDB();
		if (!"200 OK".equals(status)) {
			throw new ServletException(status);
		}

		// gmail seems to use a base64 encoding which is not accepted by
		// com.sun.mail.util.BASE64DecoderStream
		System.setProperty("mail.mime.base64.ignoreerrors", "true");

		// http://java.sun.com/javase/javaseforbusiness/docs/TLSReadme.html
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");

		// does DB now about 'me'?
		if (null != db) {
			LOG.debug("  DB exists. Check for \"me\".");
			String me = db.getConfig("me");
			if (null == me || !me.equals(configTO.me)) {
				db.saveConfig("me", configTO.me);
				db.initFromMeToMeFlag();
				db.updateFromMeToMeFlag(MailAPIUtil.FROM);
				db.updateFromMeToMeFlag(MailAPIUtil.TO);
			}
		}
		LOG.debug("  MailServiceImpl - init done.");
	}

	private String initDB() {
		String res = "200 OK";
		if (null == db && null != configTO.localstore) {
			LOG.debug("MailServiceImpl, initDB: setting DB to:" + configTO.localstore);
			try {
				db = new MailDB(configTO.localstore);
				LOG.debug("MailServiceImpl, initDB: db ready");
			} catch (Exception e) {
				LOG.debug("MailServiceImpl, initDB: cannot access DB: %s", e);
				res = String.format("400 cannot access DB: %s", e);
			}
		}
		return res;
	}

	private String changeDB() {
		String res = "200 OK";
		LOG.debug("MailServiceImpl, changeDB: starting");
		if (null != db) {
			LOG.debug("MailServiceImpl, changeDB: closing open db");
			db.destroy();
			db = null;
		}
		res = initDB();
		return res;
	}

	public void destroy() {

		if (null != db) {
			db.destroy();
		}
		super.destroy();
	}

	public String ping(String s) {
		return Long.toString(System.currentTimeMillis());
	}

	/**
	 * provide direct access to attachments
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// query string contains parameter cll (change log level) --> set log
		// level
		String zLog = req.getParameter("cll");
		if (null != zLog && zLog.length() > 0) {
			changeLogConfiguration(zLog);
			return;
		}

		// query string contains parameter img --> icon download
		String zImgId = req.getParameter("img");
		int imgId = ListUtil.parse(zImgId, -1);
		if (-1 != imgId) {

			final BlobTO blobTO = db.getIcon(imgId);

			MimeTypeDescriptor mimeType = MimeTypeUtil.mimeTypeByName(blobTO.name);
			resp.setContentType(mimeType.mimetype);

			OutputStream os = resp.getOutputStream();
			os.write(blobTO.data);

			blobTO.data = null;

		} else {

			// query string contains parameter id --> attachment download
			String zAttachmentId = req.getParameter("id");
			int attachmentId = ListUtil.parse(zAttachmentId, -1);

			if (-1 == attachmentId) {
				throw new ServletException("unknown attachment id - giving up");
			}

			// from attachment id: find corresponding message id (for zip name)
			AttachmentTO aTO = db.getAttachment(attachmentId);

			MimeTypeDescriptor mimeType = MimeTypeUtil.mimeTypeByName(aTO.name);
			resp.setContentType(mimeType.mimetype);
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + aTO.name + "\"");

			db.streamAttachmentFromZipfile(aTO, resp.getOutputStream());
		}
	}

	public List<Envelope> select(final int page, MessageSearchTO messageSearchTO) {
		List<Envelope> res = null;
		if (null != messageSearchTO.fts) {
			if (messageSearchTO.fts.length() > 1) {
				res = db.selectFTS(page, messageSearchTO, configTO);
			}
		} else {
			res = db.selectRecent(page, configTO);
			// res = db.select(page, messageSearchTO, configTO);
		}
		return res;
	}

	public MailTO get(int id) {
		return db.getMessage(id);
	}

	private Mail createMail(final MailTO mailTO) {
		Message msg = null;
		Mail mail = null;
		try {
			Session s = Session.getInstance(new Properties());
			msg = MailAPIUtil.createMessage(mailTO, s, db);
			msg.saveChanges(); // in send case: done by Transport.send()
			mail = Mail2DB.msg2mail(msg, db);
			s = null;
		} catch (Exception e) {
			mail = null;
			LOG.debug("cannot create mail: %s", e);
		}
		return mail;
	}

	/**
   */
	private Mail save(final MailTO mailTO, final int categoryId) {

		final Mail mail = createMail(mailTO);
		if (null != mail) {
			TransactionBody tb = new TransactionBody() {

				public void trx() throws SQLException {
					int msgid = db.storeMail(mail);
					if (categoryId > 0) {
						db.categorizeMessage(msgid, 991);
					}
				}
			};
			db.transaction(tb);
		}
		return mail;
	}

	/**
	 * store a message for later sending
	 * 
	 * @param mailTO
	 * @throws MailxelServiceException
	 */
	public String postpone(final MailTO mailTO) {

		String status = "200 OK";

		if (null == mailTO.from || mailTO.from.length() < 1) {
			mailTO.from = configTO.me;
		}

		final Mail mail = save(mailTO, 991);

		if (null == mail) {
			status = "400 NOK mail creation failed";
		}

		return status;
	}

	/**
	 * store a message in the mailxel DB as it would have been sent by e-mail
	 * 
	 * @param mailTO
	 * @throws MailxelServiceException
	 */
	public String storeNote(final MailTO mailTO) {

		String status = "200 OK";

		mailTO.from = configTO.me;
		mailTO.to = configTO.me;

		final Mail mail = save(mailTO, -1);

		if (null == mail) {
			status = "400 NOK mail creation failed";
		}
		return status;
	}

	public List<AddressTO> suggestAddress(final String query) {

		return db.suggestAddress(query, configTO.maxAddressSuggestions);

	}

	public String scanMailFolder(final String[] accounts) {

		String status = "200 OK";

		String[] accountNames = accounts;

		if (null == accountNames) {
			accountNames = configTO.accountNames;
		}

		for (int i = 0; i < accountNames.length; i++) {

			AccountConfigTO acTO = configTO.getAccountConfigTOByAccountName(accountNames[i]);

			if (null == acTO.password) {
				String msg = String.format("not logged in; cannot scan account '%s'", accountNames[i]);
				LOG.info(msg);
				return "401 NOK " + msg;
			}

			LOG.debug("scanMailFolder: scanning account '%s'", accountNames[i]);

			status = Mail2DB.scanMailFolders(acTO.protocol, acTO.server, acTO.isSSL, acTO.user, acTO.password,
					acTO.port, acTO.scannedfolders, acTO.excludedfolders, db, configTO.maxMailDownloadsPerFolder);
			if (!"200 OK".equals(status)) {
				return status;
			}
		}

		return status;
	}

	public boolean hasConfig() {

		if (null != configTO.localstore) {
			return true;
		}
		return false;

	}

	public ConfigTO getConfig() {
		return configTO;
	}

	public List<AddressTO> searchAddresses(final String shortname, final String address) {
		return db.searchAddresses(shortname, address);
	}

	public void saveAddresses(final List<AddressTO> addresses) {

		db.saveAddresses(addresses);
	}

	public void saveAddresses(final String lineSeparatedShortNameAddress) {

		if (null == lineSeparatedShortNameAddress) {
			return;
		}

		String[] lines = lineSeparatedShortNameAddress.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (null != lines[i] && lines[i].length() > 0) {
				String[] parts = lines[i].split(",");
				if (null != parts && parts.length == 2 && null != parts[0] && null != parts[1]) {
					String shortName = parts[0].trim();
					String address = parts[1].trim();
					AddressTO aTO = db.createAddress(address);
					aTO.shortname = shortName;
					aTO.shortNameDirty = true;
					db.updateAddress(aTO);
				}
			}
		}

	}

	public void setPassword(final String accountName, final String password) {

		// if (null == password) {
		// return;
		// }

		if (null == configTO) {
			LOG.warn("cannot login: no config environment");
			return;
		}

		if (MailService.ACCOUNT_SEND.equals(accountName)) {
			configTO.smtpPassword = password;
			return;
		}

		AccountConfigTO acTO = configTO.getAccountConfigTOByAccountName(accountName);

		if (null == accountName) {
			acTO = configTO.accounts[0];
		}

		acTO.password = password;

	}

	public void forgetPassword(final String accountName) {
		setPassword(accountName, null);
	}

	public void saveConfig(ConfigTO configTO) {

		LOG.debug("MailServiceImpl.saveConfig: Starting");

		ConfigTO oldCTO = this.configTO;
		if (null == configTO) {
			LOG.debug("will not save null config. Giving up.");
			return;
		}
		ConfigManager.saveConfig(configTO);
		this.configTO = configTO;
		// keep send password, if set
		if (null != oldCTO && null != oldCTO.smtpPassword && oldCTO.smtpPassword.length() > 1) {
			configTO.smtpPassword = oldCTO.smtpPassword;
		}
		// keep account passwords, if set
		String[] actNames = configTO.accountNames;
		for (int i = 0; i < actNames.length; i++) {
			AccountConfigTO actCfg = oldCTO.getAccountConfigTOByAccountName(actNames[i]);
			if (null != actCfg && null != actCfg.password && actCfg.password.length() > 1) {
				configTO.accounts[i].password = actCfg.password;
			}
		}

		// if user moved tablespace: start DB anew
		if (null == oldCTO || null == oldCTO.localstore || !oldCTO.localstore.equals(configTO.localstore)) {
			changeDB();
		}

		db.saveConfig("me", configTO.me);
		LOG.debug("MailServiceImpl.saveConfig: Done.");
	}

	public void deleteConfig() {
		ConfigManager.deleteConfig();
	}

	public String reorgMailFolder(final String[] accounts) {

		String status = "200 OK";

		Store store = null;

		String[] accountNames = accounts;

		if (null == accounts) {
			accountNames = configTO.accountNames;
		}

		for (int i = 0; i < accountNames.length; i++) {

			AccountConfigTO aTO = configTO.getAccountConfigTOByAccountName(accountNames[i]);
			// AccountConfigTO aTO = configTO.accounts[i];
			try {

				if (null == aTO.password) {
					status = "400 password not defined - skip";
					LOG.debug(status);
					return status;
				}
				store = MailAPIUtil.connect(aTO.protocol, aTO.isSSL, aTO.user, aTO.password, aTO.server, aTO.port);

				ReorgRuleTO reorgRuleTO = MailReorganizerFolderHandler.parseRule(aTO.reorgRules);

				if (null != reorgRuleTO.parseResult) {
					LOG.debug(reorgRuleTO.parseResult);
				}

				if (reorgRuleTO.rules.size() < 1) {
					status = "400 no reorg rules defined - skipping";
					LOG.debug(status);
					return status;
				}

				int Nfolders = reorgRuleTO.folderNames.size();
				for (int j = 0; j < Nfolders; j++) {

					String[] folderNames = { reorgRuleTO.folderNames.get(j) };
					String rule = reorgRuleTO.rules.get(j);

					FolderHandler folderHandler = new MailReorganizerFolderHandler(store, rule,
							reorgRuleTO.copyMaxMessages, reorgRuleTO.keepLastNHours);

					MailFolderTraverser mailFolderTraverser = new MailFolderTraverser(aTO.excludedfolders);
					mailFolderTraverser.traverseFolder(store, folderNames, folderHandler);
				}

				store.close();
				store = null;
			} catch (Exception e) {
				status = "400 problem reading folder or mail:" + e;
				LOG.warn(status, e);
			} finally {
				if (null != store) {
					try {
						store.close();
						store = null;
					} catch (MessagingException e) {
						LOG.warn("cannot close store", e);
					}
				}
			}
		}
		return status;
	}

	private void copyMessageToFolder(final Message msg, final String accountName, final String folderName)
			throws Exception {

		Store store = null;

		AccountConfigTO aTO = configTO.getAccountConfigTOByAccountName(accountName);

		if (null == aTO.password) {
			throw new IllegalArgumentException("password for selected account not defined");
		}

		try {

			store = MailAPIUtil.connect(aTO.protocol, aTO.isSSL, aTO.user, aTO.password, aTO.server, aTO.port);

			Folder folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			Message[] msgs = new Message[1];
			msgs[0] = msg;
			folder.appendMessages(msgs);

			// close w/o expunge
			folder.close(false);
			folder = null;

			store.close();
			store = null;
		} catch (Exception e) {
			String status = String.format("problem copying message to folder '%s' of account '%s'", accountName,
					folderName);
			throw new Exception(status, e);
		} finally {
			if (null != store) {
				try {
					store.close();
					store = null;
				} catch (MessagingException e) {
					LOG.warn("cannot close store", e);
				}
			}
		}
	}

	public void saveCategories(final List<Category> categories) {
		db.saveCategories(categories);
	}

	public List<Category> searchCategories(final String name) {
		return db.searchCategories(name);
	}

	public List<Category> suggestCategory(final String query) {
		return db.suggestCategory(query);
	}

	public void markAsDeleted(final List<Integer> ids) {
		db.markAsDeleted(ids);
	}

	public void updateCategories(final List<Integer> ids, final String categories) {
		db.updateCategories(ids, categories);
	}

	public void markStatus(final int msgId, final int categoryId) {
		db.markStatus(msgId, categoryId);
	}

	public void changeLogConfiguration(String mode) {
		LOG.setMode(mode);
	}

	public void changeLogConfiguration(int mode) {

		switch (mode) {
		case 1:
			changeLogConfiguration("P");
			break;

		default:
			changeLogConfiguration("D");
			break;
		}
	}

	public void setDBTiming(final boolean doTimeDB) {
		db.setTiming(doTimeDB);
	}

	public void importMboxFile(final String mboxFile) {

		File file = new File(mboxFile);
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				File f2 = new File(file, children[i]);
				importMboxFile(f2.getAbsolutePath());
			}
		} else {
			handleMboxFile(mboxFile);
		}

	}

	private void handleMboxFile(final String mboxFile) {

		try {
			final MboxReader mboxReader = new MboxReader(mboxFile);
			TransactionBody tb = new TransactionBody() {

				public void trx() throws SQLException {
					while (mboxReader.hasNext()) {
						Message msg = null;
						try {
							msg = mboxReader.nextMessage();
						} catch (IOException e) {
							// TODO Auto-generated catch block
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
						}

						if (null != msg) {
							Mail mail = Mail2DB.msg2mail(msg, db);
							if (null != mail) {
								LOG.debug("now storing: " + mail.uuid + " " + mail.subject);
								db.storeMail(mail);
							}
							mail = null;
							msg = null;
						}
					}
				}
			};
			db.transaction(tb);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

	}

	public String send(final MailTO mailTO) {

		String res = "200 OK";

		Message msg = null;

		if (configTO.sentHandlingCopySent) {
			AccountConfigTO aTO = configTO.getAccountConfigTOByAccountName(configTO.sentHandlingCopyAccountName);
			if (null == aTO.password) {
				res = "400 NOK will not be able to copy message (not logged into account)";
				return res;
			}
		}

		try {

			if (null == mailTO.from || mailTO.from.length() < 1) {
				mailTO.from = configTO.me;
			}

			msg = MailAPIUtil.send(configTO, mailTO, db);

		} catch (Exception e) {
			res = "400 NOK " + e.toString();
			return res;
		}

		if (null != msg && configTO.sentHandlingStoreLocally) {
			final Mail mail = Mail2DB.msg2mail(msg, db);
			if (null != mail) {
				TransactionBody tb = new TransactionBody() {

					public void trx() throws SQLException {
						db.storeMail(mail);
					}
				};
				db.transaction(tb);
			}
		}

		if (null != msg && configTO.sentHandlingCopySent) {
			try {
				copyMessageToFolder(msg, configTO.sentHandlingCopyAccountName, configTO.sentHandlingCopyFolderName);
			} catch (Exception e) {
				res = "400 NOK " + e.toString();
				LOG.warn("cannot copy message", e);
			}
		}

		msg = null;

		return res;
	}

	public String getAttachmentData(final int attachmentId) {

		return db.getAttachmentData(attachmentId);
	}

	public long getTotalNumMessages() {
		if (null == db) {
			return 0;
		}
		return db.getTotalNumMessages();

	}

	public boolean isPasswordSet(final String account) {

		if (null == account) {
			return false;
		}

		if (MailService.ACCOUNT_SEND.equals(account)) {
			if (null == configTO.smtpPassword) {
				return false;
			}
			return true;
		}

		AccountConfigTO acTO = configTO.getAccountConfigTOByAccountName(account);
		if (null == acTO) {
			return false;
		}

		if (null == acTO.password) {
			return false;
		}
		return true;

	}

	public List<AttachmentTO> getAttachments(final int mailId) {
		return db.getAttachments(mailId);
	}

	public StatisticsTO calculateStatistics() {
		return db.calculateStatistics();
	}

	public void updateToMeFlag() {
		db.updateFromMeToMeFlag(MailAPIUtil.TO);
	}

	public void updateFromMeFlag() {
		db.updateFromMeToMeFlag(MailAPIUtil.FROM);
	}

	public MessageQueryTO updateMessageQuery(final MessageQueryTO mqTO) {
		return db.updateMessageQuery(mqTO);
	}

	public List<MessageQueryTO> searchQueries(final String type, final String name) {
		return db.searchQueries(type, name);
	};

	public List<Envelope> executeMessageQuery(final MessageQueryTO mqTO) {
		return db.executeMessageQuery(0, 100, mqTO);
	}

	public long count(MessageQueryTO mqTO) {
		if (null != db) {
			return db.count(mqTO);
		} else {
			return 0;
		}
	}

	public List<IconTO> searchIcons(final String name) {
		return db.searchIcons(name);
	}

	public String housekeeping() {
		String res = "200 OK";
		try {
			db.optimizeFTS();
			db.vacuum();
		} catch (SQLException e) {
			res = "400 NOK" + e.toString();
		}
		return res;
	}

	public String snippet(final int id) {
		return "snippet not implemented";
		// return db.snippet(id);
	}

	public Category getCategory(final int id) {
		return db.getCategory(id);
	}

	public IconTO getIconForCategory(final int id) {

		IconTO res = new IconTO();

		Category cat = getCategory(id);
		if (Constants.UNDEFINED_ID != id) {
			res = db.getIconTO(cat.iconid);
		}

		return res;
	}

	public List<AttachedCategoryTO> getCategoryHistory(final int mailID) {

		List<AttachedCategoryTO> res = db.getAttachedCategories(mailID);

		return res;
	}

	public List<AttachmentWithEnvelopes> searchAttachment(final String query) {

		List<AttachmentWithEnvelopes> res = db.searchAttachment(query);

		return res;

	}

}

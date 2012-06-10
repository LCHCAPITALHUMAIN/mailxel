/*
 * Copyright (C) 2008-2011 by Simon Hefti. All rights reserved.
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * Initial Developer: Simon Hefti
 */
package ch.heftix.mailxel.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.xel.db.TransactionBody;
import ch.heftix.xel.log.LOG;

public class Mail2DB {

	public synchronized static Mail storeMessage(final File file, final MailDB db) {

		if (null == file) {
			return null;
		}

		final Mail[] res = new Mail[1];
		Message msg = null;

		LOG.debug("storeMessage: file (%.2f MB) %s", file.length()/1024.0, file);

		try {
			InputStream is = new FileInputStream(file);
			if (null != is) {
				Session session = Session.getDefaultInstance(new Properties());
				msg = new MimeMessage(session, is);
				if (null != msg) {
					try {
						res[0] = Mail2DB.msg2mail(msg, db);
					} catch (RuntimeException e) {
						LOG.debug("problem parsing mail: %s", e);
					}
				}
				is.close();
				is = null;
			}
		} catch (Exception e) {
			LOG.debug("cannot prepare message for storage: %s", e);
		}

		final TransactionBody tb = new TransactionBody() {

			public void trx() {
				try {
					if (null != res[0]) {
						db.storeMail(res[0]);
					}
				} catch (Exception e) {
					LOG.debug("cannot store message: %s", e);
				}
			}
		};

		db.transaction(tb);
		
		msg = null;
		return res[0];
	}

	/**
	 * @param msg
	 *            message to parse
	 * @param db
	 *            database used to check if message was already stored
	 * @return {@link Mail} or null if message exists or was deleted on server
	 */
	public static File storeMessageTemporarily(final Message msg) {

		Mail mail = new Mail();
		mail.uuid = MailAPIUtil.getUUID(msg);

		boolean deleted = MailAPIUtil.isDeleted(msg);

		if (deleted) {
			return null;
		}
		File file = null;
		FileOutputStream fos = null;
		try {
			file = File.createTempFile("mailxel-", ".eml");
			file.deleteOnExit();
			fos = new FileOutputStream(file);
		} catch (Exception e) {
			LOG.debug("cannot create temp file to store message temporarily - skipping: %s", e);
			return null;
		}

		try {
			msg.writeTo(fos);
			fos.close();
		} catch (Exception e) {
			LOG.debug("cannot write message temporarily - skipping: %s", e);
			return null;
		}

		return file;
	}

	/**
	 * @param msg
	 *            message to parse
	 * @param db
	 *            database used to check if message was already stored
	 * @return {@link Mail} or null if message exists or was deleted on server
	 */
	public static Mail msg2mail(final Message msg, final MailDB db) {

		Mail mail = new Mail();
		mail.uuid = MailAPIUtil.getUUID(msg);

		// check uuid
		if (db.uuidExists(mail.uuid)) {
			return null;
		}

		boolean deleted = MailAPIUtil.isDeleted(msg);

		if (deleted) {
			return null;
		}

		mail.subject = MailAPIUtil.getSubject(msg);
		mail.date = MailAPIUtil.getSentDate(msg);
		mail.addTo(MailAPIUtil.getTo(msg));
		mail.addFrom(MailAPIUtil.getFrom(msg));
		mail.addCc(MailAPIUtil.getCC(msg));
		mail.addBcc(MailAPIUtil.getBCC(msg));
		mail.inReplyTo = MailAPIUtil.getInReplyTo(msg);
		mail.addReferences(MailAPIUtil.getReferences(msg));
		MailPartList mailPartList = MailAPIUtil.buildPartList(msg);
		mail.body = MailAPIUtil.getFirstTextPart(mailPartList);
		mail.setAttachmentDescriptor(MailAPIUtil.storeAttachmentsTemporarily(mailPartList));

		mailPartList.clear();
		mailPartList = null;

		return mail;
	}

	/**
	 */
	public static Envelope msg2env(final Message msg) {

		Mail mail = new Mail();
		mail.uuid = MailAPIUtil.getUUID(msg);
		mail.subject = MailAPIUtil.getSubject(msg);
		mail.date = MailAPIUtil.getSentDate(msg);
		mail.addTo(MailAPIUtil.getTo(msg));
		mail.addFrom(MailAPIUtil.getFrom(msg));
		mail.addCc(MailAPIUtil.getCC(msg));
		mail.addBcc(MailAPIUtil.getBCC(msg));

		Envelope res = mail.getEnvelope();

		return res;
	}

	public static String scanMailFolders(final String protocol, final String server, final boolean isSSL,
			final String user, final String password, final String port, final String[] folderNames,
			final String[] excludeNames, final MailDB db, final int maxMessagesPerFolder) {

		String status = "200 OK";

		Store store = null;

		try {

			store = MailAPIUtil.connect(protocol, isSSL, user, password, server, port);
			if (null == store) {
				status = String.format("400 NOK connection to %s,%s:%s failed", protocol, server, port);
				return status;
			}

			FolderHandler folderHandler = new FolderHandler() {

				public void handleFolder(Folder folder) throws MessagingException, Exception {
					LOG.info("reading mails from '%s'", folder);
					folder.open(Folder.READ_ONLY);

					FetchProfile fp = new FetchProfile();
					// fp.add(UIDFolder.FetchProfileItem.UID);
					fp.add("Message-ID");

					final Message[] msgs = folder.getMessages();
					folder.fetch(msgs, fp);

					int msgCnt = 0;

					for (int i = msgs.length - 1; i >= 0; i--) {

						if (maxMessagesPerFolder > 0 && msgCnt > maxMessagesPerFolder) {
							break;
						}

						Message msg = msgs[i];

						if (!MailAPIUtil.isDeleted(msg)) {
							String uuid = MailAPIUtil.getUUID(msg);
							LOG.debug("uuid: %s", uuid);
							if (!db.uuidExists(uuid)) {
								File tmpFile = storeMessageTemporarily(msg);
								// Mail mail = msg2mail(msg, db);
								if (null != tmpFile) {
									db.enqueue(tmpFile);
									msgCnt++;
									db.startMailSaveWorker();
									// db.storeMail(mail);
								}
								// mail = null;
								tmpFile = null;
								msgs[i] = null;
							}
						}

					}

					db.startMailSaveWorker();

					folder.close(false);
				}
			};

			MailFolderTraverser mailFolderTraverser = new MailFolderTraverser(excludeNames);
			mailFolderTraverser.traverseFolder(store, folderNames, folderHandler);

			store.close();
			store = null;
		} catch (MessagingException e) {
			status = "400 problem reading folder or mail:" + e;
			LOG.debug(status, e);
		} catch (Exception e) {
			status = "400 generic problem reading folder:" + e;
			LOG.debug(status, e);
		} finally {

			// add stop marker
			db.enqueueLast();

			if (null != store) {
				try {
					store.close();
					store = null;
				} catch (MessagingException e) {
					LOG.warn("cannot close store: %s", e);
				}
			}
		}
		return status;
	}

	public static List<Envelope> previewNewMessages(final String protocol, final String server, final boolean isSSL,
			final String user, final String password, final String port) {

		List<Envelope> envs = new ArrayList<Envelope>();
		Store store = null;

		try {

			store = MailAPIUtil.connect(protocol, isSSL, user, password, server, port);
			if (null == store) {
				LOG.debug("Mail2DB.previewNewMessages: connection to %s,%s:%s failed", protocol, server, port);
				return envs;
			}

			Folder folder = store.getDefaultFolder();
			folder = folder.getFolder("INBOX");

			LOG.info("Mail2DB.previewNewMessages: reading mails from '%s'", folder);
			folder.open(Folder.READ_ONLY);

			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

			Message msgs[] = folder.search(ft);

			for (int i = msgs.length - 1; i >= 0; i--) {

				Message msg = msgs[i];

				Envelope env = msg2env(msg);

				System.out.println("preview: " + env.subject + " " + env.from);
			}

			folder.close(false);
			store.close();
			store = null;
		} catch (MessagingException e) {
			LOG.debug("Mail2DB.previewNewMessages: problem reading folder or mail: %s", e);
		} catch (Exception e) {
			LOG.debug("Mail2DB.previewNewMessages: problem accessing folder: %s", e);
		} finally {
			if (null != store) {
				try {
					store.close();
					store = null;
				} catch (MessagingException e) {
					LOG.warn("cannot close store: %s", e);
				}
			}
		}
		return envs;
	}
}

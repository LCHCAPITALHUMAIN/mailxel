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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.freeutils.tnef.Attachment;
import net.freeutils.tnef.TNEFInputStream;
import net.freeutils.tnef.mime.RawDataSource;
import ch.heftix.mailxel.client.ListUtil;
import ch.heftix.mailxel.client.MimeTypeDescriptor;
import ch.heftix.mailxel.client.MimeTypeUtil;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.Constants;
import ch.heftix.mailxel.client.to.MailTO;
import ch.heftix.xel.log.LOG;
import ch.heftix.xel.util.Util;

public class MailAPIUtil {

	//
	public static final String FROM = "F";
	public static final String TO = "T";
	public static final String CC = "C";
	public static final String BCC = "B";

	//
	public static final String ANSWERED = "A";
	public static final String DELETED = "D";
	public static final String DRAFT = "T";

	// // @todo move to MimeTypeUtil
	private static final Map<String, String> fileextensions = new HashMap<String, String>();

	static {
		fileextensions.put("image/jpeg", ".jpg");
		fileextensions.put("image/gif", ".gif");
		fileextensions.put("text/plain", ".txt");
	}

	/**
	 * @param key
	 *            name of header value, e.g. Message-ID
	 * @return headers value of message with given name
	 */
	protected static String[] getHeaders(final String key, final Message message) {
		String[] uuids = null;
		try {
			uuids = message.getHeader(key);
		} catch (MessagingException e) {
			// silent
		}
		return uuids;
	}

	/**
	 * @param key
	 *            name of header value, e.g. Message-ID
	 * @return single header value of message with given name
	 */
	protected static String getHeader(final String key, final Message message) {
		String res = null;
		String[] uuids = getHeaders(key, message);
		if (uuids != null && uuids.length > 0) {
			res = uuids[0];
		}
		return res;
	}

	/**
	 * @return Message-ID header value of message, i.e. the unique message id
	 */
	public static String getUUID(final Message message) {
		return getHeader("Message-ID", message);
	}

	public static String getSubject(final Message message) {
		String res = null;
		try {
			res = MailAPIUtil.decode(message.getSubject());
		} catch (MessagingException e) {
			// silent
		}
		return res;
	}

	public static Date getSentDate(final Message message) {
		Date res = null;
		try {
			res = message.getSentDate();
			if (null == res) {
				res = message.getReceivedDate();
			}
		} catch (MessagingException e) {
			// silent
		}
		return res;
	}

	/**
	 * @return In-Reply-To header value of message (for thread view)
	 */
	public static String getInReplyTo(final Message message) {
		return getHeader("In-Reply-To", message);
	}

	/**
	 * @return References header value of message (for thread view)
	 */
	public static String getReferencesRaw(final Message message) {
		return getHeader("References", message);
	}

	/**
	 * @return References header value of message (for thread view)
	 */
	public static String[] getReferences(final Message message) {
		String[] res = new String[0];
		String tmp = getHeader("References", message);
		if (null == tmp) {
			return res;
		}
		res = tmp.split(" ");
		return res;
	}

	private static String[] getParty(final String type, final Message message) {

		return getLowercasedParty(type, message);
	}

	private static String[] getLowercasedParty(final String type, final Message message) {

		InternetAddress[] from = null;
		String[] res = new String[0];
		try {
			if (FROM.equals(type)) {
				from = (InternetAddress[]) message.getFrom();
			} else if (TO.equals(type)) {
				from = (InternetAddress[]) message.getRecipients(RecipientType.TO);
			} else if (CC.equals(type)) {
				from = (InternetAddress[]) message.getRecipients(RecipientType.CC);
			} else if (BCC.equals(type)) {
				from = (InternetAddress[]) message.getRecipients(RecipientType.BCC);
			}
			if (null != from) {
				res = new String[from.length];
				for (int i = 0; i < from.length; i++) {
					String tmp = from[i].getAddress();
					if (null != tmp) {
						res[i] = tmp.toLowerCase();
					}
				}
			}
		} catch (MessageRemovedException e) {
			LOG.debug("getLowercasedParty: message has been removed in the mean time - skip.");
		} catch (MessagingException e) {
			LOG.debug("getLowercasedParty: cannot access party: %s", e);
		}
		return res;
	}

	public static String[] getFrom(final Message message) {
		return getParty(FROM, message);
	}

	public static String getFromAsString(final Message message) {
		return (ListUtil.asComaSeparted(getFrom(message)));
	}

	public static String[] getTo(final Message message) {
		return getParty(TO, message);
	}

	public static String getToAsString(final Message message) {
		return (ListUtil.asComaSeparted(getTo(message)));
	}

	public static String[] getCC(final Message message) {
		return getParty(CC, message);
	}

	public static String getCCAsString(final Message message) {
		return (ListUtil.asComaSeparted(getCC(message)));
	}

	public static String[] getBCC(final Message message) {
		return getParty(BCC, message);
	}

	public static String getBCCAsString(final Message message) {
		return (ListUtil.asComaSeparted(getBCC(message)));
	}

	private static void buildMailPartList(final MailPartList partlist, final Multipart mp, final int level)
			throws MessagingException, IOException {

		if (null == mp) {
			return;
		}

		for (int i = 0; i < mp.getCount(); i++) {
			Part aPart = mp.getBodyPart(i);
			// System.out.println("D content type: " + aPart.getContentType());
			if (aPart.isMimeType("multipart/*")) {
				buildMailPartList(partlist, (Multipart) aPart.getContent(), level);
			} else if (aPart.isMimeType("text/*")) {
				partlist.add(aPart);
			} else if (aPart.isMimeType("message/rfc822")) {
				MimeMessage nested = (MimeMessage) aPart.getContent();
				if (nested.isMimeType("multipart/*")) {
					Multipart tmp = (Multipart) nested.getContent();
					buildMailPartList(partlist, tmp, level + 1);
				} else {
					Part p = (Part) nested;
					partlist.add(p);
				}
			} else if (aPart.isMimeType("application/ms-tnef")) {
				TNEFInputStream in = new TNEFInputStream(aPart.getInputStream());
				net.freeutils.tnef.Message tm = new net.freeutils.tnef.Message(in);
				List as = tm.getAttachments();
				for (int k = 0; k < as.size(); k++) {
					Attachment a = (Attachment) as.get(k);
					BodyPart bodyPart = new MimeBodyPart();
					bodyPart.setFileName(a.getFilename());
					RawDataSource rds = new RawDataSource(a.getRawData(), "application/octet-stream");
					bodyPart.setDataHandler(new DataHandler(rds));
					partlist.add(bodyPart);
				}
			} else {
				String disposition = aPart.getDisposition();
				if (null != disposition) {
					// accept both Part.INLINE and Part.ATTACHMENT
					String mimeType = aPart.getContentType();
					String fn = aPart.getFileName();
					LOG.debug("attachement: %s, %s", mimeType, fn);
					MailPart mailPart = new MailPart(aPart);
					partlist.add(mailPart);
				} else {
					String fn = aPart.getFileName();
					if (null != fn) {
						LOG.debug("attachment w/o disposition: contentType %s, fn %s", aPart.getContentType(), fn);
						MailPart mailPart = new MailPart(aPart);
						partlist.add(mailPart);
					} else {
						LOG.debug("Skipping attachment: contentType %s", aPart.getContentType());
					}
				}
			}
		}
	}

	/**
	 * builds list of all parts of this message (recursing through all nested
	 * mails). In case the message is not a multipart message, it puts the
	 * message into the list
	 */
	public static MailPartList buildPartList(final Message message) {

		// application/ms-tnef

		MailPartList mailPartList = new MailPartList();
		try {
			if (message.isMimeType("multipart/*")) {

				Multipart mp = (Multipart) message.getContent();

				buildMailPartList(mailPartList, mp, 0);
			} else {
				mailPartList.add(message);
			}
		} catch (MessagingException e) {
			LOG.debug("buildPartList: ", e);
		} catch (IOException e) {
			LOG.debug("buildPartList: ", e);
		}
		return mailPartList;

	}

	public static AttachmentDescriptor storeAttachmentsTemporarily(final MailPartList partList) {

		AttachmentDescriptor attachment = new AttachmentDescriptor();
		Map<String, String> nameCache = new HashMap<String, String>();

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			LOG.warn("MD5 algorithm not available");
		}

		ZipOutputStream out = null;
		InputStream is = null;

		try {

			int cnt = partList.size();

			if (cnt > 0) {
				attachment.zipFile = File.createTempFile("mailxel-", ".zip");
				attachment.zipFile.deleteOnExit();
				LOG.debug("MailAPIUtil: writing to temp zip file: %s", attachment.zipFile);
				out = new ZipOutputStream(new FileOutputStream(attachment.zipFile));

				for (int i = 0; i < cnt; i++) {

					Part p = partList.getPart(i);

					String fn = decode(p.getFileName());
					String md5str = null;
					if (null == fn) {
						fn = Integer.toString(i) + guessExtension(p);
					}
					fn = Util.downcaseFileExtension(fn);
					if (nameCache.containsKey(fn)) {
						// need to render attachment names unique
						fn = Integer.toString(i) + "-" + fn;
					} else {
						nameCache.put(fn, fn);
					}

					is = p.getInputStream();
					LOG.debug("MailAPIUtil:   adding file: " + fn);
					if (null != digest) {
						digest.reset();
					}

					// Add ZIP entry to output stream.
					out.putNextEntry(new ZipEntry(fn));
					int bufSize = (int) 65536;
					// Read in the bytes
					int numRead = 0;
					byte[] buf = new byte[bufSize];
					numRead = is.read(buf);
					while (numRead > 0) {
						out.write(buf, 0, numRead);
						if (null != digest) {
							digest.update(buf, 0, numRead);
						}
						numRead = is.read(buf);
					}
					// Complete the entry
					out.closeEntry();
					if (null != digest) {
						byte[] md5sum = digest.digest();
						BigInteger bigInt = new BigInteger(1, md5sum);
						md5str = bigInt.toString(16);
					}

					attachment.addAttachmentFileName(fn, md5str);
					is.close();
					is = null;
					p = null;
					fn = null;
				}
				// Complete the ZIP file
				out.close();
				out = null;
			}

		} catch (MessageRemovedException e) {
			LOG.info("MailAPIUtil: message has been removed in the mean time - skip.");
			attachment = new AttachmentDescriptor();
		} catch (FileNotFoundException e) {
			LOG.warn("MailAPIUtil: file not found: %s", e);
		} catch (MessagingException e) {
			LOG.warn("MailAPIUtil: error reading the message: %s", e);
		} catch (IOException e) {
			LOG.warn("MailAPIUtil: error writing attachment: %s", e);
		} finally {
			if (null != out) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					LOG.warn("MailAPIUtil: cannot close zip file: %s", e);
				}
			}
			if (null != is) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					LOG.warn("MailAPIUtil: cannot close InputStream of Part: %s", e);
				}
			}
		}

		return attachment;
	}

	/**
	 * @return first content of this message which is of type text/plain
	 */
	public static String getText(final Part part) {

		String res = null;

		try {
			if (null != part && part.isMimeType("text/*")) {
				Object tmp = part.getContent();
				if (null == tmp) {
					LOG.debug("MailAPIUtil.getText: content object is null");
					return res;
				} else if (tmp instanceof String) {
					res = (String) tmp;
				} else if (tmp instanceof InputStream) {
					LOG.debug("MailAPIUtil.getText: handling InputStream");
					StringBuffer sb = new StringBuffer(4096);
					byte[] buf = new byte[4096];
					InputStream is = (InputStream) tmp;
					int read = is.read(buf);
					while (read > 0) {
						String s = new String(buf);
						sb.append(s);
						read = is.read(buf);
					}
					is.close();
					res = sb.toString();
				} else {
					LOG.debug("unexpected message part content detected: %s", tmp);
					res = tmp.toString();
				}
			}
		} catch (Exception e) {
			LOG.debug("MailAPIUtil.getText: cannot access text", e);
		}
		return res;
	}

	/**
	 * @return first content of this message which is of type text/plain
	 */
	public static String getFirstTextPart(final MailPartList partList) {

		String res = null;

		try {
			res = partList.dropFirstTextPart();

		} catch (Exception e) {
			LOG.debug("MailAPIUtil.getFirstTextPart: cannot find first text", e);
		}
		return res;
	}

	/**
	 * @return mime decoded string
	 */
	public static String decode(final String string) {
		String res = null;
		if (null != string) {
			try {
				res = MimeUtility.decodeText(string);
			} catch (UnsupportedEncodingException e) {
				res = string;
			}
		}
		return res;
	}

	/**
	 * @return extension for mime type starting with mimeType
	 */
	public static String getExtension(final Part part) {
		String res = ".txt";
		try {
			String mt = part.getContentType();
			res = guessExtension(mt);
		} catch (MessagingException e) {
			// silent
		}
		return res;
	}

	/**
	 * @return extension for mime type starting with mimeType
	 */
	public static String guessExtension(final Part part) {
		String res = ".dat";
		try {
			String mt = part.getContentType();
			res = guessExtension(mt);
		} catch (MessagingException e) {
			// silent
		}
		return res;
	}

	/**
	 * @return extension for mime type starting with mimeType
	 */
	public static String guessExtension(final String mimeType) {

		if (null == mimeType) {
			return ".dat";
		}

		MimeTypeDescriptor mtd = MimeTypeUtil.guessExtension(mimeType);

		String res = mtd.extension;

		if (null == res) {

			String mt1 = mimeType.toLowerCase();
			String[] tmp = mt1.split(";");
			mt1 = tmp[0];

			// text/html
			String[] parts = mt1.split("/");
			String mt = null;
			if (parts.length == 2) {
				mt = parts[1];
			} else {
				mt = parts[0];
			}
			res = "." + mt;
		}
		return res;

	}

	/**
	 * @return extension for mime type starting with mimeType
	 */
	public static String guessExtension2(final String mimeType) {

		if (null == mimeType) {
			return ".dat";
		}

		String res = null;

		if (null == res) {

			String mt = mimeType.toLowerCase();

			Set<Entry<String, String>> entries = fileextensions.entrySet();
			for (Entry<String, String> entry : entries) {
				String key = entry.getKey();
				if (mt.startsWith(key)) {
					res = entry.getValue();
					break;
				}
			}
		}

		if (null == res) {

			String mt1 = mimeType.toLowerCase();
			String[] tmp = mt1.split(";");
			mt1 = tmp[0];

			// text/html
			String[] parts = mt1.split("/");
			String mt = null;
			if (parts.length == 2) {
				mt = parts[1];
			} else {
				mt = parts[0];
			}
			res = "." + mt;
		}
		return res;

	}

	public static boolean isAnswered(final Message msg) {
		boolean res = false;
		try {
			Flags flags = msg.getFlags();
			res = flags.contains(Flag.ANSWERED);
		} catch (Exception e) {
			// silent
		}
		return res;
	}

	public static boolean isDeleted(final Message msg) {
		boolean res = false;
		try {
			Flags flags = msg.getFlags();
			res = flags.contains(Flag.DELETED);
		} catch (Exception e) {
			// silent
		}
		return res;
	}

	public static Store connect(final String protocol, final boolean isSSL, final String user, final String password,
			final String server, final String port) {

		Properties p = new Properties();

		if (isSSL) {

			// configure the jvm to use the jsse security.
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

			DummySSLSocketFactory sf = new DummySSLSocketFactory();

			p.setProperty("mail.imap.ssl.enable", "true");
			p.setProperty("mail.imap.ssl.socketFactory.class", "ch.heftix.mailxel.server.DummySSLSocketFactory");
			p.setProperty("mail.imap.ssl.socketFactory.fallback", "false");

			p.setProperty("mail.imaps.ssl.enable", "true");
			p.setProperty("mail.imaps.ssl.socketFactory.class", "ch.heftix.mailxel.server.DummySSLSocketFactory");
			p.setProperty("mail.imaps.ssl.socketFactory.fallback", "false");

			p.setProperty("mail.pop3.ssl.enable", "true");
			p.setProperty("mail.pop3.ssl.socketFactory.class", "ch.heftix.mailxel.server.DummySSLSocketFactory");
			p.setProperty("mail.pop3.ssl.socketFactory.fallback", "false");

			if (null != port) {
				p.setProperty("mail.imap.socketFactory.port", port);
			}
		}

		Session session = Session.getInstance(p);

		// session.setDebug(true);

		Store store = null;
		try {
			store = session.getStore(protocol);
			store.connect(server, user, password);

			if (null != store && store.isConnected()) {
				LOG.debug("connected: %s,%s:%s", protocol, server, port);
			} else {
				LOG.warn("connection to %s,%s:%s failed", protocol, server, port);
				store = null;
			}

		} catch (NoSuchProviderException e) {
			LOG.debug("MailAPIUtil - protocol not supported: " + e.toString());
		} catch (MessagingException e) {
			LOG.debug("MailAPIUtil - cannot connect to mail server: " + e.toString());
		}

		return store;
	}

	/**
	 * @param smtpHost
	 * @param smtpPort
	 * @param user
	 *            if STMP host requires login, use this user. null -> no login
	 *            attempted
	 * @param from
	 * @param tos
	 *            comma separated list of mail addresses
	 * @param ccs
	 * @param bccs
	 * @param subject
	 * @param body
	 * @return status message
	 */
	public static String send(final String smtpHost, final String smtpPort, final String user, final String password,
			final String from, final String tos, final String ccs, final String bccs, final String subject,
			final String body, final List<AttachmentTO> attachements) {

		String res = "200 OK";

		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);

		Session session = null;

		if (null != user && user.length() > 0) {

			props.put("mail.smtp.auth", "true");

			Authenticator a2 = new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			};
			session = Session.getInstance(props, a2);
		} else {
			session = Session.getInstance(props);
		}

		// Construct the message
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(from));
			String[] tmp = tos.split(",");
			for (int i = 0; i < tmp.length; i++) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(tmp[i]));
			}
			if (null != ccs && ccs.length() > 0) {
				tmp = ccs.split(",");
				for (int i = 0; i < tmp.length; i++) {
					msg.addRecipient(Message.RecipientType.CC, new InternetAddress(tmp[i]));
				}
			}
			if (null != bccs && bccs.length() > 0) {
				tmp = bccs.split(",");
				for (int i = 0; i < tmp.length; i++) {
					msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(tmp[i]));
				}
			}
			// bcc to myself
			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(from));

			msg.setSubject(subject);

			if (null != attachements && attachements.size() > 0) {
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setText(body);

				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(bodyPart);

				for (AttachmentTO attachment : attachements) {
					bodyPart = new MimeBodyPart();
					String name = attachment.name;
					DataSource source = new MailxelFileDataSource(name);
					bodyPart.setDataHandler(new DataHandler(source));
					File file = new File(name);
					String fn = file.getName();
					bodyPart.setFileName(fn);

					multipart.addBodyPart(bodyPart);
				}
				msg.setContent(multipart);

			} else {
				msg.setText(body);
			}

			// send
			Transport.send(msg);

			session = null;
			msg = null;

		} catch (AddressException e) {
			res = "400 problem with address: " + e.toString();
			LOG.debug("MailAPIUtil, send: " + res, e);
		} catch (MessagingException e) {
			res = "400 problem sending: " + e.toString();
			LOG.debug("MailAPIUtil, send: " + res, e);
		} catch (Exception e) {
			res = "400 generic problem: " + e.toString();
			LOG.debug("MailAPIUtil, send: " + res, e);
		}

		return res;

	}

	/*
	 * @param Confi
	 * 
	 * @return Message
	 */

	public static Message send(final ConfigTO cTO, MailTO mailTO) throws Exception {
		return send(cTO, mailTO, null);
	}

	/**
	 * @param cTO
	 *            configuration object
	 * @param mailTO
	 *            mail object
	 * @param mailDB
	 *            db (for attachment access)
	 * @return Message
	 */
	public static Message send(final ConfigTO cTO, MailTO mailTO, MailDB mailDB) throws Exception {

		if (null == cTO) {
			throw new IllegalArgumentException("cTO may not be null");
		}
		if (null == mailTO) {
			throw new IllegalArgumentException("mailTO may not be null");
		}
		if (null == mailTO.from) {
			throw new IllegalArgumentException("from may not be null");
		}
		if (null == mailTO.to) {
			throw new IllegalArgumentException("to may not be null");
		}

		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", cTO.smtpHost);
		props.put("mail.smtp.port", cTO.smtpPort);

		Session session = null;

		if (null != cTO.smtpUser && cTO.smtpUser.length() > 0) {

			props.put("mail.smtp.auth", "true");

			Authenticator a2 = new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(cTO.smtpUser, cTO.smtpPassword);
				}
			};
			session = Session.getInstance(props, a2);
		} else {
			session = Session.getInstance(props);
		}

		// Construct the message
		Message msg = null;
		try {
			msg = createMessage(mailTO, session, mailDB);
			if (cTO.sentHandlingBCC) {
				// bcc to myself
				msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(cTO.me));
			}

			// send
			Transport.send(msg);

			session = null;

		} catch (AddressException e) {
			LOG.debug("MailAPIUtil, problem with address '%s':", e.toString(), e);
			throw new Exception(e);
		} catch (MessagingException e) {
			LOG.debug("MailAPIUtil, cannot send", e);
			throw new Exception(e);
		}

		return msg;
	}

	public static Message createMessage(final MailTO mailTO, final Session session, final MailDB mailDB)
			throws MessagingException, UnsupportedEncodingException {

		String charset = "iso-8859-1";

		// Construct the message
		MimeMessage msg = new MimeMessage(session);
		String pname = mailDB.getAddressName(mailTO.from);
		if (null != pname) {
			msg.setFrom(new InternetAddress(mailTO.from, pname));
		} else {
			msg.setFrom(new InternetAddress(mailTO.from));
		}
		String[] tmp = mailTO.to.split(",");
		for (int i = 0; i < tmp.length; i++) {
			pname = mailDB.getAddressName(tmp[i]);
			addRecipient(msg, Message.RecipientType.TO, tmp[i], pname);
		}
		if (null != mailTO.cc && mailTO.cc.length() > 0) {
			tmp = mailTO.cc.split(",");
			for (int i = 0; i < tmp.length; i++) {
				pname = mailDB.getAddressName(tmp[i]);
				addRecipient(msg, Message.RecipientType.CC, tmp[i], pname);
			}
		}
		if (null != mailTO.bcc && mailTO.bcc.length() > 0) {
			tmp = mailTO.bcc.split(",");
			for (int i = 0; i < tmp.length; i++) {
				pname = mailDB.getAddressName(tmp[i]);
				addRecipient(msg, Message.RecipientType.BCC, tmp[i], pname);
			}
		}

		msg.setSubject(mailTO.subject, charset);

		if (null != mailTO.attachments && mailTO.attachments.size() > 0) {

			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setText(mailTO.body, charset);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart);

			for (AttachmentTO attachment : mailTO.attachments) {
				bodyPart = new MimeBodyPart();
				DataSource source = null;
				if (Constants.UNDEFINED_ID == attachment.id) {
					// try to read from file system
					String name = attachment.name;
					source = new MailxelFileDataSource(name);
				} else {
					if (null != mailDB) {
						// try to read from DB/zip file
						source = new MailxelDBDataSource(attachment.id, mailDB);
					} else {
						LOG.warn("cannot add attchments from DB if DB is null");
					}
				}
				bodyPart.setDataHandler(new DataHandler(source));
				File file = new File(attachment.name);
				String fn = file.getName();
				bodyPart.setFileName(fn);

				multipart.addBodyPart(bodyPart);
			}
			msg.setContent(multipart);

		} else {
			msg.setText(mailTO.body, charset);
		}

		msg.setSentDate(new Date());

		return msg;

	}

	protected static void addRecipient(final Message msg, final Message.RecipientType type, final String address,
			final String personal) throws MessagingException, UnsupportedEncodingException {
		if (null != personal) {
			msg.addRecipient(type, new InternetAddress(address, personal));
		} else {
			msg.addRecipient(type, new InternetAddress(address));
		}

	}

	public static boolean isText(final Part part) {
		boolean res = false;
		try {
			if (part.isMimeType("text/*")) {
				res = true;
			}
		} catch (MessagingException e) {
			// silently
		}
		return res;

	}

	public static boolean isHTML(final Part part) {
		boolean res = false;
		try {
			if (part.isMimeType("text/html")) {
				res = true;
			}
		} catch (MessagingException e) {
			// silently
		}
		return res;

	}

	/**
	 * @return mime type
	 */
	public static String getMimeType(final Part part) {
		String res = "text/plain";
		try {
			String mt = part.getContentType();
			if (null != mt) {
				int idx = mt.indexOf(";");
				if (idx > 0) {
					mt = mt.substring(0, idx);
				}
				res = mt;
			}
		} catch (MessagingException e) {
			// silent
		}
		return res;
	}

}

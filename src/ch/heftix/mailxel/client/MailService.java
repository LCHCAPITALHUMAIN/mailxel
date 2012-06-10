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
package ch.heftix.mailxel.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import ch.heftix.mailxel.client.to.AddressTO;
import ch.heftix.mailxel.client.to.AttachedCategoryTO;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.AttachmentWithEnvelopes;
import ch.heftix.mailxel.client.to.Category;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.mailxel.client.to.IconTO;
import ch.heftix.mailxel.client.to.MailTO;
import ch.heftix.mailxel.client.to.MessageQueryTO;
import ch.heftix.mailxel.client.to.MessageSearchTO;
import ch.heftix.mailxel.client.to.StatisticsTO;


@RemoteServiceRelativePath("mailxel")
public interface MailService extends RemoteService {

  public final static String ACCOUNT_SEND = "___mailxel_send_account___";

  public String ping(String s);

  public List<Envelope> select(final int page, MessageSearchTO messageSearchTO);

  public MailTO get(final int id);

  // public void storeNote(final MailTO mailTO) throws MailxelServiceException;
  public String storeNote(final MailTO mailTO);

  public String send(final MailTO mailTO);

  public List<AddressTO> suggestAddress(final String query);

  /** @param accounts download from all accounts if null */
  public String scanMailFolder(final String[] accounts);

  public boolean hasConfig();

  public void saveConfig(final ConfigTO config);

  public ConfigTO getConfig();

  public void deleteConfig();

  public List<AddressTO> searchAddresses(final String shortname, final String address);

  public void saveAddresses(final List<AddressTO> addresses);

  public void saveAddresses(final String lineSeparatedShortNameAddress);

  public void setPassword(final String accountName, final String password);

  public void forgetPassword(final String accountName);

  public String reorgMailFolder(final String[] accounts);

  public void saveCategories(final List<Category> categories);

  public List<Category> searchCategories(final String name);

  public List<Category> suggestCategory(final String query);

  public void markAsDeleted(final List<Integer> ids);

  public void updateCategories(final List<Integer> ids, final String categories);

  public void markStatus(final int msgId, final int categoryId);

  public void changeLogConfiguration(final int logConfiguration);

  public void setDBTiming(final boolean doTimeDB);

  public long getTotalNumMessages();

  /**
   * try to import messages from mbox file
   * 
   * @param mboxFile path to file
   */
  public void importMboxFile(final String mboxFile);

  /**
   * accesses attachment data as string; intended to be used for text
   * attachments
   * 
   * @param attachmentId
   * @return attachment content as string
   */
  public String getAttachmentData(final int attachmentId);

  public boolean isPasswordSet(final String account);

  public List<AttachmentTO> getAttachments(final int mailId);

  public StatisticsTO calculateStatistics();

  public void updateToMeFlag();

  public void updateFromMeFlag();

  public MessageQueryTO updateMessageQuery(final MessageQueryTO mqTO);

  public List<MessageQueryTO> searchQueries(final String type, final String name);

  public List<Envelope> executeMessageQuery(final MessageQueryTO mqTO);

  public List<IconTO> searchIcons(final String name);
  
  public String postpone(final MailTO mailTO);
  
  public long count(final MessageQueryTO mqTO);
  
  public String housekeeping();
  
  public String snippet(final int id);
  
  public Category getCategory(final int id);
  
  public IconTO getIconForCategory(final int id);
  
  public List<AttachedCategoryTO> getCategoryHistory(final int mailID);

  public List<AttachmentWithEnvelopes> searchAttachment(final String query);
}

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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MailServiceAsync {

	public void ping(String s, AsyncCallback<String> callback);

	public void select(final int page, MessageSearchTO messageSearchTO, AsyncCallback<List<Envelope>> callback);

	public void get(final int id, AsyncCallback<MailTO> callback);

	public void storeNote(final MailTO mailTO, AsyncCallback<String> callback);

	public void send(final MailTO mailTO, AsyncCallback<String> callback);

	public void suggestAddress(final String query, AsyncCallback<List<AddressTO>> callback);

	public void scanMailFolder(final String[] accounts, AsyncCallback<String> callback);

	public void hasConfig(AsyncCallback<Boolean> callback);

	public void saveConfig(final ConfigTO config, AsyncCallback<Void> callback);

	public void getConfig(AsyncCallback<ConfigTO> callback);

	public void deleteConfig(AsyncCallback<Void> callback);

	public void searchAddresses(final String shortname, final String address, AsyncCallback<List<AddressTO>> callback);

	public void saveAddresses(final List<AddressTO> addresses, AsyncCallback<Void> callback);

	public void saveAddresses(final String lineSeparatedShortNameAddress, AsyncCallback<Void> callback);

	public void setPassword(final String accountName, final String password, AsyncCallback<Void> callback);

	public void forgetPassword(final String accountName, AsyncCallback<Void> callback);

	public void reorgMailFolder(final String[] accounts, AsyncCallback<String> callback);

	public void saveCategories(final List<Category> categories, AsyncCallback<Void> callback);

	public void searchCategories(final String name, AsyncCallback<List<Category>> callback);

	public void suggestCategory(final String query, AsyncCallback<List<Category>> callback);

	public void markAsDeleted(final List<Integer> ids, AsyncCallback<Void> callback);

	public void updateCategories(final List<Integer> ids, final String categories, AsyncCallback<Void> callback);

	public void markStatus(final int msgId, final int categoryId, AsyncCallback<Void> callback);

	public void changeLogConfiguration(final int logConfiguration, AsyncCallback<Void> callback);

	public void setDBTiming(final boolean doTimeDB, AsyncCallback<Void> callback);

	public void importMboxFile(final String mboxFile, AsyncCallback<Void> callback);

	public void getAttachmentData(final int attachmentId, AsyncCallback<String> callback);

	public void getTotalNumMessages(AsyncCallback<Long> callback);

	public void isPasswordSet(final String account, AsyncCallback<Boolean> callback);

	public void getAttachments(final int mailId, AsyncCallback<List<AttachmentTO>> callback);

	public void calculateStatistics(AsyncCallback<StatisticsTO> callback);

	public void updateToMeFlag(AsyncCallback<Void> callback);

	public void updateFromMeFlag(AsyncCallback<Void> callback);

	public void updateMessageQuery(final MessageQueryTO mqTO, AsyncCallback<MessageQueryTO> callback);

	public void searchQueries(final String type, final String name, AsyncCallback<List<MessageQueryTO>> callback);

	public void executeMessageQuery(final MessageQueryTO mqTO, AsyncCallback<List<Envelope>> callback);

	public void searchIcons(final String name, AsyncCallback<List<IconTO>> callback);

	public void postpone(final MailTO mailTO, AsyncCallback<String> callback);

	public void count(final MessageQueryTO mqTO, AsyncCallback<Long> callback);

	public void housekeeping(AsyncCallback<String> callback);

	public void snippet(final int id, AsyncCallback<String> callback);

	public void getCategory(final int id, AsyncCallback<Category> callback);

	public void getIconForCategory(final int id, AsyncCallback<IconTO> callback);

	public void getCategoryHistory(final int mailID, AsyncCallback<List<AttachedCategoryTO>> callback);

	public void searchAttachment(final String query, AsyncCallback<List<AttachmentWithEnvelopes>> callback);

}

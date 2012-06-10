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

import java.util.Calendar;
import java.util.Date;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Flags.Flag;

import ch.heftix.mailxel.client.ListUtil;
import ch.heftix.mailxel.client.to.ReorgRuleTO;
import ch.heftix.xel.log.LOG;
import ch.heftix.xel.util.Util;


/**
 * rule syntax: inbox: file-and-forget/%yyyy/%MM/%yyyy-MM-dd,sent:
 * sent-mails/%yyyy/%MM/%yyyy-MM-dd
 */
public class MailReorganizerFolderHandler implements FolderHandler {

  private Store store = null;
  private String[] rParts = new String[0];
  private boolean[] isParameter = new boolean[0];
  private int copyMaxMessages = 100;
  private int keepLastNHours = 24;

  public MailReorganizerFolderHandler(final Store store,
      final String rule,
      final int copyMaxMessages,
      final int keepLastNHours) {

    this.store = store;
    this.copyMaxMessages = copyMaxMessages;
    this.keepLastNHours = keepLastNHours;

    if (null == rule) {
      LOG.info("no rule defined - skipping folder.");
      return;
    }

    String[] tmp = rule.split("/");
    if (null == tmp || tmp.length < 0) {
      LOG.info("cannot parse rule: '%s'. Skipping folder.", rule);
      return;
    }

    rParts = new String[tmp.length];
    isParameter = new boolean[rParts.length];

    for (int j = 0; j < rParts.length; j++) {
      String name = tmp[j].trim();
      if (null != name && name.length() > 0) {
        if (name.startsWith("%")) {
          rParts[j] = name.substring(1);
          isParameter[j] = true;
        } else {
          rParts[j] = name;
        }
      }
    }

  }


  public void handleFolder(Folder folder) throws MessagingException, Exception {

    LOG.info("reorganizing mails from '%s'", folder);

    folder.open(Folder.READ_WRITE);

    Message[] msgs = folder.getMessages();

    int numCopied = 0;

    for (int i = 0; i < msgs.length; i++) {

      Message msg = msgs[i];
      boolean deleted = MailAPIUtil.isDeleted(msg);

      // skip already deleted messages
      if (deleted) {
        msgs[i] = null;
        continue;
      }

      // skip messages from today
      Date sentDate = MailAPIUtil.getSentDate(msg);
      Calendar c1 = Calendar.getInstance();
      c1.add(Calendar.HOUR, -keepLastNHours);
      Date yest = c1.getTime();

      if (!sentDate.before(yest)) {
        msgs[i] = null;
        continue;
      }

      // find corresponding folder: file-and-forget/yyyy/mm/yyyy-mm-dd
      Folder parent = store.getFolder(rParts[0]);
      if (!parent.exists()) {
        parent.create(Folder.HOLDS_MESSAGES | Folder.HOLDS_FOLDERS);
      }
      Folder f = parent;
      for (int j = 1; j < rParts.length; j++) {
        String name = rParts[j];
        if (isParameter[j]) {
          name = Util.format(rParts[j], sentDate);
        }

        f = f.getFolder(name);
        if (!f.exists()) {
          LOG.debug("creating folder %s", name);
          f.create(Folder.HOLDS_MESSAGES | Folder.HOLDS_FOLDERS);
        };
      }

      Message[] tocopy = {msg};

      folder.copyMessages(tocopy, f);
      msg.setFlag(Flag.DELETED, true);

      msgs[i] = null;

      if (numCopied > copyMaxMessages) {
        LOG.debug(Integer.toString(numCopied) + " messages copied");
        break;
      }

      numCopied++;
    }

    if (numCopied > 0) {

      LOG.debug(String.format("copied %d messages; now expunging mails from '%s'", numCopied,
          folder));
      folder.expunge();
    }

    folder.close(false);
  }


  public static ReorgRuleTO parseRule(final String[] reorgRules) {

    ReorgRuleTO res = new ReorgRuleTO();

    if (null == reorgRules || reorgRules.length < 1) {
      res.parseResult = "200 OK no reorg rule defined - skipping.";
      return res;
    }

    int copyMaxMessages = 100;
    int keepLastNHours = 24;

    for (int j = 0; j < reorgRules.length; j++) {

      if (null == reorgRules[j] || reorgRules[j].length() < 1) {
        res.parseResult = "200 OK null or empty rule - skipping.";
        return res;
      }

      String[] rParts = reorgRules[j].split(":");
      if (null == rParts || rParts.length != 2) {
        res.parseResult = String.format("400 cannot parse reorg rule '%s' - skipping.", rParts[j]);
        return res;
      }

      if (null == rParts[0] || null == rParts[1]) {
        res.parseResult = String.format("400 cannot parse reorg rule '%s' - skipping.", rParts[j]);
        return res;
      }

      String folderName = rParts[0].trim();
      String rule = rParts[1].trim();

      if ("max".equals(folderName)) {
        res.copyMaxMessages = ListUtil.parse(rule, copyMaxMessages);
        LOG.debug("reorg rule: handling max %d messages", res.copyMaxMessages);
      } else if ("keep".equals(folderName)) {
        res.keepLastNHours = ListUtil.parse(rule, keepLastNHours);
        LOG.debug("reorg rule: keeping last %d hours of messages", res.keepLastNHours);
      } else {
        res.folderNames.add(folderName);
        res.rules.add(rule);

      }
    }
    return res;

  }
}

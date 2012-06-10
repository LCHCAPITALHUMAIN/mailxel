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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import ch.heftix.mailxel.client.AddressTOUtil;
import ch.heftix.mailxel.client.ListUtil;
import ch.heftix.mailxel.client.to.AccountConfigTO;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.xel.log.LOG;

public class ConfigManager {

  public static void saveConfig(final ConfigTO config) {

    LOG.debug("saveConfig: starting");

    Preferences prefs = getPreferences();

    LOG.debug("saveConfig: got preferences node");

    prefs.put("me", config.me);
    prefs.put("replyAddressDisplayType", Integer.toString(config.replyAddressDisplayType));
    prefs.put("displayTime", Boolean.toString(config.displayTime));
    prefs.put("explcitWildcards", Boolean.toString(config.explcitWildcards));
    prefs.put("maxMailDownloadsPerFolder", Integer.toString(config.maxMailDownloadsPerFolder));

    prefs.put("smtpHost", config.smtpHost);
    prefs.put("smtpPort", config.smtpPort);
    prefs.put("smtpUser", config.smtpUser);

    prefs.put("sentHandlingBCC", Boolean.toString(config.sentHandlingBCC));
    prefs.put("sentHandlingStoreLocally", Boolean.toString(config.sentHandlingStoreLocally));
    prefs.put("sentHandlingCopySent", Boolean.toString(config.sentHandlingCopySent));
    prefs.put("sentHandlingCopyAccountName", config.sentHandlingCopyAccountName);
    prefs.put("sentHandlingCopyFolderName", config.sentHandlingCopyFolderName);

    prefs.put("maxSearchRows", Integer.toString(config.maxSearchRows));
    prefs.put("maxAddressSuggestions", Integer.toString(config.maxAddressSuggestions));

    prefs.put("logConfiguration", Integer.toString(config.logConfiguration));

    prefs.put("downloadMailDelay", Integer.toString(config.downloadMailDelay));

    String[] accountNames = config.accountNames;
    prefs.put("accounts", ListUtil.asComaSeparted(config.accountNames));
    for (int i = 0; i < accountNames.length; i++) {
      AccountConfigTO acTO = config.accounts[i];
      prefs.put(accountNames[i] + ".protocol", acTO.protocol);
      prefs.put(accountNames[i] + ".server", acTO.server);
      prefs.put(accountNames[i] + ".isSSL", Boolean.toString(acTO.isSSL));
      prefs.put(accountNames[i] + ".user", acTO.user);
      prefs.put(accountNames[i] + ".port", acTO.port);
      prefs.put(accountNames[i] + ".scannedfolders", ListUtil.asComaSeparted(acTO.scannedfolders));
      prefs.put(accountNames[i] + ".excludedfolders", ListUtil.asComaSeparted(acTO.excludedfolders));
      prefs.put(accountNames[i] + ".reuseSendPassword", Boolean.toString(acTO.reuseSendPassword));
      prefs.put(accountNames[i] + ".reorgRules", ListUtil.asComaSeparted(acTO.reorgRules));
    }

    prefs.put("localstore", config.localstore);
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      LOG.warn("Cannot store preferences: %s", e);
    }

    LOG.debug("saveConfig: done");

    return;
  }

  public static ConfigTO readConfig() {


    Preferences prefs = getPreferences();

    ConfigTO cfgTO = new ConfigTO();

    cfgTO.localstore = prefs.get("localstore", null);
    if (null == cfgTO.localstore) {
      return cfgTO;
    }
    // if localstore is set, we assume that a config exists and load it
    cfgTO.me = prefs.get("me", null);
    String tmp = prefs.get("replyAddressDisplayType", "1");
    cfgTO.replyAddressDisplayType = ListUtil.parse(tmp, AddressTOUtil.DISPLAY_NAME);
    cfgTO.displayTime = ListUtil.parse(prefs.get("displayTime", "false"));
    cfgTO.explcitWildcards = ListUtil.parse(prefs.get("explcitWildcards", "true"));
    tmp = prefs.get("maxMailDownloadsPerFolder", "100");
    cfgTO.maxMailDownloadsPerFolder = ListUtil.parse(tmp, 100);

    cfgTO.smtpHost = prefs.get("smtpHost", null);
    cfgTO.smtpPort = prefs.get("smtpPort", null);
    cfgTO.smtpUser = prefs.get("smtpUser", null);

    cfgTO.sentHandlingBCC = ListUtil.parse(prefs.get("sentHandlingBCC", "false"));
    cfgTO.sentHandlingStoreLocally = ListUtil.parse(prefs.get("sentHandlingStoreLocally", "false"));
    cfgTO.sentHandlingCopySent = ListUtil.parse(prefs.get("sentHandlingCopySent", "false"));

    cfgTO.sentHandlingCopyAccountName = prefs.get("sentHandlingCopyAccountName", null);
    cfgTO.sentHandlingCopyFolderName = prefs.get("sentHandlingCopyFolderName", null);

    tmp = prefs.get("maxSearchRows", "50");
    cfgTO.maxSearchRows = ListUtil.parse(tmp, 50);

    tmp = prefs.get("maxAddressSuggestions", "10");
    cfgTO.maxAddressSuggestions = ListUtil.parse(tmp, 10);

    tmp = prefs.get("logConfiguration", Integer.toString(1));
    cfgTO.logConfiguration = ListUtil.parse(tmp, 1);

    tmp = prefs.get("downloadMailDelay", "0");
    cfgTO.downloadMailDelay = ListUtil.parse(tmp, 0);

    tmp = prefs.get("accounts", "");

    cfgTO.accountNames = ListUtil.getFromComaSeparated(tmp);
    cfgTO.accounts = new AccountConfigTO[cfgTO.accountNames.length];
    for (int i = 0; i < cfgTO.accountNames.length; i++) {
      AccountConfigTO acTO = new AccountConfigTO();
      acTO.name = cfgTO.accountNames[i];
      acTO.protocol = prefs.get(cfgTO.accountNames[i] + ".protocol", "imap");
      acTO.server = prefs.get(cfgTO.accountNames[i] + ".server", null);
      acTO.isSSL = ListUtil.parse(prefs.get(cfgTO.accountNames[i] + ".isSSL", "false"));
      acTO.user = prefs.get(cfgTO.accountNames[i] + ".user", null);
      acTO.port = prefs.get(cfgTO.accountNames[i] + ".port", null);
      acTO.scannedfolders = ListUtil.getFromComaSeparated(prefs.get(cfgTO.accountNames[i]
          + ".scannedfolders", ""));
      acTO.excludedfolders = ListUtil.getFromComaSeparated(prefs.get(cfgTO.accountNames[i]
          + ".excludedfolders", ""));
      acTO.reuseSendPassword = ListUtil.parse(prefs.get(cfgTO.accountNames[i]
          + ".reuseSendPassword", "false"));
      acTO.reorgRules = ListUtil.getFromComaSeparated(prefs.get(cfgTO.accountNames[i]
          + ".reorgRules", ""));
      cfgTO.accounts[i] = acTO;
    }

    prefs = null;

    return cfgTO;
  }


  public static void main(String[] args) {

    ConfigTO cfgTO = readConfig();
    String txt = cfgTO.toString();
    System.out.println(txt);
  }

  public static void deleteConfig() {

    Preferences prefs = getPreferences();
    try {
      prefs.removeNode();
    } catch (BackingStoreException e) {
      LOG.warn("Cannot remove configuration: %s", e);
    }
  }

  private static Preferences getPreferences() {

    String test = System.getProperty("mailxel.installtest");
    Preferences prefs = null;
    if (null != test) {
      prefs = new TransientPreferences();
    } else {
      prefs = Preferences.userNodeForPackage(ConfigManager.class);
    }
    return prefs;
  }


}

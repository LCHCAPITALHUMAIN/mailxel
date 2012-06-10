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
package ch.heftix.mailxel.client.to;

import java.io.Serializable;

import ch.heftix.mailxel.client.AddressTOUtil;


public class ConfigTO implements Serializable {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -4315995401317533205L;

  public String localstore = null; // d:/app-data/mailxel

  public String smtpHost = null; // smtp.company.com
  public String smtpPort = "25";
  public String smtpUser = null;
  public String smtpPassword = null;

  public boolean sentHandlingBCC = true;
  public boolean sentHandlingStoreLocally = false;
  public boolean sentHandlingCopySent = true;
  public String sentHandlingCopyAccountName = null;
  public String sentHandlingCopyFolderName = "sent";

  public int logConfiguration = 1;

  public int maxSearchRows = 50;
  public int maxAddressSuggestions = 10;

  public String me = "firstname.lastname@company.com"; // simon.hefti@gmail.com

  public int replyAddressDisplayType = AddressTOUtil.DISPLAY_NAME;

  public boolean displayTime = false;
  public boolean explcitWildcards = true;
  public int maxMailDownloadsPerFolder = 100;

  public int downloadMailDelay = 0; // download mail all x minutes

  public String[] accountNames = new String[0];
  public AccountConfigTO[] accounts = new AccountConfigTO[0];


  public AccountConfigTO getAccountConfigTOByAccountName(final String accountName) {

    if (null == accountName) {
      return null;
    }

    int accountIdx = -1;

    for (int i = 0; i < accountNames.length; i++) {
      if (accountName.equals(accountNames[i])) {
        accountIdx = i;
        break;
      }
    }

    if (accountIdx < 0) {
      return null;
    }

    return accounts[accountIdx];
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("mailxel config:\n");
    sb.append("DB location:    " + localstore);
    sb.append("\n");
    sb.append("me:             " + me);
    sb.append("\n");
    sb.append("reply address t:" + replyAddressDisplayType);
    sb.append("\n");
    sb.append("display time:   " + displayTime);
    sb.append("\n");
    sb.append("explcitWildcard:" + explcitWildcards);
    sb.append("\n");
    sb.append("max downloads per folder:" + maxMailDownloadsPerFolder);
    sb.append("\n");
    sb.append("mail download delay:" + downloadMailDelay);
    sb.append("\n");
    sb.append("send host/port: " + smtpHost + ":" + smtpPort);
    sb.append("\n");
    for (int i = 0; i < accountNames.length; i++) {
      sb.append("+ account " + accountNames[i]);
      sb.append("\n");
      sb.append("  server: " + accounts[i].server);
      sb.append("\n");
      sb.append("  prot:   " + accounts[i].protocol);
      sb.append("\n");
      sb.append("  SSL:    " + accounts[i].isSSL);
      sb.append("\n");
      sb.append("  Port:   " + accounts[i].port);
      sb.append("\n");
      sb.append("  + Folders:");
      sb.append("\n");
      for (int j = 0; j < accounts[i].scannedfolders.length; j++) {
        sb.append("    + " + accounts[i].scannedfolders[j]);
        sb.append("\n");
      }
      sb.append("  + Excluded Folders:");
      sb.append("\n");
      for (int j = 0; j < accounts[i].excludedfolders.length; j++) {
        sb.append("    + " + accounts[i].excludedfolders[j]);
        sb.append("\n");
      }
      sb.append("  User:   " + accounts[i].user);
      sb.append("\n");
    }
    String res = sb.toString();
    return res;
  }
}

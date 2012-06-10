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


import ch.heftix.mailxel.client.to.MailTO;

public class MailTOUtil {

  // /** @return comma-separated list of to addresses */
  // public static String getToAddresses(final MailTO mailTO) {
  // return AddressTOUtil.commaSepartedAddresses(mailTO.toATOs, ADDRESS);
  // }
  //
  // /** @return comma-separated list of cc addresses */
  // public static String getCcAddresses(final MailTO mailTO) {
  // return AddressTOUtil.commaSepartedAddresses(mailTO.ccATOs, ADDRESS);
  // }
  //
  // /** @return comma-separated list of bcc addresses */
  // public static String getBccAddresses(final MailTO mailTO) {
  // return AddressTOUtil.commaSepartedAddresses(mailTO.bccATOs, ADDRESS);
  // }

  /** @return comma-separated list of display texts of to addresses */
  public static String getToAddressesDisplayText(final MailTO mailTO, final int type) {
    return AddressTOUtil.commaSepartedAddresses(mailTO.toATOs, type);
  }

  /** @return comma-separated list of display texts of cc addresses */
  public static String getCcAddressesDisplayText(final MailTO mailTO, final int type) {
    return AddressTOUtil.commaSepartedAddresses(mailTO.ccATOs, type);
  }

}

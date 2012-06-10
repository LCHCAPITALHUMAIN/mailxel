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


public class AddressTOUtil {

  public static final int DISPLAY_SHORTNAME = 0;
  public static final int DISPLAY_NAME = 1;
  public static final int DISPLAY_ADDRESS = 2;

  public static String getDisplayText(final AddressTO addressTO, final int type) {

    String res = addressTO.address;
    // first, pick by type
    switch (type) {
      case DISPLAY_SHORTNAME:
        if (null != addressTO.shortname && addressTO.shortname.length() > 0) {
          res = addressTO.shortname;
        } else if (null != addressTO.name && addressTO.name.length() > 0) {
          res = addressTO.name;
        } else {
          res = addressTO.address;
        }
        break;
      case DISPLAY_NAME:
        if (null != addressTO.name && addressTO.name.length() > 0) {
          res = addressTO.name;
        } else {
          res = addressTO.address;
        }
        break;
      default:
        res = addressTO.address;
        break;
    }
    return res;
  }

  public static String getSuggestDisplayText(final AddressTO addressTO) {
    StringBuffer sb = new StringBuffer(128);
    if (null != addressTO.shortname) {
      sb.append(addressTO.shortname);
      sb.append(" / ");
    }
    if (null != addressTO.name) {
      sb.append(addressTO.name);
      sb.append(" / ");
    }
    if (null != addressTO.address) {
      sb.append(addressTO.address);
      sb.append(" / ");
    }
    sb.append(addressTO.lastSeen);
    sb.append(" / ");
    sb.append(addressTO.count);

    String res = sb.toString();
    return res;
  }

  public static String commaSepartedAddresses(final List<AddressTO> vals, final int type) {
    if (null == vals) {
      return null;
    }

    StringBuffer sb = new StringBuffer(vals.size() * 32);
    for (int i = 0; i < vals.size(); i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(getDisplayText(vals.get(i), type));
    }
    return sb.toString();
  }

}

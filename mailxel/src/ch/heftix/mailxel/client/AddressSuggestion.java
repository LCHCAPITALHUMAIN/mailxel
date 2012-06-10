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

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import ch.heftix.mailxel.client.to.AddressTO;

public class AddressSuggestion implements Suggestion {

  private AddressTO addressTO;

  public AddressSuggestion(final AddressTO addressTO) {
    this.addressTO = addressTO;
  }

  public String getDisplayString() {
    return addressTO.address;
  }

  public String getReplacementString() {
    return addressTO.address;
  }

  public AddressTO getAddressTO() {
    return addressTO;
  }

}

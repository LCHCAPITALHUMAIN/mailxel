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

public class AddressBar extends MultiLinePanel {

  public AddressBar() {
    super(100, " , ");
  }

  public void add(final AddressTO addressTO) {
    if (null != addressTO) {
      AddressLabel aLabel = new AddressLabel(addressTO);
      addWidget(aLabel, aLabel.getDisplayTextLength());
    }
  }

  public void add(final List<AddressTO> addressTOs) {
    if (null != addressTOs) {
      for (AddressTO aTO : addressTOs) {
        add(aTO);
      }
    }
  }

}

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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import ch.heftix.mailxel.client.to.AddressTO;

public class AddressEditBar extends HorizontalPanel implements ItemDeletionHandler<AddressTO> {

  private SuggestBox sb = null;
  private List<DeletableItem<AddressTO>> items = new ArrayList<DeletableItem<AddressTO>>();
  private MultiLinePanel mlPanel = null;

  public AddressEditBar(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    // mlPanel = new MultiLinePanel(100, new Label(","), 1);
    mlPanel = new MultiLinePanel(100, null);
    AddressSuggestOracle oracle = new AddressSuggestOracle(mailxelService, mailxelPanel);
    sb = new SuggestBox(oracle);
    sb.setWidth("80px");
    sb.addSelectionHandler(new SelectionHandler<Suggestion>() {

      public void onSelection(SelectionEvent<Suggestion> event) {
        AddressSuggestion s = (AddressSuggestion) event.getSelectedItem();
        AddressTO aTO = s.getAddressTO();
        addAddress(aTO);
        sb.setValue("");
      }
    });
    Image add = new Image("img/plus.png");
    add.setTitle("add recipient");
    add.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        String tmp = sb.getText();
        if (null != tmp && tmp.length() > 3 && tmp.contains("@")) {
          AddressTO aTO = new AddressTO();
          aTO.address = tmp.trim();
          addAddress(aTO);
          sb.setValue("");
        }
      }
    });

    Image erase = new Image("img/eraser.png");
    erase.setTitle("remove all recipients");
    erase.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        for (DeletableItem<AddressTO> di : items) {
          mlPanel.removeWidget(di);
        }
        items.clear();
      }
    });
    add(sb);
    add(add);
    add(erase);
    add(mlPanel);
  }

  public void addAddress(final AddressTO aTO) {
    AddressLabel al = new AddressLabel(aTO);
    DeletableItem<AddressTO> di = new DeletableItem<AddressTO>(al, aTO, AddressEditBar.this);
    mlPanel.addWidget(di, al.getDisplayTextLength());
    items.add(di);
  }

  public void addAddresses(final List<AddressTO> addresses) {
    for (AddressTO aTO : addresses) {
      addAddress(aTO);
    }
  }

  public String getAddresses() {
    StringBuffer sb = new StringBuffer(128);
    boolean first = true;
    for (DeletableItem<AddressTO> di : items) {
      if (!first) {
        sb.append(",");
      } else {
        first = false;
      }
      AddressTO aTO = di.getPayload();
      sb.append(aTO.address);
    }
    return sb.toString();
  }

  public void onDeletion(DeletableItem<AddressTO> item) {
    mlPanel.removeWidget(item);
    items.remove(item);
  }

}

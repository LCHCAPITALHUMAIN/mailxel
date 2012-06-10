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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddressUploadGrid extends VerticalPanel {

  private Label statusMessage = new Label();

  private TextArea textArea = new TextArea();

  public AddressUploadGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    HorizontalPanel toolbar = new HorizontalPanel();
    Image save = new Image("img/save.png");
    save.setTitle("Save");
    save.setStylePrimaryName("mailxel-toolbar-item");
    save.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        String text = textArea.getText();
        mailxelService.saveAddresses(text, new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
            statusMessage.setText("Cannot save addresses: " + caught);
          }

          public void onSuccess(Void result) {
            statusMessage.setText("Addresses saved");
          }
        });
      }
    });

    toolbar.add(save);
    toolbar.add(statusMessage);
    add(toolbar);

    textArea.setCharacterWidth(80);
    textArea.setVisibleLines(25);

    add(textArea);
  }

}

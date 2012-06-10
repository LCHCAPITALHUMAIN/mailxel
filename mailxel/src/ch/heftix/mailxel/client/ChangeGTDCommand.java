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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PopupPanel;

import ch.heftix.mailxel.client.to.Envelope;


public class ChangeGTDCommand extends PopupCommand {

  private MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;
  private List<CheckBox> checkboxes = null;
  private List<Envelope> envelopes = null;
  private String gtd = null;


  public ChangeGTDCommand(final MailServiceAsync mailxelService,
      final PopupPanel popupPanel,
      final MailxelPanel mailxelPanel,
      final List<CheckBox> checkboxes,
      final List<Envelope> envelopes,
      final String gtd) {

    super();
    setPopupPanel(popupPanel);
    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;
    this.checkboxes = checkboxes;
    this.envelopes = envelopes;
    this.gtd = gtd;
  }

  public void execute() {

    popupPanel.hide();

    final StatusItem si = mailxelPanel.statusStart("updating categories on selected messages");
    List<Integer> ids = new ArrayList<Integer>();
    for (int i = 0; i < envelopes.size(); i++) {
      CheckBox cb = checkboxes.get(i);
      if (cb.getValue()) {
        Envelope env = envelopes.get(i);
        ids.add(env.id);
      }
    }

    mailxelService.updateCategories(ids, gtd, new AsyncCallback<Void>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

      public void onSuccess(Void result) {
        si.done();
      }
    });

  }
}

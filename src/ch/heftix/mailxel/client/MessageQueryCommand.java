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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.mailxel.client.to.MessageQueryTO;


/**
 * query mails based on pre-defined query
 */
public class MessageQueryCommand extends PopupCommand {

  protected MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;
  private MessageQueryTO mqTO = null;

  public MessageQueryCommand(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel,
      final PopupPanel popupPanel,
      final MessageQueryTO mqTO) {

    super();
    setPopupPanel(popupPanel);
    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;
    this.mqTO = mqTO;
  }

  public void execute() {

    final StatusItem si = mailxelPanel.statusStart("execute message query " + mqTO.shortname);

    // hide menu
    popupPanel.hide();

    mailxelService.executeMessageQuery(mqTO, new AsyncCallback<List<Envelope>>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

      public void onSuccess(List<Envelope> result) {
        final MailOverviewGrid p = new MailOverviewGrid(true, mailxelService, mailxelPanel);
        p.fillGrid(result);
        mailxelPanel.addTab(p, mqTO.shortname);
        si.done();
      }
    });
  }
}

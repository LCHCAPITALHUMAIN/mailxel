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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import ch.heftix.mailxel.client.to.MessageQueryTO;

public class MessageQueryEditGrid extends VerticalPanel implements HasMailxelTab {

  private FlexTable grid = null;
  private TextAreaWChangeDetection taSql = new TextAreaWChangeDetection();
  private TextBoxWChangeDetection tbShortname = new TextBoxWChangeDetection();
  private TextBoxWChangeDetection tbName = new TextBoxWChangeDetection();
  private MailxelTab mailxelTab = null;

  public MessageQueryEditGrid(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel,
      final MessageQueryTO mqTO) {

    HorizontalPanel toolbar = new HorizontalPanel();
    Image save = new Image("img/save.png");
    save.setTitle("Save");
    save.setStylePrimaryName("mailxel-toolbar-item");
    save.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final StatusItem si = mailxelPanel.statusStart("message query save");

        if (tbName.isDirty()) {
          mqTO.nameDirty = true;
          mqTO.name = tbName.getTextTrimmed();
        }
        if (tbShortname.isDirty()) {
          mqTO.shortNameDirty = true;
          mqTO.shortname = tbShortname.getTextTrimmed();
        }
        if (taSql.isDirty()) {
          mqTO.sqlDirty = true;
          mqTO.sql = taSql.getTextTrimmed();
        }

        mailxelService.updateMessageQuery(mqTO, new AsyncCallback<MessageQueryTO>() {

          public void onFailure(Throwable caught) {
            si.error(caught);
          }

          public void onSuccess(MessageQueryTO result) {
            si.done();
            if (null != mailxelTab) {
              mailxelTab.setTabText(result.name);
            }
          }
        });
      }
    });

    toolbar.add(save);
    add(toolbar);

    grid = new FlexTable();

    tbName.setText(mqTO.name);
    tbShortname.setText(mqTO.shortname);
    taSql.setText(mqTO.sql);

    grid.setText(0, 0, "Id");
    grid.setText(0, 1, Integer.toString(mqTO.id));
    grid.setText(1, 0, "Access Count");
    grid.setText(1, 1, Integer.toString(mqTO.count));
    grid.setText(2, 0, "Shortname");
    grid.setWidget(2, 1, tbShortname);
    grid.setText(3, 0, "Name");
    grid.setWidget(3, 1, tbName);

    add(grid);
    taSql.setCharacterWidth(80);
    taSql.setVisibleLines(25);

    taSql.setText(mqTO.sql);
    add(taSql);
  }

  /**
   * {@inheritDoc}
   */
  public void setMailxelTab(final MailxelTab mailxelTab) {
    this.mailxelTab = mailxelTab;
  }
}

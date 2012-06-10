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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

import ch.heftix.mailxel.client.to.IconTO;

public class IconOverviewGrid extends VerticalPanel {

  private List<IconTO> icons = new ArrayList<IconTO>();

  public static final int LABEL_ROW = 0;
  public static final int HEADER_ROW_1 = 1;
  public static final int HEADER_ROW_2 = 2;
  public static final int FIRST_PAYLOAD_ROW = 3;

  final FlexTable grid = new FlexTable();

  public IconOverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    HorizontalPanel toolbar = new HorizontalPanel();

    // Image addQuery = new Image("img/plus.png");
    // addQuery.setTitle("New Query");
    // addQuery.setStylePrimaryName("mailxel-toolbar-item");
    // addQuery.addClickHandler(new ClickHandler() {
    //
    // public void onClick(ClickEvent sender) {
    // MessageQueryTO mqTO = new MessageQueryTO();
    // mqTO.id = MessageQueryTO.NEW_ID;
    // mqTO.name = "new query";
    // queries.add(mqTO);
    // updateGrid(queries);
    // MessageQueryEditGrid dg = new MessageQueryEditGrid(mailxelService,
    // mailxelPanel, mqTO);
    // mailxelPanel.addTab(dg, "new message query");
    // }
    // });
    // // toolbar.add(addQuery);

    add(toolbar);

    final TextBox query = new TextBox();
    query.setWidth("400px");

    // header
    grid.setText(LABEL_ROW, 0, "Id");
    grid.setText(LABEL_ROW, 1, "Icon");
    grid.setText(LABEL_ROW, 2, "Name");


    final MailxelAsyncCallback<List<IconTO>> callback = new MailxelAsyncCallback<List<IconTO>>() {

      private StatusItem si = null;

      public void setStatusItem(StatusItem statusItem) {
        this.si = statusItem;
      }

      public void onSuccess(List<IconTO> result) {

        icons = result;
        int row = updateGrid(result);
        si.done("found " + Integer.toString(row - FIRST_PAYLOAD_ROW) + " icons.", 0);
      }

      public void onFailure(Throwable caught) {
        si.error(caught);
      }
    };


    final KeyPressHandler kbla = new KeyPressHandler() {

      public void onKeyPress(KeyPressEvent event) {
        if (KeyCodes.KEY_ENTER == event.getCharCode()) {
          String snTxt = UIUtil.trimNull(query.getText());

          StatusItem si = mailxelPanel.statusStart("icon search");
          callback.setStatusItem(si);
          mailxelService.searchIcons(snTxt, callback);

        }
      }
    };

    query.addKeyPressHandler(kbla);

    grid.setWidget(HEADER_ROW_1, 2, query);

    grid.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent clickEvent) {
        Cell cell = grid.getCellForEvent(clickEvent);
        final int row = cell.getRowIndex();
        // row >= FIRST_PAYLOAD_ROW: skip header
        if (row >= FIRST_PAYLOAD_ROW) {
          IconTO icon = icons.get(row - FIRST_PAYLOAD_ROW);
        }
      }

    });


    add(grid);
  }

  private int updateGrid(final List<IconTO> icons) {
    // clean all except header
    int rows = grid.getRowCount();
    for (int i = rows - 1; i >= FIRST_PAYLOAD_ROW; i--) {
      grid.removeRow(i);
    }

    int row = FIRST_PAYLOAD_ROW;
    for (IconTO iconTO : icons) {
      grid.setText(row, 0, Integer.toString(iconTO.id));
      Image icon = new Image();
      String url = GWT.getModuleBaseURL() + "mailxel?img=" + Integer.toString(iconTO.id);
      icon.setUrl(url);
      grid.setWidget(row, 1, icon);
      grid.setText(row, 2, iconTO.name);
      row++;
    }

    return row;
  }
}

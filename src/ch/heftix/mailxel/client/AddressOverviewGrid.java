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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;

import ch.heftix.mailxel.client.to.AddressTO;

public class AddressOverviewGrid extends VerticalPanel {

  private List<AddressTO> addresses = new ArrayList<AddressTO>();

  public static final int LABEL_ROW = 0;
  public static final int HEADER_ROW_1 = 1;
  public static final int HEADER_ROW_2 = 2;
  public static final int FIRST_PAYLOAD_ROW = 3;

  final FlexTable grid = new FlexTable();

  private StatusItem statusItem = null;

  // private Label statusMessage = new Label();

  public AddressOverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    HorizontalPanel toolbar = new HorizontalPanel();
    Image save = new Image("img/save.png");
    save.setTitle("Save");
    save.setStylePrimaryName("mailxel-toolbar-item");
    save.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final StatusItem si = mailxelPanel.statusStart("address update");

        List<AddressTO> addressesToSave = new ArrayList<AddressTO>();

        int len = grid.getRowCount();

        for (int i = FIRST_PAYLOAD_ROW; i < len; i++) {

          TextBoxWChangeDetection tbS = (TextBoxWChangeDetection) grid.getWidget(i, 0);
          TextBoxWChangeDetection tbN = (TextBoxWChangeDetection) grid.getWidget(i, 1);
          TextBoxWChangeDetection tbA = (TextBoxWChangeDetection) grid.getWidget(i, 2);
          CheckBoxWChangeDetection cbV = (CheckBoxWChangeDetection) grid.getWidget(i, 3);
          CheckBoxWChangeDetection cbP = (CheckBoxWChangeDetection) grid.getWidget(i, 4);

          if (null != tbS
              && null != tbA
              && null != cbV
              && null != cbP
              && (tbS.isDirty() || tbN.isDirty() || tbA.isDirty() || cbV.isDirty() || cbP.isDirty())) {
            // add to save list

            AddressTO to = new AddressTO();

            to.shortname = tbS.getText();
            if( tbS.isDirty()) {
              to.shortNameDirty = true;
            }

            to.name = tbN.getText();
            if( tbN.isDirty()) {
              to.nameDirty = true;
            }

            to.address = tbA.getText();

            to.isValid = cbV.getValue();
            if( cbV.isDirty()) {
              to.validDirty = true;
            }

            to.isPreferred = cbP.getValue();
            if( cbP.isDirty()) {
              to.preferredDirty = true;
            }

            addressesToSave.add(to);
          }
        }

        mailxelService.saveAddresses(addressesToSave, new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
            si.error(caught);

          }

          public void onSuccess(Void result) {
            si.done();
          }
        });
      }
    });

    toolbar.add(save);
    // toolbar.add(statusMessage);
    add(toolbar);

    final TextBox shortname = new TextBox();
    shortname.setWidth("50px");

    final TextBox address = new TextBox();
    address.setWidth("300px");


    // header
    grid.setText(LABEL_ROW, 0, "Shortname");
    grid.setText(LABEL_ROW, 1, "Name");
    grid.setText(LABEL_ROW, 2, "Address");
    grid.setText(LABEL_ROW, 3, "Valid");
    grid.setText(LABEL_ROW, 4, "Pref.");
    grid.setText(LABEL_ROW, 5, "Last Seen");
    grid.setText(LABEL_ROW, 6, "# Seen");

    ColumnFormatter fmt = grid.getColumnFormatter();
    fmt.setWidth(0, "50px");
    fmt.setWidth(1, "100px");
    fmt.setWidth(2, "300px");
    fmt.setWidth(3, "50px");
    fmt.setWidth(4, "50px");
    fmt.setWidth(5, "100px");
    fmt.setWidth(6, "100px");

    final AsyncCallback<List<AddressTO>> callback = new AsyncCallback<List<AddressTO>>() {

      public void onSuccess(List<AddressTO> result) {

        addresses = result;

        // clean all except header
        int rows = grid.getRowCount();
        for (int i = rows - 1; i >= FIRST_PAYLOAD_ROW; i--) {
          grid.removeRow(i);
        }

        int row = FIRST_PAYLOAD_ROW;
        for (AddressTO address : result) {
          TextBoxWChangeDetection snTB = new TextBoxWChangeDetection();
          snTB.setText(address.shortname);
          snTB.setWidth("50px");
          grid.setWidget(row, 0, snTB);
          // grid.setText(row, 1, address.address);

          TextBoxWChangeDetection nTB = new TextBoxWChangeDetection();
          if (null != address.name) {
            nTB.setText(address.name);
          }
          nTB.setWidth("100px");
          grid.setWidget(row, 1, nTB);

          TextBoxWChangeDetection aTB = new TextBoxWChangeDetection();
          aTB.setText(address.address);
          aTB.setWidth("300px");
          grid.setWidget(row, 2, aTB);

          CheckBoxWChangeDetection cbValid = new CheckBoxWChangeDetection();
          cbValid.setValue(address.isValid);
          grid.setWidget(row, 3, cbValid);

          CheckBoxWChangeDetection cbPref = new CheckBoxWChangeDetection();
          cbPref.setValue(address.isPreferred);
          grid.setWidget(row, 4, cbPref);

          grid.setText(row, 5, address.lastSeen);
          grid.setText(row, 6, Integer.toString(address.count));

          row++;
        }

        if (null != statusItem) {
          statusItem.done("found " + Integer.toString(row - FIRST_PAYLOAD_ROW) + " addresses.", 0);
          statusItem = null;
        }
      }

      public void onFailure(Throwable caught) {
        statusItem.error(caught);
      }
    };

    final KeyPressHandler kbla = new KeyPressHandler() {

      public void onKeyPress(KeyPressEvent event) {
        if (KeyCodes.KEY_ENTER == event.getCharCode()) {
          String snTxt = UIUtil.trimNull(shortname.getText());
          String aTxt = UIUtil.trimNull(address.getText());

          statusItem = mailxelPanel.statusStart("address search");

          mailxelService.searchAddresses(snTxt, aTxt, callback);
        }
      }
    };

    // final KeyboardListenerAdapter kbla = new KeyboardListenerAdapter() {
    //
    // public void onKeyPress(Widget sender, char keyCode, int modifiers) {
    // if (KEY_ENTER == keyCode) {
    // String snTxt = UIUtil.trimNull(shortname.getText());
    // String aTxt = UIUtil.trimNull(address.getText());
    //
    // statusItem = mailxelPanel.statusStart("address search started");
    //
    // mailxelService.searchAddresses(snTxt, aTxt, callback);
    // }
    // }
    // };

    shortname.addKeyPressHandler(kbla);
    address.addKeyPressHandler(kbla);

    grid.setWidget(HEADER_ROW_1, 0, shortname);
    grid.setWidget(HEADER_ROW_1, 2, address);

    add(grid);
  }
}

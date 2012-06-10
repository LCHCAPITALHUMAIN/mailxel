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

import ch.heftix.mailxel.client.to.Category;
import ch.heftix.mailxel.client.to.Constants;

public class CategoryOverviewGrid extends VerticalPanel {

  private List<Category> categories = new ArrayList<Category>();

  public static final int LABEL_ROW = 0;
  public static final int HEADER_ROW_1 = 1;
  public static final int HEADER_ROW_2 = 2;
  public static final int FIRST_PAYLOAD_ROW = 3;

  final FlexTable grid = new FlexTable();

  public CategoryOverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    HorizontalPanel toolbar = new HorizontalPanel();

    Image save = new Image("img/save.png");
    save.setTitle("Save");
    save.setStylePrimaryName("mailxel-toolbar-item");
    save.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final StatusItem si = mailxelPanel.statusStart("category update");

        List<Category> categoriesToSave = new ArrayList<Category>();

        int len = grid.getRowCount();
        for (int i = FIRST_PAYLOAD_ROW; i < len; i++) {
          TextBoxWChangeDetection tbS = (TextBoxWChangeDetection) grid.getWidget(i, 0);
          if (null != tbS && tbS.isDirty()) {
            // add to save list
            Category existing = categories.get(i - FIRST_PAYLOAD_ROW);
            existing.name = tbS.getText();
            categoriesToSave.add(existing);
          }
        }

        mailxelService.saveCategories(categoriesToSave, new AsyncCallback<Void>() {

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

    Image addCategory = new Image("img/plus.png");
    addCategory.setTitle("New Category");
    addCategory.setStylePrimaryName("mailxel-toolbar-item");
    addCategory.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        Category cat = new Category();
        cat.id = Constants.UNDEFINED_ID;
        cat.name = "new category";
        categories.add(cat);
        updateGrid(categories);
      }
    });
    toolbar.add(addCategory);


    add(toolbar);

    final TextBox category = new TextBox();
    category.setWidth("400px");

    // header
    grid.setText(LABEL_ROW, 0, "Category");


    final MailxelAsyncCallback<List<Category>> callback = new MailxelAsyncCallback<List<Category>>() {

      private StatusItem si = null;

      public void setStatusItem(StatusItem statusItem) {
        this.si = statusItem;
      }

      public void onSuccess(List<Category> result) {

        categories = result;
        int row = updateGrid(result);
        si.done("found " + Integer.toString(row - FIRST_PAYLOAD_ROW) + " categories.", 0);
      }

      public void onFailure(Throwable caught) {
        si.error(caught);
      }
    };

    // final KeyboardListenerAdapter kbla = new KeyboardListenerAdapter() {
    //
    // public void onKeyPress(Widget sender, char keyCode, int modifiers) {
    // if (KEY_ENTER == keyCode) {
    // String snTxt = UIUtil.trimNull(category.getText());
    //
    // mailxelPanel.setStatus("category search started");
    //
    // mailxelService.searchCategories(snTxt, callback);
    // }
    // }
    // };

    final KeyPressHandler kbla = new KeyPressHandler() {

      public void onKeyPress(KeyPressEvent event) {
        if (KeyCodes.KEY_ENTER == event.getCharCode()) {
          String snTxt = UIUtil.trimNull(category.getText());

          StatusItem si = mailxelPanel.statusStart("category search");
          callback.setStatusItem(si);
          mailxelService.searchCategories(snTxt, callback);

        }
      }
    };

    category.addKeyPressHandler(kbla);

    grid.setWidget(HEADER_ROW_1, 0, category);

    add(grid);
  }

  private int updateGrid(final List<Category> categories) {
    // clean all except header
    int rows = grid.getRowCount();
    for (int i = rows - 1; i >= FIRST_PAYLOAD_ROW; i--) {
      grid.removeRow(i);
    }

    int row = FIRST_PAYLOAD_ROW;
    for (Category category : categories) {
      TextBoxWChangeDetection aTB = new TextBoxWChangeDetection();
      aTB.setText(category.name);
      aTB.setWidth("400px");
      grid.setWidget(row, 0, aTB);
      row++;
    }

    return row;
  }
}

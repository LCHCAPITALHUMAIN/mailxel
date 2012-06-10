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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DeletableItem<T> extends HorizontalPanel {

  private T payload = null;

  public DeletableItem(final Widget widget,
      final T payload,
      final ItemDeletionHandler<T> deletionHandler) {
    this.payload = payload;
    add(widget);
    Image icon = new Image("img/close.png");
    icon.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        deletionHandler.onDeletion(DeletableItem.this);
      }
    });
    add(icon);
  }

  public T getPayload() {
    return payload;
  }

}

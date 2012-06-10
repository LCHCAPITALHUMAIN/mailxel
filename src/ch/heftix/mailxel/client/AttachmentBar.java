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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import ch.heftix.mailxel.client.to.AttachmentTO;


public class AttachmentBar extends HorizontalPanel implements ItemDeletionHandler<AttachmentTO> {

  private MailServiceAsync mailxelService = null;
  private List<DeletableItem<AttachmentTO>> items = new ArrayList<DeletableItem<AttachmentTO>>();

  public AttachmentBar(final MailServiceAsync mailxelService) {
    this.mailxelService = mailxelService;
  }


  public void addAttachement(final String filename) {

    Image icon = createIcon(filename);
    add(icon);
  }

  public void addAttachement(final AttachmentTO aTO,
      final MailxelPanel mailxelPanel,
      final OnDemandTabPanel bodyPanel) {

    final Image icon = createIcon(aTO.name);
    icon.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        boolean directDownload = false;

        // attachment clicked; check for button
        MouseEvent me = (MouseEvent) sender;
        if (NativeEvent.BUTTON_RIGHT == me.getNativeButton()) {
          directDownload = true;
        }

        final AttachmentPanel aPanel = new AttachmentPanel(aTO, mailxelService, mailxelPanel,
            directDownload);
        if (null != bodyPanel) {
          bodyPanel.add(aPanel, UIUtil.shorten(aTO.name));
          // bodyPanel.selectTab(bodyPanel.getWidgetCount() - 1);
        } else {
          final PopupWindow pWin = new PopupWindow(aTO.name, aPanel, mailxelPanel);
          int x = getAbsoluteLeft();
          int y = getAbsoluteTop();
          pWin.setPopupPosition(x, y);
          pWin.show();
        }
      }
    });

    DeletableItem<AttachmentTO> di = new DeletableItem<AttachmentTO>(icon, aTO, this);
    items.add(di);

    add(icon);
  }

  public void addCheckableAttachement(final AttachmentTO aTO) {

    Image icon = createIcon(aTO.name);
    DeletableItem<AttachmentTO> di = new DeletableItem<AttachmentTO>(icon, aTO, this);
    add(di);
    items.add(di);
  }


  public List<AttachmentTO> getCheckedAttachments() {

    List<AttachmentTO> res = new ArrayList<AttachmentTO>();
    for (DeletableItem<AttachmentTO> di : items) {
      res.add(di.getPayload());
    }
    return res;

  }

  public void onDeletion(DeletableItem<AttachmentTO> item) {
    remove(item);
    items.remove(item);
  }

  public Image createIcon(final String filename) {

    Image icon = new Image("img/attach.png");
    icon.setStylePrimaryName("img/mailxel-toolbar-item");
    icon.setTitle(filename);

    MimeTypeDescriptor mtd = MimeTypeUtil.mimeTypeByName(filename);
    icon.setUrl(mtd.iconUrl);

    return icon;
  }


}

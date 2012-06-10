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
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import ch.heftix.mailxel.client.to.AddressTO;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.MailTO;

public class MailSendGrid extends VerticalPanel {

  public static final char TYPE_REPLY_ALL = 'A';
  public static final char TYPE_REPLY = 'R';
  public static final char TYPE_FORWARD = 'F';
  public static final char TYPE_NEW = 'N';
  public static final char TYPE_SELF = 'S';

  private FlexTable grid = new FlexTable();
  private TextArea bodyArea = new TextArea();
  private AddressEditBar fromBar = null;
  private AddressEditBar toBar = null;
  private AddressEditBar ccBar = null;
  private AddressEditBar bccBar = null;
  private TextBox subjectBox = null;
  private Label statusMessage = new Label();
  private AttachmentBar attachmentBar = null;

  private ConfigTO cTO = null;

  private MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;

  private boolean sending = false;

  public MailSendGrid(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel,
      final MailTO mailTO,
      final char type) {

    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;

    cTO = mailxelPanel.getConfig();

    HorizontalPanel toolbar = new HorizontalPanel();


    final Image addAttachement = new Image("img/add-attachment.png");
    addAttachement.setTitle("Add Attachement");
    addAttachement.setStylePrimaryName("mailxel-toolbar-item");
    addAttachement.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final PopupPanel pup = new PopupPanel(true);

        HorizontalPanel hp = new HorizontalPanel();

        final TextBox tb = new TextBox();
        tb.setWidth("300px");

        hp.add(tb);

        // final FileUpload upload = new FileUpload();
        // hp.add(upload);

        Button b = new Button();
        b.setText("Add");
        b.addClickHandler(new ClickHandler() {

          public void onClick(ClickEvent sender) {
            // String name = upload.getFilename();
            // // upload.g
            String name = tb.getText();
            if (null != name) {
              name = name.trim();
              if (name.length() > 0) {
                AttachmentTO aTO = new AttachmentTO();
                aTO.name = name;
                attachmentBar.addCheckableAttachement(aTO);
                pup.hide();
              }
            }
          }
        });

        hp.add(b);

        pup.add(hp);

        int x = addAttachement.getAbsoluteLeft();
        int y = addAttachement.getAbsoluteTop();

        pup.setPopupPosition(x, y);
        pup.show();
      }
    });

    final Image setFrom = new Image("img/set-from.png");
    setFrom.setTitle("Set From address");
    setFrom.setStylePrimaryName("mailxel-toolbar-item");
    setFrom.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        grid.insertRow(0);
        grid.setText(0, 0, "From");
        MailSendGrid.this.fromBar = new AddressEditBar(mailxelService, mailxelPanel);
        grid.setWidget(0, 1, fromBar);
      }
    });

    Image postpone = new Image("img/save.png");
    postpone.setTitle("Postpone Message");
    postpone.setStylePrimaryName("mailxel-toolbar-item");
    postpone.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {

        final StatusItem si = mailxelPanel.statusStart("postpone");

        MailTO mt = createMail();

        mailxelService.postpone(mt, new AsyncCallback<String>() {

          public void onFailure(Throwable caught) {
            si.error(caught);
          }

          public void onSuccess(String result) {
            if (null != result && result.startsWith("200 OK")) {
              si.done();
              mailxelPanel.closeTab(MailSendGrid.this);
            } else {
              si.error("Postpone failed: " + result);
            }
          }
        });
      }
    });


    Image send = new Image("img/paper-plane.png");
    if (TYPE_SELF == type) {
      send.setTitle("Store Note");
    } else {
      send.setTitle("Send Mail");
    }
    send.setStylePrimaryName("mailxel-toolbar-item");
    send.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final StatusItem si = mailxelPanel.statusStart("send/store");
        if (sending) {
          si.error("mail send is already in process");
          return;
        } else {
          sending = true;
        }

        MailTO mt = createMail();

        if (TYPE_SELF == type) {
          mailxelService.storeNote(mt, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
              si.error(caught);
              sending = false;
            }

            public void onSuccess(String result) {
              if (null != result && result.startsWith("200 OK")) {
                si.done();
                sending = false;
                mailxelPanel.closeTab(MailSendGrid.this);
              } else {
                si.error("Storing note failed: " + result);
                sending = false;
              }
            }

          });

        } else {

          mailxelService.send(mt, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
              si.error(caught);
              sending = false;
            }

            public void onSuccess(String status) {
              if (status.startsWith("200")) {
                si.done();
                sending = false;
                if (TYPE_NEW != type) {
                  // add a category entry: answered or forwarded
                  String cat = "ANS";
                  if (TYPE_FORWARD == type) {
                    cat = "FWD";
                  }
                  List<Integer> origMailId = new ArrayList<Integer>();
                  origMailId.add(mailTO.id);
                  final StatusItem sic = mailxelPanel.statusStart("update send/answer flags");
                  mailxelService.updateCategories(origMailId, cat, new AsyncCallback<Void>() {

                    public void onFailure(Throwable caught) {
                      sic.error(caught);
                    }

                    public void onSuccess(Void result) {
                      sic.done("sent and flags updated", 0);
                    }
                  });
                }
                // close tab
                mailxelPanel.closeTab(MailSendGrid.this);
              } else {
                si.error(status);
                sending = false;
              }
            }
          });

        }
      }
    });

    if (TYPE_SELF != type) {
      toolbar.add(setFrom);
      toolbar.add(postpone);
    }
    toolbar.add(addAttachement);
    toolbar.add(send);

    add(toolbar);
    add(statusMessage);

    initWidgets(mailTO, type);

  }

  private void initWidgets(final MailTO mailTO, final char type) {

    int rowCurs = 0;

    toBar = new AddressEditBar(mailxelService, mailxelPanel);

    attachmentBar = new AttachmentBar(mailxelService);

    subjectBox = new TextBox();
    subjectBox.setWidth("400px");


    ccBar = new AddressEditBar(mailxelService, mailxelPanel);

    if (TYPE_REPLY_ALL == type) {
      toBar.addAddress(mailTO.fromATO);
      ccBar.addAddresses(mailTO.toATOs);
      ccBar.addAddresses(mailTO.ccATOs);
    } else if (TYPE_REPLY == type) {
      toBar.addAddress(mailTO.fromATO);
    }

    bccBar = new AddressEditBar(mailxelService, mailxelPanel);

    if (TYPE_SELF != type) {

      grid.setText(rowCurs, 0, "To");
      grid.setWidget(rowCurs, 1, toBar);
      rowCurs++;

      grid.setText(rowCurs, 0, "Cc");
      grid.setWidget(rowCurs, 1, ccBar);
      rowCurs++;

      grid.setText(rowCurs, 0, "Bcc");
      grid.setWidget(rowCurs, 1, bccBar);
      rowCurs++;
    }

    grid.setText(rowCurs, 0, "Subject");
    grid.setWidget(rowCurs, 1, subjectBox);
    if (TYPE_FORWARD == type) {
      subjectBox.setText("Fwd: " + mailTO.subject);
      if (null != mailTO.attachments) {
        for (AttachmentTO aTO : mailTO.attachments) {
          attachmentBar.addCheckableAttachement(aTO);
        }
      }
    } else if (TYPE_REPLY == type || TYPE_REPLY_ALL == type) {
      subjectBox.setText("Re: " + mailTO.subject);
    }
    rowCurs++;

    grid.setWidget(rowCurs, 1, attachmentBar);

    add(grid);

    bodyArea = new OrgTextArea();
    bodyArea.setCharacterWidth(80);
    bodyArea.setVisibleLines(25);

    add(bodyArea);

    String txt = "--- forwarded message: ---\n";
    if (TYPE_FORWARD == type) {
      txt = txt + "From: "
          + AddressTOUtil.getDisplayText(mailTO.fromATO, cTO.replyAddressDisplayType) + "\n";
    } else if (TYPE_REPLY == type || TYPE_REPLY_ALL == type) {
      txt = "--- " + AddressTOUtil.getDisplayText(mailTO.fromATO, cTO.replyAddressDisplayType)
          + " wrote: ---\n";
    } else {
      // new mail
      txt = "";
    }

    if (type != TYPE_SELF && type != TYPE_NEW) {

      txt = txt + "To:   "
          + MailTOUtil.getToAddressesDisplayText(mailTO, cTO.replyAddressDisplayType) + "\n";
      txt = txt + "Date: " + mailTO.date + "\n";
      if (null != mailTO.cc) {
        txt = txt + "Cc:   "
            + MailTOUtil.getCcAddressesDisplayText(mailTO, cTO.replyAddressDisplayType) + "\n";
      }
      txt = txt + mailTO.body;
    }

    bodyArea.setText(txt);
  }

  private MailTO createMail() {

    MailTO mt = new MailTO();
    mt.body = bodyArea.getText();
    mt.subject = subjectBox.getText();

    if (null != MailSendGrid.this.fromBar) {
      mt.from = MailSendGrid.this.fromBar.getAddresses();
    }

    mt.to = toBar.getAddresses();
    mt.cc = ccBar.getAddresses();
    mt.bcc = bccBar.getAddresses();
    mt.attachments = attachmentBar.getCheckedAttachments();

    return mt;
  }

  public void setMailDetail(final int id) {

    mailxelService.get(id, new AsyncCallback<MailTO>() {

      final StatusItem si = mailxelPanel.statusStart("getting mail detail");

      public void onSuccess(MailTO result) {

        subjectBox.setText(result.subject);
        bodyArea.setText(result.body);
        for (AddressTO aTO : result.toATOs) {
          toBar.addAddress(aTO);
        }
        for (AddressTO aTO : result.ccATOs) {
          ccBar.addAddress(aTO);
        }
        for (AddressTO aTO : result.bccATOs) {
          bccBar.addAddress(aTO);
        }

        si.done();
      }

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

    });
  }


}

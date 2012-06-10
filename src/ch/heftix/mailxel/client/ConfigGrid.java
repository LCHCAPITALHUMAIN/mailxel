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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

import ch.heftix.mailxel.client.to.AccountConfigTO;
import ch.heftix.mailxel.client.to.ConfigTO;


public class ConfigGrid extends VerticalPanel {

  private FlexTable grid = new FlexTable();

  private Label status = new Label();

  public static int ILOCALSTORE = 0;
  /** me (my own mail address) */
  public static int IME = 1;
  /** index to field where max search rows are stored */
  public static int IMAXSEARCHROWS = 2;
  /** index to field where max address suggest rows are stored */
  public static int IMAXADDRESSSUGGESTROWS = 3;

  /** indices (to widget arrays) */
  public static int SH_BCC = 4;
  public static int SH_COPY_LOCAL = 5;
  public static int SH_COPY_SENT = 6;
  public static int SH_COPY_SENT_ACCOUNT = 7;
  public static int SH_COPY_SENT_FOLDER = 8;
  public static int SH_DISPLAY_TIME = 9;
  public static int SH_MAX_MAIL_DOWNLOAD_PER_FOLDER = 10;
  public static int SH_EXPLICIT_WILDCARDS = 11;
  public static int SH_MAILDOWNLOAD_DELAY = 12;

  public static int INAME = 0;
  public static int IHOST = 1;
  public static int IPORT = 2;
  public static int IPROTOCOL = 3;
  public static int ISSL = 4;
  public static int IFOLDERS = 5;
  public static int IUSER = 6;
  public static int IEXCLUDE = 7;
  public static int IREUSESENDPW = 8;
  public static int IREORGRULE = 9;

  // public static int ILOGCONFIGURATION = 9;
  public static int IDBTIMECONFIGURATION = 10;
  // public static int IADDRESSDISPLAYTYPE = 11;

  public static int INUMROWS = 13;
  public static int IMINCOLS = 4;

  ListBox lb = new ListBox();
  ListBoxUtil<Integer> lbu = new ListBoxUtil<Integer>();


  ListBox lBAddressDisplay = new ListBox();
  ListBoxUtil<Integer> lBUAddressDisplay = new ListBoxUtil<Integer>();

  /**
   * create (more than needed) text boxes to fill the matrix with 10 rows and 1
   * + accounts cols
   */
  private TextBox[][] textBoxes = null;
  private CheckBox[][] checkBoxes = null;

  private MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;


  public ConfigGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;

    lbu.add("Production", LOGConstants.PRODUCTION);
    lbu.add("Test", LOGConstants.TEST);
    lbu.add("Development", LOGConstants.DEV);

    lBUAddressDisplay.add("Shortname", AddressTOUtil.DISPLAY_SHORTNAME);
    lBUAddressDisplay.add("Name", AddressTOUtil.DISPLAY_NAME);
    lBUAddressDisplay.add("Address", AddressTOUtil.DISPLAY_ADDRESS);

    // save
    HorizontalPanel toolbar = new HorizontalPanel();
    Image save = new Image("img/save.png");
    save.setTitle("Save");
    save.setStylePrimaryName("mailxel-toolbar-item");
    save.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        final ConfigTO cTO = new ConfigTO();

        cTO.localstore = textBoxes[1][ILOCALSTORE].getText();
        cTO.me = textBoxes[1][IME].getText();
        cTO.replyAddressDisplayType = lBUAddressDisplay.getSelectedValue().intValue();
        cTO.sentHandlingBCC = checkBoxes[1][SH_BCC].getValue();
        cTO.sentHandlingStoreLocally = checkBoxes[1][SH_COPY_LOCAL].getValue();
        cTO.sentHandlingCopySent = checkBoxes[1][SH_COPY_SENT].getValue();
        cTO.sentHandlingCopyAccountName = textBoxes[1][SH_COPY_SENT_ACCOUNT].getText();
        cTO.sentHandlingCopyFolderName = textBoxes[1][SH_COPY_SENT_FOLDER].getText();

        String tmp = textBoxes[1][IMAXSEARCHROWS].getText();
        cTO.maxSearchRows = ListUtil.parse(tmp, 50);

        tmp = textBoxes[1][IMAXADDRESSSUGGESTROWS].getText();
        cTO.maxAddressSuggestions = ListUtil.parse(tmp, 10);

        cTO.logConfiguration = lbu.getSelectedValue().intValue();
        cTO.replyAddressDisplayType = lBUAddressDisplay.getSelectedValue().intValue();

        cTO.displayTime = checkBoxes[1][SH_DISPLAY_TIME].getValue();

        cTO.explcitWildcards = checkBoxes[1][SH_EXPLICIT_WILDCARDS].getValue();
        tmp = textBoxes[1][SH_MAX_MAIL_DOWNLOAD_PER_FOLDER].getText();
        cTO.maxMailDownloadsPerFolder = ListUtil.parse(tmp, 100);

        tmp = textBoxes[1][SH_MAILDOWNLOAD_DELAY].getText();
        cTO.downloadMailDelay = ListUtil.parse(tmp, 0);

        int col = 3;

        cTO.smtpHost = textBoxes[col][IHOST].getText();
        cTO.smtpPort = textBoxes[col][IPORT].getText();
        cTO.smtpUser = textBoxes[col][IUSER].getText();

        List<AccountConfigTO> accounts = new ArrayList<AccountConfigTO>();
        List<String> accountNames = new ArrayList<String>();

        int numAccounts = textBoxes.length - 4;

        for (int i = 0; i < numAccounts; i++) {

          col++;

          String accountName = textBoxes[col][INAME].getText();
          AccountConfigTO aTO = new AccountConfigTO();
          aTO.name = accountName;
          aTO.server = textBoxes[col][IHOST].getText();
          aTO.protocol = textBoxes[col][IPROTOCOL].getText();
          aTO.isSSL = checkBoxes[col][ISSL].getValue();
          aTO.port = textBoxes[col][IPORT].getText();
          aTO.scannedfolders = ListUtil.getFromComaSeparated(textBoxes[col][IFOLDERS].getText());
          aTO.excludedfolders = ListUtil.getFromComaSeparated(textBoxes[col][IEXCLUDE].getText());
          aTO.user = textBoxes[col][IUSER].getText();
          aTO.reorgRules = ListUtil.getFromComaSeparated(textBoxes[col][IREORGRULE].getText());

          accountNames.add(accountName);
          accounts.add(aTO);
        }

        String[] aNames = new String[accountNames.size()];
        accountNames.toArray(aNames);

        cTO.accountNames = aNames;

        AccountConfigTO[] aTOs = new AccountConfigTO[accounts.size()];
        accounts.toArray(aTOs);

        cTO.accounts = aTOs;

        final StatusItem si = mailxelPanel.statusStart("saving configuration");

        mailxelService.saveConfig(cTO, new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
            si.error(caught);
          }

          public void onSuccess(Void result) {
            si.done();
            mailxelPanel.setConfig(cTO);
            // mailxelPanel.closeTab(ConfigGrid.this);
          }
        });

      }
    });

    Image addAccount = new Image("img/server_add.png");
    addAccount.setTitle("New Account");
    addAccount.setStylePrimaryName("mailxel-toolbar-item");
    addAccount.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        grid.clear();
        ConfigTO cTO = mailxelPanel.getConfig();
        addNewAccount(cTO);
        fillGrid(cTO);
      }
    });

    ColumnFormatter cf = grid.getColumnFormatter();
    cf.setStylePrimaryName(0, "col-bg");

    RowFormatter rf = grid.getRowFormatter();
    for (int i = 1; i < INUMROWS; i = i + 2) {
      rf.setStylePrimaryName(i, "row-bg");
    }

    toolbar.add(save);
    toolbar.add(addAccount);
    toolbar.add(status);

    add(toolbar);

    add(grid);

    ConfigTO cTO = mailxelPanel.getConfig();
    fillGrid(cTO);

  }

  private void fillGrid(final ConfigTO cTO) {

    if (null == cTO) {
      status.setText("no config found");
      return;
    }

    int numCols = IMINCOLS + cTO.accountNames.length;

    textBoxes = new TextBox[numCols][INUMROWS];
    checkBoxes = new CheckBox[numCols][INUMROWS];

    // --- general data ---

    grid.setText(1, 0, "General");

    grid.setText(2, 0, "DB Location");
    textBoxes[1][ILOCALSTORE] = new TextBox();
    grid.setWidget(2, 1, textBoxes[1][ILOCALSTORE]);

    grid.setText(3, 0, "my e-mail address");
    textBoxes[1][IME] = new TextBox();
    grid.setWidget(3, 1, textBoxes[1][IME]);

    grid.setText(4, 0, "max search results");
    textBoxes[1][IMAXSEARCHROWS] = new TextBox();
    grid.setWidget(4, 1, textBoxes[1][IMAXSEARCHROWS]);

    grid.setText(5, 0, "max address suggestions");
    textBoxes[1][IMAXADDRESSSUGGESTROWS] = new TextBox();
    grid.setWidget(5, 1, textBoxes[1][IMAXADDRESSSUGGESTROWS]);

    grid.setText(6, 0, "BCC to myself");
    checkBoxes[1][SH_BCC] = new CheckBox();
    grid.setWidget(6, 1, checkBoxes[1][SH_BCC]);

    grid.setText(7, 0, "store sent locally");
    checkBoxes[1][SH_COPY_LOCAL] = new CheckBox();
    grid.setWidget(7, 1, checkBoxes[1][SH_COPY_LOCAL]);

    grid.setText(8, 0, "copy sent to server");
    checkBoxes[1][SH_COPY_SENT] = new CheckBox();
    grid.setWidget(8, 1, checkBoxes[1][SH_COPY_SENT]);

    checkBoxes[1][SH_COPY_SENT].addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        if (checkBoxes[1][SH_COPY_SENT].getValue()) {
          textBoxes[1][SH_COPY_SENT_ACCOUNT].setVisible(true);
          textBoxes[1][SH_COPY_SENT_FOLDER].setVisible(true);
        } else {
          textBoxes[1][SH_COPY_SENT_ACCOUNT].setVisible(false);
          textBoxes[1][SH_COPY_SENT_FOLDER].setVisible(false);
        }
      }
    });

    grid.setText(9, 0, "account name");
    textBoxes[1][SH_COPY_SENT_ACCOUNT] = new TextBox();
    grid.setWidget(9, 1, textBoxes[1][SH_COPY_SENT_ACCOUNT]);

    grid.setText(10, 0, "folder name");
    textBoxes[1][SH_COPY_SENT_FOLDER] = new TextBox();
    grid.setWidget(10, 1, textBoxes[1][SH_COPY_SENT_FOLDER]);

    grid.setText(11, 0, "log config");
    lb = lbu.createListBox();
    lb.setVisibleItemCount(1);
    lb.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent event) {
        int logConfig = lbu.getSelectedValue().intValue();
        final StatusItem si = mailxelPanel.statusStart("changing log level");
        mailxelService.changeLogConfiguration(logConfig, new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
            si.error(caught);
          }

          public void onSuccess(Void result) {
            si.done();
          }
        });
      }
    });
    lbu.setSelectedValue(cTO.logConfiguration);
    grid.setWidget(11, 1, lb);

    grid.setText(12, 0, "write DB time log");
    checkBoxes[1][IDBTIMECONFIGURATION] = new CheckBox();
    grid.setWidget(12, 1, checkBoxes[1][IDBTIMECONFIGURATION]);

    checkBoxes[1][IDBTIMECONFIGURATION].addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        final AsyncCallback<Void> cb = new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
          }

          public void onSuccess(Void result) {
          }
        };
        if (checkBoxes[1][IDBTIMECONFIGURATION].getValue()) {
          mailxelService.setDBTiming(true, cb);
        } else {
          mailxelService.setDBTiming(false, cb);
        }
      }
    });

    grid.setText(13, 0, "address display");
    lBAddressDisplay = lBUAddressDisplay.createListBox();
    lBAddressDisplay.setVisibleItemCount(1);
    lBAddressDisplay.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent event) {
        int tmp = lBUAddressDisplay.getSelectedValue().intValue();
        // ConfigTO cTO = mailxelPanel.getConfig();
        cTO.replyAddressDisplayType = tmp;
      }
    });
    lBUAddressDisplay.setSelectedValue(cTO.replyAddressDisplayType);
    grid.setWidget(13, 1, lBAddressDisplay);

    grid.setText(14, 0, "display time");
    checkBoxes[1][SH_DISPLAY_TIME] = new CheckBox();
    grid.setWidget(14, 1, checkBoxes[1][SH_DISPLAY_TIME]);

    grid.setText(15, 0, "mail download limit");
    textBoxes[1][SH_MAX_MAIL_DOWNLOAD_PER_FOLDER] = new TextBox();
    grid.setWidget(15, 1, textBoxes[1][SH_MAX_MAIL_DOWNLOAD_PER_FOLDER]);

    grid.setText(16, 0, "explicit wildcards");
    checkBoxes[1][SH_EXPLICIT_WILDCARDS] = new CheckBox();
    grid.setWidget(16, 1, checkBoxes[1][SH_EXPLICIT_WILDCARDS]);
    
    grid.setText(17, 0, "download mail every minutes");
    textBoxes[1][SH_MAILDOWNLOAD_DELAY] = new TextBox();
    grid.setWidget(17, 1, textBoxes[1][SH_MAILDOWNLOAD_DELAY]);
    
    textBoxes[1][SH_MAILDOWNLOAD_DELAY].addChangeHandler(new ChangeHandler() {
      
      public void onChange(ChangeEvent event) {
        String tmp = textBoxes[1][SH_MAILDOWNLOAD_DELAY].getText();
        int delay = ListUtil.parse(tmp, 0);
        mailxelPanel.startDownloadTimer(delay);
      }
    });

    InfoLabel name = new InfoLabel("account_name", "Account Name", "a1", mailxelPanel);
    grid.setWidget(1, 2, name);
    grid.setText(2, 2, "User");
    grid.setText(3, 2, "Host/Server");
    grid.setText(4, 2, "Port");
    grid.setText(5, 2, "Protocol");
    grid.setText(6, 2, "use SSL");
    grid.setText(7, 2, "Scanned Folders");
    grid.setText(8, 2, "Excluded Folders");
    InfoLabel rr = new InfoLabel("reorganize", "reorg rules",
        "inbox: file-and-forget/%yyyy/%MM/%yyyy-MM-dd", mailxelPanel);
    grid.setWidget(10, 2, rr);

    // --- send account ---

    int col = 3;
    grid.setText(1, col, "Send");

    textBoxes[col][IUSER] = new TextBox();
    grid.setWidget(2, col, textBoxes[col][IUSER]);

    textBoxes[col][IHOST] = new TextBox();
    grid.setWidget(3, col, textBoxes[col][IHOST]);

    textBoxes[col][IPORT] = new TextBox();
    grid.setWidget(4, col, textBoxes[col][IPORT]);

    textBoxes[col][IPROTOCOL] = new TextBox();
    grid.setWidget(5, col, textBoxes[col][IPROTOCOL]);

    // --- user specific accounts ---

    for (int i = 0; i < cTO.accountNames.length; i++) {
      col++;

      textBoxes[col][INAME] = new TextBox();
      grid.setWidget(1, col, textBoxes[col][INAME]);

      textBoxes[col][IUSER] = new TextBox();
      grid.setWidget(2, col, textBoxes[col][IUSER]);

      textBoxes[col][IHOST] = new TextBox();
      grid.setWidget(3, col, textBoxes[col][IHOST]);

      textBoxes[col][IPORT] = new TextBox();
      grid.setWidget(4, col, textBoxes[col][IPORT]);

      textBoxes[col][IPROTOCOL] = new TextBox();
      grid.setWidget(5, col, textBoxes[col][IPROTOCOL]);

      // sslBoxes[i] = new CheckBox();
      checkBoxes[col][ISSL] = new CheckBox();
      grid.setWidget(6, col, checkBoxes[col][ISSL]);

      textBoxes[col][IFOLDERS] = new TextBox();
      grid.setWidget(7, col, textBoxes[col][IFOLDERS]);

      textBoxes[col][IEXCLUDE] = new TextBox();
      grid.setWidget(8, col, textBoxes[col][IEXCLUDE]);

      textBoxes[col][IREORGRULE] = new TextBox();
      grid.setWidget(10, col, textBoxes[col][IREORGRULE]);
    }

    // fill values
    textBoxes[1][ILOCALSTORE].setText(cTO.localstore);
    textBoxes[1][IME].setText(cTO.me);
    textBoxes[1][IMAXSEARCHROWS].setText(Integer.toString(cTO.maxSearchRows));
    textBoxes[1][IMAXADDRESSSUGGESTROWS].setText(Integer.toString(cTO.maxAddressSuggestions));

    checkBoxes[1][SH_DISPLAY_TIME].setValue(cTO.displayTime);
    textBoxes[1][SH_MAX_MAIL_DOWNLOAD_PER_FOLDER].setValue(Integer.toString(cTO.maxMailDownloadsPerFolder));
    checkBoxes[1][SH_EXPLICIT_WILDCARDS].setValue(cTO.explcitWildcards);

    checkBoxes[1][SH_BCC].setValue(cTO.sentHandlingBCC);
    checkBoxes[1][SH_COPY_LOCAL].setValue(cTO.sentHandlingStoreLocally);
    checkBoxes[1][SH_COPY_SENT].setValue(cTO.sentHandlingCopySent);
    textBoxes[1][SH_COPY_SENT_ACCOUNT].setText(cTO.sentHandlingCopyAccountName);
    textBoxes[1][SH_COPY_SENT_FOLDER].setText(cTO.sentHandlingCopyFolderName);
    
    textBoxes[1][SH_MAILDOWNLOAD_DELAY].setText(Integer.toString(cTO.downloadMailDelay));
    

    col = 3;
    textBoxes[col][IHOST].setText(cTO.smtpHost);
    textBoxes[col][IPORT].setText(cTO.smtpPort);
    textBoxes[col][IUSER].setText(cTO.smtpUser);

    for (int i = 0; i < cTO.accountNames.length; i++) {
      AccountConfigTO aTO = cTO.accounts[i];
      col++;
      textBoxes[col][INAME].setText(cTO.accountNames[i]);
      textBoxes[col][IHOST].setText(aTO.server);
      textBoxes[col][IPORT].setText(aTO.port);
      textBoxes[col][IPROTOCOL].setText(aTO.protocol);
      checkBoxes[col][ISSL].setValue(aTO.isSSL);
      textBoxes[col][IFOLDERS].setText(ListUtil.asComaSeparted(aTO.scannedfolders));
      textBoxes[col][IEXCLUDE].setText(ListUtil.asComaSeparted(aTO.excludedfolders));
      textBoxes[col][IUSER].setText(aTO.user);
      textBoxes[col][IREORGRULE].setText(ListUtil.asComaSeparted(aTO.reorgRules));
    }

    if (!checkBoxes[1][SH_COPY_SENT].getValue()) {
      textBoxes[1][SH_COPY_SENT_ACCOUNT].setVisible(false);
      textBoxes[1][SH_COPY_SENT_FOLDER].setVisible(false);
    }

  }

  private void addNewAccount(final ConfigTO cTO) {

    String[] oldNames = cTO.accountNames;
    String[] names = new String[oldNames.length + 1];
    AccountConfigTO[] accounts = new AccountConfigTO[oldNames.length + 1];
    for (int i = 0; i < oldNames.length; i++) {
      names[i] = oldNames[i];
      accounts[i] = cTO.accounts[i];
    }
    names[oldNames.length] = "new account";
    accounts[oldNames.length] = new AccountConfigTO();
    accounts[oldNames.length].name = names[oldNames.length];
    cTO.accountNames = names;
    cTO.accounts = accounts;
  }
}

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

import ch.heftix.mailxel.client.to.StatisticsTO;

public class StatisticsGrid extends VerticalPanel {

  final FlexTable grid = new FlexTable();

  private StatusItem statusItem = null;

  public StatisticsGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    statusItem = mailxelPanel.statusStart("calculate statistics");

    mailxelService.calculateStatistics(new AsyncCallback<StatisticsTO>() {

      public void onFailure(Throwable caught) {
        statusItem.error(caught);
      }

      public void onSuccess(StatisticsTO result) {
        statusItem.done();
        grid.setText(1, 0, "last day");
        grid.setText(2, 0, "last week");
        grid.setText(3, 0, "last month");
        grid.setText(4, 0, "last year");
        
        grid.setText(0, 1, "Sent");
        grid.setText(0, 2, "Same domain");
        grid.setText(0, 3, "Other domain");

        grid.setText(0, 4, "Recieved");
        grid.setText(0, 5, "Same domain");
        grid.setText(0, 6, "Other domain");

        grid.setText(0, 7, "Total");

        grid.setText(1, 1, Integer.toString(result.numSentLastDay));
        grid.setText(1, 4, Integer.toString(result.numRecievedLastDay));
        grid.setText(1, 7, Integer.toString(result.totalLastDay));

        grid.setText(2, 1, Integer.toString(result.numSentLastWeek));
        grid.setText(2, 2, Integer.toString(result.numSentLastWeekSameDomain));
        grid.setText(2, 3, Integer.toString(result.numSentLastWeekOtherDomain));
        grid.setText(2, 4, Integer.toString(result.numRecievedLastWeek));
        grid.setText(2, 5, Integer.toString(result.numRecievedLastWeekSameDomain));
        grid.setText(2, 6, Integer.toString(result.numRecievedLastWeekOtherDomain));
        grid.setText(2, 7, Integer.toString(result.totalLastWeek));

        grid.setText(3, 1, Integer.toString(result.numSentLastMonth));
        grid.setText(3, 4, Integer.toString(result.numRecievedLastMonth));
        grid.setText(3, 7, Integer.toString(result.totalLastMonth));
        
        grid.setText(4, 1, Integer.toString(result.numSentLastYear));
        grid.setText(4, 4, Integer.toString(result.numRecievedLastYear));
        grid.setText(4, 7, Integer.toString(result.totalLastYear));
      }
    });
    
    add(grid);

  }
}

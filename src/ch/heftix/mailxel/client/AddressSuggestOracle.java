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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

import ch.heftix.mailxel.client.to.AddressTO;

public class AddressSuggestOracle extends SuggestOracle {

  private MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;
  private String afterLastComma = null;
  private String beforeComma = null;

  public AddressSuggestOracle(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {
    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;
  }

  public void requestSuggestions(final Request request, final Callback callback) {

    String query = request.getQuery();
    if (null == query) {
      return;
    }

    if (query.length() < 2) {
      return;
    }

    afterLastComma = query.trim();
    beforeComma = "";

    int splitIndex = query.lastIndexOf(",");
    if (splitIndex > 0) {
      afterLastComma = query.substring(splitIndex + 1);
      if (null != afterLastComma) {
        afterLastComma = afterLastComma.trim();
      }
      beforeComma = query.substring(0, splitIndex);
      if (null != beforeComma) {
        beforeComma = beforeComma.trim();
      }
    }

    if (afterLastComma.length() < 2) {
      return;
    }

    final StatusItem si = mailxelPanel.statusStart("listing address matches for " + query);

    mailxelService.suggestAddress(afterLastComma, new AsyncCallback<List<AddressTO>>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

      public void onSuccess(List<AddressTO> result) {

        si.done();
        List<AddressSuggestion> suggestions = new ArrayList<AddressSuggestion>();
        for (AddressTO aTO : result) {
          AddressSuggestion tmp = new AddressSuggestion(aTO);
          suggestions.add(tmp);
        }

        Response resp = new Response(suggestions);

        callback.onSuggestionsReady(request, resp);

      }
    });

  }
}

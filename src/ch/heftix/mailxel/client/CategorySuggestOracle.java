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

import ch.heftix.mailxel.client.to.Category;

public class CategorySuggestOracle extends SuggestOracle {

  private MailServiceAsync mailxelService = null;
  private String afterLastComma = null;
  private String beforeComma = null;

  public CategorySuggestOracle(final MailServiceAsync mailxelService) {
    this.mailxelService = mailxelService;
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

    mailxelService.suggestCategory(afterLastComma, new AsyncCallback<List<Category>>() {

      public void onFailure(Throwable caught) {
        System.out.println("E - error on suggest callback:" + caught);
      }

      public void onSuccess(List<Category> result) {

        List<SimpleSuggestion> suggestions = new ArrayList<SimpleSuggestion>();
        for (Category category : result) {
          String replacement = category.name;
          if (null != beforeComma && beforeComma.length() > 0) {
            replacement = beforeComma + ", " + category.name;
          }
          SimpleSuggestion tmp = new SimpleSuggestion(category.name, replacement);
          suggestions.add(tmp);
        }

        Response resp = new Response(suggestions);

        callback.onSuggestionsReady(request, resp);

      }
    });

  }
}

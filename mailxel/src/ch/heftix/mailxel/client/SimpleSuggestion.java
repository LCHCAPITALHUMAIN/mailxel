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

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SimpleSuggestion implements Suggestion {

  private String display = null;
  private String replacement = null;

  public SimpleSuggestion(final String display, final String replacement) {
    this.display = display;
    this.replacement = replacement;
  }

  public SimpleSuggestion(final String replacement) {
    this.display = replacement;
    this.replacement = replacement;
  }

  public String getDisplayString() {
    return display;
  }

  public String getReplacementString() {
    return replacement;
  }

}

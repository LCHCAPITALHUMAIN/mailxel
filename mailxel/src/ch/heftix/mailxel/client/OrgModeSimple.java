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


public class OrgModeSimple {

  public String prefixLines(final String str, final String prefix) {

    if (null == str) {
      return "";
    }

    if (null == prefix) {
      return str;
    }

    String[] lines = str.split("\n");
    StringBuffer sb = new StringBuffer(str.length());
    for (String line : lines) {
      sb.append(prefix);
      sb.append(line);
      sb.append("\n");
    }
    String res = sb.toString();
    return res;

  }

  public String dePrefixLines(final String str, final String prefix) {

    if (null == str) {
      return "";
    }

    if (null == prefix) {
      return str;
    }

    int startIdx = prefix.length();
    String[] lines = str.split("\n");
    StringBuffer sb = new StringBuffer(str.length());
    for (String line : lines) {
      if (line.startsWith(prefix)) {
        sb.append(line.substring(startIdx));
      } else {
        sb.append(line);
      }
      sb.append("\n");
    }
    String res = sb.toString();
    return res;

  }

  public String prefixSelection(final String str, final String prefix, final int pos, final int len) {

    OrgModeSelectionHandler omSH = new OrgModeSelectionHandler() {

      public String handleSelection(String str) {
        return prefixLines(str, prefix);
      }
    };

    return handleSelection(omSH, str, pos, len);
  }

  private String handleSelection(final OrgModeSelectionHandler omSH,
      final String text,
      final int start,
      final int len) {

    if (null == omSH) {
      return text;
    }

    if (null == text) {
      return "";
    }
    
    if(len < 1) {
      return text;
    }

    int istart = start;
    int ilen = len;

    if (istart < 0) {
      istart = 0;
    }

    if (ilen > text.length() - istart) {
      ilen = text.length() - istart;
    }

    StringBuffer sb = new StringBuffer(text.length());

    String p1 = text.substring(0, istart);
    String p2 = text.substring(istart, istart + ilen);
    String p3 = text.substring(istart + ilen);

    sb.append(p1);
    String replacement = omSH.handleSelection(p2);
    sb.append(replacement);
    sb.append(p3);

    String res = sb.toString();

    return res;

  }

}

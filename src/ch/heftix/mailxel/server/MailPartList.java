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
package ch.heftix.mailxel.server;

import java.util.ArrayList;

import javax.mail.Part;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MailPartList extends ArrayList<MailPart> {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -3870550173064026485L;

  public void add(final Part part) {
    MailPart mailPart = new MailPart(part);
    add(mailPart);
  }

  public Part getPart(final int i) {
    Part res = null;
    MailPart mailPart = get(i);
    if (null != mailPart) {
      res = mailPart.part;
    }
    return res;
  }

  /**
   * finds and drops the first part of type text in this list
   * 
   * @return text (body) of first text part in this list
   */
  public String dropFirstTextPart() {
    String res = "";
    int toBeDeleted = -1;
    for (int i = 0; i < size(); i++) {
      MailPart mailPart = get(i);
      if (null != mailPart && mailPart.isText) {
        res = MailAPIUtil.getText(mailPart.part);
        // check for HTML
        if (MailAPIUtil.isHTML(mailPart.part)) {
          // the first text attachment is a HTML doc
          // convert it to text, but keep it
          Document doc = Jsoup.parse(res);
          if( null != doc) {
            res = doc.text();
          }
        } else {
          // attachment can be deleted
          toBeDeleted = i;
        }
        break;
      }
    }
    if (toBeDeleted >= 0) {
      remove(toBeDeleted);
    }
    return res;

  }

}

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

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Part;

public class MailPart {

  public boolean isText = false;
  public String mimeType = null;
  public Part part = null;
  public int level = 0;

  public MailPart(final Part part) {
    this.part = part;
    if (MailAPIUtil.isText(part)) {
      this.isText = true;
    }
    this.mimeType = MailAPIUtil.getMimeType(part);
  }

  public String getText() throws IOException, MessagingException {
    String res = "";
    res = (String) part.getContent();
    return res;
  }
}

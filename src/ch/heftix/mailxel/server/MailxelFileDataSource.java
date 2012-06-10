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

import javax.activation.FileDataSource;

import ch.heftix.mailxel.client.MimeTypeDescriptor;
import ch.heftix.mailxel.client.MimeTypeUtil;


public class MailxelFileDataSource extends FileDataSource {

  private String filename = null;


  public MailxelFileDataSource(final String filename) {
    super(filename);
    this.filename = filename;
  }

  public String getContentType() {

    MimeTypeDescriptor mtd = MimeTypeUtil.mimeTypeByName(filename);
    return mtd.mimetype;
  }
}

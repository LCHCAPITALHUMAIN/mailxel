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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataSource;

import ch.heftix.mailxel.client.MimeTypeDescriptor;
import ch.heftix.mailxel.client.MimeTypeUtil;
import ch.heftix.mailxel.client.to.AttachmentTO;


public class MailxelDBDataSource implements DataSource {

  private AttachmentTO aTO = null;
  private MailDB mailDB = null;
  private File zipFile = null;

  public MailxelDBDataSource(final int id, final MailDB db) {

    this.mailDB = db;
    aTO = db.getAttachment(id);
    zipFile = db.getAttachmentZipfile(aTO);

  }

  public String getContentType() {
    MimeTypeDescriptor mtd = MimeTypeUtil.mimeTypeByName(aTO.name);
    return mtd.mimetype;
  }

  public InputStream getInputStream() throws IOException {

    ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
    boolean found = false;
    ZipEntry entry = in.getNextEntry();
    while (!found) {
      if (null != entry && null != entry.getName() && entry.getName().equals(aTO.name)) {
        found = true;
        break;
      }
      entry = in.getNextEntry();
    }

    if (null == entry) {
      throw new IOException("attachment not found in zip file");
    }

    return in;
  }

  public String getName() {
    return aTO.name;
  }

  public OutputStream getOutputStream() throws IOException {
    throw new RuntimeException("write attachments is not a public API");
  }


}

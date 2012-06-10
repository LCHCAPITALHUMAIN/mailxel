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

public class MimeTypeUtil {

  private static final List<MimeTypeDescriptor> mtds = new ArrayList<MimeTypeDescriptor>();

  private static MimeTypeDescriptor defMimeType = new MimeTypeDescriptor(".dat",
      MimeTypeDescriptor.TYPE_ANY, "application/octet-stream", "img/document.png");

  static {
    mtds.add(new MimeTypeDescriptor(".html", MimeTypeDescriptor.TYPE_HTM, "text/html", "img/html.png"));
    mtds.add(new MimeTypeDescriptor(".pdf",  MimeTypeDescriptor.TYPE_ANY, "application/pdf","img/mimetype-pdf.png"));
    mtds.add(new MimeTypeDescriptor(".jpg",  MimeTypeDescriptor.TYPE_IMG, "image/jpeg","img/image.png"));
    mtds.add(new MimeTypeDescriptor(".gif",  MimeTypeDescriptor.TYPE_IMG, "image/gif","img/image.png"));
    mtds.add(new MimeTypeDescriptor(".png",  MimeTypeDescriptor.TYPE_IMG, "image/png","img/image.png"));
    mtds.add(new MimeTypeDescriptor(".bmp",  MimeTypeDescriptor.TYPE_IMG, "image/bmp","img/image.png"));
    mtds.add(new MimeTypeDescriptor(".txt",  MimeTypeDescriptor.TYPE_TXT, "text/plain","img/text-x-generic.png"));
    mtds.add(new MimeTypeDescriptor(".zip",  MimeTypeDescriptor.TYPE_ANY, "application/zip","img/zip.png"));
    mtds.add(new MimeTypeDescriptor(".xml",  MimeTypeDescriptor.TYPE_TXT, "text/xml","img/text-x-generic.png"));
    mtds.add(new MimeTypeDescriptor(".docx", MimeTypeDescriptor.TYPE_ANY, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "img/docx.png"));
    mtds.add(new MimeTypeDescriptor(".doc",  MimeTypeDescriptor.TYPE_ANY, "application/msword","img/x-office-document.png"));
    mtds.add(new MimeTypeDescriptor(".pptx", MimeTypeDescriptor.TYPE_ANY, "application/vnd.openxmlformats-officedocument.presentationml.presentation", "img/pptx.png"));
    mtds.add(new MimeTypeDescriptor(".ppt",  MimeTypeDescriptor.TYPE_ANY, "application/mspowerpoint", "img/x-office-presentation.png"));
    mtds.add(new MimeTypeDescriptor(".xlsx", MimeTypeDescriptor.TYPE_ANY, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "img/xlsx.png"));
    mtds.add(new MimeTypeDescriptor(".xls",  MimeTypeDescriptor.TYPE_ANY, "application/msexcel","img/x-office-spreadsheet.png"));
    mtds.add(new MimeTypeDescriptor(".mp3",  MimeTypeDescriptor.TYPE_ANY, "audio/mpeg","img/music.png"));
    mtds.add(new MimeTypeDescriptor(".dat",  MimeTypeDescriptor.TYPE_ANY, "application/octet-stream", "img/document.png"));
    mtds.add(new MimeTypeDescriptor(".p7s",  MimeTypeDescriptor.TYPE_ANY, "application/pkcs7-signature", "img/safe.png"));
    mtds.add(new MimeTypeDescriptor(".ics",  MimeTypeDescriptor.TYPE_TXT, "text/calendar","img/ics.png"));
    mtds.add(new MimeTypeDescriptor(".calendar", MimeTypeDescriptor.TYPE_TXT, "text/calendar","img/ics.png"));
    mtds.add(new MimeTypeDescriptor(".properties",  MimeTypeDescriptor.TYPE_TXT, "text/plain","img/text-x-generic.png"));
    mtds.add(new MimeTypeDescriptor(".cmd",  MimeTypeDescriptor.TYPE_TXT, "text/plain","img/text-x-generic.png"));
  }


  /**
   * @return extension for mime type starting with mimeType
   */
  public static MimeTypeDescriptor guessExtension(final String mimeType) {

    if (null == mimeType) {
      return defMimeType;
    }
    
    String intMimeType = mimeType.toLowerCase();

    for (MimeTypeDescriptor mtd : mtds) {
      if (intMimeType.startsWith(mtd.mimetype)) {
        return mtd;
      }
    }

    return defMimeType;
  }

  /**
   * @return mime type descriptor for extension
   */
  public static MimeTypeDescriptor mimeTypeByExtension(final String extension) {

    if (null == extension) {
      return defMimeType;
    }

    for (MimeTypeDescriptor mtd : mtds) {
      if (mtd.extension.equals(extension)) {
        return mtd;
      }
    }

    return defMimeType;
  }

  /**
   * @return mime type descriptor for file name
   */
  public static MimeTypeDescriptor mimeTypeByName(final String filename) {

    if (null == filename) {
      return defMimeType;
    }

    for (MimeTypeDescriptor mtd : mtds) {
      if (filename.endsWith(mtd.extension)) {
        return mtd;
      }
    }

    return defMimeType;
  }

  public static int fileType(final String filename) {
    if (null == filename) {
      return MimeTypeDescriptor.TYPE_ANY;
    }

    for (MimeTypeDescriptor mtd : mtds) {
      if (filename.endsWith(mtd.extension)) {
        return mtd.fileType;
      }
    }

    return MimeTypeDescriptor.TYPE_ANY;

  }
}

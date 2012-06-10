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

public class MimeTypeDescriptor {

  public static final int TYPE_ANY = 0;
  public static final int TYPE_IMG = 1;
  public static final int TYPE_TXT = 2;
  public static final int TYPE_HTM = 3;

  public String extension = null;
  public String mimetype = null;
  public String iconUrl = null;
  public int fileType = TYPE_ANY;

  public MimeTypeDescriptor(final String extension,
      final int fileType,
      final String mimetype,
      final String iconUrl) {
    this.extension = extension;
    this.mimetype = mimetype;
    this.iconUrl = iconUrl;
    this.fileType = fileType;
  }

}

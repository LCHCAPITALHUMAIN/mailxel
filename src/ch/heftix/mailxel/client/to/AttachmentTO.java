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
package ch.heftix.mailxel.client.to;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AttachmentTO implements IsSerializable {

  public int id = Constants.UNDEFINED_ID;
  public int messageid = Constants.UNDEFINED_ID;
  public String name;
  public String md5;

  public AttachmentTO() {
    //
  }

  public AttachmentTO(final String fn, final String md5) {
    this.name = fn;
    this.md5 = md5;
  }

  public String toString() {
    return name;
  }
}

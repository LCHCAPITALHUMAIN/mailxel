/*
 * Copyright (C) 2008-2011 by Simon Hefti.
 * All rights reserved.
 * 
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 */
package ch.heftix.mailxel.server;

import java.io.Serializable;


public class BlobTO implements Serializable {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -1464959968033449471L;
  public byte[] data = null;
  public int id = -1;
  public String name = null;
}

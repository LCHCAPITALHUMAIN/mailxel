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
package ch.heftix.mailxel.client;

import java.io.Serializable;


public class MailxelServiceException extends Throwable implements Serializable {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -1174220658306609561L;

  public MailxelServiceException(Exception e) {
    super(e.getMessage());
  }
}

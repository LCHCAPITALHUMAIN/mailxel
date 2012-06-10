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


public class MessageSearchTO implements IsSerializable {

  public String from = null;
  public String to = null;
  public String fromOrTo = null;
  public String cc = null;
  public String bcc = null;
  public String dateFrom = null;
  public int daysRange = 30;
  public String gtd = null;
  public String subject = null;
  public String attachmentName = null;
  public String fts = null;
}

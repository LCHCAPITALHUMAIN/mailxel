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

import java.util.List;

import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.mailxel.client.to.MailTO;


public interface MailDBAccessAPI {

  public List<Envelope> select(final int page,
      String from,
      String to,
      String cc,
      final String fromOrTo,
      String bcc,
      String dateFrom,
      int daysRange,
      String gtd,
      String subject,
      int maxRows);

  public MailTO getMessage(int id);

}

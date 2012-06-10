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

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class SMTPUsernamePasswordAuthenticator extends Authenticator {

  private PasswordAuthentication authentication;

  public SMTPUsernamePasswordAuthenticator(final String user, final String password) {
    authentication = new PasswordAuthentication(user, password);
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return authentication;
  }

}

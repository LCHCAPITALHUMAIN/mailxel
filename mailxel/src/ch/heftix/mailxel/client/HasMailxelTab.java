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


public interface HasMailxelTab {

  /**
   * set tab widget - used to set tab description later on, e.g. when moving
   * through a resultset with the CategorizationToolbar
   */
  public void setMailxelTab(final MailxelTab mailxelTab);
}

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

import java.io.Serializable;

public class AddressTO implements Serializable {

  /** generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = 7137068493328518773L;

  /** primary key */
  public int id = Constants.UNDEFINED_ID;
  /** unique */
  public String address;
  public String shortname;
  public boolean shortNameDirty = false;
  public String name;
  public boolean nameDirty = false;
  public boolean isValid;
  public boolean validDirty = false;
  public boolean isPreferred;
  public boolean preferredDirty = false;
  public String lastSeen;
  public int count;

  public String toString() {
    return address;
  }

}

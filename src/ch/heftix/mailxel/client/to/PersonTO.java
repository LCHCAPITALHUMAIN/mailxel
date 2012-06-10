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
import java.util.ArrayList;
import java.util.List;

public class PersonTO implements Serializable {

  private static final long serialVersionUID = -2325756101831906681L; // generated
  public int id = Constants.UNDEFINED_ID; // primary key
  public String address; // unique

  public String shortname;
  public boolean shortNameDirty = false;

  public String name;
  public boolean nameDirty = false;
  
  public List<AddressTO> addresses = new ArrayList<AddressTO>();

  public String toString() {
    return address;
  }

}

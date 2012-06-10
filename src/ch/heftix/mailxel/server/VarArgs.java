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

import java.util.ArrayList;
import java.util.List;


public class VarArgs {

  private List<Object> list = new ArrayList<Object>();

  public void add(final Object obj) {
    list.add(obj);
  }

  public Object[] toArray() {
    Object[] tmp = new Object[list.size()];
    list.toArray(tmp);
    return tmp;
  }
  
  public int size() {
    return list.size();
  }
}

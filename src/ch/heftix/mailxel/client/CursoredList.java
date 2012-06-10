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

import java.util.List;

public class CursoredList<T> {

  private List<T> list = null;
  private int pos = -1; // indicate "cursor never moved"

  public CursoredList(final List<T> list) {
    this.list = list;
  }

  public T next() {
    if (pos < list.size() - 1) {
      pos++;
    }
    T res = list.get(pos);
    return res;
  }

  public T prev() {
    if (pos > 0) {
      pos--;
    }
    T res = list.get(pos);
    return res;
  }

  public int getCursorPosition() {
    return pos;
  }

  public void setCursorPosition(final int pos) {
    this.pos = pos;
  }
}

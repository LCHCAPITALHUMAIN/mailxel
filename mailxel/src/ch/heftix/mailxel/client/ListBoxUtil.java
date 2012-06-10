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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;


/**
 * 
 */
public class ListBoxUtil<T> {

  List<T> values = new ArrayList<T>();
  List<String> texts = new ArrayList<String>();
  ListBox lb = null;

  public void add(final String text, final T value) {
    texts.add(text);
    values.add(value);
  }

  public ListBox createListBox() {

    lb = new ListBox();
    int cnt = texts.size();
    for (int i = 0; i < cnt; i++) {
      lb.addItem(texts.get(i), values.get(i).toString());
    }

    return lb;

  }

  public T getSelectedValue() {

    int idx = lb.getSelectedIndex();
    return values.get(idx);
  }

  public void setSelectedValue(final T value) {
    if (null == lb) {
      throw new IllegalStateException("ListBox must be created before selected value can be set");
    }
    int idx = values.indexOf(value);
    lb.setSelectedIndex(idx);
  }
}

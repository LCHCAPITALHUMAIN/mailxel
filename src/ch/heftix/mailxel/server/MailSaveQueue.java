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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailSaveQueue {

  List<File> queue = new ArrayList<File>();

  public synchronized void enqueue(File file) {
    if (!queue.contains(file)) {
      queue.add(file);
      notify();
    }
  }

  public synchronized File dequeue() {
    File res = null;
    if (!queue.isEmpty()) {
      res = queue.remove(0);
    }
    return res;
  }

  public synchronized boolean isEmpty() {
    return queue.isEmpty();
  }
}

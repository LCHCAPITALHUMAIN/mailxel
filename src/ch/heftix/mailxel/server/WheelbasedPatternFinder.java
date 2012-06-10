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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WheelbasedPatternFinder {

  private int cursor = 0;
  private byte[] bPattern = null;
  private long pos = -1;
  private long patternStartPos = -1;
  private int initialWheelPosition = -1;

  private long numLines = 0;

  public WheelbasedPatternFinder(final String pattern, final int initialWheelPosition)
      throws FileNotFoundException, IOException {

    this.bPattern = pattern.getBytes();
    this.initialWheelPosition = initialWheelPosition;
    this.cursor = initialWheelPosition;
  }

  public long test(byte b) {
    pos++;
    if ('\n' == b) {
      numLines++;
    }
    if (bPattern[cursor] == b) {
      // exact match
      if (cursor == initialWheelPosition) {
        patternStartPos = pos;
        cursor++;
      } else if (bPattern.length - 1 == cursor) {
        cursor = 0;
        return patternStartPos;
      } else {
        cursor++;
      }
    } else {
      patternStartPos = -1;
      cursor = 0;
      // maybe a new pattern starts here?
      if (bPattern[cursor] == b) {
        if (cursor == initialWheelPosition) {
          patternStartPos = pos;
        }
        cursor++;
      }
    }
    return -1;
  }

  public long getTotalTestedBytes() {
    return pos;
  }

  public long getLine() {
    return numLines;
  }

  /**
   * find start positions of new messages in a mbox file
   * 
   * @param is {@link InputStream} to read from
   * @return uneven list of positions; first is typically 0, last is size of
   * file
   * @throws IOException
   */
  public static int[] findStartPositions(final String pattern,
      final int initialWheelPosition,
      final InputStream is) throws IOException {

    /** result */
    int[] res = new int[0];

    /** temp list to add new positions while seeking */
    List<Long> msgPositions = new ArrayList<Long>();

    BufferedInputStream bis = new BufferedInputStream(is);
    WheelbasedPatternFinder wpf = new WheelbasedPatternFinder(pattern, initialWheelPosition);
    int c = bis.read();
    while (c != -1) {
      byte b = (byte) c;
      long pos = wpf.test(b);
      if (pos != -1) {
        msgPositions.add(pos);
      }
      c = bis.read();
    }
    bis.close();

    if (msgPositions.size() > 0) {
      msgPositions.add(wpf.getTotalTestedBytes());
    }

    res = new int[msgPositions.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = msgPositions.get(i).intValue();
    }

    return res;
  }


}

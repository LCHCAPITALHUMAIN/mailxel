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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedMaxReadInputStream extends FilterInputStream {

  private int maxBytesToRead = 0;
  private int bytesRead = 0;

  public LimitedMaxReadInputStream(final InputStream in, final int maxBytestoRead) {

    // super(in, maxBytestoRead > 8192 ? 8192 : maxBytestoRead);
    super(in);
    this.maxBytesToRead = maxBytestoRead;
  }

  public synchronized int read() throws IOException {
    if (bytesRead > maxBytesToRead) {
      return -1;
    }
    int c = super.read();
    if( -1 != c) {
      bytesRead++;
    }
//    if (c > 0) {
//      byte b = (byte) c;
//      byte[] bs = {b};
//      String z = new String(bs);
//      System.out.print(z);
//    }
    return c;
  }

  public synchronized int read(byte b[], int off, int len) throws IOException {
    if (bytesRead > maxBytesToRead) {
      return -1;
    }
    int modLen = len;
    if (bytesRead + len > maxBytesToRead) {
      modLen = (maxBytesToRead - bytesRead);
    }
    int read = super.read(b, off, modLen);
    bytesRead += read;
    return read;
  }

  public int read(byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  public long skip(long n) throws IOException {
    if (bytesRead > maxBytesToRead) {
      return 0;
    }
    int modLen = (int) n;
    if (bytesRead + n > maxBytesToRead) {
      modLen = (maxBytesToRead - bytesRead);
    }
    long read = super.skip(modLen);
    bytesRead += read;
    return read;
  }

}

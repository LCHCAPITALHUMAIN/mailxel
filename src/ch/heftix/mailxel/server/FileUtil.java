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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.heftix.xel.log.LOG;


public class FileUtil {

  /**
   * create a new zip from source zip file with name zipDest with reduced
   * content
   * 
   * @param zipSource
   * @param zipDest
   * @param toBeDeletedEntryNames
   */
  public static void removeFromZip(final File zipSource,
      final File zipDest,
      final List<String> toBeDeletedEntryNames) {

    ZipInputStream is = null;
    ZipOutputStream os = null;
    try {
      is = new ZipInputStream(new FileInputStream(zipSource));
      ZipEntry entry = is.getNextEntry();
      os = new ZipOutputStream(new FileOutputStream(zipDest));
      while (null != entry) {
        String entryName = entry.getName();
        boolean deleteEntry = false;
        if (null != entryName) {
          if (null != toBeDeletedEntryNames) {
            if (toBeDeletedEntryNames.contains(entryName)) {
              deleteEntry = true;
              LOG.debug("FileUtil, removeFromZip: deleting entry: %s", entryName);
            }
          }
        }
        if (!deleteEntry) {
          // Add ZIP entry to output stream.
          os.putNextEntry(new ZipEntry(entryName));
          int bufSize = (int) 65536;
          // Read in the bytes
          int numRead = 0;
          byte[] buf = new byte[bufSize];
          numRead = is.read(buf);
          while (numRead > 0) {
            os.write(buf, 0, numRead);
            numRead = is.read(buf);
          }
          // Complete the entry
          os.closeEntry();
        } else {
          // Add ZIP entry to output stream.
          os.putNextEntry(new ZipEntry("omitted-" + entryName + ".txt"));
          String content = "omitted this file since it has already been stored with a different mail";
          byte[] buf = content.getBytes();
          os.write(buf);
          // Complete the entry
          os.closeEntry();
        }
        entry = is.getNextEntry();
      }

      is.close();
      is = null;
      os.close();
      is = null;

    } catch (FileNotFoundException e) {
      LOG.warn("FileUtil, removeFromZip: file not found: %s", e);
    } catch (IOException e) {
      LOG.warn("FileUtil, removeFromZip: error reading/writing: %s", e);
    } finally {

      if (null != is) {
        try {
          is.close();
          is = null;
        } catch (IOException e) {
          LOG.warn("FileUtil, removeFromZip: cannot close InputStream: %s", e);
        }
      }
      if (null != os) {
        try {
          os.close();
          os = null;
        } catch (IOException e) {
          LOG.warn("FileUtil, removeFromZip: cannot close OutputStream: %s", e);
        }
      }
    }
  }

  public static String[] zipEntries(final File zipSource) {

    List<String> tmp = new ArrayList<String>();
    try {
      // Open the ZIP file
      ZipFile zf = new ZipFile(zipSource);

      // Enumerate each entry
      for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
        // Get the entry name
        String zipEntryName = ((ZipEntry) entries.nextElement()).getName();
        tmp.add(zipEntryName);
      }
    } catch (IOException e) {
      LOG.debug("FileUtil, zipEntries: cannot read entries: %s", e);
    }
    String[] res = new String[tmp.size()];
    tmp.toArray(res);
    return res;
  }

  public static String truncateRoot(final File file, final File root) {
    String res = null;
    String fPath = file.getAbsolutePath();
    String rPath = root.getAbsolutePath();
    // truncate attachementRoot
    if (null != fPath && fPath.startsWith(rPath)) {
      res = fPath.substring(rPath.length());
      if (null != res && res.startsWith("/"))
        ;
    }
    return res;
  }


}

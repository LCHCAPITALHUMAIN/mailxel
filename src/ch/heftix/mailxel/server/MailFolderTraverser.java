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

import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import ch.heftix.xel.log.LOG;


public class MailFolderTraverser {

  private Map<String, String> excludeMap = new HashMap<String, String>();

  public MailFolderTraverser(final String[] excludeFolderNames) {
    for (int i = 0; i < excludeFolderNames.length; i++) {
      excludeMap.put(excludeFolderNames[i], excludeFolderNames[i]);
    }
  }

  public void traverseFolder(final Store store,
      final String[] topFolderNames,
      final FolderHandler folderHandler) throws MessagingException, Exception {

    Folder root = store.getDefaultFolder();

    if (null == topFolderNames || 0 == topFolderNames.length) {
      LOG.debug("no folder name given - starting from root folder");
      handleFolder(root, folderHandler);
    } else if (topFolderNames.length == 1 && topFolderNames[0].length() < 1) {
      LOG.debug("empty folder name given - starting from root folder");
      handleFolder(root, folderHandler);
    } else {
      for (int i = 0; i < topFolderNames.length; i++) {
        if (null != topFolderNames[i] && topFolderNames[i].length() > 1) {
          handleFolder(root, topFolderNames[i], folderHandler);
        } else {
          LOG.debug("skipping empty folder name");
        }
      }
    }
  }

  private void handleFolder(final Folder root,
      final String folderName,
      final FolderHandler folderHandler) throws MessagingException, Exception {

    // check exclude list
    if (excludeMap.containsKey(folderName)) {
      LOG.debug("skip folder '%s' due to exclude settings", folderName);
      return;
    }

    Folder folder = root.getFolder(folderName);
    if (folder == null) {
      LOG.warn(String.format("folder '%s' not found", folderName));
      return;
    }

    LOG.debug("handling folder '%s'", folderName);
    handleFolder(folder, folderHandler);
  }

  private void handleFolder(final Folder folder, final FolderHandler folderHandler)
      throws MessagingException, Exception {

    // check exclude list
    String fName = folder.getName();
    if (excludeMap.containsKey(fName)) {
      LOG.debug("skip folder '%s' due to exclude settings", fName);
      return;
    }

    int folderType = folder.getType();
    if ((Folder.HOLDS_FOLDERS & folderType) != 0) {
      Folder[] children = new Folder[0];
      try {
        children = folder.list();
      } catch (MessagingException e) {
        LOG.debug("cannot list children of " + folder.getName());
      }
      for (int i = children.length - 1; i >= 0; i--) {
        handleFolder(children[i], folderHandler);
        children[i] = null;
      }
      children = null;
    }

    if ((Folder.HOLDS_MESSAGES & folderType) != 0) {
      folderHandler.handleFolder(folder);
    }
  }
}

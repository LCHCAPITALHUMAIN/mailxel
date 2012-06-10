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

import ch.heftix.mailxel.client.to.AttachmentTO;

public class AttachmentDescriptor {

  public File zipFile = null;
  private List<AttachmentTO> attachmentNames = new ArrayList<AttachmentTO>();

  public void addAttachmentFileName(final String fn, final String md5) {
    AttachmentTO nameMD5 = new AttachmentTO(fn, md5);
    attachmentNames.add(nameMD5);
  }

  public AttachmentTO[] getAttachmentNames() {
    AttachmentTO[] res = new AttachmentTO[attachmentNames.size()];
    attachmentNames.toArray(res);
    return res;
  }
  
  public String toString() {
    return attachmentNames.toString();
  }
}

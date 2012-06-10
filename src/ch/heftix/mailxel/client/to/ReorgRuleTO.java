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

public class ReorgRuleTO implements Serializable {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -4886709243169454490L;

  public int copyMaxMessages = 100;
  public int keepLastNHours = 24;
  public List<String> folderNames = new ArrayList<String>();
  public List<String> rules = new ArrayList<String>();
  
  public String parseResult = null;

}

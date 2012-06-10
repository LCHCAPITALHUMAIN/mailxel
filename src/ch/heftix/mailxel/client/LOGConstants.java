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

/**
 * Simple LOG API
 */
public interface LOGConstants {

  public final static int PRODUCTION = 1;
  public final static int TEST = 2;
  public final static int DEV = 3;

  public final static int DEBUG = 1;
  public final static int WARN = 3;
  public final static int ERROR = 4;

  public final static int INFO = 2;

  public final static int MODE_STACKTRACE = 1;
  public final static int MODE_NO_STACKTRACE = 2;

}

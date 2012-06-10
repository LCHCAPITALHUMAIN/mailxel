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

public class StatisticsTO implements Serializable {

  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -3302277841772464446L;
  
  public int totalLastDay = 0;
  public int totalLastWeek = 0;
  public int totalLastMonth = 0;
  public int totalLastYear = 0;

  public int numRecievedLastDay = 0;
  public int numRecievedLastWeek = 0;
  public int numRecievedLastMonth = 0;
  public int numRecievedLastYear = 0;

  public int numSentLastDay = 0;
  public int numSentLastWeek = 0;
  public int numSentLastMonth = 0;
  public int numSentLastYear = 0;
  
  public int numSentLastWeekSameDomain = 0;
  public int numSentLastWeekOtherDomain = 0;

  public int numRecievedLastWeekSameDomain = 0;
  public int numRecievedLastWeekOtherDomain = 0;
  
  public int numRecievedLastDaySameDomain = 0;
  public int numRecievedLastDayOtherDomain = 0;
}

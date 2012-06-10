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

import java.util.List;


public class ListUtil {

  public static boolean parse(final String val) {
    boolean res = false;
    if (null != val) {
      Boolean tmp = Boolean.valueOf(val);
      res = tmp.booleanValue();
    }
    return res;
  }

  public static String[] getFromComaSeparated(final String str) {
    if (null == str) {
      return null;
    }
    String[] res = str.split(",");
    for (int i = 0; i < res.length; i++) {
      res[i] = res[i].trim();
    }
    return res;
  }

  public static String asComaSeparted(final String[] str) {
    if (null == str) {
      return null;
    }
    StringBuffer sb = new StringBuffer(str.length * 32);
    for (int i = 0; i < str.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(str[i]);
    }
    return sb.toString();
  }

  public static String asSQLIn(final int[] vals) {
    if (null == vals) {
      return null;
    }
    StringBuffer sb = new StringBuffer(vals.length * 8);
    sb.append("(");
    for (int i = 0; i < vals.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(Integer.toString(vals[i]));
    }
    sb.append(")");
    return sb.toString();
  }

  public static String[] asArray(List<String> list) {
    if (null == list) {
      return null;
    }
    String[] res = new String[list.size()];
    list.toArray(res);
    return res;

  }

  public static int[] asIntArray(List<Integer> list) {
    if (null == list) {
      return null;
    }
    Integer[] t2 = new Integer[list.size()];
    list.toArray(t2);
    int[] res = new int[t2.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = t2[i].intValue();
    }
    return res;
  }

  public static String asComaSeparted(final int[] vals) {
    if (null == vals) {
      return null;
    }
    StringBuffer sb = new StringBuffer(vals.length * 8);
    for (int i = 0; i < vals.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(Integer.toString(vals[i]));
    }
    return sb.toString();
  }

  public static String asComaSeparted(final List vals) {
    if (null == vals) {
      return null;
    }

    StringBuffer sb = new StringBuffer(vals.size() * 32);
    for (int i = 0; i < vals.size(); i++) {
      if (i > 0) {
        sb.append(",");
      }
      Object val = vals.get(i);
      if (val instanceof Integer) {
        Integer tmp = (Integer) val;
        sb.append(Integer.toString(tmp));
      } else {
        sb.append(val);
      }
    }
    return sb.toString();
  }


  public static int parse(final String val, final int defaultValue) {
    int res = defaultValue;
    try {
      Integer tmp = Integer.parseInt(val);
      res = tmp.intValue();
    } catch (NumberFormatException e) {
    }
    return res;
  }

}

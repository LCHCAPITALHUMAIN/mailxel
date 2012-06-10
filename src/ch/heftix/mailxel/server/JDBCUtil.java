/*
 * Copyright (C) 2008-2011 by Simon Hefti.
 * All rights reserved.
 * 
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 */
package ch.heftix.mailxel.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ch.heftix.xel.db.PrepStmt;
import ch.heftix.xel.db.ResultSetHandler;
import ch.heftix.xel.log.LOG;



/** @deprecated */
public class JDBCUtil {

  public static String[] getString(final Connection conn, final String sql) {
    String[] res = new String[0];
    List<String> tmp = new ArrayList<String>();

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        tmp.add(rs.getString(1));
      }
      rs.close();
      rs = null;
      stmt.close();
      stmt = null;
      res = new String[tmp.size()];
      tmp.toArray(res);
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", sql, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", sql, e.toString());
      } // sauberer: finally mit geschachteltem try-catch
    }

    return res;
  }

  public static List<String> getString(final PrepStmt ps) {
    List<String> res = new ArrayList<String>();
    if (null == ps || null == ps.ps) {
      return res;
    }

    ResultSet rs = null;

    try {
      rs = ps.ps.executeQuery();
      while (rs.next()) {
        res.add(rs.getString(1));
      }
      rs.close();
      rs = null;
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", ps, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", ps, e.toString());
      }
    }

    return res;
  }

  public static List<String[]> getStrings(final PrepStmt ps, int[] indexes) {
    List<String[]> res = new ArrayList<String[]>();
    if (null == ps || null == ps.ps) {
      return res;
    }

    ResultSet rs = null;

    try {
      rs = ps.ps.executeQuery();
      while (rs.next()) {
        String[] tmp = new String[indexes.length];
        for (int i = 0; i < tmp.length; i++) {
          tmp[i] = rs.getString(indexes[i]);
        }
        res.add(tmp);
      }
      rs.close();
      rs = null;
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", ps, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", ps, e.toString());
      }
    }

    return res;
  }

  public static List<Object[]> get(final PrepStmt ps, final int[] types, final int[] indexes) {
    List<Object[]> res = new ArrayList<Object[]>();
    if (null == ps || null == ps.ps) {
      return res;
    }

    ResultSet rs = null;

    try {
      rs = ps.ps.executeQuery();
      while (rs.next()) {
        Object[] tmp = new Object[indexes.length];
        for (int i = 0; i < tmp.length; i++) {
          switch (types[i]) {
            case Types.DATE:
              tmp[i] = rs.getDate(indexes[i]);
              break;
            case Types.INTEGER:
              tmp[i] = rs.getInt(indexes[i]);
              break;
            default:
              tmp[i] = rs.getString(indexes[i]);
              break;
          }
        }
        res.add(tmp);
      }
      rs.close();
      rs = null;
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", ps, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", ps, e.toString());
      }
    }

    return res;
  }

  /** @deprecated */
  public static int[] getInt(final Connection conn, final String sql) {
    int[] res = new int[0];
    List<Integer> tmp = new ArrayList<Integer>();

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        tmp.add(rs.getInt(1));
      }
      rs.close();
      rs = null;
      stmt.close();
      stmt = null;
      Integer[] t2 = new Integer[tmp.size()];
      tmp.toArray(t2);
      res = new int[t2.length];
      for (int i = 0; i < res.length; i++) {
        res[i] = t2[i].intValue();
      }
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", sql, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", sql, e.toString());
      } // sauberer: finally mit geschachteltem try-catch
    }

    return res;
  }

  public static int[] getInt(final PrepStmt ps) {

    int[] res = new int[0];

    if (null == ps || null == ps.ps) {
      return res;
    }

    final List<Integer> tmpRes = new ArrayList<Integer>();

    ResultSetHandler rsh = new ResultSetHandler() {

      public void handle(ResultSet rs) throws SQLException {
        while (rs.next()) {
          tmpRes.add(rs.getInt(1));
        }
      }
    };

    get(ps, rsh);

    Integer[] t2 = new Integer[tmpRes.size()];
    tmpRes.toArray(t2);
    res = new int[t2.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = t2[i].intValue();
    }
    return res;
  }

  public static int execute(final Connection conn, final String sql) {
    Statement stmt = null;
    int res = 0;
    try {
      stmt = conn.createStatement();
      res = stmt.executeUpdate(sql);
      LOG.debug("JDBCUtil - executed: " + sql);
      stmt.close();
      stmt = null;
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", sql, e.toString());
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", sql, e.toString());
      }
    }
    return res;
  }

  public static boolean execute(final PrepStmt ps) {
    boolean res = false;
    try {
      res = ps.ps.execute();
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with prepared '%s': %s.", ps, e.toString());
    } finally {
    }
    return res;
  }

  /**
   * @param conn
   * @param name sequence name
   * @return new id, or -1 in case of error
   */
  public static synchronized int poorMansSequence(final Connection conn, final String name) {

    String sql = String.format("update sequence set val=val+1 where name='%s'", name);

    execute(conn, sql);

    sql = String.format("select val from sequence where name='%s'", name);
    final int[] res = new int[1];
    res[0] = -1;

    ResultSetHandler rsHandler = new ResultSetHandler() {

      public void handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
          res[0] = rs.getInt(1);
        }
      }
    };

    get(conn, sql, rsHandler);

    return res[0];
  }

  public static String escape(final String str) {

    if (null == str) {
      return str;
    }
    // StringEscapeUtils.escapeSql(str);
    String res = str.replaceAll("'", "''");
    return res;
  }

  public static void get(final PrepStmt ps, final ResultSetHandler rsHandler) {
    if (null == ps || null == ps.ps) {
      return;
    }

    ResultSet rs = null;

    try {
      rs = ps.ps.executeQuery();
      rsHandler.handle(rs);
      rs.close();
      rs = null;
    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", ps, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", ps, e.toString());
      }
    }
  }

  public static void get(final Connection conn,
      final String sql,
      final ResultSetHandler resultSetHandler) {
    if (null == conn || null == sql || null == resultSetHandler) {
      return;
    }

    ResultSet rs = null;
    Statement stmt = null;

    try {

      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      resultSetHandler.handle(rs);
      rs.close();
      rs = null;
      stmt.close();
      stmt = null;

    } catch (SQLException e) {
      LOG.warn("JDBCUtil - problem with '%s': %s.", sql, e.toString());
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (SQLException e) {
        LOG.warn("JDBCUtil - cannot finalize processing of '%s': %s.", sql, e.toString());
      }
    }
  }


}

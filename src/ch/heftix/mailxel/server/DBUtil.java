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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.heftix.xel.db.PrepStmt;
import ch.heftix.xel.db.PrepStmtCache;
import ch.heftix.xel.db.ResultSetHandler;
import ch.heftix.xel.log.LOG;

public class DBUtil {

	PrepStmtCache psc = null;

	public DBUtil(final PrepStmtCache psc) {
		this.psc = psc;
	}

	public void executeAndBringBack(final ResultSetHandler rsh,
			final String sql, final Object... args) {

		PrepStmt ps = psc.borrowAndPrepare(sql, args);

		if (null == ps || null == ps.ps) {
			LOG.debug("cannot prepare statement. sql=%s", sql);
			return;
		}

		psc.execute(ps, rsh);

		psc.bringBack(ps);
	}

	public void updateAndBringBack(final String sql, final Object... args) {

		executeAndBringBack(null, sql, args);

	}

	public int[] getInts(final String sql, final Object... args) {

		ch.heftix.xel.db.PrepStmt ps = psc.borrowAndPrepare(sql, args);

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

		psc.execute(ps, rsh);

		Integer[] t2 = new Integer[tmpRes.size()];
		tmpRes.toArray(t2);
		res = new int[t2.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = t2[i].intValue();
		}

		psc.bringBack(ps);

		return res;
	}

	public int getInt(final String sql, final Object... args) {

		final int[] res = new int[1];
		ResultSetHandler rsh = new ResultSetHandler() {

			public void handle(ResultSet rs) throws SQLException {
				if (rs.next()) {
					res[0] = rs.getInt(1);
				}
			}
		};
		executeAndBringBack(rsh, sql, args);
		return res[0];
	}

	public String[] getStrings(final String sql, final Object... args) {

		String[] res = new String[0];

		List<String> tmpRes = getStringAsList(sql, args);

		res = new String[tmpRes.size()];
		tmpRes.toArray(res);

		return res;
	}

	public String getString(final String sql, final Object... args) {

		final String[] res = new String[1];
		ResultSetHandler rsh = new ResultSetHandler() {
			public void handle(ResultSet rs) throws SQLException {
				if (rs.next()) {
					res[0] = rs.getString(1);
				}
			}
		};
		executeAndBringBack(rsh, sql, args);
		return res[0];
	}

	public List<String> getStringAsList(final String sql, final Object... args) {
		return getStringAsList(sql, -1, args);
	}

	/**
	 * @param sql
	 * @param maxRows
	 *            -1 returns all rows
	 * @param args
	 * @return
	 */
	public List<String> getStringAsList(final String sql, final int maxRows,
			final Object... args) {

		PrepStmt ps = psc.borrowAndPrepare(sql, args);

		final List<String> res = new ArrayList<String>();

		if (null == ps || null == ps.ps) {
			return res;
		}

		ResultSetHandler rsh = new ResultSetHandler() {

			public void handle(ResultSet rs) throws SQLException {

				int cntRows = 0;
				while (rs.next()) {
					cntRows++;
					if (maxRows > 0 && cntRows > maxRows) {
						break;
					}
					res.add(rs.getString(1));
				}
			}
		};

		psc.execute(ps, rsh);

		psc.bringBack(ps);

		return res;
	}

	public synchronized int poorMansSequence(final String name) {

		ch.heftix.xel.db.PrepStmt ps1 = psc.borrowAndPrepare(
				"update sequence set val=val+1 where name=?", name);
		psc.execute(ps1, null);
		psc.bringBack(ps1);

		PrepStmt ps2 = psc.borrowAndPrepare(
				"select val from sequence where name=?", name);

		final int[] res = new int[1];
		res[0] = -1;

		ResultSetHandler rsh = new ResultSetHandler() {

			public void handle(ResultSet rs) throws SQLException {
				if (rs.next()) {
					res[0] = rs.getInt(1);
				}
			}
		};

		psc.execute(ps2, rsh);
		psc.bringBack(ps2);

		return res[0];
	}

}

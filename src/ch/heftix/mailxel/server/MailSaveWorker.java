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

import ch.heftix.xel.log.LOG;

public class MailSaveWorker extends Thread {

	MailDB mailDB = null;
	private boolean halted = false;
	private long sleep = 5000;

	public MailSaveWorker(final MailDB mailDB) {
		this.mailDB = mailDB;
	}

	public void run() {
		if (null == mailDB) {
			LOG.debug("MailSaveWorker not initalized (mailDB is null).");
			return;
		}
		while (true) {
			File file = mailDB.dequeue();
			if (null == file) {
				LOG.debug("skipping file (null)");
				try {
					LOG.debug("sleeping " + Long.toString(sleep) + " ms after empty dequeue");
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					LOG.debug("MailSaveWorker - interrupted.");
					// ignore
				}
				sleep *= 2;
				continue;
			}

			sleep = 5000;

			if (mailDB.isLast(file)) {
				LOG.debug("end of queue reached - halting worker");
				halted = true;
				break;
			}

			Mail2DB.storeMessage(file, mailDB);
			file.delete();
		}

	}

	public synchronized boolean isHalted() {
		return halted;
	}
}

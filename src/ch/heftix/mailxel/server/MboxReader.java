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
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MboxReader {

  private int[] messagePositions = null;
  private Session session = null;
  private int cursor = 0;
  private File file = null;
  FileInputStream inputStream = null;

  public MboxReader(final String fileName) throws FileNotFoundException, IOException {

    file = new File(fileName);

    inputStream = new FileInputStream(file);
    messagePositions = WheelbasedPatternFinder.findStartPositions("\nFrom ", 1, inputStream);
    inputStream.close();

    inputStream = new FileInputStream(file);

    session = Session.getInstance(new Properties());
  }

  public boolean hasNext() {

    if (cursor > messagePositions.length - 2) {
      if (null != inputStream) {
        try {
          inputStream.close();
        } catch (IOException e) {
          // ignore
        }
      }
      return false;
    }
    return true;
  }

  public Message nextMessage() throws IOException, MessagingException {

    if (!hasNext()) {
      return null;
    }

    LimitedMaxReadInputStream limitIS = new LimitedMaxReadInputStream(inputStream,
        messagePositions[cursor + 1] - messagePositions[cursor]);
    Message msg = new MimeMessage(session, limitIS);
    cursor++;
    return msg;
  }

  public void printMessageSummary(final Message msg) throws MessagingException {
    InternetAddress[] from = (InternetAddress[]) msg.getFrom();
    System.out.print(from[0].getAddress());
    System.out.print(" ");
    System.out.print(msg.getSubject());
    Date date = msg.getSentDate();
    System.out.print(" ");
    System.out.println(date);
  }
}

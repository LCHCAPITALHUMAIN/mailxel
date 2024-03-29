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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class TransientPreferences extends Preferences {

  private Map<String, String> map = new HashMap<String, String>();

  @Override
  public String absolutePath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addNodeChangeListener(NodeChangeListener ncl) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
    // TODO Auto-generated method stub

  }

  @Override
  public String[] childrenNames() throws BackingStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void clear() throws BackingStoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void exportNode(OutputStream os) throws IOException, BackingStoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void flush() throws BackingStoreException {
    // TODO Auto-generated method stub

  }

  public String get(String key, String def) {
    if (!map.containsKey(key)) {
      return def;
    }
    String res = map.get(key);
    return res;
  }

  @Override
  public boolean getBoolean(String key, boolean def) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public byte[] getByteArray(String key, byte[] def) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getDouble(String key, double def) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public float getFloat(String key, float def) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getInt(String key, int def) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getLong(String key, long def) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isUserNode() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String[] keys() throws BackingStoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String name() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Preferences node(String pathName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean nodeExists(String pathName) throws BackingStoreException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Preferences parent() {
    // TODO Auto-generated method stub
    return null;
  }

  public void put(String key, String value) {
    map.put(key, value);
  }

  @Override
  public void putBoolean(String key, boolean value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void putByteArray(String key, byte[] value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void putDouble(String key, double value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void putFloat(String key, float value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void putInt(String key, int value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void putLong(String key, long value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void remove(String key) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeNode() throws BackingStoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeNodeChangeListener(NodeChangeListener ncl) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sync() throws BackingStoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return null;
  }

}

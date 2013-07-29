/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umass.cs.gnrs.httpserver;

import edu.umass.cs.gnrs.main.GNS;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static edu.umass.cs.gnrs.main.StartLocalNameServer.debugMode;
import static edu.umass.cs.gnrs.util.Util.println;

/**
 *
 * @author westy
 */
public class SHA1HashFunction extends BasicHashFunction {

  MessageDigest hashfunction;

  private SHA1HashFunction() {

    try {
      hashfunction = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      GNS.getLogger().severe("Error: " + e);
    }
  }

  @Override
  public synchronized byte[] hash(String key) {
    hashfunction.update(key.getBytes());
    return hashfunction.digest();

  }
  
  public synchronized byte[] hash(byte[] bytes) {
    hashfunction.update(bytes);
    return hashfunction.digest();
  }

  public static SHA1HashFunction getInstance() {
    return SHA1HashFunctionHolder.INSTANCE;
  }

  private static class SHA1HashFunctionHolder {

    private static final SHA1HashFunction INSTANCE = new SHA1HashFunction();
  }
}

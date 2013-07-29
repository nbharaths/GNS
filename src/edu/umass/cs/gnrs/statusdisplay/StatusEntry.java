/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umass.cs.gnrs.statusdisplay;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 *
 * @author westy
 */
public class StatusEntry implements Comparable<StatusEntry> {

  public enum State {

    PENDING, INITIALIZING, RUNNING, TERMINATED, ERROR;
  }
  private int id;
  private String name;
  private String ip;
  private Point2D location;
  private State state;
  private String statusString;
  private Date time;

  public StatusEntry(int id) {
    this.id = id;
    this.name = null;
    this.state = State.PENDING;
    this.time = new Date();
    this.location = null;
    this.ip = null;
  }

  public StatusEntry(int id, String name) {
    this(id);
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public State getState() {
    return state;
  }

  public String getStatusString() {
    return statusString;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Point2D getLocation() {
    return location;
  }

  public void setLocation(Point2D location) {
    this.location = location;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setStatusString(String statusString) {
    this.statusString = statusString;
  }
   
  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }
 
  @Override
  public int compareTo(StatusEntry that) {
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;

    if (this == that) {
      return EQUAL;
    }

    if (this.id < that.id) {
      return BEFORE;
    }
    if (this.id > that.id) {
      return AFTER;
    }

    return EQUAL;
  }

  
  @Override
  public String toString() {
    return "StatusEntry{" + "id=" + id + ", name=" + name + ", ip=" + ip + ", location=" + location + ", state=" + state + ", statusString=" + statusString + ", time=" + time + '}';
  }
  
}

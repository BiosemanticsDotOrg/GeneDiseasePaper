/*
 * Concept profile generation tool suite
 * Copyright (C) 2015 Biosemantics Group, Erasmus University Medical Center,
 *  Rotterdam, The Netherlands
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.erasmusmc.concurrency;

import java.util.ArrayList;
import java.util.List;

public abstract class Task {
  protected List<TaskListener> listeners = new ArrayList<TaskListener>();
  protected long lastFiredAdvanced = -1;
  
  public synchronized void requestStop(){
    isStopRequested = true;
  }

  // PRIVATE ////////

  protected boolean isStopRequested = false;

  protected synchronized boolean isStopRequested() {
    return isStopRequested;
  }
  public long getLength() {
    return 0;
  }
  
  public long getProgress() {
    return 0;
  }
  
  public String getMessage() {
    return "Processing";
  }
  
  public void addListener(TaskListener listener) {
    listeners.add(listener);
  }
  
  public void removeTaskListener(TaskListener listener) {
    listeners.remove(listener);
  }
  
  protected void fireStartedEvent() {
    for (TaskListener listener: listeners)
      listener.taskStarted(this);
  }
  
  protected void fireAdvancedEvent() {
    if (lastFiredAdvanced + 500 < System.currentTimeMillis()) {
      for (TaskListener listener: listeners)
        listener.taskAdvanced(this);
      
      lastFiredAdvanced = System.currentTimeMillis();
    }
  }
  
  protected void fireDoneEvent() {
    for (TaskListener listener: listeners)
      listener.taskDone(this);
  }
  
  public void execute() throws Exception {
    fireStartedEvent();
    run();
    fireDoneEvent();
  }
  
  protected abstract void run() throws Exception;
  
  /*for example;
   *   public void run() {
    boolean isDone = false;

    while (!isStopRequested() && !isDone){
      //perform the work
    }
  }
   * 
   */
}

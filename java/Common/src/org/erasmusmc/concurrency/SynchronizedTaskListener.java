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

import javax.swing.SwingUtilities;

public class SynchronizedTaskListener implements TaskListener {
  protected TaskListener taskListener;
  
  public SynchronizedTaskListener(TaskListener taskListener) {
    this.taskListener = taskListener;
  }
  
  public void taskStarted(final Task task) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          taskListener.taskStarted(task);
        }
      });
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void taskAdvanced(final Task task) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          taskListener.taskAdvanced(task);
        }
      });
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void taskDone(final Task task) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          taskListener.taskDone(task);
        }
      });
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}

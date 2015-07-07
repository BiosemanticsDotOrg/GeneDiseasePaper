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

import java.util.LinkedList;
import java.util.Queue;


public class QueueSemaphore<T> {
	private Queue<T> queue = new LinkedList<T>();
  

  public synchronized void take(T value) {
    queue.offer(value);
    this.notify();
  }

  public synchronized T release() {
    while(queue.isEmpty())
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    return queue.poll();
  }

}

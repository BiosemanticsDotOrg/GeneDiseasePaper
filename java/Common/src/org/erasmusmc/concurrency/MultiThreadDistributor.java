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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadDistributor<T> {
	private MultiThreadDistributorListener<T> listener;
	private Iterator<T> iterator;
	private List<ExecutionThread> threads;
	private int numberOfThreads;
	private ReentrantLock lock = new ReentrantLock();
	
  public MultiThreadDistributor(MultiThreadDistributorListener<T> listener, int numberOfThreads){
  	this.listener = listener;
  	threads =  new ArrayList<ExecutionThread>(numberOfThreads);
  	this.numberOfThreads = numberOfThreads;
  }
  
  public void process(Iterator<T> iterator){
  	this.iterator = iterator;
  	threads.clear();
  	for (int i = 0; i < numberOfThreads; i++){
  		ExecutionThread thread = new ExecutionThread();
  		thread.start();
  		threads.add(thread);
  	}
  	
  	for (ExecutionThread thread : threads)
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
  }
  
  private class ExecutionThread extends Thread {
  	
  	public void run(){
  		T object = getNext();
  		while (object != null){
  			listener.process(object);
  			object = getNext();
  		}
  	}
  	
  	private T getNext(){
  		T result;
  		lock.lock();
  		if (iterator.hasNext())
  		  result = iterator.next();
  		else
  			result = null;
  		lock.unlock();
  		return result;
  	}
  }
}

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


/**
 * Thread class for processing data in batches. After construction, the thread will stay alive
 * until terminate() is called. When proceed() is called, the function process() is executed in the 
 * thread. waitUntilFinished() will wait until the thread is done executing process().
 * 
 * @author schuemie
 *
 */
public abstract class BatchProcessingThread extends Thread {
	private final Semaphore newInputSemaphore = new Semaphore();
	private final Semaphore newOutputSemaphore = new Semaphore();
  private boolean terminated = false;
  
	public BatchProcessingThread(){
		super();
		this.start();
		
	}
  public void run(){
    while (true){
    	newInputSemaphore.release();
      if (terminated)
      	break;
      process();
      newOutputSemaphore.take();
    }
  }
  
	protected abstract void process();
	
  public void proceed(){ //This method will be run in the other thread!
  	newInputSemaphore.take();
  }
   
  public void waitUntilFinished(){ //Runs in other thread!
  	newOutputSemaphore.release();
  }
  
  public synchronized void terminate(){//Runs in other thread!
    terminated = true;
    newInputSemaphore.take();
  }
  

}

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

import java.util.Iterator;
import java.util.List;

/**
 * Same as MultiThreadSequenceProcessing, but without preservation of sequence (but better load balancing).
 * @author schuemie
 *
 * @param <I>
 * @param <O>
 */
public abstract class MultiThreadStreamProcessing<I,O> implements Iterator<O> {

	public static int nrOfThreads = Runtime.getRuntime().availableProcessors();

	private Iterator<I> inputIterator;
	private Iterator<O> outputIterator;
	private O buffer;	
	private QueueSemaphore<ProcessingThread> queue;
	private int activeThreads;

	public MultiThreadStreamProcessing(Iterator<I> inputIterator){
		this.inputIterator = inputIterator;
		queue = new QueueSemaphore<ProcessingThread>();
		activeThreads = 0;
		for (int i = 0; i < nrOfThreads; i++){
			if (inputIterator.hasNext()){
				ProcessingThread thread = new ProcessingThread();
				thread.input = inputIterator.next();
				activeThreads++;
				thread.proceed();
			}
		}
		outputIterator = getNextOutputIterator();
		if (outputIterator != null && outputIterator.hasNext())
			buffer = outputIterator.next();
	}


	private Iterator<O> getNextOutputIterator() {
		while (activeThreads != 0){
			ProcessingThread thread = queue.release();
			List<O> output = thread.output;
			thread.output = null;
			if (inputIterator.hasNext()){
				thread.input = inputIterator.next();
				thread.proceed();
			} else {
				thread.terminate();
				activeThreads--;
			}
			if (output != null && output.size() != 0)
				return output.iterator();
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return (buffer != null);
	}

	@Override
	public O next() {
		O next = buffer;
		if (outputIterator.hasNext())
			buffer = outputIterator.next();
		else {
			outputIterator = getNextOutputIterator();
			if (outputIterator == null)
				buffer = null;
			else 
				buffer = outputIterator.next();
		}

		return next;
	}

	@Override
	public void remove() {		
	}

	public abstract List<O> processInput(I input);


	private class ProcessingThread extends Thread {

		public I input;
		public List<O> output;
		private boolean terminated = false;
		private Semaphore start = new Semaphore();

		public ProcessingThread(){
			this.start();
		}

		public void proceed(){
			start.take();
		}

		public void run() {
			while (!terminated){
				start.release();
				if (terminated)
					break;
				output = processInput(input);
				queue.take(this);
			}
		}
		
		public void terminate(){
			terminated = true;
			start.take();
		}
	}



}
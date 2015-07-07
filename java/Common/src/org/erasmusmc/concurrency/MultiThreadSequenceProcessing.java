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

/**
 * This class can be used when objects coming from an iterator have to processed, and the results of the processing is a list of objects over which
 * you need to iterate. So the input is an iterator over objects of class I, and the output is an iterator over objects of class O. The processInputs method 
 * must be implemented to consume input and produce a list of outputs (can be size 0). The processing is performed in parallel threads. The sequence of input to
 * output is preserved.
 * @author schuemie
 *
 * @param <I>	Class of the input objects
 * @param <O>	Class of the output objects
 */
public abstract class MultiThreadSequenceProcessing<I,O> implements Iterator<O> {
	
	private Iterator<I> inputIterator;
	private List<ProcessingThread> threads;
	private int threadCursor;
	private Iterator<O> outputIterator;
	private O buffer;	
	private boolean noMoreInputs;
	
	public MultiThreadSequenceProcessing(Iterator<I> inputIterator, int nrOfThreads){
		this.inputIterator = inputIterator;
		threads = new ArrayList<ProcessingThread>(nrOfThreads);
		for (int i = 0; i < nrOfThreads; i++){
			if (inputIterator.hasNext()){
				ProcessingThread thread = new ProcessingThread();
				thread.input = inputIterator.next();
			  threads.add(thread);
			  thread.proceed();
			}
		}
		threadCursor = 0;
		noMoreInputs = false;
		outputIterator = getNextOutputIterator();
		if (outputIterator != null && outputIterator.hasNext())
			buffer = outputIterator.next();
	}
	
	
	private Iterator<O> getNextOutputIterator() {
		while (!noMoreInputs){
			ProcessingThread thread = threads.get(threadCursor);
			if (thread == null)
				noMoreInputs = true;
			else {
				thread.waitUntilFinished();
				List<O> output = thread.output;
				thread.output = null;
				if (inputIterator.hasNext()){
				  thread.input = inputIterator.next();
				  thread.proceed();
				} else {
					thread.terminate();
					threads.set(threadCursor, null);
				}

				threadCursor++;
				if (threadCursor == threads.size())
					threadCursor = 0;
				if (output == null)
					System.err.println("asdf");
				if (output != null && output.size() != 0)
					return output.iterator();
			}
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
	
	
	private class ProcessingThread extends BatchProcessingThread {

		public I input;
		public List<O> output;
		@Override
		protected void process() {
		  output = processInput(input);
		}
	}
	
	
	
}
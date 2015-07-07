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

import java.io.InputStream;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.erasmusmc.streams.BytesReadTrackingInputStream;

public abstract class StreamReadingTask extends Task {
  protected BytesReadTrackingInputStream inputStream;
  protected long bytesToRead;
  protected String streamName;
  
  public StreamReadingTask(InputStream inputStream, long bytesToRead, String streamName) {
    this.inputStream = new BytesReadTrackingInputStream(inputStream);
    this.bytesToRead = bytesToRead;
    this.streamName = streamName;
    
    installStreamListener();
  }
  
  protected void installStreamListener() {
    inputStream.listeners.add(new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        fireAdvancedEvent();
      }
    });
  }
  
  public long getProgress() {
    return inputStream.bytesRead();
  }
  
  public long getLength() {
    return bytesToRead;
  }
  
  public String getMessage() {
    return "Reading \"" + streamName + "\"";
  }
}

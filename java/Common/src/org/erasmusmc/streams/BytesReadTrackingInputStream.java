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

package org.erasmusmc.streams;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class BytesReadTrackingInputStream extends FilterInputStream {
  protected int count;
  public List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  public int eventInterval = 100000;

  public BytesReadTrackingInputStream(InputStream in) {
    super(in);
  }

  public int bytesRead() {
    return count;
  }

  public int read() throws IOException {
    count++;
    
    if (count % eventInterval == 0)
      fireChangedEvent();

    return super.read();
  }

  public int read(byte[] buffer) throws IOException {
    return read(buffer, 0, buffer.length);
  }

  public int read(byte[] buffer, int offset, int length) throws IOException {
    int actual = super.read(buffer, offset, length);

    if (actual > 0) {
      count += actual;
    
      if (count % eventInterval < actual)
        fireChangedEvent();
    }

    return actual;
  }
  
  protected void fireChangedEvent() {
    ChangeEvent changeEvent = new ChangeEvent(this);
    
    for (ChangeListener changeListener: listeners)
      changeListener.stateChanged(changeEvent);
  }
}
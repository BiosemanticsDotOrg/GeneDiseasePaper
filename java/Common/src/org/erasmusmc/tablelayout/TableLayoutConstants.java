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

package org.erasmusmc.tablelayout;



/**
 * Constants used by TableLayout.
 *
 * @author  Daniel E. Barbalace
 */

public interface TableLayoutConstants
{



/** Indicates that the component is left justified in its cell */
public static final int LEFT = 0;

/** Indicates that the component is top justified in its cell */
public static final int TOP = 0;

/** Indicates that the component is centered in its cell */
public static final int CENTER = 1;

/** Indicates that the component is full justified in its cell */
public static final int FULL = 2;

/** Indicates that the component is bottom justified in its cell */
public static final int BOTTOM = 3;

/** Indicates that the component is right justified in its cell */
public static final int RIGHT = 3;

/** Indicates that the row/column should fill the available space */
public static final double FILL = -1.0;

/** Indicates that the row/column should be allocated just enough space to
    accomidate the preferred size of all components contained completely within
    this row/column. */
public static final double PREFERRED = -2.0;

/** Indicates that the row/column should be allocated just enough space to
    accomidate the minimum size of all components contained completely within
    this row/column. */
public static final double MINIMUM = -3.0;

/** Minimum value for an alignment */
public static final int MIN_ALIGN = 0;

/** Maximum value for an alignment */
public static final int MAX_ALIGN = 3;



}

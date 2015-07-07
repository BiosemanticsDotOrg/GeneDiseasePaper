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

package org.erasmusmc.net.compression;

public interface CompressionConstants {
    
    /** Constants for 6-bit code values. */

    /** No operation: used to pad words on flush. */
    static final int NOP     = 0;  

    /** Introduces raw byte format. */
    static final int RAW     = 1;  

    /** Format indicator for characters found in lookup table. */
    static final int BASE    = 2;  

    /** A character's code is it's index in the lookup table. */
    static final String codeTable =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ,.!?\"'()";

}

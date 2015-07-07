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

package org.erasmusmc.utilities;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.collections.Pair;

public class ShortFormLongFormMatcher {

  private int lastEOS;

  public List<Pair<String, String>> extractSFLFmatches(String text) {
    List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();

    // Detect parenthesis
    int startIndex = -1;
    lastEOS = 0;
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '.' | ch == ',' | ch == '!' | ch == '?' | (int) ch == 10) {
        startIndex = -1;
        lastEOS = i;
      }
      else if (ch == '(') {
        startIndex = i + 1;
      }
      else if (ch == ')') {
        if (startIndex != -1) {
          Pair<String, String> pair = processParenthesizedString(text, startIndex, i);
          if (pair != null)
            result.add(pair);
          startIndex = -1;
        }
      }
    }
    return result;
  }

  private Pair<String, String> processParenthesizedString(String text, int startIndex, int endIndex) {
    String string = text.substring(startIndex, endIndex);
    String longForm;
    String shortForm;
    boolean shortFormKnownSize;
    if (isLongForm(string)) {
      longForm = string;
      shortForm = findSFbefore(text, startIndex);
      shortFormKnownSize = false;
    }
    else {
      shortForm = string;
      longForm = text.substring(Math.max(lastEOS, startIndex - maxLFlength(shortForm) - 1), startIndex - 1);
      shortFormKnownSize = true;
    }
    if (shortForm.length() > 1 && Character.isLetter(shortForm.charAt(0)) && !shortForm.contains("=") && !StringUtilities.isRomanNumeral(shortForm)) {
      return findLongForm(longForm, shortForm, shortFormKnownSize);
    }
    return null;
  }

  private static boolean evenParenthesis(String string) {
    int open = 0;
    int close = 0;
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);
      if (ch == '(')
        open++;
      if (ch == ')')
        close++;
    }
    return open == close;
  }

  public static Pair<String, String> findLongForm(String longForm, String shortForm, boolean shortFormSizeIsKnown) {
    String longFormLC = longForm.toLowerCase();
    String shortFormLC = shortForm.toLowerCase();
    int lfIndex = longFormLC.length();
    int lastLetterSFIndex = -1;
    int lastMatchedLFIndex = -1;
    boolean nonConsecutiveMatch = false;
    for (int sfIndex = shortFormLC.length() - 1; sfIndex >= 0; sfIndex--) {
      char ch = shortFormLC.charAt(sfIndex);
      boolean matched = false;
      boolean unmatchable = false;
      if (!Character.isLetterOrDigit(ch)) { // Character is neither digit nor
                                            // letter
        unmatchable = true;
      }
      else if (Character.isDigit(ch)) { // Character is digit
        // Attempt to find number:
        for (int numberIndex = lfIndex - 1; numberIndex >= 0; numberIndex--) {
          if (longFormLC.charAt(numberIndex) == ch) {
            lfIndex = numberIndex;
            matched = true;
            break;
          }
        }
        // If number not found: try looking for roman numeral:
        if (!matched && lfIndex > 0) {
          String romanNumeral = convertToNumeral(ch);
          int romanNumeralIndex = longFormLC.substring(0, lfIndex - 1).indexOf(romanNumeral);
          if (romanNumeralIndex != -1) {
            lfIndex = romanNumeralIndex;
            matched = true;
          }
        }
      }
      else { // Character is letter
        // Attempt to find letter:
        for (int letterIndex = lfIndex - 1; letterIndex >= 0; letterIndex--) {
          if (longFormLC.charAt(letterIndex) == ch) {
            if (sfIndex != 0 || letterIndex == 0 || !Character.isLetterOrDigit(longFormLC.charAt(letterIndex - 1))) {
              lfIndex = letterIndex;
              matched = true;
              lastLetterSFIndex = sfIndex;
              break;
            }
          }
        }
      }
      if (matched) {
        if (lastMatchedLFIndex != -1 && lastMatchedLFIndex > lfIndex + 1)
          nonConsecutiveMatch = true;
        lastMatchedLFIndex = lfIndex;
      }
      else if (!unmatchable) {
        if (shortFormSizeIsKnown || !nonConsecutiveMatch)
          return null;
        else // Could be that we have the complete shortform aligned. Do some
              // checks:
        // If last matched SF letter also occurres at begin of LF, place pointer
        // there:
        if (lastLetterSFIndex != -1 && longFormLC.charAt(0) == shortFormLC.charAt(lastLetterSFIndex))
          lfIndex = 0;

        // Check if we have the complete long-form, and the last aligned SF
        // letter is preceded by a word delimiter:
        if (lfIndex == 0 && lastLetterSFIndex > sfIndex + 1)
          return checkSFLF(longForm, shortForm.substring(lastLetterSFIndex));
        else
          return null;
      }
    }
    // All letters matched, return pair:
    if (nonConsecutiveMatch)
      return checkSFLF(longForm.substring(lfIndex), shortForm);
    else
      return null;
  }

  private static Pair<String, String> checkSFLF(String longForm, String shortForm) {
    // If LF contains parenthesis, these should be matching.
    if (evenParenthesis(longForm) && !longForm.toLowerCase().startsWith(shortForm.toLowerCase()))
      return new Pair<String, String>(shortForm, longForm);
    else
      return null;
  }

  private static String convertToNumeral(char ch) {
    switch (ch) {
      case '1':
        return "i";
      case '2':
        return "ii";
      case '3':
        return "iii";
      case '4':
        return "iv";
      case '5':
        return "v";
      case '6':
        return "vi";
      case '7':
        return "vii";
      case '8':
        return "viii";
      case '9':
        return "ix";
    }
    return "0";
  }

  private static String findSFbefore(String text, int index) {
    int delimiters = 0;
    int minIndex = Math.max(0, index - 10);
    int startIndex = minIndex;
    for (int i = index - 1; i >= minIndex; i--) {
      if (!Character.isLetterOrDigit(text.charAt(i))) {
        delimiters++;
        if (delimiters == 2) {
          startIndex = i + 1;
          break;
        }
      }
    }
    return text.substring(startIndex, index - 1);
  }

  private static boolean isLongForm(String string) {
    return (string.split(" ").length > 2 || string.length() > 7);
  }

  private static int maxLFlength(String shortForm) {
    int matchable = 0;
    for (int i = 0; i < shortForm.length(); i++) {
      if (Character.isLetterOrDigit(shortForm.charAt(i))) {
        matchable++;
      }
    }
    return matchable * 15;
  }

}

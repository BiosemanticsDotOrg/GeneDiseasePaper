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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public abstract class AbstractNormaliser implements Serializable {


    /**
     * If true, the cache will grow when a new word is normalised. <br>
     * <br>
     * The default value is False;
     */
    public boolean growCache = false;

    /**
     * If false, abbreviations (tokens with a majority of uppercase letters) will
     * be normalised, even if the term as a whole is set to be matched normalised.
     * <br>
     * <br>
     * The default value is True.
     */
    public boolean doNotNormaliseAbbreviations = true;
    
    /**
     * if true, for abbreviations that consists of all uppercase letters, except the last 
     * letter which is an 's' the last letter is removed.<br>
     * For example: ADRs becomes ADR.
     * <br>
     * <br>
     * The default value is True. 
     */
    public boolean convertPluralAbbreviations = true;

    /**
     * Normalizes a set of input strings.
     * 
     * @param string
     *          The input string to be normalised.
     * @return The string of normalised words in alphabetical order.
     */
    public List<String> normalise(List<String> tokens) {
      List<String> result = new ArrayList<String>(tokens.size());
      for (int i = 0; i < tokens.size(); i++) {
        result.add(i, normWord(tokens.get(i)));
      }
      return result;
    }

    /**
     * Converts the input string to a string of normalised words in alphabetical
     * order.
     * 
     * @param string
     *          The input string to be normalised.
     * @return The string of normalised words in alphabetical order.
     */
    public String normalise(String string) {
      if (string == "") {
        return "";
      }
      else {
        List<String> words = StringUtilities.mapToWords(string);
        List<String> normwords = normalise(words);
        Collections.sort(normwords);
        return StringUtilities.join(normwords, " ");
      }
    }

    /**
     * Converts the input string to a string of normalised words in the original
     * order.
     * 
     * @param string
     *          The input string to be normalised.
     * @return The string of normalised words.
     */
    public String normaliseInOrder(String string) {
      if (string == "") {
        return "";
      }
      else {
        List<String> words = StringUtilities.mapToWords(string);
        List<String> normwords = normalise(words);
        return StringUtilities.join(normwords, " ");
      }
    }

    /**
     * Save the cache as a text file.
     * 
     * @param filename
     *          The path and filename of the text file.
     */
    public void saveCache(String filename) {
      Iterator<Map.Entry<String, String>> cacheiterator = cache.entrySet().iterator();
      try {
        FileOutputStream PSFFile = new FileOutputStream(filename);
        BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile), 1000000);
        try {
          for (int i = 0; i < cache.size(); i++) {
            Map.Entry<String, String> entry = cacheiterator.next();
            bufferedWrite.write(entry.getKey().toString() + "=" + entry.getValue().toString());
            bufferedWrite.newLine();
          }
          bufferedWrite.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    /**
     * Save the cache as a binary file.
     * 
     * @param filename
     *          The path and filename of the binary file.
     */
    public void saveCacheBinary(String filename) {
      try {
        FileOutputStream binFile = new FileOutputStream(filename);
        try {
          ObjectOutputStream out = new ObjectOutputStream(binFile);
          out.writeObject(cache);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    /**
     * Load the cache from a binary file.
     * 
     * @param filename
     *          The path and filename of the binary file.
     */
    @SuppressWarnings("unchecked")
    public void loadCacheBinary(String filename) {
      try {
        FileInputStream binFile = new FileInputStream(filename);
        try {
          ObjectInputStream inp = new ObjectInputStream(binFile);
          try {
            cache = (Map<String, String>) inp.readObject();
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    /**
     * Load the cache from a text file.
     * 
     * @param filename
     *          The path and filename of the text file.
     */
    public void loadCache(String filename) {
      try {
        FileInputStream PSFFile = new FileInputStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(PSFFile, "UTF-8"), 1000000);

        try {
          while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            String[] subs = line.split("=");
            cache.put(subs[0], subs[1]);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    /**
     * Normalise a single word.
     * 
     * @param word
     *          The input word.
     * @return The normalised word.
     */
    public String normWord(String word) {
      String result = word;
      if (!doNotNormaliseAbbreviations || !StringUtilities.isAbbr(word)) {
        result = result.toLowerCase();
        if (!StringUtilities.containsNumber(result)) {// contains number: no
          result = removePosessive(result);
          
          // check cache
          String value = cache.get(result);
          if (value != null) {
            result = value;
          }
          else { // not in cache: use LVG
            String newResult = externalnormalise(result);
            if (growCache)
              cache.put(result, newResult);
            result = newResult;
          }
        }
      } else if (convertPluralAbbreviations &&
          word.length() > 1 &&
          word.charAt(word.length()-1) == 's' &&
          StringUtilities.countsCharactersInLowerCase(word) == 1) 
        result = word.substring(0,word.length()-1);


      return result;
    }
    
    /* Implement this method! */
    protected abstract String externalnormalise(String word);

    /** Clear the cache. */
    public void clearCache() {
      cache = new HashMap<String, String>();
    }

    private String removePosessive(String lcword) {
      if (lcword.length() > 2 && lcword.charAt(lcword.length() - 2) == '\'' && lcword.charAt(lcword.length() - 1) == 's') {
        return lcword.substring(0, lcword.length() - 2);
      }
      else
        return lcword;
    }
    public int getCacheSize(){
    	return cache.size();
    }

    protected Map<String, String> cache = new HashMap<String, String>();

    private static final long serialVersionUID = -7895782040241683680L;

    private void writeObject(ObjectOutputStream s) throws IOException {
      s.writeObject(cache);
    }

    @SuppressWarnings( { "unchecked"})
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      cache = (Map<String, String>) s.readObject();
    }
  }


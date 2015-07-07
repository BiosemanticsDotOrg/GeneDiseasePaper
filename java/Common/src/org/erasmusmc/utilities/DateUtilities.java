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

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtilities {
  public static long second = 1000;
  public static long minute = 60 * second;
  public static long hour = 60 * minute;
  public static long day = 24 * hour;
  public static long week = 7 * day;
  public static long year = 365 * day;
  public static long century = 100 * year;
  public static long millenium = 1000 * year;
  
  public static long goBack(long time, int amount, int unit){
    if (unit == Calendar.DATE)
      return time-day;
    
    calendar.setTimeInMillis(time);
    int date = calendar.get(Calendar.DATE);
    int month = calendar.get(Calendar.MONTH);
    int year =  calendar.get(Calendar.YEAR);
    if (unit == Calendar.MONTH){      
      month--;
      if (month<0){
        month = 11;
        year--;
      }      
    } else if (unit == Calendar.YEAR)      
      year--;     
    
    calendar.set(year, month, date);
    return calendar.getTimeInMillis();
  }
  
  public static long goForward(long time, int amount, int unit){
    if (unit == Calendar.DATE)
      return time+day;
    
    calendar.setTimeInMillis(time);
    int date = calendar.get(Calendar.DATE);
    int month = calendar.get(Calendar.MONTH);
    int year =  calendar.get(Calendar.YEAR);
    if (unit == Calendar.MONTH){      
      month++;
      if (month>11){
        month = 0;
        year++;
      }      
    } else if (unit == Calendar.YEAR)      
      year++;     
    
    calendar.set(year, month, date);
    return calendar.getTimeInMillis();
  }
  
  private static Calendar calendar = new GregorianCalendar();
}

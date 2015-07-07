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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HTTPUtilities {
  public HTTPUtilities(){
    client = new HttpClient();
  }
  private HttpClient client;
  public int waitingPeriod = 0;


  public String[] get(String url){
    try {
      Thread.sleep(waitingPeriod);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    String[] lines = null;

    GetMethod method = new GetMethod(url);
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
    try {
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }

      InputStream responseBody = method.getResponseBodyAsStream();
      lines = stream2array(responseBody);
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      method.releaseConnection();
    }
    return lines;
  }

  public String[] post(String url, NameValuePair[] data){
    String[] lines = null;
    PostMethod method = new PostMethod(url);
    method.setRequestBody(data);
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
    try {
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }

      /*InputStream responseBody = method.getResponseBodyAsStream();
      lines = stream2array(responseBody);
       */
      lines = method.getResponseBodyAsString().split("\n");
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      method.releaseConnection();
    }
    return lines;
  }  

  private String[] stream2array(InputStream stream) throws IOException{
    byte[] bytes = new byte[1024];
    StringBuffer result = new StringBuffer();
    int len;
    while ((len = stream.read(bytes)) != -1) {
      for (int i = 0; i < len; i++)
        result.append((char)bytes[i]);
    }
    stream.close();
    return result.toString().split("\n");
  }

  public static String htmlEncode( String s )
  {
    StringBuffer buf = new StringBuffer();
    for ( int i = 0; i < s.length(); i++ )
    {
      char c = s.charAt( i );
      if ( c>='a' && c<='z' || c>='A' && c<='Z' || c>='0' && c<='9' || c == '+') {
        buf.append( c );
      } else if (c == ' '){
        buf.append("+");
      } else {
        buf.append( "&#" + (int)c + ";" );
      }
    }
    return buf.toString();
  }



}

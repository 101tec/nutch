/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.protocol;

import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.util.NutchConfiguration;

import org.apache.nutch.util.WritableTestUtils;

import junit.framework.TestCase;

/** Unit tests for Content. */

public class TestContent extends TestCase {
    
  private static Configuration conf = NutchConfiguration.create();
    
  public TestContent(String name) { super(name); }

  public void testContent() throws Exception {

    String page = "<HTML><BODY><H1>Hello World</H1><P>The Quick Brown Fox Jumped Over the Lazy Fox.</BODY></HTML>";

    String url = "http://www.foo.com/";

    ContentProperties metaData = new ContentProperties();
    metaData.put("Host", "www.foo.com");
    metaData.put("Content-Type", "text/html");

    Content r = new Content(url, url, page.getBytes("UTF8"), "text/html",
                            metaData, conf);
                        
    WritableTestUtils.testWritable(r);
    assertEquals("text/html", r.getMetadata().get("Content-Type"));
    assertEquals("text/html", r.getMetadata().get("content-type"));
  }

  /** Unit tests for getContentType(String, String, byte[]) method. */
  public void testGetContentType() throws Exception {
    Content c = null;
    ContentProperties p = new ContentProperties();

    c = new Content("http://www.foo.com/",
                    "http://www.foo.com/",
                    "".getBytes("UTF8"),
                    "text/html; charset=UTF-8", p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/foo.html",
                    "http://www.foo.com/",
                    "".getBytes("UTF8"),
                    "", p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/foo.html",
                    "http://www.foo.com/",
                    "".getBytes("UTF8"),
                    null, p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/",
                    "http://www.foo.com/",
                    "<html></html>".getBytes("UTF8"),
                    "", p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/foo.html",
                    "http://www.foo.com/",
                    "<html></html>".getBytes("UTF8"),
                    "text/plain", p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/foo.png",
                    "http://www.foo.com/",
                    "<html></html>".getBytes("UTF8"),
                    "text/plain", p, conf);
    assertEquals("text/html", c.getContentType());

    c = new Content("http://www.foo.com/",
                    "http://www.foo.com/",
                    "".getBytes("UTF8"),
                    "", p, conf);
    assertEquals("", c.getContentType());

    c = new Content("http://www.foo.com/",
                    "http://www.foo.com/",
                    "".getBytes("UTF8"),
                    null, p, conf);
    assertNull(c.getContentType());
  }
	
}

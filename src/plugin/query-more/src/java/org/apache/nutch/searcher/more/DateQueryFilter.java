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

package org.apache.nutch.searcher.more;

import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Query.Clause;
import org.apache.nutch.searcher.QueryFilter;
import org.apache.nutch.searcher.QueryException;

import org.apache.nutch.util.LogFormatter;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.index.Term;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.logging.Logger;

/**
 * Handles "date:" query clauses, causing them to search the field "date"
 * indexed by MoreIndexingFilter.java
 *
 * @author John Xing
 */
public class DateQueryFilter implements QueryFilter {

  public static final Logger LOG
    = LogFormatter.getLogger(DateQueryFilter.class.getName());

  private static final String FIELD_NAME = "date";

  // query syntax is defined as date:yyyymmdd-yyyymmdd
  private static final Pattern pattern = Pattern.compile("^(\\d{8})-(\\d{8})$");
    
  public BooleanQuery filter(Query input, BooleanQuery output)
    throws QueryException {

    // examine each clause in the Nutch query
    Clause[] clauses = input.getClauses();
    
    for (int i = 0; i <clauses.length; i++) { 
      Clause c = clauses[i];
      
      //skip if not date clauses
      if (!c.getField().equals(FIELD_NAME)) 
        continue;
            
      String x = c.getTerm().toString();
       
      Matcher matcher = pattern.matcher(x);
      if (!matcher.matches()) {
        LOG.warning("Wrong query syntax for field "+FIELD_NAME+":"+x);
        // or just ignore silently?
        return output;
      }

      // do it as lucene RangeQuery
      Term xLower = new Term(FIELD_NAME, matcher.group(1));
      Term xUpper = new Term(FIELD_NAME, matcher.group(2));

      // inclusive
      RangeQuery rangeQuery = new RangeQuery(xLower, xUpper, true);
          
      output.add(rangeQuery, c.isRequired(), c.isProhibited());
             
    }

    return output;
  }

}
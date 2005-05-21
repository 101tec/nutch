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

package org.apache.nutch.mapred;

import java.io.IOException;

import org.apache.nutch.io.Writable;
import org.apache.nutch.io.WritableComparable;

/** Default {@link MapRunnable} implementation.*/
public class MapRunner implements MapRunnable {
  private Mapper mapper;
  private Class inputKeyClass;
  private Class inputValueClass;

  public void configure(JobConf job) {
    mapper = (Mapper)job.newInstance(job.getMapperClass());
    inputKeyClass = job.getInputKeyClass();
    inputValueClass = job.getInputValueClass();
  }

  public void run(RecordReader input, OutputCollector output)
    throws IOException {
    while (true) {
      // allocate new key & value instances
      WritableComparable key = null;
      Writable value = null;
      try {
        key = (WritableComparable)inputKeyClass.newInstance();
        value = (Writable)inputValueClass.newInstance();
      } catch (Exception e) {
        throw new IOException(e.toString());
      }

      // read next key & value
      if (!input.next(key, value))
        return;

      // map pair to output
      mapper.map(key, value, output);
    }
  }

}
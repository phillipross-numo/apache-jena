/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.riot;

import java.io.InputStream ;

import org.openjena.atlas.lib.Sink ;
import org.openjena.atlas.web.ContentType ;

import com.hp.hpl.jena.sparql.util.Context ;

/** Interface to parsing processes that takes an input stream and emit T items.
 *  The "read" operation may be called repeatedly for a single Reader, with different
 *  arguments but calsl are not overlapping.
 */

public interface ReaderRIOT<T>
{
    public void read(InputStream in, String baseURI, ContentType ct, Sink<T> sink, Context context) ;
}

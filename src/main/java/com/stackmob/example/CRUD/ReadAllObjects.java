/**
 * Copyright 2012-2013 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackmob.example.CRUD;

import com.stackmob.core.DatastoreException;
import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.*;

import java.net.HttpURLConnection;
import java.util.*;

/**
 * This example will show a user how to write a custom code method
 * with one parameter `schema_name` that will query the specified schema
 * for all objects contained within it.
 */

public class ReadAllObjects implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "CRUD_Read_All_Objects";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("schema_name");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    LoggerService logger = serviceProvider.getLoggerService(ReadAllObjects.class);

    // I'll be using this map to print messages to console as feedback to the operation
    Map<String, List<SMObject>> feedback = new HashMap<String, List<SMObject>>();
    Map<String, String> errMap = new HashMap<String, String>();

    DataService ds = serviceProvider.getDataService();
    List<SMCondition> query = new ArrayList<SMCondition>();
    // We don't have to edit the query because we want to read ALL objects
    List<SMObject> results;

    String schema = request.getParams().get("schema_name");
    if (Util.hasNulls(schema)){
      return Util.badRequestResponse(errMap);
    }

    try {
      // Read objects from the schema that was passed in
      results = ds.readObjects(schema, query);

      if (results != null && results.size() > 0) {
        feedback.put(schema, results);
      }

    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}

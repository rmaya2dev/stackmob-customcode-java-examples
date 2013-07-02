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

import com.stackmob.core.InvalidSchemaException;
import com.stackmob.core.DatastoreException;
import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.example.Util;
import com.stackmob.sdkapi.SDKServiceProvider;
import com.stackmob.sdkapi.*;

import java.net.HttpURLConnection;
import java.util.*;

/**
 * This example will show a user how to write a custom code method
 * with two parameters `make` and `year` in order demonstrate how to
 * create a StackMob OR query as well as paginate the results.
 */

public class OrQuery implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "CRUD_Or_Query";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("make", "year");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    Map<String, List<SMObject>> feedback = new HashMap<String, List<SMObject>>();
    Map<String, String> errMap = new HashMap<String, String>();

    String make = request.getParams().get("make");
    String year = request.getParams().get("year");
    if (Util.hasNulls(make, year)){
      return Util.badRequestResponse(errMap);
    }

    // Make a new ResultFilter that starts at 0 and ends at 9 to paginate at every 10 results
    ResultFilters filters = new ResultFilters(0, 9, null, null);

    // Create the conditions that you'd like to match on your query
    List<SMCondition> orArguments = new ArrayList<SMCondition>();
    orArguments.add(new SMGreaterOrEqual("year", new SMInt(Long.parseLong(year))));
    orArguments.add(new SMEquals("make", new SMString(make)));

    SMOr orStatement = new SMOr(orArguments);

    List<SMCondition> query = new ArrayList<SMCondition>();
    DataService ds = serviceProvider.getDataService();
    List<SMObject> results;

    try {
      // add the OR statement (containing the conditions from orArguments) to our query
      query.add(orStatement);
      results = ds.readObjects("car", query , 0, filters);

      if (results != null && results.size() > 0) {
        feedback.put(make, results);
      }

    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}
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
 * with one parameter `make` that queries the `car` schema for all objects
 * that match the given make field, and then deletes them in bulk
 */

public class DeleteMultiple implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "CRUD_DeleteMultiple";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("make");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    Map<String, List<SMObject>> feedback = new HashMap<String, List<SMObject>>();
    Map<String, String> errMap = new HashMap<String, String>();

    List<SMCondition> query = new ArrayList<SMCondition>();
    DataService ds = serviceProvider.getDataService();
    List<SMObject> results;

    String make = request.getParams().get("make");

    if (Util.hasNulls(make)){
      return Util.badRequestResponse(errMap);
    }

    try {
      // Create a query condition to match all car objects to the `make` that was passed in
      query.add(new SMEquals("make", new SMString(make)));
      results = ds.readObjects("car", query); // Read all objects in `car` schema that match the query

      if (results != null && results.size() > 0) {
        feedback.put("Deleting", results); // To show what has been deleted
        for(SMObject smo : results) {
          SMString carID = (SMString) smo.getValue().get("car_id"); // Get the ID of each car
          ds.deleteObject("car", carID); // Finally the object gets deleted by ID.
        }
      }
    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}

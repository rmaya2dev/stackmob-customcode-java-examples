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
 * with one parameter that deletes the specified object from their schema
 * when given a unique ID.
 */

public class DeleteObject implements CustomCodeMethod {

  @Override
  public String getMethodName() {
    return "CRUD_Delete";
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("car_ID");
  }

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider) {
    LoggerService logger = serviceProvider.getLoggerService(DeleteObject.class);
    Map<String, SMObject> feedback = new HashMap<String, SMObject>();
    Map<String, String> errMap = new HashMap<String, String>();

    DataService ds = serviceProvider.getDataService();
    String carID = request.getParams().get("car_ID");

    if (Util.hasNulls(carID)){
      return Util.badRequestResponse(errMap);
    }

    try {
      /* In order to get the parameters (in this case car_ID)
       * from a DELETE operation, you must use request.getParams
       */
      ds.deleteObject("car", new SMString(carID));
    } catch (InvalidSchemaException ise) {
      return Util.internalErrorResponse("invalid_schema", ise, errMap);  // http 500 - internal server error
    } catch (DatastoreException dse) {
      return Util.internalErrorResponse("datastore_exception", dse, errMap);  // http 500 - internal server error
    }

    return new ResponseToProcess(HttpURLConnection.HTTP_OK, feedback);
  }

}

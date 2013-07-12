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

package com.stackmob.example;

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.rest.ProcessedAPIRequest;
import com.stackmob.core.rest.ResponseToProcess;
import com.stackmob.sdkapi.SDKServiceProvider;

import com.stackmob.sdkapi.http.HttpService;
import com.stackmob.sdkapi.http.request.HttpRequest;
import com.stackmob.sdkapi.http.request.GetRequest;
import com.stackmob.sdkapi.http.response.HttpResponse;
import com.stackmob.core.ServiceNotActivatedException;
import com.stackmob.sdkapi.http.exceptions.AccessDeniedException;
import com.stackmob.sdkapi.http.exceptions.TimeoutException;
import java.net.MalformedURLException;
import com.stackmob.sdkapi.http.request.PostRequest;
import com.stackmob.sdkapi.http.Header;
import com.stackmob.sdkapi.LoggerService;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.TwilioRestResponse;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;


public class TwilioToken implements CustomCodeMethod {

  //Create your Twilio Acct at twilio.com and enter 
  //Your accountsid and accesstoken below.
  public static final String accountsid = "AC4c5992cd16e20dc406f79f9225cdd39a";
  public static final String accesstoken = "15c473789b7c499153d6eeab505e63c4";
    
  @Override
  public String getMethodName() { 
    return "twilio_token"; 
  }

  @Override
  public List<String> getParams() {
    return Arrays.asList("tophonenumber","message");
  }  

  @Override
  public ResponseToProcess execute(ProcessedAPIRequest request, SDKServiceProvider serviceProvider){
    int responseCode = 0;
    String responseBody = "";

    LoggerService logger = serviceProvider.getLoggerService(TwilioToken.class);
      
    // TO phonenumber should be YOUR cel phone
    String toPhoneNumber = request.getParams().get("tophonenumber");
      
    //  FROM phonenumber should be one create in the twilio dashboard at twilio.com
    String fromPhoneNumber = "18572541790";
      
    //  text message you want to send
    String message = request.getParams().get("message");

    if (toPhoneNumber == null || toPhoneNumber.isEmpty()) {
      logger.error("Missing phone number");
    }
      
    if (message == null || message.isEmpty()) {
      logger.error("Missing message");
    }

    StringBuilder body = new StringBuilder();
    
	Map<String, Object> map = new HashMap<String, Object>();
    
    try{
    
    TwilioRestClient client = new TwilioRestClient(accountsid, accesstoken);
    Account mainAccount = client.getAccount();
    
	// Send an sms
	SmsFactory smsFactory = mainAccount.getSmsFactory();
	Map<String, String> smsParams = new HashMap<String, String>();
	smsParams.put("To", toPhoneNumber); // Replace with a valid phone number
	smsParams.put("From", fromPhoneNumber); // Replace with a valid phone
												// number in your account
	smsParams.put("Body", message);
	smsFactory.create(smsParams);

	TwilioRestResponse resp = client.request("/2010-04-01/Accounts", "GET",
			null);
	
	responseCode = resp.getHttpStatus();

    map.put("response_body", resp.getResponseText());
    
    }catch(TwilioRestException e)
    {
        logger.error(e.getMessage(), e);
        responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
        responseBody = e.getMessage();
        map.put("response_body", responseBody);
    }
	

     
    return new ResponseToProcess(responseCode, map);
  }
}

/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.scim.example.user;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;
import org.scim.example.utils.SCIMSamplesUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class DeleteUser {
    
    public static void main(String[] args) {

        try {
            //load sample configuration
            SCIMSamplesUtils.loadConfiguration();
            //set the keystore
            SCIMSamplesUtils.setKeyStore();

            String userId = getSCIMIdOfUser(SCIMSamplesUtils.userNameToDeleteUser);

            String url = SCIMSamplesUtils.userEndpointURL + "/" + userId;
            //now send the delete request.
            DeleteMethod deleteMethod = new DeleteMethod(url);
            //add authorization header
            String authHeader = SCIMSamplesUtils.getAuthorizationHeader();
            deleteMethod.addRequestHeader(SCIMConstants.AUTHORIZATION_HEADER, authHeader);
            
            HttpClient httpDeleteClient = new HttpClient();
            int deleteResponseStatus = httpDeleteClient.executeMethod(deleteMethod);
            String deleteResponse = deleteMethod.getResponseBodyAsString();

            System.out.println("");
            System.out.println("");
            System.out.println("/******SCIM user delete response status: " + deleteResponseStatus);
            System.out.println("SCIM user delete response data: " + deleteResponse + "******/");
            System.out.println("");

        } catch (CharonException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSCIMIdOfUser(String userName) throws Exception {
        String userId = null;
        try {
            //create http client
            HttpClient httpFilterUserClient = new HttpClient();
            //create get method for filtering
            GetMethod getMethod = new GetMethod(SCIMSamplesUtils.userEndpointURL);
            //add authorization header
            String authHeader = SCIMSamplesUtils.getAuthorizationHeader();
            getMethod.addRequestHeader(SCIMConstants.AUTHORIZATION_HEADER, authHeader);
            //get corresponding userIds
            String filter = SCIMSamplesUtils.USER_FILTER + userName;
            getMethod.setQueryString(filter);
            int responseCode = httpFilterUserClient.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();

            SCIMClient scimClient = new SCIMClient();
            //check for success of the response
            if (scimClient.evaluateResponseStatus(responseCode)) {
                ListedResource listedUserResource =
                        scimClient.decodeSCIMResponseWithListedResource(
                                response, SCIMConstants.identifyFormat(SCIMSamplesUtils.CONTENT_TYPE),
                                SCIMConstants.USER_INT);
                List<SCIMObject> filteredUsers = listedUserResource.getScimObjects();
                for (SCIMObject filteredUser : filteredUsers) {
                    //we expect only one result here
                    userId = ((User) filteredUser).getId();
                }

            }
        } catch (IOException e) {
            throw new Exception("Error in obtaining the SCIM Id for user: " + userName);
        } catch (CharonException e) {
            throw new Exception("Error in obtaining the SCIM Id for user: " + userName);
        }
        return userId;
    }

}

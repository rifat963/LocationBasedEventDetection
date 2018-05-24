/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package unipv.irma.opentripplanner.android.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptPsw {
	
	// generate SHA
	public static String setShaPsw(String password) {
		MessageDigest digest=null;
    	String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes());

            hash = bytesToHexString(digest.digest());

        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		return hash;
	}
	
	// utility function for sha
	private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

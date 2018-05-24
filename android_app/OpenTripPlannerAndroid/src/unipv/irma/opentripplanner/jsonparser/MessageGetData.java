/**
 * 
 */
package unipv.irma.opentripplanner.jsonparser;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author rifat
 *
 */
public class MessageGetData {
	private MessageMap message;


    public MessageMap JSONdataGet(String JSOND){
     
    try {
             ObjectMapper mapper = new ObjectMapper();
             mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
             message = mapper.readValue(JSOND, MessageMap.class);
     	}catch(Exception e){
     }

        return message;
    }

}

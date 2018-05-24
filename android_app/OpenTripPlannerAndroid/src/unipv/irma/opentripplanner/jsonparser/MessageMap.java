/**
 * 
 */
package unipv.irma.opentripplanner.jsonparser;

import java.util.ArrayList;

/**
 * @author rifat
 *
 */
public class MessageMap {
	
	
	
	private ArrayList<MessageMap> messageMap;  
	private int id;
       /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	private Double lat;
       /**
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	public Double getLng() {
		return lng;
	}

	/**
	 * @param lng the lng to set
	 */
	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	private Double lng;
       private String name;
       private String Description;
       private String type;       
	    /**
	 * @return the messageMap
	 */
	public ArrayList<MessageMap> getMessageMap() {
		return messageMap;
	}

	/**
	 * @param messageMap the messageMap to set
	 */
	public void setMessageMap(ArrayList<MessageMap> messageMap) {
		this.messageMap = messageMap;
	}

}

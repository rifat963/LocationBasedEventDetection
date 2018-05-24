package unipv.irma.opentripplanner.android.model;

public class TripMonitoredModel {
	private int id;
	private int user_id;
	private String start_location;
	private String end_location;
	private String start_lat;
	private String start_lon;
	private String end_lat;
	private String end_lon;
	private String date;
	private String time;
	private String timestamp;
	private int count;

	private static TripMonitoredModel instance = null;

	public TripMonitoredModel() {
	}

	public static TripMonitoredModel getInstance() {
		if (instance == null) {
			instance = new TripMonitoredModel();
		}
		return instance;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getStart_location() {
		return start_location;
	}

	public void setStart_location(String start_location) {
		this.start_location = start_location;
	}

	public String getEnd_location() {
		return end_location;
	}

	public void setEnd_location(String end_location) {
		this.end_location = end_location;
	}

	public String getStart_lat() {
		return start_lat;
	}

	public void setStart_lat(String start_lat) {
		this.start_lat = start_lat;
	}

	public String getStart_lon() {
		return start_lon;
	}

	public void setStart_lon(String start_lon) {
		this.start_lon = start_lon;
	}

	public String getEnd_lat() {
		return end_lat;
	}

	public void setEnd_lat(String end_lat) {
		this.end_lat = end_lat;
	}

	public String getEnd_lon() {
		return end_lon;
	}

	public void setEnd_lon(String end_lon) {
		this.end_lon = end_lon;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}

package unipv.irma.opentripplanner.android.model;

import java.util.List;

import org.opentripplanner.routing.core.TraverseMode;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowRouteModel {
	MarkerOptions markerOptions;
	TraverseMode traverseMode;
	List<LatLng> points;
	int stepIndex;
	float scaleFator;

	/**
	 * @return the markerOptions
	 */
	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	/**
	 * @param markerOptions
	 *            the markerOptions to set
	 */
	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}

	/**
	 * @return the traverseMode
	 */
	public TraverseMode getTraverseMode() {
		return traverseMode;
	}

	/**
	 * @param traverseMode
	 *            the traverseMode to set
	 */
	public void setTraverseMode(TraverseMode traverseMode) {
		this.traverseMode = traverseMode;
	}

	/**
	 * @return the points
	 */
	public List<LatLng> getPoints() {
		return points;
	}

	/**
	 * @param points
	 *            the points to set
	 */
	public void setPoints(List<LatLng> points) {
		this.points = points;
	}

	/**
	 * @return the stepIndex
	 */
	public int getStepIndex() {
		return stepIndex;
	}

	/**
	 * @param stepIndex
	 *            the stepIndex to set
	 */
	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	/**
	 * @return the scaleFator
	 */
	public float getScaleFator() {
		return scaleFator;
	}

	/**
	 * @param scaleFator
	 *            the scaleFator to set
	 */
	public void setScaleFator(float scaleFator) {
		this.scaleFator = scaleFator;
	}

}

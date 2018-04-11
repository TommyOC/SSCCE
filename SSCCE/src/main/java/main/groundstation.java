package main;

import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.TopocentricFrame;

public class groundstation {
	double latitude;
	double longitude;
	double altitude;
	double minEleveation;
	TopocentricFrame frame;
	String name = "gs";
	
	public groundstation(double lat, double lon, double alt, double minEle) {
		this.altitude = alt;
		this.latitude = lat;
		this.longitude = lon;
		this.minEleveation = minEle;
	}
	
	public GeodeticPoint getLocation() {
		return new GeodeticPoint(latitude, longitude, altitude);
	}
	
	public void initFrame(BodyShape earthBody) {
		this.frame = new TopocentricFrame(earthBody, new GeodeticPoint(latitude, longitude, altitude), name);
	}
	
	public TopocentricFrame getFrame() {
		return frame;
	}
}

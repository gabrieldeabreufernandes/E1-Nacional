package com.actia.mexico.gpsgm_168.gps;

public class PointGPS {
	
	private String type;
	private String UTCtime;
	private String status;
	private double latitude;
	private String NS;
	private double longitude;
	private String EW;
	private double speedKnots;
	private double heading;
	private String date;
	private String magneticVar;
	private String declination;
	private String modeAndChecksum;
	
	public PointGPS(){
		
	}
	
	public PointGPS(String _type,
					String _UTCtime,
					String _status,
					double _latitude,
					String _NS,
					double _longitude,
					String _EW,
					double _speedKnot,
					double _heading,
					String _date,
					String _magneticVar,
					String _declination,
					String _modeAndChecksum
					){
		
		this.type = _type;
		this.UTCtime = _UTCtime;
		this.status = _status;
		this.latitude = _latitude;
		this.NS = _NS;
		this.longitude = _longitude;
		this.EW = _EW;
		this.speedKnots = _speedKnot;
		this.heading = _heading;
		this.date = _date;
		this.magneticVar = _magneticVar;
		this.declination = _declination;
		this.modeAndChecksum = _modeAndChecksum;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUTCtime() {
		return UTCtime;
	}
	public void setUTCtime(String uTCtime) {
		UTCtime = uTCtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getNS() {
		return NS;
	}
	public void setNS(String nS) {
		NS = nS;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getEW() {
		return EW;
	}
	public void setEW(String eW) {
		EW = eW;
	}
	public double getSpeedKnots() {
		return speedKnots;
	}
	public void setSpeedKnots(double speedKnots) {
		this.speedKnots = speedKnots;
	}
	public double getHeading() {
		return heading;
	}
	public void setHeading(double heading) {
		this.heading = heading;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMagneticVar() {
		return magneticVar;
	}
	public void setMagneticVar(String magneticVar) {
		this.magneticVar = magneticVar;
	}
	public String getDeclination() {
		return declination;
	}
	public void setDeclination(String declination) {
		this.declination = declination;
	}
	public String getModeAndChecksum() {
		return modeAndChecksum;
	}
	public void setModeAndChecksum(String modeAndChecksum) {
		this.modeAndChecksum = modeAndChecksum;
	}
	
	@Override
	public String toString() {
		String str =
		type + "," +
		UTCtime + "," +
		status + "," +
		latitude + "," +
		NS + "," +
		longitude + "," +
		EW + "," +
		speedKnots + "," +
		heading + "," +
		date + "," +
		magneticVar + "," +
		declination + "," +
		modeAndChecksum;
		
		return str;
	}
	
//	public PointGPS(String type2, String uTCtime2, String status2,
//			double latitude2, String nS2, double longitude2, String eW2,
//			double speedKnots2, double heading2, String date2,
//			String magneticVar2, String declination2, String modeAndChecksum2) {
//		
//		this.type = type2;
//		this.UTCtime = uTCtime2;
//		this.status = status2;
//		this.latitude = latitude2;
//		this.NS = nS2;
//		this.longitude = longitude2;
//		this.EW = eW2;
//		this.speedKnots = speedKnots2;
//		this.heading = heading2;
//		this.date = date2;
//		this.magneticVar = magneticVar2;
//		this.declination = declination2;
//		this.modeAndChecksum = modeAndChecksum2;
//	}
//	public String type;
//	public String UTCtime;
//	public String status;
//	public double latitude;
//	public String NS;
//	public double longitude;
//	public String EW;
//	public double speedKnots;
//	public double heading;
//	public String date;
//	public String magneticVar;
//	public String declination;
//	public String modeAndChecksum;
	
}

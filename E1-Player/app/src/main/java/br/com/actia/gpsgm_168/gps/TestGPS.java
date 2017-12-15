package br.com.actia.gpsgm_168.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class TestGPS {
	private static PL2303Driver mSerial;
	private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
	
	//Esta variable debe de tener el nombre del paquete de la aplicacion
	private static final String ACTION_USB_PERMISSION = "com.actia.e1transregio.USB_PERMISSION";
	private static String TAG = "GpsUsbUtility";
	private static final boolean SHOW_DEBUG = true;
	private static final String NMEA_MESSAGETYPE_RMC=  "$GPRMC";
	private static final int INT_BUFFERVAL = 128;
	boolean isConnected = true;
	private PointGPS globalDate;
	
	public PointGPS getGPSData(){
				
		PointGPS point = null;
		
		if(mSerial!=null){
			if(verifyConnection()){
					openUsbSerial();
					point = getDataFromGPSAntenna();
				
				Log.d(TAG, "ENDING GPS DATA");
				}else{
					point = null;
					isConnected = false;
				}
			}
		return point;
	}
	
	private PointGPS getDataFromGPSAntenna(){
		
		Log.d(TAG, "starting getDateFromGPSAntenna");
		
		PointGPS point = null;
		String message = readDataFromSerial();

		if(message != null)
			if(!message.equals("")){
				String RMC = getRMCMessage(message);
				if(RMC!=null){
					point =splitData(RMC);
//					if(point != null)
//						if(point.status.equals("A"))
//						Log.d("Coords", String.valueOf(point.latitude) + 
//								String.valueOf(point.longitude));
//						if(point.latitude != 0)
//					Log.d("Coords", NMEAToLatLon(point.latitude, 
//							point.NS, point.longitude, point.EW));
				}
			}
		return point;
	}
	
	public void initUSB(Context context){
		
		// get service
		mSerial = new PL2303Driver((UsbManager) context.getSystemService(Context.USB_SERVICE),
				context, ACTION_USB_PERMISSION); 
		
		// check USB host function.
		if (!mSerial.PL2303USBFeatureSupported()) {
			Log.d(TAG, "No Support USB host API");
			mSerial = null;
		}
	}
	
	public void endUSB(){
		if(mSerial != null)
			mSerial.end();
	}
	
	public boolean verifyConnection(){
		//if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))        
			if(!mSerial.isConnected()) {
				if (SHOW_DEBUG) {
					Log.d(TAG, "New instance : " + mSerial);
				}

				if( !mSerial.enumerate() ) {

					Log.d(TAG, "no more devices found");
					return false;
				} else {
					Log.d(TAG, "onResume:enumerate succeeded!");
					return true;
				}    		 
			} 
			else{
				Log.d(TAG, "Leave onResume");
				return true;
			}
	}
	
	public boolean openUsbSerial() {
		Log.d(TAG, "Enter  openUsbSerial");


		if(null==mSerial)
			return false;   	 

		if (mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "openUsbSerial : isConnected ");
			}
			String str = "9600";
			int baudRate= Integer.parseInt(str);
			switch (baudRate) {
			case 9600:
				mBaudrate = PL2303Driver.BaudRate.B9600;
				break;
			case 19200:
				mBaudrate =PL2303Driver.BaudRate.B19200;
				break;
			case 115200:
				mBaudrate =PL2303Driver.BaudRate.B115200;
				break;
			default:
				mBaudrate =PL2303Driver.BaudRate.B9600;
				break;
			}   		            
			Log.d(TAG, "baudRate:" + baudRate);
			// if (!mSerial.InitByBaudRate(mBaudrate)) {
				if (!mSerial.InitByBaudRate(mBaudrate,700)) {
					if(!mSerial.PL2303Device_IsHasPermission()) {
						Log.d(TAG, "cannot open, maybe no permission");
					}

					if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
//						Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "cannot open, "
								+ "maybe this chip has no support, please use "
								+ "PL2303HXD / RA / EA chip.");
					}
				} else {        	
					Log.d(TAG, "Connected: ");
				}
				return true;
		}//isConnected
		else{
			Log.d(TAG, "Not Connected: ");
			return false;
		}

	}//openUsbSerial
	
	private String readDataFromSerial() {

		String message = null;
		int len;
//		byte[] rbuf = new byte[4096];
		byte[] rbuf = new byte[INT_BUFFERVAL];
//		byte[] rbuf = new byte[128];
		StringBuffer sbHex=new StringBuffer();

		Log.d(TAG, "Enter readDataFromSerial");

		if(null==mSerial)
			return null;        

		if(!mSerial.isConnected()) 
			return "NOT CONNECTED";

		len = mSerial.read(rbuf);
		if(len<0) {
			Log.d(TAG, "Fail to bulkTransfer(read data)");
			return "Fail to bulkTransfer(read data)";
		}

		if (len > 0) {        	
			if (SHOW_DEBUG) {
				Log.d(TAG, "read len : " + len);
			}                
			//rbuf[len] = 0;
			for (int j = 0; j < len; j++) {            	   
				//String temp=Integer.toHexString(rbuf[j]&0x000000FF);
				//Log.i(TAG, "str_rbuf["+j+"]="+temp);
				//int decimal = Integer.parseInt(temp, 16);
				//Log.i(TAG, "dec["+j+"]="+decimal);
				//sbHex.append((char)decimal);
				//sbHex.append(temp);
				sbHex.append((char) (rbuf[j]&0x000000FF));
			}    
			
			message = sbHex.toString();   
		}
		else {     	
			if (SHOW_DEBUG) {
				Log.d(TAG, "read len : 0 ");
			}
			return "EMPTY DATA";
		}

//		try {
//			Thread.sleep(2000); //ESPERAR 2 Segundos para que le de tiempo al buffer de vaciarse
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		Log.d(TAG, "Leave readDataFromSerial");
		
		return message;	
	}//readDataFromSerial
	
	private String getRMCMessage(String message){
		
		String RMC =null;
		String[] log = message.split("\n");

		for(int j=0; j<log.length; j++){
			if(log[j].contains(NMEA_MESSAGETYPE_RMC)){
				RMC = log[j];
			break;	
			}
				
		}
		
		return RMC;
	}
	
	/**
	 * Convierte un valor NMEA degree a Decimal Degrees 
	 * @param value
	 * @return
	 */
	private double NMEAToDd(double value) {
        double result = 0;
        if(value != 0){
        double degValue = value / 100;
        int degrees = (int) degValue;
        double decMinutesSeconds = ((degValue - degrees)) / .60;
        double aux = degrees + decMinutesSeconds;
        result = aux;
        }
        
        return result;
    }
	
	/**
	 * Convierte una latitud con longitud obtenida del GPRMC formato NMEA a LatLon
	 * @param lat
	 * @param NS
	 * @param lon
	 * @param EW
	 * @return
	 */
	public String NMEAToLatLon(double lat, String NS, double lon, String EW){
		String latlon = null;
		
		if(lat != 0 && lon != 0){
			
		}else{
			return null;
		}
		
		lat = NMEAToDd(lat);
		lon = NMEAToDd(lon);
		
		if(NS.equals("S")){
			lat = lat * -1;
		}
		if(EW.equals("W")){
			lon = lon * -1;
		}
		
		latlon = String.valueOf(lat) + "," + String.valueOf(lon);
		
		return latlon;
		
	}
	
	private PointGPS splitData(String RMC) {
		
		Log.d(TAG, "parsing: " + RMC);
		
		PointGPS point = null;
		
			String type = "";
			String UTCtime = "";
			String status = "";
			double latitude ;
			String NS = "";
			double longitude ;
			String EW = "";
			double speedKnots ;
			double heading = 0 ;
			String date = "";
			String magneticVar = "";
			String declination = "";
			String modeAndChecksum = "";
			
			String[] data = RMC.split(",");
			
			if(data.length == 13){
			 type = data[0];
			 UTCtime = data[1];
			 status = data[2];
			 latitude = validateDouble(data[3]);
			 NS = data[4];
			 longitude = validateDouble(data[5]);
			 EW = data[6];
			 speedKnots = validateDouble(data[7]);
			 heading = validateDouble(data[8]);
			 date = data[9];
			 magneticVar = data[10];
			 declination = data[11];
			 modeAndChecksum = data[12];
			 
			 point = new PointGPS(type, UTCtime, status, latitude, NS, 
					 longitude, EW, speedKnots, heading, date, magneticVar, 
					 declination, modeAndChecksum);

//				try {
//					if(!point.getDate().equals(""))
//					fec = ConversionDate(point.getDate(),point.getUTCtime());
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
			}
			
		return point;
	}
	
	private double validateDouble(String stringNumber){
		double aux = 0;
		
		if(!stringNumber.equals(""))
			aux = Double.valueOf(stringNumber);
		
		return aux;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Date ConversionDate(String yearP, String timeP ) throws ParseException {
		
		String HH = (String) timeP.subSequence(0, 2);
		String mm = (String) timeP.subSequence(2, 4);
		String ss = (String) timeP.subSequence(4, 6);
		
		String dd = (String) yearP.subSequence(0, 2);
		String MM = (String) yearP.subSequence(2, 4);
		String YY = (String) yearP.subSequence(4, 6);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		String strdate = null;
		String dateInString = dd + "/" + MM+ "/" + "20"+YY + " " + HH + ":" + mm + ":" + ss;
	 	Date theResult = null;
        
	 	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	 	Date date = null;
	 	try {
	 	    //Setteando timezone a UTC, ya que asi llegan los datos del GPS
	 	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	 	date = sdf.parse(dateInString);
	 	} catch (ParseException e) {
	 	e.printStackTrace();
	 	}
	 	    //Setteando timezone especificamente
	 	sdf.setTimeZone(TimeZone.getTimeZone("Mexico/General"));
	 	String sDate =  sdf.format(date);
	 	theResult = formatter.parse(sDate);
	 	
		return theResult;
	}
	
	public PointGPS getData(){
		return globalDate;
	}
	
}

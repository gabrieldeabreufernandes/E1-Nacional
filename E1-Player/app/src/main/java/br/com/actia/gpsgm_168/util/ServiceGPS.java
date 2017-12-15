package br.com.actia.gpsgm_168.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.actia.mexico.gpsgm_168.gps.GpsUsbAsync;
import com.actia.mexico.gpsgm_168.gps.PointGPS;
import com.actia.mexico.gpsgm_168.gps.TestGPS;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ServiceGPS extends Service implements OnGPSListener{

	private final IBinder mBinder = new ServiceBinder();
//	private Handler gpsHandler = new Handler();
	boolean bound=false;
	
    private  volatile Looper mServiceLooper;
    private  volatile  ServiceHandler mServiceHandler;
    private PointGPS globalDate = null;

	private  final  class  ServiceHandler extends Handler {
        public  ServiceHandler(Looper looper) {
            super(looper); 
        } 
         
        @Override
        public  void  handleMessage(Message msg) {
//            Bundle arguments = (Bundle)msg.obj; 
         
//            String txt = arguments.getString("name"); 
             
//            Log.i("ServiceStartArguments", "Message: " + msg + ", " 
//                    + arguments.getString("name")); 
             
//            showNotification(); 
 
    		
    				 final TestGPS gps = new TestGPS();
    				
    				 gps.initUSB(getApplicationContext());
    				 
    				 boolean isConnected = gps.verifyConnection();
    	            
    				if(isConnected){
	    				
	    	            final ScheduledExecutorService executor = Executors
	    	    	            .newScheduledThreadPool(1);
	    	            
	    	            executor.scheduleAtFixedRate(new Runnable() {
	    	    			
	    	    			@Override
	    	    			public void run() {
	    	    				synchronized (this) {
	    	    					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
	    	    					if(globalDate != null){
	    	    					executor.shutdownNow();	
	    	    					gps.endUSB();
	    	    					Date finalDate = convertDate(globalDate);
//	    	    					actualizarFechaSistema(finalDate);
	    	    					showNotification(globalDate.toString());
	//        							globalDate = gps.getGPSData(); // Prueba de gps infinito
	    	    					}else{
	    							globalDate = gps.getGPSData();
	    	    					}
	    						}
	    	    			}
	    	    		}, 0, 2000, TimeUnit.MILLISECONDS);
    				} //Fin conectado
    				
            Log.i("ServiceStartArguments", "Done with #" + msg.arg1);
            stopSelf(msg.arg1); 
        } 
 
    }; 
    
    private  void  showNotification(String data) {


		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		intent.setAction("com.actia.usb");
		intent.putExtra("DATA",data.toString());
		sendBroadcast(intent);
		Log.v("Service GPS", "sending Brodcast");
	  
    			
    } 
    
    
    
    private Date convertDate(PointGPS globalDate) {
		Date gpsDate = null;

    	if(globalDate != null)
    		{
    		try {
				if(globalDate.getDate().length()>0 && globalDate.getUTCtime().length()>0)
				gpsDate = GpsUsbAsync.ConversionDate(globalDate.getDate(), globalDate.getUTCtime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if(gpsDate != null)
				Log.e("FECHA", gpsDate.toString());
    		}
		return gpsDate;
    }
	
	
	public class ServiceBinder extends Binder {
		public ServiceGPS getService(){
			return ServiceGPS.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		if(!bound){
			
			HandlerThread thread = new HandlerThread("Thread name", android.os.Process.THREAD_PRIORITY_LOWEST);
			thread.start();
			mServiceLooper = thread.getLooper();
	        mServiceHandler = new  ServiceHandler(mServiceLooper); 
						
			}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ServiceStartArguments",
				"Starting #" + startId + ": " + intent.getExtras());
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId; 
        msg.arg2 = flags; 
        msg.obj = intent.getExtras(); 
        mServiceHandler.sendMessage(msg); 
        
        
        Log.i("ServiceStartArguments", "Sending: " + msg);
        return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mServiceLooper.quit();
	}

	@Override
	public void OnGPSListenerComplete(Date gpsDate) {
		if(gpsDate != null){
			actualizarFechaSistema(gpsDate);
		}else{
			Log.e("SERVICEGPS", "No se actualizo la fecha");
		}
	}
	
	
	public void actualizarFechaSistema(Date date){
		try{			
			setTime(date.getTime());
		}catch(Exception e){
			e.printStackTrace();
			Log.e("GETINITDATE", "FECHA NO ACTUALIZADA");
		}
		Log.d("GETINITDATE", "FECHA ACTUALIZADA");
	}
	
	public void setTime(long time) {
	    if (ShellInterface.isSuAvailable()) {
	      ShellInterface.runCommand("chmod 666 /dev/alarm");
	      SystemClock.setCurrentTimeMillis(time);
	      ShellInterface.runCommand("chmod 664 /dev/alarm");
	    }
	  }

	
//	class MyHandlerThread extends HandlerThread {
//		 
//	    private Handler mHandler;
//	 
//	    public MyHandlerThread() {
//	        super("MyHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
//	    }
//	 
//	    @Override
//	    protected void onLooperPrepared() {
//	        super.onLooperPrepared();
//	 
//	        mHandler = new Handler(getLooper()) {
//	            @Override
//	            public void handleMessage(Message msg) {
//	                switch (msg.what) {
//	                    case 1:
//	                        Log.e("SERVICE GPS", "Mensaje GPS");
//	                        break;
//	                    case 2:
//	                        // Handle message
//	                        break;
//	                }
//	            }
//	        };
//	    }
//	 
//	    public void taskOne() {
//	        mHandler.sendEmptyMessage(1);
//	    }
//	 
//	    public void taskTwo() {
//	        mHandler.sendEmptyMessage(2);
//	    }
//	     
//	}
}

package br.com.actia.e1player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.actia.model.Banner;
import br.com.actia.model.Configuration;
import br.com.actia.model.GpsPoint;
import br.com.actia.model.ItemBusStopAdapter2;
import br.com.actia.model.MediaLog;
import br.com.actia.model.MediaLoger;
import br.com.actia.model.RouteWithList;
import br.com.actia.model.Video;
import br.com.actia.model.RSS;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndImage;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

public class PlayerActivity extends Activity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final int UPDATE_INTERVAL            = 1000; //1 second
    private static final int FASTEST_INTERVAL           = 500; //500 milliseconds
    private static final long ONE_MIN                   = 1000 * 60;
    private static final long TWO_MIN                   = ONE_MIN * 1;
    private static final float MIN_ACCURACY             = 15.0f;
    private static final float MIN_LAST_READ_ACCURACY   = 100.0f; //500.0f
    private static final float BUSSTOP_START_ACCURACY   = 20.0f; //75.0f
    private static final float BUSSTOP_START_ALERT     = 40.0f;
    private static final String IT_ROUTE_NAME	= "route";
    private static final String IT_CONFIG_OBJ	= "config";
    private static final String IT_GPS_POINT	= "gpsPoint";
    private static final int IT_REQUEST_STATUS	= 1;
    private static final String ROUTE_SUFIX	    = ".json";
    private static final int RETURN_TIME = 800;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mBestReading;

    //private ListView lstViewBusStop;
    private ExpandableListView lstViewBusStop;
    private TextView txtRouteName;
    private LinearLayout frameVideo;
    private VideoView videoView;
    private ImageView imageView;
    private LinearLayout rssView;

    private List<Video> lstVideos;
    private List<Banner> lstBanners;
    private List<GpsPoint> lstGpsPoint;
    private List<RSS> lstRSSFromRoute;
    private RouteWithList routeWL = null;
    private Configuration config = null;

    private int videoIndex;
    private int bannerIndex;
    private int rssIndex;
    private boolean bannerTurn;
    private int currentTime;
    //private boolean alertBusStop = false;
    //private boolean BusStop = false;

    //busStopBanner
    private ImageView busStopImage;
    private TextView busStopTxt;
    private LinearLayout busStopAlertLayout;

    private ScheduledExecutorService scheduleTaskExecutorBanner;
    private ScheduledExecutorService scheduleTaskExecutorRSS;
    private ScheduledExecutorService scheduleTaskExecutorGeneral;

    //rss
    private List<SyndEntry> lstRSS = null;
    private TextView txtRssTitle = null;
    private TextView txtRssContent = null;
    private TextView txtRssDate = null;
    private ImageView rssImage = null;

    private MediaLoger mediaLoger = null;

    private GpsPoint currentGpsPoint = null;
    private Drawable rssImageDrawable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize GPS
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            // Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //Uri uri = Uri.parse("market://details?id=" + getPackageName());
            //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(intent);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.titleDialogGooglePlayServicesDownload));
            alertDialog.setMessage(getString(R.string.messageDialogGooglePlayServicesDownload));

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String packageName = "com.google.android.gms";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
            });

            alertDialog.show();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Get references
        setContentView(R.layout.activity_player);
        lstViewBusStop = (ExpandableListView) findViewById(R.id.listRoute);
        txtRouteName = (TextView) findViewById(R.id.txtRouteName);
        frameVideo = (LinearLayout) findViewById(R.id.frameVideo);
        videoView = (VideoView) findViewById(R.id.videoView);
        imageView = (ImageView) findViewById(R.id.bannerView);

        rssView = (LinearLayout) findViewById(R.id.rssView);
        txtRssTitle = (TextView) findViewById(R.id.txtTitle);
        txtRssContent = (TextView) findViewById(R.id.txtContent);
        txtRssDate = (TextView) findViewById(R.id.txtDate);
        rssImage = (ImageView) findViewById(R.id.rssImage);

        busStopAlertLayout = (LinearLayout) findViewById(R.id.busStopAlertLayout);
        busStopImage = (ImageView) findViewById(R.id.imageView2);
        busStopTxt = (TextView) findViewById(R.id.textView);

        // Get the route from intent
        Intent intent = getIntent();
        String routeName = intent.getStringExtra(IT_ROUTE_NAME);
        txtRouteName.setText(routeName);
        config = (Configuration) intent.getSerializableExtra(IT_CONFIG_OBJ);

        logInitialize();
        loadRouteWL(routeName);
        crateBusStopList();
        startVideoPlayer();
        startBannerPlayer();

        if (savedInstanceState != null) {
            rssIndex = savedInstanceState.getInt("rssIndex");
            bannerIndex = savedInstanceState.getInt("bannerIndex");
        }
    }

    public void showBusStopAlert(GpsPoint gpsPoint) {
        shutdownTaskExecutors();

        //bannerView hide
        imageView.setVisibility(View.GONE);

        //RSSView hide
        rssView.setVisibility(View.GONE);

        //init elements busStopAlert
        busStopTxt.setText(getString(R.string.nextBusStop) + " " + gpsPoint.getName());
        busStopTxt.setTextColor(Color.BLACK);
        busStopTxt.setTextSize(40);

        busStopImage.setImageResource(R.drawable.busstop);

        busStopAlertLayout.setVisibility(View.VISIBLE);
    }

    private void shutdownTaskExecutors(){
        if(scheduleTaskExecutorGeneral != null && !scheduleTaskExecutorGeneral.isShutdown()) {
            scheduleTaskExecutorGeneral.shutdownNow();
        }

        if(scheduleTaskExecutorBanner != null && !scheduleTaskExecutorBanner.isShutdown()) {
            scheduleTaskExecutorBanner.shutdownNow();
        }

        if(scheduleTaskExecutorRSS != null && !scheduleTaskExecutorRSS.isShutdown()) {
            scheduleTaskExecutorRSS.shutdownNow();
        }
    }

    void hideBusStopAlert()
    {
        //Show Banner View
        imageView.setVisibility(View.VISIBLE);
        //Show RSS View
        rssView.setVisibility(View.VISIBLE);
        //Gone BusStopAlert View
        busStopAlertLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        shutdownTaskExecutors();

        videoView.pause();
        currentTime = videoView.getCurrentPosition() - RETURN_TIME;

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("ON RESUME");

        //Reset video to your current state before interrupt
        rebuildVideoView();
        videoView.seekTo(currentTime);
        videoView.start();

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,);
        if(mGoogleApiClient != null) {
            System.out.println("mGoogleApiClient.connect();");
            mGoogleApiClient.connect();
        }

        hideBusStopAlert();

        if(scheduleTaskExecutorGeneral != null && scheduleTaskExecutorGeneral.isShutdown()){
            //readFeed(routeWL.getListBanner().getListRss());
            List<String> rssUrls = getListRssUrls();
            readFeed(rssUrls);
            startExecutors();
        }

        requestFocusOut();
    }

/*    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }

    /**
     * Get return from StopAlertActivity for remove gpsPoint from list
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && currentGpsPoint != null) {
            Iterator<GpsPoint> it = lstGpsPoint.iterator();
            GpsPoint gpsPoint;

            while (it.hasNext()) {
                gpsPoint = it.next();

                if (gpsPoint == null || gpsPoint.getName().equals(currentGpsPoint.getName())) {
                    it.remove();
                    currentGpsPoint = null;

                    //reset bus stop list
                    if(it.hasNext())
                        crateBusStopList();
                    break;
                } else {
                    //remove past GPS Points
                    it.remove();
                }
            }

            //Return from the last GpsPoint?
            if (lstGpsPoint.size() < 1) {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//############# GPS METHODS #############
    @Override
    public void onLocationChanged(Location location) {
        if(mBestReading != null) {
            Log.d("LOGS", "ACCURACY = " + location.getAccuracy() + "  BEST = " + mBestReading.getAccuracy());
            Log.d("LOGS", "DISTANCIA = " + mBestReading.distanceTo(location));
            Log.d("LOGS", "TEMPO = " + (mBestReading.getTime() - location.getTime())); //tempo em segundos
            Log.d("LOGS", "SPEED = " + mBestReading.getSpeed());
        }

        // Determine whether new location is better than current best estimate
        if (null == mBestReading || location.getAccuracy() <= mBestReading.getAccuracy() ||
                (location.getAccuracy() <= MIN_ACCURACY) && (location.distanceTo(mBestReading) > location.getAccuracy())) {
            mBestReading = location;
            Log.d("logs", "ACCURACY = " + mBestReading.getAccuracy());

            checkGpsPoints(location);
        }
    }

    private void checkGpsPoints(Location location) {
        GpsPoint intentGpsPoint = null;

        Iterator<GpsPoint> it = lstGpsPoint.iterator();
        while(it.hasNext()) {
            GpsPoint point = it.next();
            Location locFromLst = new Location("locBusStop");

            locFromLst.setLatitude(point.getLatitude());
            locFromLst.setLongitude(point.getLongitude());

            System.out.println("DISTANCE AT = " + point.getName() + " ==== " + location.distanceTo(locFromLst));

            if((BUSSTOP_START_ALERT >= location.distanceTo(locFromLst))&&(BUSSTOP_START_ACCURACY <= location.distanceTo(locFromLst)))
            {
                showBusStopAlert(point);
                break;
            }

            if(BUSSTOP_START_ACCURACY >= location.distanceTo(locFromLst)) {
                intentGpsPoint = point;
                break;
            }
        }

        if(intentGpsPoint != null) {
            currentGpsPoint = intentGpsPoint;

            Intent intent = new Intent(this, StopAlertActivity.class);
            intent.putExtra(IT_GPS_POINT, intentGpsPoint);
            intent.putExtra(IT_CONFIG_OBJ, config);
            startActivityForResult(intent, IT_REQUEST_STATUS);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("### onStatusChanged");
    }

    public void onProviderEnabled(String provider) {
        System.out.println("### onProviderEnabled");
    }

    public void onProviderDisabled(String provider) {
        System.out.println("### onProviderEnabled");
    }


    @Override
    public void onConnected(Bundle dataBundle) {
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "## onConnectionSuspended ##", Toast.LENGTH_LONG).show();
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Toast.makeText(getApplicationContext(), "## onConnectionFailed ##", Toast.LENGTH_LONG).show();
    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }


    //############# Other Methods #############

    /**
     * Initialize loger
     */
    private void logInitialize() {
        String logPath;
        logPath = config.getLogFilesFullPath("logDefault" + ROUTE_SUFIX);

        String directoryPath = config.getLogFilesFolder();
        File directoryPathFile = new File(directoryPath);
        if(!directoryPathFile.exists()){
            directoryPathFile.mkdir();
        }

        try {
            mediaLoger = new MediaLoger(logPath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Log does not initialized!", Toast.LENGTH_SHORT).show();
        }
    }

    private void rebuildVideoView() {
        frameVideo.removeView(videoView);
        frameVideo.addView(videoView, 0);
    }

    /**
     * Start video player for route
     * Set action videoView OnCompletionListener
     */
    private void startVideoPlayer() {
        videoIndex = 0;
        playVideo(lstVideos.get(videoIndex).getName());

        videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoIndex = ++videoIndex >= lstVideos.size() ? 0 : videoIndex;

                System.out.println("onCompletion => videoIndex = " + videoIndex);
                rebuildVideoView();
                playVideo(lstVideos.get(videoIndex).getName());
                requestFocusOut();
            }
        });
    }
    /**
     * Play video from videoName path
     * @param videoName String with video name to path
     */
    void playVideo(String videoName) {
        String videoPath = config.getVideoMediasFullPath(videoName);
        videoView.setVideoPath(videoPath);
        videoView.start();

        try {
            mediaLoger.log(videoName, MediaLog.TYPE_VIDEO, bestLastKnownLocation(MIN_LAST_READ_ACCURACY, TWO_MIN));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start executor for show banners
     */
    private void startBannerPlayer() {
        //Read RSS
        rssIndex = 0;
        bannerIndex = 0;
        System.out.println("******** INICIALIZANDO O READER RSS ********");
        //readFeed(routeWL.getListBanner().getListRss());
        List<String> rssUrls = getListRssUrls();
        readFeed(rssUrls);

        startExecutors();
    }

    private void startExecutors() {
        scheduleTaskExecutorGeneral = Executors.newScheduledThreadPool(2);

        bannerTurn = true;
        scheduleTaskExecutorGeneral.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if(bannerTurn){
                    executeBanner();
                    bannerTurn = !bannerTurn;
                } else {
                    executeRSS();
                    bannerTurn = !bannerTurn;
                }
            }
        }, 0, config.getBannerInterval() , TimeUnit.SECONDS);
    }

    private void executeBanner() {
        if(scheduleTaskExecutorRSS != null) {
            scheduleTaskExecutorRSS.shutdownNow();
            scheduleTaskExecutorRSS = null;
        }

        if(scheduleTaskExecutorBanner != null && !scheduleTaskExecutorBanner.isShutdown()) {
            scheduleTaskExecutorBanner.shutdownNow();
            scheduleTaskExecutorBanner = null;
        }

        scheduleTaskExecutorBanner = Executors.newScheduledThreadPool(2);
        scheduleTaskExecutorBanner.scheduleAtFixedRate(new Runnable() {
            public void run() {

                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        showBanner();
                    }
                });
            }
        }, 0, config.getBannerTime() , TimeUnit.SECONDS);
    }

    private void executeRSS() {
        if(scheduleTaskExecutorBanner != null) {
            scheduleTaskExecutorBanner.shutdownNow();
            scheduleTaskExecutorBanner = null;
        }

        if(scheduleTaskExecutorRSS != null && !scheduleTaskExecutorRSS.isShutdown()) {
            scheduleTaskExecutorRSS.shutdownNow();
            scheduleTaskExecutorRSS = null;
        }

        scheduleTaskExecutorRSS = Executors.newScheduledThreadPool(2);
        scheduleTaskExecutorRSS.scheduleAtFixedRate(new Runnable() {
            public void run() {

                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        showRSS();
                    }
                });
            }
        }, 0, config.getRssTime() , TimeUnit.SECONDS);
    }

    private void showRSS() {
        if(lstRSS != null && lstRSS.size() > 0) {
            //Toast.makeText(getApplicationContext(), lstRSS.get(bannerIndex).getTitle(), Toast.LENGTH_LONG).show();

            SyndEntry entry = lstRSS.get(rssIndex++);
            rssIndex = rssIndex >= lstRSS.size() ? 0 : rssIndex;

            try {
                String aux = entry.getTitle();
                txtRssTitle.setText(getTextFromHtml(aux));

                aux = entry.getDescription().getValue();
                txtRssContent.setText(getTextFromHtml(aux));

                /*aux = entry.getPublishedDate().toString();
                if (aux != null && !aux.isEmpty())
                    txtRssDate.setText(aux.trim());
                else*/
                    txtRssDate.setText("");
                    rssImage.setImageDrawable(rssImageDrawable);

                //rssImage.setImageURI(Uri.parse());
            } catch(Exception e) {
                e.printStackTrace();
            }

            busStopAlertLayout.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            rssView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remove all Html tags from text
     * @param html with tags
     * @return String withou html tags
     */
    private String getTextFromHtml(String html) {
        String text = "";

        if(html == null || html.isEmpty()) {
            return text;
        }
        html = html.trim();
        html = html.replaceAll("<(.*?)\\>"," ");//Removes all items in brackets
        html = html.replaceAll("<(.*?)\\\n"," ");//Must be undeneath
        html = html.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
        html = html.replaceAll("&nbsp;"," ");
        html = html.replaceAll("&amp;"," ");
        text = html.trim();

        return text;
    }

    private void showBanner() {
        System.out.println("########## SHOW BANNER ##########");
        //String bannerPath = BANNER_FOLDER + lstBanners.get(bannerIndex).getImage();
        String bannerPath = config.getBannerMediasFullPath(lstBanners.get(bannerIndex).getImage());
                Uri myUri = Uri.parse(bannerPath);
        imageView.setImageURI(myUri);
        imageView.setVisibility(View.VISIBLE);
        rssView.setVisibility(View.GONE);
        busStopAlertLayout.setVisibility(View.GONE);

        if(++bannerIndex >= lstBanners.size())
            bannerIndex = 0;

        try {
            mediaLoger.log(bannerPath, MediaLog.TYPE_BANNER, bestLastKnownLocation(MIN_LAST_READ_ACCURACY, TWO_MIN));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a RouteWithList
     * @param routeName received from intent
     */
    private void loadRouteWL(String routeName) {
        routeWL = new RouteWithList(config);

        try {
            routeWL.fromFile(config.getRouteFilesFullPath(routeName + ROUTE_SUFIX));

            lstVideos = routeWL.getListVideo().getListVideo();
            lstBanners = routeWL.getListBanner().getListBanner();
            lstGpsPoint = routeWL.getListGpsPoint().getListGpsPoint();
            lstRSSFromRoute = routeWL.getListRSS().getListRSS();
        } catch (Exception e) {
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create list of bus stop to show in listview
     */
    private void crateBusStopList() {
        lstViewBusStop.setAdapter(new ItemBusStopAdapter2(this, lstGpsPoint));
        lstViewBusStop.setCacheColorHint(Color.TRANSPARENT);
        lstViewBusStop.expandGroup(0);

        /*RETIRAR SOMENTE PARA DEMONSTRAÇÂO*/
        lstViewBusStop.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                GpsPoint curGpsPoint = (GpsPoint)adapterView.getItemAtPosition(position);
                currentGpsPoint = curGpsPoint;

                Intent intent = new Intent(getApplicationContext(), StopAlertActivity.class);
                intent.putExtra(IT_GPS_POINT, curGpsPoint);
                intent.putExtra(IT_CONFIG_OBJ, config);
                startActivityForResult(intent, IT_REQUEST_STATUS);

                return false;
            }
        });
    }

    /**
     * Get URL paths from the RSS list
     */
    private List<String> getListRssUrls(){
        ArrayList<String> listUrls = new ArrayList<String>();

        for(RSS rss : lstRSSFromRoute){
            listUrls.add(rss.getPath());
        }

        return listUrls;
    }

    /**
     * Read RSS from list of URLS into BannerList
     * @param urls - List of RSS Urls
     */
    private void readFeed(final List<String> urls){
        new Thread(){
            public void run(){
                URL url;
                lstRSS = new ArrayList<SyndEntry>();

                try {
                    Iterator<String> itUrls = urls.iterator();
                    while(itUrls.hasNext()) {
                        String rssUrl = itUrls.next();

                        url = new URL(rssUrl);
                        SyndFeedInput input = new SyndFeedInput();
                        SyndFeed feed = input.build(new XmlReader(url));
                        List inputs = feed.getEntries();
                        Iterator itIputs = inputs.iterator();

                        //COLOCAR IMAGEM DO PROVEDOR NA TELA
                        try {
                            System.out.println("####CARREGANDO IMAGEM DO RSS###");
                            SyndImage syndImage = feed.getImage();
                            String link = syndImage.getUrl();

                            System.out.println("#### RSS LINK IMAGE = " + link);
                            if (link != null && !link.isEmpty()) {
                                InputStream is = (InputStream) new URL(link).getContent();
                                rssImageDrawable = Drawable.createFromStream(is, "src name");
                            }
                            //COLOCAR IMAGEM DO PROVEDOR NA TELA END
                        } catch(Exception rssE) {
                            rssE.printStackTrace();
                        }

                        while(itIputs.hasNext()) {
                            SyndEntry aux = (SyndEntry) itIputs.next();
                            lstRSS.add(aux);
                        }
                    }

                    System.out.println("QUANTIDADE DE RSS = " + lstRSS.size());
                    /*runOnUiThread(new Runnable(){
                        public void run(){
                            adapter.notifyDataSetChanged();
                        }
                    });*/

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (FeedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("rssIndex", rssIndex);
        outState.putInt("bannerIndex", bannerIndex);
    }

    private void requestFocusOut(){
        getCurrentFocus().clearFocus();
    }
}

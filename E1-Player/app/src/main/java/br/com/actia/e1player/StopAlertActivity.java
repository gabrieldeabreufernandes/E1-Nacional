package br.com.actia.e1player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.actia.jsonParser.ParserFileJson;
import br.com.actia.model.Configuration;
import br.com.actia.model.GpsPoint;
import br.com.actia.model.ItemBusStopAdapterAlert;
import br.com.actia.model.ListVideo;
import br.com.actia.model.MediaLog;
import br.com.actia.model.MediaLoger;
import br.com.actia.model.Video;

import static br.com.actia.e1player.R.layout.stopalert;

public class StopAlertActivity extends Activity implements MediaPlayer.OnCompletionListener {
    private static final String IT_GPS_POINT	= "gpsPoint";
    private static final String IT_CONFIG_OBJ	= "config";
    private static final String ROUTE_SUFIX	    = ".json";
    private GpsPoint gpsPoint = null;
    private FrameLayout mainLayout = null;
    private LinearLayout frameImg = null;
    private LinearLayout frameVideo = null;
    private LinearLayout frameBanner = null;
    private List<Video> lstBusStopVideos = null;
    private VideoView videoView = null;
    private ImageView imageView = null;
    // private TextView txtvBusStopBanner = null;
    private ExpandableListView expBusStopBanner = null;
    private int videoIndex = 0;
    private MediaPlayer mediaPlayer = null;
    private boolean isAudio = false;
    private MediaLoger mediaLoger = null;
    private Configuration config = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(stopalert);

        //mainLayout = (FrameLayout) findViewById(R.id.busStopLayout);
        frameImg = (LinearLayout) findViewById(R.id.frameImg);
        frameVideo = (LinearLayout) findViewById(R.id.frameVideoStopAlert);
        frameBanner = (LinearLayout) findViewById(R.id.frameBanner);
        // txtvBusStopBanner = (TextView) findViewById(R.id.busStopBanner);
        expBusStopBanner = (ExpandableListView) findViewById(R.id.busStopBanner);
        videoView = (VideoView) findViewById(R.id.videoViewStopAlert);

        if(!getGpsLocationFromIntent()) {
            Toast.makeText(getApplicationContext(), "ERRO NO PARAMETRO GPS POINT", Toast.LENGTH_LONG).show();

            setResult(RESULT_CANCELED, null);
            finish();
        }

        // txtvBusStopBanner.setText(getString(R.string.busStop) + gpsPoint.getName());
        createExpBusStopList();
        logInitialize();
    }

    /**
     * Show BusStop alert Banner and start video player for BusStop ListVideos
     */
    @Override
    protected void onStart() {
        super.onStart();

        boolean needPlayVideo = !showBusStop();
        initializeBusStopVideos();

        if(needPlayVideo) {
            showVideoView();
            playVideo(lstBusStopVideos.get(videoIndex).getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    private boolean getGpsLocationFromIntent() {
        Intent intent = getIntent();
        gpsPoint = (GpsPoint) intent.getSerializableExtra(IT_GPS_POINT);
        config = (Configuration) intent.getSerializableExtra(IT_CONFIG_OBJ);
        return (gpsPoint != null);
    }

    /**
     * Show BusStop Banner (image and audio)
     * @return - True = Audio Played, False = Audio error
     */
    private boolean showBusStop() {
        String imgPath = config.getImageMediasFullPath(gpsPoint.getIndication().getImage());
        String audioPath = config.getAudioMediasFullPath(gpsPoint.getIndication().getAudio());

        File fileBg = new File(imgPath);
        File fileAudio = new File(audioPath);

        if(fileBg.exists() && fileBg.isFile()) {
            imageView = new ImageView(this);

            //set company logo
            Bitmap img = BitmapFactory.decodeFile(imgPath);
            imageView.setImageBitmap(img);
            frameImg.addView(imageView);
            frameImg.setVisibility(View.VISIBLE);
            frameVideo.setVisibility(View.GONE);

            showBanner();
            //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), imgPath);
            //mainLayout.setBackground(bitmapDrawable);
            //if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                //mainLayout.setBackgroundDrawable(bitmapDrawable);
            //}
        }

        System.out.println("AUDIO PATH = " + audioPath);
        if(fileAudio.exists() && fileBg.isFile()) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(this);
                isAudio = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            isAudio = false;
        }
        return isAudio;
    }

    private void showBanner() {
        frameBanner.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize VideoView for BusStopVideos and register listener to onCompletion
     */
    private void initializeBusStopVideos() {
        try {
            String videoPath = config.getVideoFilesFullPath(gpsPoint.getVideoPath());
            ParserFileJson jsonFP = new ParserFileJson(videoPath);
            ListVideo lstVideos = jsonFP.toObject(ListVideo.class);

            if(lstVideos != null) {
                lstBusStopVideos = lstVideos.getListVideo();

                //Initialize VideoView
                // videoView = new VideoView(getApplicationContext());

                //initialize video index and listener
                videoIndex = 0;
                videoView.setOnCompletionListener(this);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start show video view
     */
    private void showVideoView() {
        //mainLayout.removeAllViews();
        //mainLayout.addView(videoView);
        frameVideo.setVisibility(View.VISIBLE);
        frameImg.setVisibility(View.GONE);
        showBanner();
    }

    private void rebuildVideoView() {
        frameVideo.removeView(videoView);
        frameVideo.addView(videoView);
    }

    /**
     * Play video from videoName path
     * @param videoName String with video name to path
     */
    void playVideo(String videoName) {
        videoView.setVideoPath(config.getVideoMediasFullPath(videoName));
        videoView.start();
        log(videoName);
    }

    /**
     * OnCompletion start next Video
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(isAudio) {
            showVideoView();
            isAudio = false;
            --videoIndex;
        }

        if(++videoIndex < lstBusStopVideos.size()) {
            System.out.println("onCompletion => videoIndex = " + videoIndex);
            rebuildVideoView();
            playVideo(lstBusStopVideos.get(videoIndex).getName());

        }
        else {
            //retorna da intent
            setResult(RESULT_OK, null);
            finish();
        }
    }

    //### Media Loger ###
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

    private void log(String videoName) {
        try {
            Location location = new Location(gpsPoint.getName());
            location.setLatitude(gpsPoint.getLatitude());
            location.setLongitude(gpsPoint.getLongitude());

            if(mediaLoger == null)
                System.out.println("LOGER NAO INICIALIZADO!!!!!!!!!!!");
            mediaLoger.log(videoName, MediaLog.TYPE_VIDEO, location);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createExpBusStopList() {
        ArrayList<GpsPoint> lstGpsPoint = new ArrayList<GpsPoint>();
        lstGpsPoint.add(gpsPoint);
        expBusStopBanner.setAdapter(new ItemBusStopAdapterAlert(this, lstGpsPoint));
        // expBusStopBanner.setCacheColorHint(Color.TRANSPARENT);
        // expBusStopBanner.setBackgroundColor(Color.TRANSPARENT);
        expBusStopBanner.expandGroup(0);
        expBusStopBanner.setEnabled(false);
    }
}

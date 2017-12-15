package com.android.Launcher1;

import com.android.Json.ParserFileJson;
import com.android.model.*;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;

public class MainActivity extends Activity implements OnClickListener {

    private List<ImageButton> listImageButton;
    private LinearLayout layout;
    private FrameLayout layoutPai;
    private FilesRelease filesRelease;
    private ImageView imgVLogo;

    //private final String CONFIG_LAUNCHER_FOLDER	= "/sdcard/external_sdcard/E1Player/launcher/"; //para o tablet genesis
    private final String CONFIG_LAUNCHER_FOLDER	= "/mnt/extsd/E1Player/launcher/";     //E1
    //private final String CONFIG_LAUNCHER_FOLDER	    = "/mnt/sdcard/E1Player/launcher/";         //Moto G
    //private final String CONFIG_LAUNCHER_FOLDER     = "/storage/extSdCard/E1Player/launcher/";    //Samsung galaxy s3
    private final String LAUNCHER_DEFAULTS_FOLDER = CONFIG_LAUNCHER_FOLDER + "/Defaults/";
    private final String CONFIG_FILE	= CONFIG_LAUNCHER_FOLDER + "config.json";
    private final int COLOR_BG_DEFAULT = R.color.Black;
    private final String IMAGE_BG_DEFAULT = "";
    private final String IMAGE_COMPANY_DEFAULT = CONFIG_LAUNCHER_FOLDER + "actia.png";
    private final String PASSWORD_DEFAULT = "1212";
    private final String AUDIO_MEDIA_FOLDER = "/mnt/extsd/E1Player/audios/";
    private final String BANNER_CONFIG_FOLDER = "/mnt/extsd/E1Player/banner/";
    private final String BANNER_MEDIA_FOLDER = "/mnt/extsd/E1Player/banners/";
    private final String GPS_CONFIG_FOLDER = "/mnt/extsd/E1Player/gps/";
    private final String IMAGE_MEDIA_FOLDER = "/mnt/extsd/E1Player/images/";
    private final String ROUTE_CONFIG_FOLDER = "/mnt/extsd/E1Player/route/";
    private final String RSS_CONFIG_FOLDER = "/mnt/extsd/E1Player/rss/";
    private final String VIDEO_CONFIG_FOLDER = "/mnt/extsd/E1Player/video/";
    private final String VIDEO_MEDIA_FOLDER = "/mnt/extsd/E1Player/videos/";

    private Configuration configuration;

    EditText etPassword = null;
	boolean login = false;
    IntentButton intentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        layout = (LinearLayout) findViewById(R.id.LinearLayoutFilho);
        layoutPai =  (FrameLayout) findViewById(R.id.FrameLayoutPai);
        imgVLogo = (ImageView) findViewById(R.id.imgLogo);

        hidingNavigationBar();

        try {
            configuration = getConfigFile();
            buildFoldersStructure();
            initializeField();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

 /* *************************************************************************************************
 * Funções para a criação dinâmica da tela
 *
 * **************************************************************************************************/

 /* ------------------------------------------------------------------------------------------------
 *  Função criar elementos da UI a partir do arquivo de conf Json
 *
 * -------------------------------------------------------------------------------------------------*/
    private void initializeField() {
        listImageButton = new ArrayList<ImageButton>();
        getWindow().getDecorView().setBackgroundColor(configuration.getColorBG());

        //set main activity background
        Bitmap bmImg = BitmapFactory.decodeFile(configuration.getImageBG());
        BitmapDrawable background = new BitmapDrawable(bmImg);
        layoutPai.setBackground(background);

        //set company logo
        Bitmap logoBmp = BitmapFactory.decodeFile(configuration.getImageCompany());
        imgVLogo.setImageBitmap(logoBmp);

        Iterator<apps> it = configuration.getListApps().iterator();
        while(it.hasNext())
        {
            apps app = it.next();

            IntentButton intentButton = new IntentButton(getApplicationContext(), app.getIntent());

            Bitmap bmp = null;
            if(app.getImage() != null && app.getImage() != ""){
                bmp = BitmapFactory.decodeFile(app.getImage());
            } else {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image_undefined);
            }

            intentButton.setImageBitmap(bmp);
            intentButton.setAdjustViewBounds(true);
            intentButton.setMaxHeight(256);
            intentButton.setMaxWidth(256);
            intentButton.setIntent(app.getIntent());
            intentButton.setHaspassword(app.getHaspassword());
            intentButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(intentButton);

            intentButton.setOnClickListener(this);
            //intentButton.setFocusable(false);
            //intentButton.setFocusableInTouchMode(false);
        }

	}

 /* ------------------------------------------------------------------------------------------------
 *  Função para obter a configuração da tela a partir do arquivo
 *
 * -------------------------------------------------------------------------------------------------*/
    private Configuration getConfigFile() throws IOException {
        Configuration configObj;
        File sourceFolder;
        File configFile;

        sourceFolder = new File(CONFIG_LAUNCHER_FOLDER);
        sourceFolder.mkdir();
        if(!sourceFolder.exists()) {
            sourceFolder.createNewFile(); //if boolean
        }

        configFile = new File(CONFIG_FILE);

        if(configFile.exists()) {
            ParserFileJson pfJson = new ParserFileJson(CONFIG_FILE);
            configObj = pfJson.toObject(Configuration.class);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.launcherConfigNotSet), Toast.LENGTH_LONG).show();

            copyConfigFilesFromResources();
            configObj = createDefaultConfiguration();
            configFile.createNewFile(); //if boolean

            ParserFileJson pfJson = new ParserFileJson(CONFIG_FILE);
            pfJson.toFile(configObj);
        }

        return configObj;
    }

 /* ------------------------------------------------------------------------------------------------
 *  Função que cria uma configuração Default
 *
 * -------------------------------------------------------------------------------------------------*/
     private Configuration createDefaultConfiguration() {
        Configuration config = new Configuration();
        config.setColorBG(COLOR_BG_DEFAULT);
        config.setImageBG(IMAGE_BG_DEFAULT);
        config.setImagesFolder(LAUNCHER_DEFAULTS_FOLDER);
        config.setImageCompany(IMAGE_COMPANY_DEFAULT);
        config.setPassword(PASSWORD_DEFAULT);

        ArrayList<apps> apps = new ArrayList<apps>();
        apps app1 = new apps("/mnt/extsd/E1Player/launcher/play.png", "Player", "br.com.actia.e1player", false);
        apps.add(app1);
        apps app2 = new apps("/mnt/extsd/E1Player/launcher/update.png", "Update", "", true);
        apps.add(app2);

        config.setListApps(apps);
        return config;
    }

/* ------------------------------------------------------------------------------------------------
 *  Função que cria as pastas necessárias, se não existirem
 *
 * -------------------------------------------------------------------------------------------------*/
    private void buildFoldersStructure() throws IOException {
        File audioMediaFolder;
        File bannerConfigFolder;
        File bannerMediaFolder;
        File gpsConfigFolder;
        File imageMediaFolder;
        File routeConfigFolder;
        File rssConfigFolder;
        File videoConfigFolder;
        File videoMediaFolder;

        audioMediaFolder = new File(AUDIO_MEDIA_FOLDER);
        if(!audioMediaFolder.exists()) {
            if(!audioMediaFolder.mkdir()) {
                audioMediaFolder.createNewFile();
            }
        }

        bannerConfigFolder = new File(BANNER_CONFIG_FOLDER);
        if(!bannerConfigFolder.exists()) {
            if(!bannerConfigFolder.mkdir()) {
                bannerConfigFolder.createNewFile();
            }
        }

        bannerMediaFolder = new File(BANNER_MEDIA_FOLDER);
        if(!bannerMediaFolder.exists()) {
            if(!bannerMediaFolder.mkdir()) {
                bannerMediaFolder.createNewFile();
            }
        }

        gpsConfigFolder = new File(GPS_CONFIG_FOLDER);
        if(!gpsConfigFolder.exists()) {
            if(!gpsConfigFolder.mkdir()) {
                gpsConfigFolder.createNewFile();
            }
        }

        imageMediaFolder = new File(IMAGE_MEDIA_FOLDER);
        if(!imageMediaFolder.exists()) {
            if(!imageMediaFolder.mkdir()) {
                imageMediaFolder.createNewFile();
            }
        }

        routeConfigFolder = new File(ROUTE_CONFIG_FOLDER);
        if(!routeConfigFolder.exists()) {
            if(!routeConfigFolder.mkdir()) {
                routeConfigFolder.createNewFile();
            }
        }

        rssConfigFolder = new File(RSS_CONFIG_FOLDER);
        if(!rssConfigFolder.exists()) {
            if(!rssConfigFolder.mkdir()) {
                rssConfigFolder.createNewFile();
            }
        }

        videoConfigFolder = new File(VIDEO_CONFIG_FOLDER);
        if(!videoConfigFolder.exists()) {
            if(!videoConfigFolder.mkdir()) {
                videoConfigFolder.createNewFile();
            }
        }

        videoMediaFolder = new File(VIDEO_MEDIA_FOLDER);
        if(!videoMediaFolder.exists()) {
            if(!videoMediaFolder.mkdir()) {
                videoMediaFolder.createNewFile();
            }
        }

    }

/* ------------------------------------------------------------------------------------------------
 *  Função que copia as imagens padrão para o diretório LaunchConfigFolder
 *
 * -------------------------------------------------------------------------------------------------*/
    private void copyConfigFilesFromResources(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        File originFile = null;
        File destinationFile = null;

        destinationFile = new File(CONFIG_LAUNCHER_FOLDER + "play.png");

        //create a file to write bitmap data
        originFile = new File(getApplicationContext().getCacheDir(), "play_tocopy.png");
        try {
            originFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/ , bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(originFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inChannel = new FileInputStream(originFile).getChannel();
            outChannel = new FileOutputStream(destinationFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        destinationFile = new File(CONFIG_LAUNCHER_FOLDER + "update.png");

        originFile = new File(getApplicationContext().getCacheDir(), "update_tocopy.png");
        try {
            originFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.update);
        bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        bitmapdata = bos.toByteArray();

        fos = null;
        try {
            fos = new FileOutputStream(originFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inChannel = new FileInputStream(originFile).getChannel();
            outChannel = new FileOutputStream(destinationFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 /* ------------------------------------------------------------------------------------------------
 *  Função para mostrar o alerta de login ou chamar a intent
 *
 * -------------------------------------------------------------------------------------------------*/
    //@Override
   // public void


     @Override
    public void onClick(View v) {

        intentButton = (IntentButton) v;

        if((!login)&&(intentButton.getHaspassword()))
			showAlertLogin();

        else
            goIntent();

    }

 /* ------------------------------------------------------------------------------------------------
 *  Função para chamar a intent desejada ou chamar a rotina de atualização do conteúdo
 *
 * -------------------------------------------------------------------------------------------------*/
    public void goIntent()
    {
        login = false;
        Intent it = getPackageManager().getLaunchIntentForPackage(intentButton.getIntent());

        if(it==null)
        {
            ReleaseUSB();
            super.recreate();
        }
    else
    startActivity(it);

    hidingNavigationBar();
}

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
    }


    /* ------------------------------------------------------------------------------------------------
    *  Função de atualização do conteúdo via USB
    *
    * -------------------------------------------------------------------------------------------------*/
    public void ReleaseUSB()
    {
        new ReleaseUSBTask().execute(null, null, null);
    }

    private class ReleaseUSBTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, getString(R.string.updateUpdatingFiles), getString(R.string.updateWait), true, false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            filesRelease = new FilesRelease();
            if(filesRelease.Release()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true) {
                Toast.makeText(MainActivity.this, getString(R.string.updateOK), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.updateERR), Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

 /* ------------------------------------------------------------------------------------------------
 *  Função para mostrar a caixa de diálogo de login
 *
 * -------------------------------------------------------------------------------------------------*/
    private void showAlertLogin() {
        etPassword = new EditText(this);
        etPassword.setHint(getString(R.string.pswHint));
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        new Builder(this)
                .setTitle(getString(R.string.pswTitle))
                .setView(etPassword)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(etPassword.getText().toString().equals(configuration.getPassword()))
                        {
                            login = true;
                            goIntent();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.pswErr),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        login = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

 /* ------------------------------------------------------------------------------------------------
 *  Função para não permitir a visualização da Navigation Bar
 *
 * -------------------------------------------------------------------------------------------------*/
	private void hidingNavigationBar() {
    	View decorView = getWindow().getDecorView();
		// Hide both the navigation bar and the status bar.
		// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
		// a general rule, you should design your app to hide the status bar whenever you
		// hide the navigation bar.
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		              | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}
}





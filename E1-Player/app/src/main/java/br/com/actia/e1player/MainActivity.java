package br.com.actia.e1player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.actia.jsonParser.ParserFileJson;
import br.com.actia.model.Configuration;
import br.com.actia.model.ListBanner;
import br.com.actia.model.ListVideo;
import br.com.actia.model.RouteWithList;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;


public class MainActivity extends Activity {
    //private final String SOURCE_FOLDER	= "/sdcard/external_sdcard/E1Player";   //Tablet Genesis
    private final String SOURCE_FOLDER	= "/mnt/extsd/E1Player";                    //E1 hardware
    //private final String SOURCE_FOLDER	    = "/mnt/sdcard/E1Player";               //Moto G
    //private final String SOURCE_FOLDER	= "/storage/extSdCard/E1Player";            //Samsung Galaxy 3
    private final String CONFIG_FILE	= SOURCE_FOLDER + "/config.json";
    private final String ROUTE_SUFIX	= ".json";
    private final String IT_ROUTE_NAME	= "route";
    private final String IT_CONFIG_OBJ  = "config";

    private Configuration config = null;
    private List<String> lstRoute = null;
    private ListView listViewRoute = null;
    private SearchView searchRoute = null;
    private Filter filterRoute = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchRoute = (SearchView) findViewById(R.id.searchRoute);
        listViewRoute = (ListView) findViewById(R.id.listRoute);

        configSearchRoute();
        configListRoute();

        try {
            config = getConfigFile();

            lstRoute = getRouteLists();
            if(lstRoute.size() > 0) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_list_item_1, lstRoute );

                listViewRoute.setAdapter(arrayAdapter);
                listViewRoute.setTextFilterEnabled(true);
                filterRoute = arrayAdapter.getFilter();
            }
            else {
                //Create default route
                //RouteWithList routewl = makeDefaultRoute();

                //Call route activity with default route
            }

        } catch (Exception e) {
            e.printStackTrace();

            Context context = getApplicationContext();
            CharSequence text = e.getLocalizedMessage();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    //########## Action Methods ##########
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    //########## Other Methods ##########
    private void configListRoute() {
        listViewRoute.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long id) {
                String strItem = (String) adapter.getItemAtPosition(position);
                callVideoActivity(strItem);
            }
        });
    }

    private void configSearchRoute() {
		/*Change text color from SearchView*/
        int tvId = searchRoute.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView tv = (TextView) searchRoute.findViewById(tvId);
        tv.setTextColor(Color.WHITE);
		
		/*Set ListRoute filter */
        searchRoute.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    filterRoute.filter(null);
                }
                else {
                    filterRoute.filter(newText);
                }
                return true;
            }
        });

        searchRoute.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            //Show keyboard when SearchRout has in focus
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
            }
        });
		
		/*On Focus show keyboard*/
		/*InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchRoute, InputMethodManager.SHOW_IMPLICIT);*/
    }

    /**
     * Call video player activity
     * @param route name
     */
    protected void callVideoActivity(String route) {
        this.makeLogBackup();
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(IT_ROUTE_NAME, route);
        intent.putExtra(IT_CONFIG_OBJ, config);
        startActivity(intent);
    }

    /*
     * Rename log file to backup
     */
    private void makeLogBackup(){
        String logFilePath = config.getLogFilesFullPath("logDefault" + ROUTE_SUFIX);
        File logFile = new File(logFilePath);

        if(logFile.exists()){
            logFile.renameTo(new File(config.getLogFilesFullPath("log_" + System.currentTimeMillis() + ROUTE_SUFIX)));
        }
    }

    /**
     * Create a default routeWithList
     * @return RouteWithList
     */
    private RouteWithList makeDefaultRoute() {
        RouteWithList rtwl = new RouteWithList(config);

        ListBanner lstBanner = new ListBanner();
        lstBanner.setId(1);
        lstBanner.setVersion(1);
        lstBanner.setName("allBanners");
        //lstBanner.setListBanner(listBanner);

        ListVideo lstVideo = new ListVideo();
        lstVideo.setId(1);
        lstVideo.setName("allVideos");
        //lstVideo.setListVideo(listVideo);

        rtwl.setListBanner(lstBanner);
        rtwl.setListVideo(lstVideo);
        rtwl.setListGpsPoint(null);

        return null;
    }

    /**
     * Get list of route files into route folder
     * @return List<String> List of route files names
     */
    private List<String> getRouteLists() {
        List<String> lstRoute = new ArrayList<String>();
        String routeFolder = config.getRouteFilesFolder();

        File folderRoute = new File(routeFolder);
        folderRoute.mkdir();

        if(folderRoute.exists()) {
            File[] files = folderRoute.listFiles();

            for(int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String routeName = fileName.substring(0, fileName.indexOf(ROUTE_SUFIX));
                lstRoute.add(routeName);
            }

            //List alphabetic order
            //Collections.sort(lstRoute);
        }
        return lstRoute;
    }

    /**
     * Get exists or create default Configuration from path 
     * @return Configuration Object
     * @throws IOException
     */
    private Configuration getConfigFile() throws IOException {
        Configuration configObj;
        File sourceFolder;
        File configFile;

        //Project Folder
        sourceFolder = new File(SOURCE_FOLDER);
        sourceFolder.mkdir();
        if(!sourceFolder.exists()) {
            sourceFolder.createNewFile();
        }

        //Configuration File
        configFile = new File(CONFIG_FILE);

        if(configFile.exists()) {
            ParserFileJson pfJson = new ParserFileJson(CONFIG_FILE);
            configObj = pfJson.toObject(Configuration.class);
        }
        else {
            configObj = createDefaultConfiguration();
            configFile.createNewFile();

            ParserFileJson pfJson = new ParserFileJson(CONFIG_FILE);
            pfJson.toFile(configObj);
        }

        return configObj;
    }

    /**
     * Make a default configuration file
     * @return Configuration
     */
    private Configuration createDefaultConfiguration() {
        Configuration config = new Configuration();

        config.setAudioMediasFolder(SOURCE_FOLDER + "/audios");
        config.setBannerFilesFolder(SOURCE_FOLDER + "/banner");
        config.setBannerMediasFolder(SOURCE_FOLDER + "/banners");
        config.setGpsFilesFolder(SOURCE_FOLDER + "/gps");
        config.setImageMediasFolder(SOURCE_FOLDER + "/images");
        config.setLogFilesFolder(SOURCE_FOLDER + "/logs");
        config.setRouteFilesFolder(SOURCE_FOLDER + "/route");
        config.setRssFilesFolder(SOURCE_FOLDER + "/rss");
        config.setVideoFilesFolder(SOURCE_FOLDER + "/video");
        config.setVideoMediasFolder(SOURCE_FOLDER + "/videos");
        config.setBannerTime(35);
        config.setBannerInterval(35);
        config.setRssTime(10);

        return config;
    }
}

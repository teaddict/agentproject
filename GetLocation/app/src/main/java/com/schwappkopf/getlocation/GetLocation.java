package com.schwappkopf.getlocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;



public class GetLocation extends Activity implements LocationListener{
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    double myLocationLat=0;
    double myLocationLng=0;
    double othLocationLat=0;
    double othLocationLng=0;
    String myId,myWifiStatus;
    TextView txtLat, txtTest;
    String status;
    String othLat,othLng;
    String hname,hpass;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    List<ScanResult> wifiList;
    JSONObject user;
    Button btnColor;
    boolean network;

    JSONParser jsonParser = new JSONParser();

    private static final String url_update_user = "http://friendzonecheck.byethost6.com/agentproject/update.php";
    private static final String url_get_user = "http://friendzonecheck.byethost6.com/agentproject/get_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_USERID = "agent_id";
    private static final String TAG_LAT = "Lat";
    private static final String TAG_LNG = "Lng";
    private static final String TAG_OTH_LAT = "OthLat";
    private static final String TAG_OTH_LNG = "OthLng";
    private static final String TAG_HNAME = "hname";
    private static final String TAG_HPASS = "hpass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location);

        btnColor = (Button) findViewById(R.id.button);

        txtLat = (TextView) findViewById(R.id.textview1);
        txtTest = (TextView)findViewById(R.id.textView);
        btnColor = (Button)findViewById(R.id.button);
        myId=getIntent().getStringExtra("myId");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, this);

        UpdateActionStartsHere();
        GetActionStartsHere();
    }
    @Override
    public void onLocationChanged(Location location) {
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        myLocationLat= location.getLatitude();
        myLocationLng= location.getLongitude();
    }


    public void UpdateActionStartsHere()
    {
        againUpdateStartGPSAndSendFile();
    }


    public void againUpdateStartGPSAndSendFile()
    {
        new CountDownTimer(15000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {

                new SaveUserDetails().execute();
            }
            @Override
            public void onFinish()
            {
                UpdateActionStartsHere();
            }

        }.start();
    }

    public void GetActionStartsHere()
    {
        againGetStartGPSAndSendFile();
    }


    public void againGetStartGPSAndSendFile()
    {
        new CountDownTimer(10000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {

                new GetUserDetails().execute();
            }
            @Override
            public void onFinish()
            {
                Location locationA = new Location("point A");

                locationA.setLatitude(myLocationLat);
                locationA.setLongitude(myLocationLng);

                Location locationB = new Location("point B");

                locationB.setLatitude(othLocationLat);
                locationB.setLongitude(othLocationLng);

                float distance = locationA.distanceTo(locationB);
                txtTest.setText("Aranızdaki Mesafe:" + Float.toString(distance));
                txtTest.refreshDrawableState();
                if(distance<=50 && distance!=0) //sıfır default value old için.
                {
                    btnColor.setEnabled(true);
                    addListenerOnButton();
                    btnColor.setBackgroundResource(android.R.color.holo_green_dark);

                }
                else if(distance>50)
                {
                    btnColor.setEnabled(false);
                    btnColor.setBackgroundResource(android.R.color.holo_red_light);

                }
                GetActionStartsHere();
            }

        }.start();
    }

    public void addListenerOnButton() {



        btnColor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                network=isNetworkOnline();
                if(!network)
                {
                    new AlertDialog.Builder(GetLocation.this)
                            .setTitle("Bağlantı Hatası!")
                            .setMessage("İnternet bağlantınızda bir sorun var...")
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GetLocation.this.finish();
                                }
                            })
                            .show();
                }
                WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()){
                //wifi is enabled
                    WifiInfo wifiInfo = wifi.getConnectionInfo();
                    myWifiStatus= wifiInfo.toString();
                    if(hname==wifiInfo.getSSID())
                    {
                        //eğer doğru ağa bağlıysa
                    }
                    else
                    {
                       //daha bağlanmamışsa
                                try{
                                        WifiConfiguration conf = new WifiConfiguration();
                                        conf.SSID = "\"" + hname + "\"";
                                        conf.preSharedKey = "\"" + hpass + "\"";
                                        //      conf.hiddenSSID = true;
                                        //      conf.wepTxKeyIndex = 0;
                                        //      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                                        //      conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

                                        conf.status = WifiConfiguration.Status.ENABLED;
                                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                                        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

                                        int networkId = wifi.addNetwork(conf);
                                        wifi.enableNetwork(networkId, true);


                                }

                                    catch(Exception e) {
                                    }

                                        //turn off wifi at first
                                        WifiInfo info = wifi.getConnectionInfo ();
                                        if(hname!=info.getSSID())
                                        {
                                        wifi.setWifiEnabled(false);
                                        WifiConfiguration netConfig = new WifiConfiguration();

                                        netConfig.SSID = "\"" + hname + "\"";
                                        netConfig.hiddenSSID=true;
                                        netConfig.preSharedKey = "\""+ hpass +"\"";
                                        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                                        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                                        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                                        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                                        try{
                                            Method method = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                                            method.invoke(wifi, netConfig, true);

                                        } catch (Exception e) {
                                            Log.e(this.getClass().toString(), "", e);
                                        }
                                    }
                                }
                                // log results
                     }




            //check internet
                //check wifi is open ?
                    //if open, search the secret hotspot,
                        //if there is not our hotspot, create the one
                        //there is hotspot, connect to it
                    //else, open wifi, and do other process



            }

        });

    }



    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    class GetUserDetails extends AsyncTask<String, String, String> {



        /**
         * Getting user details in background thread
         * */
        protected String doInBackground(String... args) {

            // updating UI from Background Thread
            // Check for success tag

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_USERID, myId));

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    url_get_user, "GET", params);

            // check your log for json response
            Log.d("Single User Details", json.toString());
            int success;
            try {
                // Building Parameters


                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray userObj = json
                            .getJSONArray(TAG_USER); // JSON Array

                    // get first product object from JSON Array
                    user = userObj.getJSONObject(0);
                    othLat=user.getString(TAG_OTH_LAT);
                    othLng=user.getString(TAG_OTH_LNG);
                    hname=user.getString(TAG_HNAME);
                    hpass=user.getString(TAG_HPASS);

                    othLocationLat=Double.parseDouble(othLat);
                    othLocationLng=Double.parseDouble(othLng);


                }else{
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }


    }

    /**
     * Background Async Task to  Save product Details
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_USERID, myId));
            params.add(new BasicNameValuePair(TAG_LAT, Double.toString(myLocationLat)));
            params.add(new BasicNameValuePair(TAG_LNG, Double.toString( myLocationLng)));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    status= "updated";
                } else {
                    status= "not updated";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
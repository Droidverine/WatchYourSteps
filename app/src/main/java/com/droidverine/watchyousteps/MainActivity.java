package com.droidverine.watchyousteps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    LocationListener locationListener;
    Chronometer chronometer, chronometerkm;
    String langlong;
    TextView lc, TxttotalRun, Txtavgspeed, Txtavgspeedperkm;
    long eachkm;
    ArrayList<Location> locationArrayList=new ArrayList<>();
    public static ArrayList<Double> PerkmArraylist=new ArrayList<>();
    public  static Double dskm;
    GraphCusotmView graphCusotmView;
    Double elapsed;
    Button btnstart, btnstop;
    static double distanceInMetres;
    static Location lastLocation = null;
    long millistart, millisend,milliperkmstart,milliperkmend;

    //Distance to km= distance * 1000
    //Avg speed= distance/time.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lc = findViewById(R.id.locationttxt);
        setSupportActionBar(toolbar);
        chronometerkm = findViewById(R.id.chronometerperkm);
        chronometer = findViewById(R.id.chronometer);
        btnstart = findViewById(R.id.btnstart);
        btnstop = findViewById(R.id.btnstop);
        TxttotalRun = findViewById(R.id.txttotalrun);
        Txtavgspeed = findViewById(R.id.txtavgspeed);
        ArrayList<Integer> integers=new ArrayList<>();
         graphCusotmView=findViewById(R.id.graphview1);
        locationListener = LocServices();

        btnstop.setVisibility(View.GONE);
        FloatingActionButton floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name);
                Log.d("Files", "Path: " + getFilesDir());
                File directory = new File(getFilesDir()+"/");
                File[] files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                }
             //   startActivity(new Intent(getApplicationContext(),GraphiewActivity.class));
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager != null) {
                    distanceInMetres=0;
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                    btnstart.setVisibility(View.GONE);
                    btnstop.setVisibility(View.VISIBLE);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    millistart = System.currentTimeMillis();
                    milliperkmstart= System.currentTimeMillis();
                    PerkmArraylist.clear();

                    //  Date dt = new Date(dtMili);

                    Log.d("GetTime", "" + (millistart));
                }

            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager != null) {
                   // chronometerkm.start();
                    locationManager.removeUpdates(locationListener);
                    Double ds=0.0;
                    millisend = System.currentTimeMillis();
                    Long timeperkm=((milliperkmend - milliperkmstart) / 1000);

                  //  PerkmArraylist.add(1/(timeperkm/Double.valueOf(3600)));


                    for (int i=1;i<locationArrayList.size();i++)
                    {
                        Location nextloc=null;




                        ds += locationArrayList.get(i-1).distanceTo(locationArrayList.get(i));

                    }
                    //For getting total distance travveled
                    Log.d("Distanceala",""+ds/1000);
                    //For getting total travel time
                    Long avgtime=((millisend - millistart) / 1000);
                     dskm=(ds/1000);
                    //Correct speed
                    Log.d("Distaneala","Speed "+dskm/(avgtime/Double.valueOf(3600)));
                    Log.d("Distaneala","TIme "+avgtime/Double.valueOf(3600));
                    //Avg speed= distanceinkm/timeinhr.
                    Double avgspeed = dskm/(avgtime/Double.valueOf(3600));
                    //For getting avg speed over whole run.

                    locationArrayList.clear();

                    // locationManager = null;
                    lc.setText("");
                    btnstop.setVisibility(View.GONE);
                    btnstart.setVisibility(View.VISIBLE);
                    /*
                    elapsed = Double.valueOf(SystemClock.elapsedRealtime() - chronometer.getBase());
                    //Distance to km= distance * 1000
                    Double avghr = ((elapsed / 1000) / 3600);
                    Log.d("chrono Second", "" + (elapsed / 1000));
                    Log.d("chrono hour", "" + avghr);
                    Log.d("chrono distance in km", "" + distanceInMetres / 1000);
                    //Avg speed= distanceinkm/timeinhr.
                    Double avgspeed = (distanceInMetres / 1000) / avghr;
                    Log.d("chrono speed", "" + avgspeed);

                     */
                    Date currentTime = Calendar.getInstance().getTime();

                    File f = new File(getFilesDir(), currentTime + ".txt");

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                        bw.write("Date and Time \t" + currentTime + "\n");
                        bw.write("Distance Travelled \t" +dskm + "\n");
                        bw.write("Avg Speed \t" + avgspeed+"\n");

                        bw.write("Time Elapsed \t" + avgtime/Double.valueOf(3600));

                        Log.d("ZALA", "GHE");
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TxttotalRun.setText("" + dskm);
                    Txtavgspeed.setText("" + avgspeed);

                    distanceInMetres = 0;
                    lc.setText(""+distanceInMetres);

                    chronometer.stop();
                    graphCusotmView.invalidate();
                  // graphCusotmView.setVisibility(View.VISIBLE);
                }

                //GraphCusotmView ss=new GraphCusotmView(getApplicationContext(),integers,3);
                //ss.setMAX_KM(3);

/*
                Intent intent=new Intent(MainActivity.this,GraphiewActivity.class);
                intent.putExtra("perkm",PerkmArraylist);
                intent.putExtra("totdis",dskm);
                startActivity(intent);
                */


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                return;
            }

        } else {
            methodtoreq();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            locationManager.requestLocationUpdates("gps", 0, 1, locationListener);
            methodtoreq();
            return;

        }
    }

    void methodtoreq() {
        locationManager.requestLocationUpdates("gps", 0, 1, locationListener);
        return;

    }

    LocationListener LocServices() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationArrayList.add(location);
                langlong = String.valueOf(location.getLatitude());
                Log.d("loc", "currentloc " + location);
                Log.d("loc", "locationali " + lastLocation);


                if (lastLocation != null) {
                    distanceInMetres += location.distanceTo(lastLocation);
                    lc.setText("" +distanceInMetres / 1000 );
                    if(distanceInMetres==0)
                    {

                    }
                    if(distanceInMetres>1000)
                    {
                        milliperkmend= System.currentTimeMillis();
                        distanceInMetres=0;
                        Log.d("Eachkm",""+(milliperkmend - milliperkmstart) / 1000);


                        Long timeperkm=((milliperkmend - milliperkmstart) / 1000);
                        //Double dspkm=(distanceInMetres/1000);
                        Double avgspeekm=1/(timeperkm/Double.valueOf(3600));
                        if(avgspeekm>0)
                        {
                        PerkmArraylist.add(avgspeekm);
                        graphCusotmView.invalidate();
                        }
                        Log.d("Eachkm","Speed = "+ 1/(timeperkm/Double.valueOf(3600)));
                        milliperkmstart=System.currentTimeMillis();

                        //  PerkmArraylist.add(1/(timeperkm/Double.valueOf(3600)));
                    }

                    /*
                    Log.d("loc", "locationali " + distanceInMetres);
                    eachkm = Math.round(distanceInMetres / 1000);
                    //to get every km
                    if (eachkm > 1) {
                        Log.d("km complete", " km complete");
                        eachkm = 0;
                        millisend = System.currentTimeMillis();
                        //  Date dt = new Date(dtMili);
                        elapsed = Double.valueOf(SystemClock.elapsedRealtime() - chronometer.getBase());

                        Log.d("GetTime", "" + 1/(millisend - millistart) / 1000);
                      //  Log.d("GetTime", "" + elapsed / 1000);
                        // Log.d("chrono per km hour", "" + avghr);
                        // chronometerkm.stop();

                    }

                    lc.setText("" + (distanceInMetres / 1000));
                    */

                }
                lastLocation = location;


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                if (s.equals(LocationManager.GPS_PROVIDER)) {
                    requestforgps();
                    lastLocation=null;
                }


            }
        };
        return locationListener;
    }

    private void requestforgps() {
        Log.d("hechalla", "sdsds");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    public int getDskm() {
        if(dskm!=null)
        {
            Log.d("DSKM",""+dskm);

            return (int) Math.round(dskm)+1;

        }
        else {
            Log.d("DSKM",""+dskm);

            return 5;
        }



    }

    public ArrayList<Integer> getPerkmArraylist() {
        ArrayList<Integer> valvs=new ArrayList<>();
        Log.d("Perkm",""+PerkmArraylist.toString());

        valvs.add(1);
        for(int i=0;i<PerkmArraylist.size();i++)
        {
            valvs.add((int)Math.round(PerkmArraylist.get(i)));

        }
        //Collections.copy(valvs,PerkmArraylist);
     //   valvs.add(0);


       // valvs.add(120);


        /*
        if(PerkmArraylist!=null || PerkmArraylist.isEmpty())
        {
        for(Double d : PerkmArraylist){
            valvs.add(d.intValue());
        }
        return valvs;
        }
        else {
            valvs.add(25);
            valvs.add(1);

            valvs.add(5);
            valvs.add(10);
            return valvs;

        }
        */
        return valvs;

    }
    public int getmaxspeed() {
        ArrayList<Integer> valvs=new ArrayList<>();
        if(PerkmArraylist.size()>0)
        {
        valvs.add(1);
        for(int i=0;i<PerkmArraylist.size();i++)
        {

            valvs.add((int)Math.round(PerkmArraylist.get(i)));

        }
            int maxspeed;
        if(valvs.size()>0)
        {
         maxspeed=Collections.max(valvs);
            Log.d("MAXSPEED",""+maxspeed);

            return maxspeed;

        }
        else {
            return 0;

        }
        }
        else {

            return 120;
        }
    }
}

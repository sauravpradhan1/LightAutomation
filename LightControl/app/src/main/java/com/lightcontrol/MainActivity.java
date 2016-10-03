package com.lightcontrol;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.Callback;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    final String[] data ={"Welcome","On/Off","Motion Sensor","Schedule","GPS Location", "Info","About us"};
    final String[] fragments ={
            "com.lightcontrol.FragmentDisplay",
            "com.lightcontrol.FragmentOne",
            "com.lightcontrol.FragmentTwo",
            "com.lightcontrol.FragmentThree",
            "com.lightcontrol.FragmentFour",
            "com.lightcontrol.FragmentMore",
            "com.lightcontrol.About"

    };

    static private Pubnub pubnub;
    public static final String PUBLISH_KEY = "";
    public static final String SUBSCRIBE_KEY = "";


    public static Context mContext;
    static private Handler mHandler = new Handler();
    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    private DrawerLayout drawer;
    private ListView navList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayAdapter adapter = new ArrayAdapter(getSupportActionBar().getThemedContext(), android.R.layout.simple_list_item_1, data);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.drawer);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                        tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[pos]));
                        tx.commit();
                    }
                });
                drawer.closeDrawer(navList);
            }
        });

        drawer.openDrawer(navList);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[0]));
        tx.commit();

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.actionbar);

        mContext = getApplicationContext();
        initPubnub();

        h = new Handler();
        pd = new TransparentProgressDialog(this);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        pd.show();
        h.postDelayed(r,30000);

    }

    private void initPubnub() {
        pubnub = new Pubnub(PUBLISH_KEY,SUBSCRIBE_KEY);
        sendMessage("init", "Hello");
        checkStatus();
    }

    public static void sendMessage(String channel,String message){
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println(error.toString());
            }
        };
        pubnub.publish(channel, message, callback);
    }
    static public void sendMessage(String channel,JSONObject message,final Context context){
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println(error.toString());
            }
        };
        pubnub.publish(channel, message, callback);
    }
    private void checkStatus(){
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Connected To Server.", Toast.LENGTH_SHORT).show();
                    }
                });
                h.removeCallbacks(r);
                if (pd.isShowing() ) {
                    pd.dismiss();
                }
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Connection Failed!!", Toast.LENGTH_SHORT).show();
                    }
                });
                h.removeCallbacks(r);
                if (pd.isShowing() ) {
                    pd.dismiss();
                }
                System.out.println(error.toString());
            }
        };
        pubnub.time(callback);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // DO HERE ANYTHING WITH DATA
    }

    @Override
    protected void onStop() {
        //pubnub.shutdown();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        pubnub.shutdown();
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                if(drawer.isDrawerOpen(navList)){
                    drawer.closeDrawer(navList);
                }else {
                    drawer.openDrawer(navList);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

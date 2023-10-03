package com.ajiashi.youknowping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    ListView list;

    String TitleWeb[]={"Google","Youtube","Facebook","Twitter","Yahoo","TikTok","Snapchat","Netflix"};
    String IpWeb []= {"www.google.com","youtube.com","www.facebook.com","twitter.com","yahoo.com","www.tiktok.com","www.snapchat.com","www.netflix.com"};
    Integer [] arrayIcon = {R.drawable.googlefavicon,R.drawable.youtube,R.drawable.facebook,R.drawable.twitter,R.drawable.yahoo,R.drawable.tiktok,R.drawable.snapchat,R.drawable.netflix
    };
    ArrayList<Integer> times = new ArrayList<>();
    ArrayList<String> arraypings = new ArrayList<>();

    CustomAdapterPing adapter;

    Include include;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        include = new Include(MainActivity.this);

        list=(ListView)findViewById(R.id.list_ping);

        if (isOnline()==true){
            getAddresAndRun();
        }else {
            include.NoNetconnect();
            Toast.makeText(getApplicationContext(),"No NetworkConnected",Toast.LENGTH_SHORT).show();
        }

        ShowAds();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void getAddresAndRun(){
        for (int i = 0; i <= TitleWeb.length; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String netAddress = null;
                        try {
                            netAddress = new NetTask().execute(IpWeb[finalI]).get();
                            doPing(netAddress);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),netAddress,Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void appendResultsText(final String text,final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter=new CustomAdapterPing(MainActivity.this, TitleWeb, arraypings,arrayIcon, times);
                arraypings.add(text);
                times.add(Integer.valueOf(time));
                list.setAdapter(adapter);
            }
        });

    }

    private void doPing(String ip) throws Exception {
        String ipAddress = ip;

        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address","Invalid Ip Address");
            return;
        }

        // Perform a single synchronous ping
        PingResult pingResult = null;
        try {
            pingResult = Ping.onAddress(ipAddress).setTimeOutMillis(1000).doPing();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            appendResultsText(e.getMessage(),e.getMessage());
            return;
        }
        appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()),String.format("%.0f", pingResult.getTimeTaken()));


        // Perform an asynchronous ping
        Ping.onAddress(ipAddress).setTimeOutMillis(1000).setTimes(-1).doPing(new Ping.PingListener() {
            @Override
            public void onResult(PingResult pingResult) {
                if (pingResult.isReachable) {
                    appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()),String.format("%.0f", pingResult.getTimeTaken()));
                } else {

                }
            }

            @Override
            public void onFinished(PingStats pingStats) {
                Finished();
//                appendResultsText(String.format("Pings: %d, Packets lost: %d",
//                        pingStats.getNoPings(), pingStats.getPacketsLost()));
//                appendResultsText(String.format("Min/Avg/Max Time: %.2f/%.2f/%.2f ms",
//                        pingStats.getMinTimeTaken(), pingStats.getAverageTimeTaken(), pingStats.getMaxTimeTaken()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this,"fj",Toast.LENGTH_LONG).show();
            }
        });



    }

    public void Finished(){
        // Toast.makeText(PingSite.this,"jf",Toast.LENGTH_LONG).show();
    }

    public class NetTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            InetAddress addr = null;
            try
            {
                addr = InetAddress.getByName(params[0]);
            }

            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            return addr.getHostAddress();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.ratting:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            case R.id.privacy_policy:

                String url = "https://sites.google.com/view/youknowping/%D8%A7%D9%84%D8%B5%D9%81%D8%AD%D8%A9-%D8%A7%D9%84%D8%B1%D8%A6%D9%8A%D8%B3%D9%8A%D8%A9";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;

            case R.id.msg_with_me:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setData(Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"appamt703@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Hi Team Know You Ping");
                email.putExtra(Intent.EXTRA_TEXT, "");

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                return true;
            case R.id.rifsh:
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void ShowAds(){

    }

}
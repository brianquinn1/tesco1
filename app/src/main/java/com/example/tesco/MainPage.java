package com.example.tesco;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.example.tesco.R.id.responseView;

public class MainPage extends Activity{
    String scanContent;
    String scanFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        if(!isConnected(this)) buildDialog(this).show();

    }
    public void onClickScan(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();

        }
    }

    public void onClickSearch(View v) {
        //respond to clicks
        if (v.getId() == R.id.search_button) {
            Intent myIntent = new Intent(this, Search.class);
            startActivity(myIntent);

        }
    }

    public void onClickList(View v) {
        //respond to clicks
        if (v.getId() == R.id.list_button) {
            Intent myIntent = new Intent(this, List.class);
            startActivity(myIntent);

        }
    }


    public void onClickShop(View v) {
        //respond to clicks
        if (v.getId() == R.id.shop_button) {
            Intent myIntent = new Intent(this, Test.class);
            startActivity(myIntent);

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            Intent myIntent = new Intent(this, MainActivity.class);
            scanContent = scanningResult.getContents();
            scanFormat = scanningResult.getFormatName();
            myIntent.putExtra("scanContent",scanContent);
            myIntent.putExtra("scanFormat",scanFormat);
            startActivity(myIntent);
            Toast toast = Toast.makeText(getApplicationContext(),
                    scanContent, Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else return false;
    }


    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You need to turn on internet connection to use this app");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return builder;
    }

}


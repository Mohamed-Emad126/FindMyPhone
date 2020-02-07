package com.example.findmyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.findmyphone.DB_VM.AppDatabase;
import com.example.findmyphone.DB_VM.TrustedPerson;

import java.util.List;

public class CallReceiver extends BroadcastReceiver {

    boolean trusted = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            if(preferences.getBoolean("active_receiver", false)){



                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                    Bundle bundle = intent.getExtras();

                    String phoneNumber = null;
                    if (bundle != null) {
                        phoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    }

                    new mAsyncTask(context, phoneNumber).execute();

                    /*if(trusted){
                        Toast.makeText(context, String.valueOf(trusted), Toast.LENGTH_LONG).show();
                        doIt(context);
                    }*/

                }

            }
        }

    }

    private class mAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private List<TrustedPerson> person = null;
        private AppDatabase database;
        private String number;
        private Context context;
        public mAsyncTask(Context context, String number) {
            this.context = context;
            database = AppDatabase.getInstance(context);
            this.number = number;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(person.size() > 0){
                doIt(context);
                trusted = true;
            }

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            person = database.getDao().isTrusted(number);

            /*if(person.size() > 0){
                doIt(context);
                trusted = true;
            }
            else{
                trusted = false;
            }*/
            return true;
        }
    }

    public void doIt(Context context){

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolumeRing = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);


        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolumeRing/2,
                AudioManager.FLAG_PLAY_SOUND );

        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxVolumeRing/2,
                AudioManager.FLAG_PLAY_SOUND );

        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, maxVolumeRing/2,
                AudioManager.FLAG_PLAY_SOUND );

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeRing/2,
                AudioManager.FLAG_PLAY_SOUND );



    }

}
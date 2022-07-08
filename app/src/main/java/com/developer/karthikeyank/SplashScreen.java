package com.developer.karthikeyank;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    private TextToSpeech t1;
    private final String in= "PleaseWait";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        t1 = new TextToSpeech(SplashScreen.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!=TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ENGLISH);
                    t1.setSpeechRate(0.9f);
                    t1.speak(in,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSpeking();
            }
        },6000);
    }

    private void isSpeking() {
        if(t1.isSpeaking()){
            isSpeking();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            },1000);
        }
    }

}
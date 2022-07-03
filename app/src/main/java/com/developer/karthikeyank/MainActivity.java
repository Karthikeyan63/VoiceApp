package com.developer.karthikeyank;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int Request_code = 6383;
    private final String input = "What is the output of 2 *2?     Tell us Your Input?";
    private ImageView imageView;
    private TextToSpeech t1;
    private ProgressBar progressBar;
    private TextView bottom_text,textView;
    private  int i = 0;
    private final String anim = "( -  - )\n*\nWhat is the output of 2 *2?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        imageView.setEnabled(false);
        progressBar=findViewById(R.id.progressBar2);
        bottom_text=findViewById(R.id.textView_initialize);
        textView=findViewById(R.id.textview);

        Speech(input);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,2000);

                try {
                    startActivityForResult(intent,Request_code);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Sorry! Device Not Support", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //calling for delay speech in first time.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSpeak();
            }
        },5000);

        AnimateText(anim);

    }



    //this will perform speech is finished or not
    private void isSpeak() {
        if (t1.isSpeaking()){
            isSpeak();
        }
        else {
            progressBar.animate().alpha(0).setDuration(1000);
            bottom_text.animate().alpha(0).setDuration(1000);
            imageView.setEnabled(true);
            imageView.animate().alpha(1).setDuration(1000);
            textView.setText("( -  - )\n*\nWhat is the output of 2 *2?\n\nTap To Speak");
            Toast.makeText(this, "Speech Finished!", Toast.LENGTH_SHORT).show();
            imageView.performClick();
        }
    }

    //for speech
    private void Speech(String in){
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!=TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ENGLISH);
                    t1.setSpeechRate(1.0f);
                    t1.speak(in,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Request_code){

            if (resultCode==RESULT_OK && data!=null){
                ArrayList<String> voice_data = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (voice_data.get(0).replace(" ","").equals("4") || voice_data.get(0).replace(" ","").equals("four") ){
                    Speech("Correct Answer!");
                    Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Speech("InCorrect Answer!");
                    Toast.makeText(this, "InCorrect Answer!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    //this will animate a text
    private void AnimateText(String anim) {
        if(i<=anim.length()){
            textView.setText(anim.substring(0,i));
            ++i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimateText(anim);
                }
            },40);

        }
    }

    @Override
    protected void onDestroy() {
        t1.stop();
        t1.shutdown();
        super.onDestroy();
    }
}
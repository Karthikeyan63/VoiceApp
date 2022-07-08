package com.developer.karthikeyank;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView question_top_seek;
    private TextView question;
    private TextView input_answer;
    private ImageView retry;
    private TextView count_down;
    private ConstraintLayout constraintLayout;
    public int Number1=0,Number2=0;
    private final int Request_code = 6383;
    private TextToSpeech t1;
    int keyid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //assign id
        question_top_seek = findViewById(R.id.textView_main_count_question);
        question = findViewById(R.id.textview);
        input_answer = findViewById(R.id.textView_input_answer);
        retry = findViewById(R.id.imageView_retry);
        retry.setEnabled(false);
        count_down = findViewById(R.id.textView_cont_down);
        constraintLayout = findViewById(R.id.constrain_main_activity_top);

        StartASpeak();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyid=1;
                ActivateInput();
            }
        });

    }

    private void StartASpeak() {
        Number1=MaterialsQ.RandomNumber();
        Number2=MaterialsQ.RandomNumber();
        question_top_seek.setText("Question "+MaterialsQ.Running_Question+"/"+MaterialsQ.Total_Question);
        question.setText("What is "+Number1+" * "+Number2+" = ?");
        String total_speak = "Question "+MaterialsQ.Running_Question+" of "+MaterialsQ.Total_Question+"     What is "+Number1+" * "+Number2;
        constraintLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.updown));
        Speech(total_speak);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSpeak();
            }
        },5000);
    }

    private void Speech(String in){
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!=TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ENGLISH);
                    t1.setSpeechRate(0.7f);
                    t1.speak(in,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    private void isSpeak() {
        if (t1.isSpeaking()){
            isSpeak();
        }
        else {
            ActivateInput();
        }
    }

    private void ActivateInput(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Request_code){
            if (resultCode==RESULT_OK && data!=null){
                ArrayList<String> voice_data = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                int out = Number1*Number2;
                if (voice_data.get(0).replace(" ","").equals(String.valueOf(out))){
                    Speech("Correct Answer!");
                    input_answer.setText("Your Answer : "+voice_data.get(0)+" (correct)");
                    MaterialsQ.Total_Score += 1;
                    MoveNextQuestion();
                }
                else {
                    keyid=0;
                    Speech("InCorrect Answer!");
                    input_answer.setText("Your Answer : "+voice_data.get(0)+" (Incorrect)");
                    retry.animate().alpha(1).setDuration(500);
                    count_down.animate().alpha(1).setDuration(500);
                    retry.setEnabled(true);
                    RunCount(5);
                }
            }

        }
    }

    private void RunCount(int counts){
        if(counts==1){
            MoveNextQuestion();
        }else {
            count_down.setText(String.valueOf(counts));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(keyid==0) {
                        RunCount(counts - 1);
                    }
                }
            },1000);
        }
    }

    private void MoveNextQuestion(){
        retry.setEnabled(false);
        retry.animate().alpha(0).setDuration(1000);
        count_down.animate().alpha(0).setDuration(1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MaterialsQ.Total_Question==MaterialsQ.Running_Question){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Total Score").setMessage("You Scored : "+MaterialsQ.Total_Score)
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MaterialsQ.Running_Question=1;
                                    MaterialsQ.Total_Score=0;
                                    input_answer.setText("");
                                    StartASpeak();
                                }
                            }).create();
                    alertDialog.show();
                }
                else {
                    input_answer.setText("");
                    MaterialsQ.Running_Question += 1;
                    StartASpeak();
                }
            }
        },1500);
    }

    @Override
    protected void onDestroy() {
        t1.stop();
        t1.shutdown();
        super.onDestroy();
    }
}
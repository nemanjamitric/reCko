package com.midln.recko;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //get words from previous activity
        WordsObject wordsObject = (WordsObject) getIntent().getSerializableExtra("wordsGlobal");
        //get dp sizes
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        //prepare for adding elements to view dynamically
        LayoutInflater factory = LayoutInflater.from(this);
        LinearLayout mll = (LinearLayout) this.findViewById(R.id.firstRow);
        LinearLayout sll = (LinearLayout) this.findViewById(R.id.secondRow);

        Random rand = new Random(); //instance of random class
        List<Word> wordsAtLevel = new ArrayList<Word>();
        for(int i = 0; i < wordsObject.Words.size(); i++){
            Word wordTemp = wordsObject.Words.get(i);
            if(wordTemp.Level == 1)
                wordsAtLevel.add(wordTemp);
        }
        int upperbound = wordsAtLevel.size()-1;
        //generate random values from 0-24
        Word gameWord = wordsAtLevel.get(rand.nextInt(upperbound));

        int[] ids = new int[gameWord.WordEn.length()];
        for(int i = 0; i < gameWord.WordEn.length(); i++) {
            View myView = factory.inflate(R.layout.letter_serb, null);
            ids[i] = View.generateViewId();
            myView.setId(ids[i]);
            RelativeLayout rl = myView.findViewById(R.id.relativelayout2);
            ViewGroup.LayoutParams layoutParams = rl.getLayoutParams();
            //calculate dps
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (dpWidth/gameWord.WordEn.length() * scale + 0.5f);
            layoutParams.width = pixels;
            rl.setLayoutParams(layoutParams);
            TextView tv = myView.findViewById(R.id.textView2);
            tv.setText(String.valueOf(gameWord.WordEn.charAt(i)).toUpperCase(Locale.ROOT));
            // Add the custom layout element to the LinearLayout
            mll.addView(myView);
        }


        int[] ids2 = new int[gameWord.WordSr.length()];
        for(int i = 0; i < gameWord.WordSr.length(); i++) {
            View myView = factory.inflate(R.layout.activity_letter, null);
            ids2[i] = View.generateViewId();
            myView.setId(ids2[i]);
            RelativeLayout rl = myView.findViewById(R.id.relativelayout);
            ViewGroup.LayoutParams layoutParams = rl.getLayoutParams();
            //calculate dps
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (dpWidth/gameWord.WordSr.length() * scale + 0.5f);
            layoutParams.width = pixels;
            rl.setLayoutParams(layoutParams);
            TextView tv = myView.findViewById(R.id.textView);
            tv.setText(String.valueOf(gameWord.WordSr.charAt(i)).toUpperCase(Locale.ROOT));
            // Add the custom layout element to the LinearLayout
            sll.addView(myView);
        }
    }
}
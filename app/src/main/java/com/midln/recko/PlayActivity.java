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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayActivity extends AppCompatActivity {

    //save id's for later
    public  IdsClass idsClass;
    //global pizza ids
    int[] idsOfRandLetters = new int[9];
    //remember count for current char
    public int counterOfCurrentWordChar = 0;

    //current global level
    public int level = 0;
    //current global level progress
    public int levelProgress = 0;

    //remember current Word as global
    public Word currentWord;
    //intent globals
    public WordsObject wordsObject;
    public Users usersObject;
    public User userCurrent;

    public TextView levelTv;
    public ProgressBar levelProgressView;
    public TextView timerTv;

    public int timerCounter = 0;

    class Helper extends TimerTask {
        public void run() {
            String s = String.valueOf(timerCounter++) + "s";
          timerTv.setText(s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //global values of ids for all rand letters in pizza
        idsOfRandLetters[0] = R.id.rand1;
        idsOfRandLetters[1] = R.id.rand2;
        idsOfRandLetters[2] = R.id.rand3;
        idsOfRandLetters[3] = R.id.rand4;
        idsOfRandLetters[4] = R.id.rand5;
        idsOfRandLetters[5] = R.id.rand6;
        idsOfRandLetters[6] = R.id.rand7;
        idsOfRandLetters[7] = R.id.rand8;
        idsOfRandLetters[8] = R.id.rand9;

        super.onCreate(savedInstanceState);

        Timer timer = new Timer();

        // Helper class extends TimerTask
        TimerTask task = new Helper();
        timer.schedule(task, 200, 1000);

        setContentView(R.layout.activity_play);
        //get words from previous activity
        wordsObject = (WordsObject) getIntent().getSerializableExtra("wordsGlobal");
        usersObject = (Users) getIntent().getSerializableExtra("usersGlobal");
        userCurrent = (User) getIntent().getSerializableExtra("userGlobal");

        levelTv = this.findViewById(R.id.level);
        levelProgressView = this.findViewById(R.id.progressBar2);
        timerTv = this.findViewById(R.id.time);

        changeXP();

        //start initial game
        idsClass = loadWord(wordsObject, level, idsOfRandLetters);
        deleteLetters();

        // initiate and perform click event on restart word
        ImageButton restart = (ImageButton)findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reload word
                idsClass =  loadWord(wordsObject, level, idsOfRandLetters);
                deleteLetters();
            }
        });
        // delete letters from result
        ImageButton trash = (ImageButton)findViewById(R.id.trash);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reload word
                deleteLetters();
            }
        });

        // delete letters from result
        ImageButton next = (ImageButton)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check word
                checkWord();
            }
        });

        for (int i =0; i < idsOfRandLetters.length; i++){
            TextView tv = (TextView) findViewById(idsOfRandLetters[i]);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLetter(tv.getText().toString());
                }
            });
        }

    }

    public IdsClass loadWord(WordsObject wordsObject, int level, int[] idsOfRandLetters){
        //reset timer
        timerCounter = 0;

        //get dp sizes
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        //prepare for adding elements to view dynamically
        LayoutInflater factory = LayoutInflater.from(this);
        LinearLayout mll = (LinearLayout) this.findViewById(R.id.firstRow);
        LinearLayout sll = (LinearLayout) this.findViewById(R.id.secondRow);
        View randLetters = this.findViewById(R.id.randLetters);

        //remove previous reviews
        mll.removeAllViews();
        sll.removeAllViews();

        //remove previous letters in pizza
        for (int i = 0; i < idsOfRandLetters.length; i++ ) {
            TextView randTv = randLetters.findViewById(idsOfRandLetters[i]);
            randTv.setText("X");
        }

        Random rand = new Random(); //instance of random class
        List<Word> wordsAtLevel = new ArrayList<Word>();
        for(int i = 0; i < wordsObject.Words.size(); i++){
            Word wordTemp = wordsObject.Words.get(i);
            boolean passed = false;
            if(wordTemp.Level == level) {
                //check if user didn't already pass this word
                for (WordUser wu : userCurrent.WordsForUser) {
                    if (wu.WordSr == wordTemp.WordSr)
                        passed = true;
                }
                if (!passed)
                    wordsAtLevel.add(wordTemp);
            }
        }
        int upperbound = wordsAtLevel.size();
        //generate random values from 0-24
        Word gameWord = wordsAtLevel.get(rand.nextInt(upperbound));
        //save word as current global
        currentWord = gameWord;

        int[] ids = new int[gameWord.WordSr.length()];
        for(int i = 0; i < gameWord.WordSr.length(); i++) {
            View myView = factory.inflate(R.layout.letter_serb, null);
            ids[i] = View.generateViewId();
            myView.setId(ids[i]);
            RelativeLayout rl = myView.findViewById(R.id.relativelayout2);
            ViewGroup.LayoutParams layoutParams = rl.getLayoutParams();
            //calculate dps
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (dpWidth/gameWord.WordSr.length() * scale + 0.5f);
            layoutParams.width = pixels;
            rl.setLayoutParams(layoutParams);
            TextView tv = myView.findViewById(R.id.textView2);
            tv.setText(String.valueOf(gameWord.WordSr.charAt(i)).toUpperCase(Locale.ROOT));
            // Add the custom layout element to the LinearLayout
            mll.addView(myView);
        }

        rand = new Random();
        upperbound = gameWord.WordEn.length();
        int[] checked = new int[gameWord.WordEn.length()];

        int[] ids2 = new int[gameWord.WordEn.length()];

        for(int i = 0; i < gameWord.WordEn.length(); i++) {
            //create random value and check if that rand letter has been filled in pizza
            int random = 0;
            boolean contained = true;
            while(contained && Arrays.stream(checked).anyMatch(k -> k == 0)) {
                random = rand.nextInt(upperbound);
                contained = false;
                for (int j = 0; j < checked.length; j++) {
                    if(checked[j] == random + 1)
                        contained = true;
                }
            }
            View myView = factory.inflate(R.layout.activity_letter, null);
            ids2[i] = View.generateViewId();
            myView.setId(ids2[i]);

            //set char in pizza letters
            TextView randTv = randLetters.findViewById(idsOfRandLetters[random]);
            randTv.setText(String.valueOf(gameWord.WordEn.charAt(i)).toUpperCase(Locale.ROOT));
            checked[i] = random + 1;

            RelativeLayout rl = myView.findViewById(R.id.relativelayout);
            ViewGroup.LayoutParams layoutParams = rl.getLayoutParams();
            //calculate dps
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (dpWidth/gameWord.WordEn.length() * scale + 0.5f);
            layoutParams.width = pixels;
            rl.setLayoutParams(layoutParams);
            TextView tv = myView.findViewById(R.id.textView);
            tv.setText(String.valueOf(gameWord.WordEn.charAt(i)).toUpperCase(Locale.ROOT));
            // Add the custom layout element to the LinearLayout
            sll.addView(myView);
        }
        IdsClass idsClass = new IdsClass();
        idsClass.ids = ids;
        idsClass.ids2 = ids2;
        return idsClass;
    }

    public void deleteLetters(){
        for (int i =0; i < idsClass.ids2.length; i++) {
            View myView = this.findViewById(idsClass.ids2[i]);
            TextView tv = myView.findViewById(R.id.textView);
            tv.setText("");
            counterOfCurrentWordChar = 0;
        }
    }

    public void addLetter(String letter){
            View myView = this.findViewById(idsClass.ids2[counterOfCurrentWordChar]);
            TextView tv = myView.findViewById(R.id.textView);
            tv.setText(letter);
            counterOfCurrentWordChar++;
    }

    public void checkWord(){
        String wordToCheck = "";
        for (int i =0; i < idsClass.ids2.length; i++) {
            View myView = this.findViewById(idsClass.ids2[i]);
            TextView tv = myView.findViewById(R.id.textView);
            wordToCheck = wordToCheck.concat(tv.getText().toString());
        }
        if(wordToCheck.equals(currentWord.WordEn.toUpperCase(Locale.ROOT))) {
            //show data in toast
            Toast toast = Toast.makeText(getApplicationContext(), "Tacno!", Toast.LENGTH_SHORT);
            toast.show();
            userCurrent.XP += 20;
            int time = Integer.parseInt(timerTv.getText().toString().replace('s',' ').trim()); // hardcoded
            userCurrent.WordsForUser.add(new WordUser(currentWord.WordSr, time));
            changeXP();
            idsClass = loadWord(wordsObject,level,idsOfRandLetters);
            deleteLetters();
        }
        else{
            //show data in toast
            Toast toast = Toast.makeText(getApplicationContext(), "Pogresno.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void changeXP(){
        //set level progress
        level = userCurrent.XP/100;
        levelProgress = userCurrent.XP % (level * 100);
        levelTv.setText(String.valueOf(level));
        levelProgressView.setProgress(levelProgress);
    }
}
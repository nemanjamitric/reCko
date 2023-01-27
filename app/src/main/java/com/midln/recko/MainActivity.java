package com.midln.recko;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;
    WordsObject wordsGlobal = new WordsObject();
    Users usersGlobal = new Users();
    TextView tv;
    Button pocniBtn;
    int language = 0; //0 = serb | 1 = eng
    TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8,textView9,textView10;
    TextView textView1points,textView2points,textView3points,textView4points,textView5points,textView6points,textView7points,textView8points,textView9points,textView10points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        language = getIntent().getIntExtra("language", 0);
        ImageButton langPicker = (ImageButton) findViewById(R.id.language);
        langPicker.setBackground(ContextCompat.getDrawable(getApplicationContext(), language == 0 ? R.drawable.serb : R.drawable.eng));

        TextView leaderboard = (TextView) findViewById(R.id.leaderBoard);
        leaderboard.setText(language == 0 ? "Lestvica" : "Leaderboard");
        TextView nameLabel = (TextView) findViewById(R.id.nameFieldLabel);
        nameLabel.setText(language == 0 ? "Unesi ime" : "Enter name");
        Button pocniBtn = (Button) findViewById(R.id.pocniBtn);
        pocniBtn.setText(language == 0 ? "Počni" : "Start");

        pocniBtn = findViewById(R.id.pocniBtn);
        tv = this.findViewById(R.id.image_view);
        textView1 = this.findViewById(R.id.textView1);
        textView1points = this.findViewById(R.id.textView1points);
        textView2 = this.findViewById(R.id.textView2);
        textView2points = this.findViewById(R.id.textView2points);
        textView3 = this.findViewById(R.id.textView3);
        textView3points = this.findViewById(R.id.textView3points);
        textView4 = this.findViewById(R.id.textView4);
        textView4points = this.findViewById(R.id.textView4points);
        textView5 = this.findViewById(R.id.textView5);
        textView5points = this.findViewById(R.id.textView5points);
        textView6 = this.findViewById(R.id.textView6);
        textView6points = this.findViewById(R.id.textView6points);
        textView7 = this.findViewById(R.id.textView7);
        textView7points = this.findViewById(R.id.textView7points);
        textView8 = this.findViewById(R.id.textView8);
        textView8points = this.findViewById(R.id.textView8points);
        textView9 = this.findViewById(R.id.textView9);
        textView9points = this.findViewById(R.id.textView9points);
        textView10 = this.findViewById(R.id.textView10);
        textView10points = this.findViewById(R.id.textView10points);


        // change language
        ImageButton lang = (ImageButton) findViewById(R.id.language);
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change language where it is needed
                language = language == 0 ? 1 : 0;

                ImageButton langPicker = (ImageButton) findViewById(R.id.language);
                langPicker.setBackground(ContextCompat.getDrawable(getApplicationContext(), language == 0 ? R.drawable.serb : R.drawable.eng));

                TextView leaderboard = (TextView) findViewById(R.id.leaderBoard);
                leaderboard.setText(language == 0 ? "Lestvica" : "Leaderboard");
                TextView nameLabel = (TextView) findViewById(R.id.nameFieldLabel);
                nameLabel.setText(language == 0 ? "Unesi ime" : "Enter name");
                Button pocniBtn = (Button) findViewById(R.id.pocniBtn);
                pocniBtn.setText(language == 0 ? "Počni" : "Start");
            }
        });

        pocniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = tv.getText().toString().replace("\n", "").replace("\r", "").trim();
                User userGlobal = new User(userName, 0);
                for (User user : usersGlobal.Users) {
                    if (user.UserName.equals(userName))
                        userGlobal = user;
                }

                if (tv.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), language == 0 ? "Molimo Vas unesite vaše ime" : "Please enter your name", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent switchActivityIntent = new Intent(MainActivity.this, PlayActivity.class);
                    switchActivityIntent.putExtra("wordsGlobal", wordsGlobal);
                    switchActivityIntent.putExtra("usersGlobal", usersGlobal);
                    switchActivityIntent.putExtra("userGlobal", userGlobal);
                    switchActivityIntent.putExtra("language", language);

                    startActivity(switchActivityIntent);
                }
            }
        });

        //check if we are connected to internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if(connected) {
            //json call
            new GetWords().execute("https://api.jsonbin.io/v3/b/638b6b14003d6444ce61ea62/");
            //end of json call
        }
        else {
            //get offline version
         readFromFile("jsonWords.txt");
         readFromFile("jsonUsers.txt");
        }
    }

    //json functions
    private class GetUsers extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(language == 0 ? "Molimo sačekajte" : "Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Master-Key", "$2b$10$BPLdapvarWs/vuRwoTuoqOe0X6kGVmDMI/y9zyRDY7sRM9C1LSt/6");
                connection.setRequestProperty("X-Access-Key", "$2b$10$V6CKmezfWtmInV8l6MWvK.RZYAyQbNA6vKJpD2pbLDimvVk8IkBpi");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                   Log.d("Users: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            writeToFile(result,"jsonUsers.txt");

            Users users = new Users();
            //convert retrieved json file to object
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject record = jObject.getJSONObject("record");

                JSONArray jArray = record.getJSONArray("Users");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        // Pulling items from the array
                        String userName = jArray.getJSONObject(i).getString("UserName");
                        int xp = jArray.getJSONObject(i).getInt("XP");
                        JSONArray jArray2 = jArray.getJSONObject(i).getJSONArray("WordsForUser");

                        User user = new User(userName, xp);

                        for (int j = 0; j < jArray2.length(); j++) {
                            String wordSr = jArray2.getJSONObject(j).getString("WordSr");
                            int time = jArray2.getJSONObject(j).getInt("Time");
                            user.WordsForUser.add(new WordUser(wordSr, time));
                        }

                        users.Users.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            //show data in toast
//            Toast toast = Toast.makeText(getApplicationContext(), (CharSequence) users.Users.get(0), Toast.LENGTH_LONG);
//            toast.show();

            //save data for post
            usersGlobal = users;

            //leader board logic

            Users orderedByXPUsers = users;

            boolean sorted = false;
            User temp;
            while(!sorted) {
                sorted = true;
                for (int i = 0; i < orderedByXPUsers.Users.size() - 1; i++) {
                    int fullTime = 0;
                    for(WordUser word : orderedByXPUsers.Users.get(i).WordsForUser){
                        fullTime += word.Time;
                    }
                    int fullTime2 = 0;
                    for(WordUser word : orderedByXPUsers.Users.get(i + 1).WordsForUser){
                        fullTime2 += word.Time;
                    }
                    if ((orderedByXPUsers.Users.get(i).WordsForUser.size() * 100) / (fullTime + 1) < (orderedByXPUsers.Users.get(i + 1).WordsForUser.size() * 100) / (fullTime2 + 1)) {
                        temp = orderedByXPUsers.Users.get(i);
                        orderedByXPUsers.Users.set(i, orderedByXPUsers.Users.get(i+1));
                        orderedByXPUsers.Users.set(i + 1, temp);
                        sorted = false;
                    }
                }
            }
            for (int i = 0; i < orderedByXPUsers.Users.size(); i++) {
                User x = orderedByXPUsers.Users.get(i);
                int fullTime = 0;
                for(WordUser word : orderedByXPUsers.Users.get(i).WordsForUser){
                    fullTime += word.Time;
                }
                int score = ((100 * x.WordsForUser.size()) / (fullTime + 1) );
                switch (i){
                    case (0):{
                        textView1.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView1points.setText(String.valueOf(score));
                        break;
                    }
                    case (1):{
                        textView2.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView2points.setText(String.valueOf(score));
                        break;
                    }
                    case (2):{
                        textView3.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView3points.setText(String.valueOf(score));
                        break;
                    }
                    case (3):{
                        textView4.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView4points.setText(String.valueOf(score));
                        break;
                    }
                    case (4):{
                        textView5.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView5points.setText(String.valueOf(score));
                        break;
                    }
                    case (5):{
                        textView6.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView6points.setText(String.valueOf(score));
                        break;
                    }
                    case (6):{
                        textView7.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView7points.setText(String.valueOf(score));
                        break;
                    }
                    case (7):{
                        textView8.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView8points.setText(String.valueOf(score));
                        break;
                    }
                    case (8):{
                        textView9.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView9points.setText(String.valueOf(score));
                        break;
                    }
                    case (9):{
                        textView10.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView10points.setText(String.valueOf(score));
                        break;
                    }
                }
            }
        }
    }

    //json functions
    private class GetWords extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(language == 0 ? "Molimo sačekajte" : "Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Master-Key", "$2b$10$BPLdapvarWs/vuRwoTuoqOe0X6kGVmDMI/y9zyRDY7sRM9C1LSt/6");
                connection.setRequestProperty("X-Access-Key", "$2b$10$V6CKmezfWtmInV8l6MWvK.RZYAyQbNA6vKJpD2pbLDimvVk8IkBpi");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
//                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            //update offline file
            writeToFile(result, "jsonWords.txt");

            WordsObject words = new WordsObject();
            //convert retrieved json file to object
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject record = jObject.getJSONObject("record");
                words.Version = record.getInt("Version");

                JSONArray jArray = record.getJSONArray("Words");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        // Pulling items from the array
                        String wordSr = jArray.getJSONObject(i).getString("WordSr");
                        String wordEn = jArray.getJSONObject(i).getString("WordEn");
                        int level = jArray.getJSONObject(i).getInt("Level");
                        //add to object
                        words.Words.add(new Word(wordSr, wordEn, level));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            //show data in toast
//            Toast toast = Toast.makeText(getApplicationContext(), words.Words.get(0).WordEn, Toast.LENGTH_LONG);
//            toast.show();

            //save data for post
            wordsGlobal = words;

            //get users before start of the game and check
            new GetUsers().execute("https://api.jsonbin.io/v3/b/638b6c487966e84526d32e7e/");
        }
    }

    //end of json functions

    private void writeToFile(String data, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void readFromFile(String name) {

        String result = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput(name);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        if(name == "jsonWords.txt"){

            WordsObject words = new WordsObject();
            //convert retrieved json file to object
            try {
            JSONObject jObject = new JSONObject(result);
            JSONObject record = jObject.getJSONObject("record");
            words.Version = record.getInt("Version");

            JSONArray jArray = record.getJSONArray("Words");
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    // Pulling items from the array
                    String wordSr = jArray.getJSONObject(i).getString("WordSr");
                    String wordEn = jArray.getJSONObject(i).getString("WordEn");
                    int level = jArray.getJSONObject(i).getInt("Level");
                    //add to object
                    words.Words.add(new Word(wordSr, wordEn, level));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //save data for post
        wordsGlobal = words;
        }
        else if(name == "jsonUsers.txt"){

            Users users = new Users();
            //convert retrieved json file to object
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject record = jObject.getJSONObject("record");

                JSONArray jArray = record.getJSONArray("Users");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        // Pulling items from the array
                        String userName = jArray.getJSONObject(i).getString("UserName");
                        int xp = jArray.getJSONObject(i).getInt("XP");
                        JSONArray jArray2 = jArray.getJSONObject(i).getJSONArray("WordsForUser");

                        User user = new User(userName, xp);

                        for (int j = 0; j < jArray2.length(); j++) {
                            String wordSr = jArray2.getJSONObject(j).getString("WordSr");
                            int time = jArray2.getJSONObject(j).getInt("Time");
                            user.WordsForUser.add(new WordUser(wordSr, time));
                        }

                        users.Users.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //save data for post
            usersGlobal = users;

            //leader board logic
            Users orderedByXPUsers = users;

            boolean sorted = false;
            User temp;
            while(!sorted) {
                sorted = true;
                for (int i = 0; i < orderedByXPUsers.Users.size() - 1; i++) {
                    int fullTime = 0;
                    for(WordUser word : orderedByXPUsers.Users.get(i).WordsForUser){
                        fullTime += word.Time;
                    }
                    int fullTime2 = 0;
                    for(WordUser word : orderedByXPUsers.Users.get(i + 1).WordsForUser){
                        fullTime2 += word.Time;
                    }
                    if ((orderedByXPUsers.Users.get(i).WordsForUser.size() * (100/(fullTime + 1))) < (orderedByXPUsers.Users.get(i+1).WordsForUser.size() * (100/(fullTime2 + 1)))) {
                        temp = orderedByXPUsers.Users.get(i);
                        orderedByXPUsers.Users.set(i, orderedByXPUsers.Users.get(i+1));
                        orderedByXPUsers.Users.set(i + 1, temp);
                        sorted = false;
                    }
                }
            }
            for (int i = 0; i < orderedByXPUsers.Users.size(); i++) {
                User x = orderedByXPUsers.Users.get(i);
                int fullTime = 0;
                for(WordUser word : orderedByXPUsers.Users.get(i).WordsForUser){
                    fullTime += word.Time;
                }
                int score = x.WordsForUser.size() * (100/(fullTime + 1));
                switch (i){
                    case (0):{
                        textView1.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView1points.setText(String.valueOf(score));
                        break;
                    }
                    case (1):{
                        textView2.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView2points.setText(String.valueOf(score));
                        break;
                    }
                    case (2):{
                        textView3.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView3points.setText(String.valueOf(score));
                        break;
                    }
                    case (3):{
                        textView4.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView4points.setText(String.valueOf(score));
                        break;
                    }
                    case (4):{
                        textView5.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView5points.setText(String.valueOf(score));
                        break;
                    }
                    case (5):{
                        textView6.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView6points.setText(String.valueOf(score));
                        break;
                    }
                    case (6):{
                        textView7.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView7points.setText(String.valueOf(score));
                        break;
                    }
                    case (7):{
                        textView8.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView8points.setText(String.valueOf(score));
                        break;
                    }
                    case (8):{
                        textView9.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView9points.setText(String.valueOf(score));
                        break;
                    }
                    case (9):{
                        textView10.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView10points.setText(String.valueOf(score));
                        break;
                    }
                }
            }
        }
    }
}
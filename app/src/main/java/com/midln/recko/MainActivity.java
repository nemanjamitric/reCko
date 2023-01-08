package com.midln.recko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;
    WordsObject wordsGlobal = new WordsObject();
    Users usersGlobal = new Users();
    TextView tv;
    Button pocniBtn;
    TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8,textView9,textView10;
    TextView textView1points,textView2points,textView3points,textView4points,textView5points,textView6points,textView7points,textView8points,textView9points,textView10points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        pocniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = tv.getText().toString();
                User userGlobal = new User(userName, 0);
                for (User user : usersGlobal.Users) {
                    if (user.UserName.equals(userName))
                        userGlobal = user;
                }

                if (tv.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Molimo Vas unesite vaše ime", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent switchActivityIntent = new Intent(MainActivity.this, PlayActivity.class);
                    switchActivityIntent.putExtra("wordsGlobal", wordsGlobal);
                    switchActivityIntent.putExtra("usersGlobal", usersGlobal);
                    switchActivityIntent.putExtra("userGlobal", userGlobal);

                    startActivity(switchActivityIntent);
                }
            }
        });

        //json call
        new GetWords().execute("https://api.jsonbin.io/v3/b/638b6b14003d6444ce61ea62/");
        //end of json call
    }

    //json functions
    private class GetUsers extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Molimo sačekajte");
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
//                    Log.d("Users: ", "> " + line);   //here u ll get whole response...... :-)

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


            Users orderedByXPUsers = users;

            boolean sorted = false;
            User temp;
            while(!sorted) {
                sorted = true;
                for (int i = 0; i < orderedByXPUsers.Users.size() - 1; i++) {
                    if (orderedByXPUsers.Users.get(i).XP < orderedByXPUsers.Users.get(i+1).XP) {
                        temp = orderedByXPUsers.Users.get(i);
                        orderedByXPUsers.Users.set(i, orderedByXPUsers.Users.get(i+1));
                        orderedByXPUsers.Users.set(i + 1, temp);
                        sorted = false;
                    }
                }
            }
            for (int i = 0; i < orderedByXPUsers.Users.size(); i++) {
                User x = orderedByXPUsers.Users.get(i);
                switch (i){
                    case (0):{
                        textView1.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView1points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (1):{
                        textView2.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView2points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (2):{
                        textView3.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView3points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (3):{
                        textView4.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView4points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (4):{
                        textView5.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView5points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (5):{
                        textView6.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView6points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (6):{
                        textView7.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView7points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (7):{
                        textView8.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView8points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (8):{
                        textView9.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView9points.setText(String.valueOf(x.XP));
                        break;
                    }
                    case (9):{
                        textView10.setText(String.valueOf(x.UserName.trim().isEmpty() ? "Bezimeni" : x.UserName));
                        textView10points.setText(String.valueOf(x.XP));
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
            pd.setMessage("Molimo sačekajte");
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
}
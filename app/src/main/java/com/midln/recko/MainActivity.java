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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pocniBtn = findViewById(R.id.pocniBtn);
        tv = this.findViewById(R.id.image_view);


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
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

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
//            Toast toast = Toast.makeText(getApplicationContext(), words.Words.get(0).WordEn, Toast.LENGTH_LONG);
//            toast.show();

            //save data for post
            usersGlobal = users;
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
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

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
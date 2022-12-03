package com.midln.recko;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button pocniBtn;

    @SuppressLint("MissingInflatedId")
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pocniBtn = findViewById(R.id.pocniBtn);
        pocniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(switchActivityIntent);
            }
        });
        Toast toast = Toast.makeText(getApplicationContext(), "Cao", Toast.LENGTH_LONG);
        toast.show();
        //json call
        new JsonTask().execute("https://api.jsonbin.io/v3/b/638b6b14003d6444ce61ea62/");
        //end of json call
    }

    //json functions
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty ("X-Master-Key", "$2b$10$BPLdapvarWs/vuRwoTuoqOe0X6kGVmDMI/y9zyRDY7sRM9C1LSt/6");
                connection.setRequestProperty ("X-Access-Key", "$2b$10$V6CKmezfWtmInV8l6MWvK.RZYAyQbNA6vKJpD2pbLDimvVk8IkBpi");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
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
            if (pd.isShowing()){
                pd.dismiss();
            }
            WordsObject words = new WordsObject();
            //convert retrieved json file to object
            try {
                JSONObject jObject = new JSONObject(result);
                JSONObject record = jObject.getJSONObject("record");
                words.Version = record.getInt("version");

                JSONArray jArray = record.getJSONArray("words");
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        // Pulling items from the array
                        String wordSr = jArray.getJSONObject(i).getString("word_sr");
                        String wordEn = jArray.getJSONObject(i).getString("word_en");
                        int level = jArray.getJSONObject(i).getInt("level");
                        //add to object
                        words.Words.add(new Word(wordSr,wordEn,level));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast toast = Toast.makeText(getApplicationContext(), words.Words.get(0).WordEn, Toast.LENGTH_LONG);
            toast.show();
        }
    }
    //end of json functions
}
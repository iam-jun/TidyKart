package fpoly.pro205.fit.activities;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fpoly.pro205.fit.utils.Utils;


/**
 * Created by ADMIN on 9/9/2016.
 */
public class PostGetRequest extends AsyncTask<String,String,String> {

    private int method;
    private String api;
    private String body;

    public PostGetRequest(int method,String api,String body){
        this.method = method;
        this.api = api;
        this.body = body;
    }
    public PostGetRequest(int method,String api){
        this.method = method;
        this.api = api;
    }
    @Override
    protected String doInBackground(String... params) {

        if (method == Utils.POST_REQUEST){
            return PostRequest(this.api,this.body);
        }

        if (method == Utils.GET_REQUEST){
            return GetRequest(this.api);
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        //callback.result(s);
        System.out.println(s);

        super.onPostExecute(s);
    }

    private String GetRequest(String api){
        String output = "";
        try {

            String link = api;
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");






            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String result = "";
            while ((result = br.readLine()) != null) {
                output += result;
            }


            conn.disconnect();

        } catch (Exception e) {

            e.printStackTrace();

        }


        return output;
    }
    private String PostRequest(String api,String body){
        String output = "";
        try {

            URL url = new URL(api);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "Application/JSON");
            conn.setRequestProperty("Content-Length",  Integer.toString(body.getBytes().length));


            String input = body;
            System.out.println(input);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();



            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String result = "";
            while ((result = br.readLine()) != null) {
                output += result;
            }


            conn.disconnect();

        } catch (Exception e) {

            e.printStackTrace();

        }


        return output;
    }


}

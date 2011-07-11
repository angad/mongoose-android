package org.mongoose.android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.AsyncTask;

public class cmdline  extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        String cmd = params[0];
        
        cmdRun(cmd);
        return "";
    }
    
    protected void onPostExecute(String param) {
    }
    
    protected void onProgressUpdate(String... params) {
        mongoose.resultPublish(params[0]);
    }
    
    private boolean cmdRun(String c) {
        
        String cmd = "/data/local/" + c + " -e /data/local/download/error_log.txt";
        publishProgress("Executing " + cmd);
        Process p;
        
        try{
            p = Runtime.getRuntime().exec("su");
            //p = Runtime.getRuntime().exec(cmd);

            DataOutputStream pOut = new DataOutputStream(p.getOutputStream());
            try {
                pOut.writeBytes(cmd + "\n");
                pOut.flush();
            } 
            catch (IOException e1) {
                e1.printStackTrace();
            }

            int read;
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            char[] buffer = new char[1024];
            StringBuffer output = new StringBuffer();
            try{
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                    publishProgress(output.toString());
                    output = new StringBuffer();
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}

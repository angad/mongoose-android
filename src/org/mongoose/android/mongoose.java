package org.mongoose.android;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class mongoose extends Activity {
    
    EditText cmd;
    static TextView logs;
    
    AsyncTask<String, String, String> server;
    
    boolean started = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        cmd = (EditText)findViewById(R.id.cmd);
        //cmd.setText("mongoose");
        
        Button stop = (Button)findViewById(R.id.stop);
        stop.setOnClickListener(stopServer);
        
        Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(startServer);
        
        logs = (TextView)findViewById(R.id.logs);
        CopyNative("/data/local/mongoose", R.raw.mongoose);
        
    }
    
    /**
     * Event Handler for Stop button
     */
    public OnClickListener stopServer = new OnClickListener() {
        public void onClick(View v) {
            if(started == false) {
                resultPublish("Server not running.");
                return;
            }
            started = false;
            //server.cancel(true);
            killProcess("/data/local/mongoose");
        }
    };
    
    /**
     * Event Handler for Start button
     */
    public OnClickListener startServer = new OnClickListener() {
        public void onClick(View v) {
            if(started == true)
            {
                resultPublish("Server already running");
                return;
            }
            started = true;
            server = new cmdline();
            server.execute(cmd.getText().toString());
        }
    };
    
    protected void CopyNative(String path, int resource) {
        InputStream setdbStream = getResources().openRawResource(resource);
        try {
            byte[] bytes = new byte[setdbStream.available()];
            DataInputStream dis = new DataInputStream(setdbStream);
            dis.readFully(bytes);
            FileOutputStream setdbOutStream = new FileOutputStream(path);
            setdbOutStream.write(bytes);
            setdbOutStream.close();

            //Set executable permissions
            Process process = Runtime.getRuntime().exec("sh");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("chmod 777 " + path + "\n");
            os.writeBytes("exit\n");
            os.flush();
        } 
        catch (Exception e) {
            resultPublish("Unable to Copy native binary");
          return;
        }
      }

    /**
     * Static UI methods
     */
    private static int line_count = 0;
    private static boolean isFull = false;
    public static void resultPublish(String string) {
        Log.v("mongoose", string);
        if(line_count == 4 || isFull) {
            String txt = logs.getText().toString();
            txt = txt.substring(txt.indexOf('\n') + 1);
            logs.setText(txt);
            isFull=true;
            line_count = 0;
        }
        line_count++;
        logs.append("\n" + string);
    }

    private boolean killProcess(String path)
    {
        resultPublish("Killing " + path);
        Process p;
        StringBuffer output = new StringBuffer();
        
        //A very dirty method of killing the process
        try{
            p = Runtime.getRuntime().exec("su");
            
            DataOutputStream pOut = new DataOutputStream(p.getOutputStream());
            try {
                pOut.writeBytes("ps | grep " + path + "\nexit\n");
                pOut.flush();
            } 
            catch (IOException e1) {
                e1.printStackTrace();
            }
            
            try {
                p.waitFor();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            
            int read;
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            char[] buffer = new char[1024];
            try{
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                    resultPublish(output.toString());
                    break;
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        String pid = "";
        for(int i = 0; i<output.length(); i++)
        {
            //look for the process id
            if(output.charAt(i) > 47 && output.charAt(i) < 58)
            {
                pid = output.substring(i, i + output.substring(i).indexOf(' '));
                break;
            }
        }
        
        try{
            p = Runtime.getRuntime().exec("su");
//            p = Runtime.getRuntime().exec("ps | grep " + path);

            DataOutputStream pOut = new DataOutputStream(p.getOutputStream());
            try {
                pOut.writeBytes("kill -9 " + pid + "\nexit\n");
                pOut.flush();
            } 
            catch (IOException e1) {
                e1.printStackTrace();
            }
            
            try {
                p.waitFor();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            int read;
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            char[] buffer = new char[1024];
            try{
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                    resultPublish(output.toString());
                    break;
                    //output = new StringBuffer();
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
package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.w3c.dom.Text;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public String login_id;
    TextView people1;
    TextView people2;
    public people_list task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        people1 = (TextView)findViewById(R.id.people1);
        people2 = (TextView)findViewById(R.id.people2);

        Intent intent = getIntent();
        login_id = intent.getExtras().getString("log_ok_id");


        Button button2 =findViewById(R.id.favorite);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Favorites.class);
                startActivity(intent);
            }
        });
        Button pathSetting=(Button)findViewById(R.id.pathsetting);
        pathSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),select_path.class);
                startActivity(intent);
            }
        });
        Button button9=(Button)findViewById(R.id.guardian);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),People.class);
                intent.putExtra("log_ok_id", login_id);
                startActivity(intent);
            }
        });

        getpeople_list();
        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        TextView textView1 = findViewById(R.id.textview4);
        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0); textView1.setText(content);

        textView1.setClickable(false);
        textView1.setFocusable(false);
        TextView textView2 = findViewById(R.id.editText6);
        textView2.setClickable(false);
        textView2.setFocusable(false);
        TextView textView3 = findViewById(R.id.editText5);
        textView3.setClickable(false);
        textView3.setFocusable(false);

      //  TMapTapi tMapTapi = new TMapTapi(this);
        //relativeLayout.addView(tmapview);
      //  setContentView(relativeLayout);
    }

    private class people_list extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            String id = arg0[0];
            try {
                String link = "http://211.221.215.166/androidwithdb/people_list.php?ID=" + id;

                String ret = server_network_check(link);
                if (ret.equals("1") != true) return ret;

                HttpURLConnection urlConn = null;
                URL url = new URL(link);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(1000);
                urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");


                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("retret2", "" + ret);
                    return "-2";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                StringBuffer sb = new StringBuffer("");
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                reader.close();
                if (urlConn != null) urlConn.disconnect();

                Log.d("라인값", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private void getpeople_list()
    {
        String people_r = null;
        String[] people_array;

        task = new people_list();
        try {
            people_r = task.execute(login_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("getpeople", "가져오기");

        if(people_r.equals("0") == true) Log.d("보호자없음", "등록된 보호자가 없습니다.");
        else if(people_r.equals("-2") == true) Log.d("네트워크", "네트워크에러임");
        else
        {
            people_array = people_r.split("@");
            Log.d("네트워크2", people_r);
            if(people_array.length > 0) people1.setText(people_array[0]);
            if(people_array.length > 1) people2.setText(people_array[1]);
        }

    }

    public String server_network_check (String host){
        return "1";
    }
}

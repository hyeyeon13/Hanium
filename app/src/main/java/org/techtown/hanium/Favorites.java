package org.techtown.hanium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Favorites extends AppCompatActivity {
    EditText favor_name;
    EditText favor_addr;

    Button btnsearch;
    Button btnfavor;

    Geocoder coder;
    public Double addrLatitude, addrLongitude;
    public phpdo task;
    public String nameok, pxok, pyok, chkidok, idok;
    public String ret_return_val;
    public int isConnected;

    public String NotConnMsg = "네트워크 접속이 원활하지 않습니다. 확인 후 다시 시도 해주십시요";
    public String NotEmptyMsg = "정보를 전부 입력하신 후 다시 시도 해주십시요";
    public String NotServerMsg = "서버 접속이 원활하지 않습니다. 확인 후 다시 시도 해주십시요";
    public String NotRegister = "즐겨찾기 등록에 실패하였습니다.";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favor_name = (EditText) findViewById(R.id.favor_name);
        favor_addr = (EditText) findViewById(R.id.favor_addr);

        btnsearch = (Button) findViewById(R.id.btnsearch);
        btnfavor = (Button) findViewById(R.id.btnfavor);

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addrsearch();
            }
        });
        btnfavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameok = favor_name.getText().toString();
                pxok = addrLatitude.toString();
                pyok = addrLongitude.toString();
                Intent intent = getIntent();
                chkidok = intent.getExtras().getString("chkid");
                idok = intent.getExtras().getString("log_ok_id");
//                Log.d("정보확인", nameok + pxok + pyok + "::" + chkidok + "::" + idok);
                register_action();
            }
        });
    }

    private void addrsearch() {
        List<Address> list = null;
        favor_addr = (EditText) findViewById(R.id.favor_addr);
        String straddr = favor_addr.getText().toString();
        coder = new Geocoder(this);
        try {
            list = coder.getFromLocationName(straddr, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환 중 에러 발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                favor_addr.setText("해당되는 주소 정보는 없습니다");
            } else {
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
                addrLatitude = list.get(0).getLatitude();
                addrLongitude = list.get(0).getLongitude();
                try {
                    list = coder.getFromLocation(addrLatitude, addrLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                favor_addr.setText(list.get(0).getFeatureName());
            }
        }
    }

    private class phpdo extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String name = arg0[0];
                String px = arg0[1];
                String py = arg0[2];
                String chkid = arg0[3];
                String id = arg0[4];

                Log.d("정보확인2", name + px + py + chkid + id);

                String link = "http://211.221.215.166/androidwithdb/favorites_add.php?NAME=" + name + "&PX=" + px + "&PY=" + py + "&CHKID=" + chkid + "&ID=" + id;

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
                String line = "";

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                reader.close();
                if (urlConn != null) urlConn.disconnect();
                //  if(Integer.parseInt(sb.toString());
                if (sb.toString().equals("1"))
                    Log.i("check", "즐겨찾기가 등록되었습니다.");
                if (sb.toString().equals("0"))
                    Log.i("check", "즐겨찾기 등록에 실패하였습니다. 다시한번 시도 해주십시요.");
                Log.i("HTTP", "GET STRING:" + sb.toString());
                return sb.toString();

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        private String server_network_check(String link) {
            return "1";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    //각종 확인 메세지
    public void Alert_message(final String str) {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(Favorites.this);
        alert_ex.setMessage(str);
        alert_ex.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert_ex.setCancelable(false);
        AlertDialog alert = alert_ex.create();
        alert.show();
    }

    public static int Connection_Check(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) { // connected to wifi
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return (1);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //connected to the mobile provider's data plan
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return (2);
            }
        }
        // not connected to the internet
        return (0);
    }

    private void register_action() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //키보드창 매니저. 올리고 내리고 설정 가능.


        //네트워크 다이얼로그 창
        Context context = getApplicationContext();
        isConnected = Connection_Check(context);

        //버전 체크해서 자동으로 값 입력해줌.
        if (isConnected != 0) {
            //           if(version_check() ==-1) return;
        } else {
            Toast.makeText(getApplicationContext(), "네트워크가 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        isConnected = Connection_Check(Favorites.this);
        if (isConnected == 0) {
            Alert_message(NotConnMsg);
            return;
        }
        imm.hideSoftInputFromWindow(favor_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(favor_addr.getWindowToken(), 0);

        if (nameok.length() == 0 || pxok.length() == 0 || pyok.length() == 0) {
            Alert_message(NotEmptyMsg);
            return;
        } else {
            task = new phpdo(); //로그인 정보를 대조할 php 객체 생성

            try {
                ret_return_val = task.execute(nameok, pxok, pyok, chkidok, idok).get(); // id, pw값 대조 후 리턴값 보냄. 2일때 접근 허가
            } catch (Exception e) {
                e.printStackTrace();
            }
//
//            Log.i("LOGIN", "RETURN:" + ret_return_val);
//
            if (Integer.parseInt(ret_return_val) == -2) {
                Alert_message(NotServerMsg);
                Log.d("정보확인", nameok + pxok + pyok + "::" + chkidok + "::" + idok);

                return;
            }
            if (Integer.parseInt(ret_return_val) == 0) {
                Alert_message(NotRegister);
                return;
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("log_ok_id", idok);
            Toast.makeText(getApplicationContext(), "즐겨찾기 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
    }

}

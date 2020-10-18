package org.techtown.hanium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class People extends AppCompatActivity {
    public phpdo task;
    public Button people_register;
    public EditText nameInput, phoneInput;

    public String name_input_value;
    public String phone_input_value;
    public String people_name;
    public String people_phone;
    public String people_id;
    public String ret_return_val;
    public String login_id;

    public String NotConnMsg = "네트워크 접속이 원활하지 않습니다. 확인 후 다시 시도 해주십시요";
    public String NotEmptyMsg = "정보를 전부 입력하신 후 다시 시도 해주십시요";
    public String NotServerMsg = "서버 접속이 원활하지 않습니다. 확인 후 다시 시도 해주십시요";
    public String OverlapId = "이미 등록된 아이디입니다.";
    public String NotRegister = "아이디 등록에 실패하였습니다.";

    public int isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        nameInput = (EditText) findViewById(R.id.people_name);
        phoneInput = (EditText) findViewById(R.id.people_phone);
        people_register = (Button) findViewById(R.id.people_register);

        Intent intent = getIntent();
        login_id = intent.getExtras().getString("log_ok_id");
        Log.d("login_id", "" + login_id);



        people_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_input_value = nameInput.getText().toString();
                phone_input_value = phoneInput.getText().toString();

                register_action();
            }
        });
    }

    private class phpdo extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String name = arg0[0];
                String phone = arg0[1];
                String id = login_id;

                Log.d("idpw", "" + id + "+" + phone + "+" + name);
                String link = "http://211.221.215.166/androidwithdb/people_add.php?NAME=" + name + "&PHONE=" + phone + "&ID=" + id;

                String ret = server_network_check(link);
                if (ret.equals("1") != true) return ret;

                HttpURLConnection urlConn = null;
                URL url = new URL(link);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(1000);

                urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
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
                if (sb.toString().equals("2"))
                    Log.i("check", "이미 등록된 보호자 정보입니다.");
                if (sb.toString().equals("1"))
                    Log.i("check", "보호자가 등록되었습니다.");
                if (sb.toString().equals("0"))
                    Log.i("check", "보호자등록에 실패하였습니다. 다시한번 시도 해주십시요.");
                Log.i("HTTP", "GET STRING:" + sb.toString());
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


    //각종 확인 메세지
    public void Alert_message(final String str) {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(People.this);
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

        isConnected = Connection_Check(People.this);
        if (isConnected == 0) {
            Alert_message(NotConnMsg);
            return;
        }
        imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(phoneInput.getWindowToken(), 0);
        name_input_value = nameInput.getText().toString();
        phone_input_value = phoneInput.getText().toString();

        if (name_input_value.length() == 0 || phone_input_value.length() == 0) {
            Alert_message(NotEmptyMsg);
            return;
        } else {
            task = new People.phpdo(); //로그인 정보를 대조할 php 객체 생성

            try {
                ret_return_val = task.execute(name_input_value, phone_input_value).get(); // id, pw값 대조 후 리턴값 보냄. 2일때 접근 허가
            } catch (Exception e) {
                e.printStackTrace();
            }
//
//            Log.i("LOGIN", "RETURN:" + ret_return_val);
//
            if (Integer.parseInt(ret_return_val) == -2) {
                Alert_message(NotServerMsg);
                nameInput.setText("");
                phoneInput.setText("");
                return;
            }
            if (Integer.parseInt(ret_return_val) == 0) {
                Alert_message(NotRegister);
                return;
            }
            if (Integer.parseInt(ret_return_val) == 2) {
                Alert_message(OverlapId);
                phoneInput.setText("");
                return;
            }

            people_name = name_input_value;
            people_phone = phone_input_value;
            people_id = login_id;

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtra("signup_id", signup_id);
            Toast.makeText(getApplicationContext(), people_name + " 님이 보호자로 등록되었습니다", Toast.LENGTH_SHORT).show();
            startActivity(intent);
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
    }

    public String server_network_check (String host){
        return "1";
    }
}

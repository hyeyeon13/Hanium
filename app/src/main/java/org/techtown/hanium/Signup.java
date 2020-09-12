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

public class signup extends AppCompatActivity {
    public phpdo task;
    public Button signupok;
    public EditText nameInput, idInput, pwInput;

    public String name_input_value;
    public String id_input_value;
    public String pw_input_value;
    public String signup_name;
    public String signup_id;
    public String signup_pw;
    public String ret_return_val;

    public String NotConnMsg = "��Ʈ��ũ ������ ��Ȱ���� �ʽ��ϴ�. Ȯ�� �� �ٽ� �õ� ���ֽʽÿ�";
    public String NotEmptyMsg = "������ ���� �Է��Ͻ� �� �ٽ� �õ� ���ֽʽÿ�";
    public String NotServerMsg = "���� ������ ��Ȱ���� �ʽ��ϴ�. Ȯ�� �� �ٽ� �õ� ���ֽʽÿ�";
    public String NotServerMsg = "�̹� ��ϵ� ���̵��Դϴ�.";

    public int isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = (EditText) findViewById(R.id.nameSignup);
        idInput = (EditText) findViewById(R.id.idSignup);
        pwInput = (EditText) findViewById(R.id.pwSignup);
        signupok = (Button) findViewById(R.id.signupok);

        signupok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_input_value = idInput.getText().toString();
                id_input_value = idInput.getText().toString();
                pw_input_value = pwInput.getText().toString();

                if (name_input_value.length() > 0 || id_input_value.length() > 0 || pw_input_value.length() > 0)
                    signup_action();
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
                String id = arg0[1];
                String pw = arg0[2];
                Log.d("idpw", "" + id + "+" + pw + "+" + name);
                String link = "http://211.221.215.166/androidwithdb/signup.php?NAME=" + name + "&ID=" + id + "&PW=" + pw;

                String ret = server_network_check(link);
                if (ret.equals("1") != true) return ret;

                HttpURLConnection urlConn = null;
                URL url = new URL(link);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(1000);

                urlConn.setRequestMethod("POST"); // URL ��û�� ���� �޼ҵ� ���� : POST.
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset ����.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return "-2";

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
                    Log.i("check", "�̹� ��ϵ� �׷��̸� �Դϴ�.");
                if (sb.toString().equals("1"))
                    Log.i("check", "�׷��� ��ϵǾ����ϴ�.");
                if (sb.toString().equals("0"))
                    Log.i("check", "�׷��Ͽ� �����Ͽ����ϴ�. �ٽ��ѹ� �õ� ���ֽʽÿ�.");
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


    //���� Ȯ�� �޼���
    public void Alert_message(final String str) {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(signup.this);
        alert_ex.setMessage(str);
        alert_ex.setNegativeButton("Ȯ��", new DialogInterface.OnClickListener() {
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

    private void signup_action() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //Ű����â �Ŵ���. �ø��� ������ ���� ����.


        //��Ʈ��ũ ���̾�α� â
        Context context = getApplicationContext();
        isConnected = Connection_Check(context);

        //���� üũ�ؼ� �ڵ����� �� �Է�����.
        if (isConnected != 0) {
            //           if(version_check() ==-1) return;
        } else {
            Toast.makeText(getApplicationContext(), "��Ʈ��ũ�� ������� �ʾҽ��ϴ�.", Toast.LENGTH_SHORT).show();
            return;
        }

        isConnected = Connection_Check(signup.this);
        if (isConnected == 0) {
            Alert_message(NotConnMsg);
            return;
        }
        imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(idInput.getWindowToken(), 0);
        name_input_value = nameInput.getText().toString();
        id_input_value = idInput.getText().toString();
        pw_input_value = pwInput.getText().toString();

        if (name_input_value.length() < 1 || id_input_value.length() < 1 || pw_input_value.length() < 1) {
            Alert_message(NotEmptyMsg);
            return;
        } else {
            task = new signup.phpdo(); //�α��� ������ ������ php ��ü ����

            try {
                ret_return_val = task.execute(name_input_value, id_input_value, pw_input_value).get(); // id, pw�� ���� �� ���ϰ� ����. 2�϶� ���� �㰡
            } catch (Exception e) {
                e.printStackTrace();
            }
//
//            Log.i("LOGIN", "RETURN:" + ret_return_val);
//
            if (Integer.parseInt(ret_return_val) < -1) {
                Alert_message(NotServerMsg);
                nameInput.setText("");
                idInput.setText("");
                pwInput.setText("");
                return;
            }
            if (Integer.parseInt(ret_return_val) == 0) {
                Alert_message(NotEmptyMsg);
                nameInput.setText("");
                return;
            }
            if (Integer.parseInt(ret_return_val) == 1) {
                Alert_message(NotEmptyMsg);
                idInput.setText("");
                return;
            }


            signup_name = name_input_value;
            signup_id = id_input_value;
            signup_pw = pw_input_value;

            Intent intent = new Intent(getApplicationContext(), login.class);
            intent.putExtra("signup_id", signup_id);
            Toast.makeText(getApplicationContext(), signup_name + " �� ���ԵǾ����ϴ�", Toast.LENGTH_SHORT).show();
            startActivity(intent);
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            finish();
        }
        }

        public String server_network_check (String host){
            return "1";
        }
}

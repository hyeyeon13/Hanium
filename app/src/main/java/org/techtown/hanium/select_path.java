package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;

public class select_path extends AppCompatActivity {
    String stationName;
    Double longitude, latitude;
    final int DIALOG_TIME = 2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_path);

        Button time=(Button)findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });

        Button select=(Button)findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Marker.class);
                startActivity(intent);
            }
        });

        EditText editstart=(EditText)findViewById(R.id.editstart);
        final EditText editText1=(EditText)findViewById(R.id.editText1);
        EditText editText2=(EditText)findViewById(R.id.editText2);
        RelativeLayout relativeLayout = new RelativeLayout(this);

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(getApplicationContext(), "o35DS9VMHDOCosWoVhEYWv43HTeN5uX6ID/cO660rlI");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현
        final OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    // API Value 는 API 호출 메소드 명을 따라갑니다.
                    if (api == API.BUS_STATION_INFO) {
                        stationName = odsayData.getJson().getJSONObject("result").getString("stationName");
                        longitude = Double.valueOf(odsayData.getJson().getJSONObject("result").getString("x"));
                        latitude = Double.valueOf(odsayData.getJson().getJSONObject("result").getString("y"));
                        //String y = odsayData.getJson().getJSONObject("result").getString("y");
                        Log.d("Station name :",  stationName);
                        Log.d("longitude : ", String.valueOf(longitude));
                        Log.d("latitude : ", String.valueOf(latitude));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            // 호출 실패 시 실행
            @Override

            public void onError(int i, String s, API api) {

                if (api == API.BUS_STATION_INFO) {}
            }
        };
        // API 호출
        odsayService.requestBusStationInfo(editstart.getText().toString(), onResultCallbackListener);
        odsayService.requestBusStationInfo("107474", onResultCallbackListener);


        Button button2=(Button)findViewById(R.id.button1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odsayService.requestBusStationInfo(editstart.getText().toString(), onResultCallbackListener);
                Intent intent=new Intent(getApplicationContext(),Marker.class);
                intent.putExtra("stationName",stationName);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivity(intent);
            }
        });

    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case DIALOG_TIME :
                TimePickerDialog tpd =
                        new TimePickerDialog(select_path.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view,
                                                          int hourOfDay, int minute) {
                                        TextView textView=(TextView)findViewById(R.id.textView3);

                                        textView.setText(hourOfDay +"시 " + minute+"분");


                                    }
                                }, // 값설정시 호출될 리스너 등록
                                4,19, false); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return tpd;
        }

        return super.onCreateDialog(id);
    }
}





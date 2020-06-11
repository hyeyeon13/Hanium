package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;

public class select_path extends AppCompatActivity {
    String stationName;
    Double longitude, latitude;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_path);
        EditText editText=(EditText)findViewById(R.id.editText);
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
        odsayService.requestBusStationInfo(editText1.getText().toString(), onResultCallbackListener);
        odsayService.requestBusStationInfo("107474", onResultCallbackListener);


        Button button=(Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odsayService.requestBusStationInfo(editText1.getText().toString(), onResultCallbackListener);
                Intent intent=new Intent(getApplicationContext(),Marker.class);
                intent.putExtra("stationName",stationName);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivity(intent);
            }
        });

    }



//    OnResultCallbackListener() {
//        @Override
//        public void onSuccess(ODsayData odsayData, API api) {}
//
//        @Override
//        public void onError(int code, String message, API api) {}
//    }
}


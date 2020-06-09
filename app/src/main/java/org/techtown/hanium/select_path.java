package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;

public class select_path extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);
        EditText editText=(EditText)findViewById(R.id.editText);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        ODsayService odsayService = ODsayService.init(getApplicationContext(), "o35DS9VMHDOCosWoVhEYWv43HTeN5uX6ID/cO660rlI");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현
        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    // API Value 는 API 호출 메소드 명을 따라갑니다.
                    if (api == API.BUS_STATION_INFO) {
                        String stationName = odsayData.getJson().getJSONObject("result").getString("stationName");
                        String x = odsayData.getJson().getJSONObject("result").getString("x");
                        String y = odsayData.getJson().getJSONObject("result").getString("y");
                        Log.d("Station name :",  stationName);
                        Log.d("x : ", x);
                        Log.d("y : ", y);
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
        odsayService.requestBusStationInfo("107475", onResultCallbackListener);


//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//
//        }




        //relativeLayout.addView(tmapview);
        //setContentView(relativeLayout);
    }



//    OnResultCallbackListener() {
//        @Override
//        public void onSuccess(ODsayData odsayData, API api) {}
//
//        @Override
//        public void onError(int code, String message, API api) {}
//    }
}


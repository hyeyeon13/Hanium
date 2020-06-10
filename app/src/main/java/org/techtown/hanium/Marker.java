package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

public class Marker extends AppCompatActivity {
    TMapView tmapview;
    Double longitude, latitude;
    String stationName;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        TMapTapi tMapTapi = new TMapTapi(this);

        Intent intent=getIntent();


        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        longitude=intent.getExtras().getDouble("longitude");
        latitude=intent.getExtras().getDouble("latitude");
        stationName=intent.getExtras().getString("stationName");
        TMapPoint tMapPoint1 = new TMapPoint(latitude,longitude); // 마커 놓을 좌표 (위도, 경도 순서)

// 마커 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);

        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName(stationName); // 마커의 타이틀 지정
        tmapview.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

        tmapview.setCenterPoint( longitude, latitude ); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("위도 : ", String.valueOf(latitude));
        Log.d("경도 : ", String.valueOf(longitude));
        relativeLayout.addView(tmapview);
        setContentView(relativeLayout);
    }


}
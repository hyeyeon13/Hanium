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
    Double myLongitude, myLatitude;
    Double destLongitude, destLatitude;
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


        TMapMarkerItem myMarker = new TMapMarkerItem();
        TMapMarkerItem destMarker = new TMapMarkerItem();
        myLongitude=intent.getExtras().getDouble("curLongitude");
        myLatitude=intent.getExtras().getDouble("curLatitude");
        destLongitude = intent.getExtras().getDouble("destLongitude");
        destLatitude = intent.getExtras().getDouble("destLatitude");
        //stationName=intent.getExtras().getString("stationName");
        TMapPoint mytMapPoint = new TMapPoint(myLatitude,myLongitude); // 마커 놓을 좌표 (위도, 경도 순서)
        TMapPoint desttMappoint = new TMapPoint(destLatitude,destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)

// 마커 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);

        myMarker.setIcon(bitmap); // 마커 아이콘 지정
        myMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        myMarker.setTMapPoint( mytMapPoint ); // 마커의 좌표 지정
        myMarker.setName("start"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("startPoint", myMarker); // 지도에 마커 추가

        destMarker.setIcon(bitmap);
        destMarker.setPosition(0.5f, 1.0f);;
        destMarker.setTMapPoint(desttMappoint);;
        destMarker.setName("dest"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("destPoint", destMarker);

        double centerLong, centerLat;
        centerLong = (myLongitude+destLongitude)/2;
        centerLat = (myLatitude+destLatitude)/2;
        tmapview.setCenterPoint( centerLong, centerLat ); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("내 위도 : ", String.valueOf(myLatitude));
        Log.d("내 경도 : ", String.valueOf(myLongitude));
        Log.d("목적지 위도 : ", String.valueOf(destLatitude));
        Log.d("목적지 경도 : ", String.valueOf(destLongitude));
        relativeLayout.addView(tmapview);
        setContentView(relativeLayout);
    }


}
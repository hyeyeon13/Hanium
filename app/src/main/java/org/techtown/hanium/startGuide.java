package org.techtown.hanium;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class startGuide extends AppCompatActivity {
    TMapView tmapview;
    TMapMarkerItem myMarker = new TMapMarkerItem();
    TMapMarkerItem destMarker = new TMapMarkerItem();
    Intent intent = getIntent();
    Double myLongitude = intent.getExtras().getDouble("curLongitude"); //출발지 위도
    Double myLatitude = intent.getExtras().getDouble("curLatitude");   //출발지 경ㄷ
    TMapPoint mytMapPoint = new TMapPoint(myLatitude, myLongitude);// 마커 놓을 좌표 (위도, 경도 순서)
    Double destLatitude = intent.getExtras().getDouble("desLongitude"); //도착지 위도
    Double destLongitude = intent.getExtras().getDouble("desLatitude");   //도착지 경도
    TMapPoint desttMappoint = new TMapPoint(destLatitude, destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startguide);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.guide_map);
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        // 마커 아이콘
        Bitmap Start = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);
        Bitmap dest = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bluepoint);
        tmapview.setTMapPathIcon(Start, dest);

        myMarker.setIcon(Start); // 마커 아이콘 지정
        myMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        myMarker.setTMapPoint(mytMapPoint); // 마커의 좌표 지정
        myMarker.setName("start"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("startPoint", myMarker); // 지도에 마커 추가

        destMarker.setIcon(dest);
        destMarker.setPosition(0.5f, 1.0f);
        destMarker.setTMapPoint(desttMappoint);
        destMarker.setName("dest"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("destPoint", destMarker);
        relativeLayout.addView( tmapview );

        TMapPoint leftTop = new TMapPoint(myLatitude, myLongitude);
        TMapPoint rightBottom = new TMapPoint(destLatitude, destLongitude);
        tmapview.zoomToTMapPoint(leftTop, rightBottom);

        TMapPolyLine tpolyline = new TMapPolyLine();
        tpolyline.setLineColor(Color.BLUE);
        tpolyline.setLineWidth(2);
    }
}

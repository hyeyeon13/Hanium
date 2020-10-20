package org.techtown.hanium;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class Map extends AppCompatActivity {
    Double startLon,startLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById (R.id.map_view);
        final TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");
        tmapview.setZoomLevel(10);
        TMapTapi tMapTapi = new TMapTapi(this);

        tmapview.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, final TMapPoint tMapPoint) {
                TMapPoint point = tmapview.getLocationPoint();
                TMapData tmapdata = new TMapData();
                TMapMarkerItem destMarker = new TMapMarkerItem();
                TMapPoint desttMappoint = new TMapPoint(tMapPoint.getLatitude(),tMapPoint.getLongitude()); // 마커 놓을 좌표 (위도, 경도 순서)
                Bitmap dest = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.bluepoint);
                destMarker.setIcon(dest);
                destMarker.setPosition(0.5f, 1.0f);;
                destMarker.setTMapPoint(desttMappoint);;
                destMarker.setName("dest"); // 마커의 타이틀 지정
                tmapview.addMarkerItem("destPoint", destMarker);
                Log.d("현재위치",desttMappoint.toString());



            }

        });



        relativeLayout.addView(tmapview);

//        setContentView(relativeLayout);

    }


}




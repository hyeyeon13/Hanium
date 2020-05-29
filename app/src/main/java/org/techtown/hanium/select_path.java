package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.HashMap;

public class select_path extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        TMapTapi tMapTapi = new TMapTapi(this);

        HashMap pathInfo = new HashMap();
        pathInfo.put("rGoName", "T타워");
        pathInfo.put("rGoX", "126.985302");
        pathInfo.put("rGoY", "37.570841");

        pathInfo.put("rStName", "출발지");
        pathInfo.put("rStX", "126.926252");
        pathInfo.put("rStY", "37.557607");

        pathInfo.put("rV1Name", "경유지");
        pathInfo.put("rV1X", "126.976867");
        pathInfo.put("rV1Y", "37.576016");
        tMapTapi.invokeRoute(pathInfo);
        relativeLayout.addView(tmapview);
        setContentView(relativeLayout);
    }
    }


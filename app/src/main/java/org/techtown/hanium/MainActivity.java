package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button2 =findViewById(R.id.favorite);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Favorites.class);
                startActivity(intent);
            }
        });
        Button pathSetting=(Button)findViewById(R.id.pathsetting);
        pathSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),select_path.class);
                startActivity(intent);
            }
        });
        Button button9=(Button)findViewById(R.id.guardian);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),People.class);
                startActivity(intent);
            }
        });
        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        EditText editText4 = findViewById(R.id.editText4);
        editText4.setClickable(false);
        editText4.setFocusable(false);
        EditText editText6 = findViewById(R.id.editText6);
        editText6.setClickable(false);
        editText6.setFocusable(false);
        EditText editText5 = findViewById(R.id.editText5);
        editText5.setClickable(false);
        editText5.setFocusable(false);
        EditText editTextPerson2 = findViewById(R.id.editTextPerson2);
        editTextPerson2.setClickable(false);
        editTextPerson2.setFocusable(false);
        EditText editTextPerson1 = findViewById(R.id.editTextPerson1);
        editTextPerson1.setClickable(false);
        editTextPerson1.setFocusable(false);
      //  TMapTapi tMapTapi = new TMapTapi(this);
        //relativeLayout.addView(tmapview);
      //  setContentView(relativeLayout);
    }
}

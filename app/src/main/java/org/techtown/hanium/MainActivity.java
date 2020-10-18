package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    public String login_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        login_id = intent.getExtras().getString("log_ok_id");


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
                intent.putExtra("log_ok_id", login_id);
                startActivity(intent);
            }
        });
        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");

        TextView textView1 = findViewById(R.id.textview4);
        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0); textView1.setText(content);

        textView1.setClickable(false);
        textView1.setFocusable(false);
        TextView textView2 = findViewById(R.id.textView6);
        textView2.setClickable(false);
        textView2.setFocusable(false);
        TextView textView3 = findViewById(R.id.editText5);
        textView3.setClickable(false);
        textView3.setFocusable(false);
        TextView editTextPerson2 = findViewById(R.id.people1);
        editTextPerson2.setClickable(false);
        editTextPerson2.setFocusable(false);
        TextView editTextPerson1 = findViewById(R.id.people2);
        editTextPerson1.setClickable(false);
        editTextPerson1.setFocusable(false);

      //  TMapTapi tMapTapi = new TMapTapi(this);
        //relativeLayout.addView(tmapview);
      //  setContentView(relativeLayout);
    }
}

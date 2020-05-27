package org.techtown.hanium;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class People extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
    }

    public class MainActivity extends Activity {

        private Button setcheck;

        @SuppressLint("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // 레이아웃에서 버튼 id 가져오기.
            setcheck = (Button) findViewById(R.id.gr);
            // 해당 버튼에 레이아웃 설정하기.
            setcheck.setBackgroundResource(R.layout.gr);

        }
    }



}

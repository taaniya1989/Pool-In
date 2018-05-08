package edu.sdsu.tvidhate.pool_in.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.sdsu.tvidhate.pool_in.R;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent main = new Intent(AppInfoActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }
}

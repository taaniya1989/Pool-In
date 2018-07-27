package edu.sdsu.tvidhate.pool_in.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class SearchActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener{

    private EditText mSearchKeyword;
    private Button mSearchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchKeyword = findViewById(R.id.searchKeyword);
        mSearchButton = findViewById(R.id.searchButton);

        mSearchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SearchActivity.this,MainActivity.class);
        intent.putExtra(KEYWORD,mSearchKeyword.getText().toString().trim());
        startActivity(intent);
        finish();
    }
}

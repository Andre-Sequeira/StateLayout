package com.objectlife.statelayoutsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.objectlife.statelayout.StateLayout;

public class SetStateInXmlActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int STATE_CUSTOM = StateLayout.custom(0);
    private final static int STATE_CUSTOM_LOADING = STATE_CUSTOM | StateLayout.STATE_LOADING;

    private StateLayout mStateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_state_in_xml);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStateLayout = (StateLayout) findViewById(R.id.sl_layout_state);

        findViewById(R.id.btn_content).setOnClickListener(this);
        findViewById(R.id.btn_empty).setOnClickListener(this);
        findViewById(R.id.btn_error).setOnClickListener(this);
        findViewById(R.id.btn_loading).setOnClickListener(this);
        findViewById(R.id.btn_custom).setOnClickListener(this);
        findViewById(R.id.btn_custom_loading).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_content:
                mStateLayout.setState(StateLayout.STATE_CONTENT);
                break;

            case R.id.btn_empty:
                mStateLayout.setState(StateLayout.STATE_EMPTY);
                break;

            case R.id.btn_error:
                mStateLayout.setState(StateLayout.STATE_ERROR);
                break;

            case R.id.btn_loading:
                mStateLayout.setState(StateLayout.STATE_LOADING);
                break;
            case R.id.btn_custom:
                mStateLayout.setState(STATE_CUSTOM);
                break;
            case R.id.btn_custom_loading:
                mStateLayout.setState(STATE_CUSTOM_LOADING);
                break;
        }
    }
}

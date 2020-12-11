package com.ml.proximitysensorfix;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PermissionsActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    final static String CURRENT_STEP_POSITION_KEY = "position";

    class MyStepperAdapter extends AbstractFragmentStepAdapter {

        public MyStepperAdapter(FragmentManager fm, Context context) {
            super(fm, context);
        }

        @Override
        public Step createStep(int position) {
            final StepFragment step = new StepFragment();
            Bundle b = new Bundle();
            b.putInt(CURRENT_STEP_POSITION_KEY, position);
            step.setArguments(b);
            return step;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @NonNull
        @Override
        public StepViewModel getViewModel(@IntRange(from = 0) int position) {
            //Override this method to set Step title for the Tabs, not necessary for other stepper types
            return new StepViewModel.Builder(context)
                    .setTitle("") //can be a CharSequence instead
                    .create();
        }
    }
    static StepperLayout mStepperLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mStepperLayout =  findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new MyStepperAdapter(getSupportFragmentManager(), this));
        mStepperLayout.setListener(this);
    }

    @Override
    public void onCompleted(View completeButton) {
        finish();
        startService(this);
    }

    public void startService(Context context){
        Intent service = new Intent(context, ProximitySensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }
    @Override
    public void onError(final VerificationError verificationError) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PermissionsActivity.this, verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    public static void nextPermission(boolean nextOK){
        mStepperLayout.setNextButtonEnabled(nextOK);
    }
    public static void goNext(){
        mStepperLayout.proceed();
    }

    @Override
    public void onReturn() {
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
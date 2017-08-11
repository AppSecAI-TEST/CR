package com.ilaquidain.constructionreporter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SplashScreenActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
        SplashScreenActivity.this.startActivity(intent);
        SplashScreenActivity.this.finish();
    }
}

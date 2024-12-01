package com.dev.sk.xchangehub.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev.sk.xchangehub.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainFragment = MainFragment.instance();
        supportFragmentManager.beginTransaction()
            .add(mainFragment, MainFragment.TAG)
            .addToBackStack(MainFragment.TAG)
            .commit()
    }
}
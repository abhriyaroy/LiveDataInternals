package com.example.livedatainternals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.dataProvider.observe(this, Observer {
            liveDataTextView.text = it.toString()
        })

        mainViewModel.myDataProvider.addObserver(this, {
            it?.run {
                myOwnLiveDataTextView.text = this.toString()
            }
        })

        mainViewModel.runCounter()
    }

}
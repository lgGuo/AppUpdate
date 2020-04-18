package com.glg.app.update;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.glg.app.core_lib.UpdateHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateHelper.build(this)
                .setDownloadUrl("http://oss.pgyer.com/3a3352efb3cbef9bfd7ff0aae8ecc4bc.apk?auth_key=1587170929-6872cac50640ef06d26f401bff3b33ce-0-63a427858790366b3ef1f797b13d35d1&response-content-disposition=attachment%3B+filename%3DNCP_Manager_v10_test_10_jiagu_sign.apk")
                .setUpdateDes("1修复问题\n2.新增功能")
                .setVersionName("v1.2.0")
                .forceUpdate(true)
                .check();
    }
}

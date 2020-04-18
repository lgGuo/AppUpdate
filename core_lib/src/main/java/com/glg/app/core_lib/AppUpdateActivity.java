package com.glg.app.core_lib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

/**
 * @Author: guolinguang
 * @Date: 2020/4/17 19:26
 * @Description: APP 升级页面
 **/
public class AppUpdateActivity extends AppCompatActivity {


    private String downloadUrl, appVersionName, updateInfo;
    private boolean isForce;
    private Button mbtnUpdate;
    private TextView mUpdateInfo;
    private TextView mAppVersion;
    private View mClose;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_app);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        downloadUrl = getIntent().getStringExtra("url");
        appVersionName = getIntent().getStringExtra("app_version");
        updateInfo = getIntent().getStringExtra("update_info");
        isForce = getIntent().getBooleanExtra("force", false);
        initViews();
        initLogic();
    }

    protected void initViews() {
        mbtnUpdate=findViewById(R.id.btn_update);
        mAppVersion=findViewById(R.id.app_version);
        mUpdateInfo=findViewById(R.id.tv_update_info);
        mClose=findViewById(R.id.iv_close);
    }

    protected void initLogic() {
        mbtnUpdate.setText(isApkDownloaded(appVersionName) ? "下载完成，点击安装" : "立即下载");
        mUpdateInfo.setText(updateInfo);
        mAppVersion.setText(appVersionName);
        mClose.setOnClickListener(v -> {
            if (!isForce) {
                finish();
            }
        });

        ClickUtils.applySingleDebouncing(mbtnUpdate, v -> {
            PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    installApp();
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                    ToastUtils.showShort("你需要打开存储权限");
                    AppUtils.launchAppDetailsSettings();

                }
            }).request();

        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        downloadUrl = getIntent().getStringExtra("url");
        appVersionName = getIntent().getStringExtra("app_version");
        updateInfo = getIntent().getStringExtra("update_info");
        isForce = getIntent().getBooleanExtra("force", false);


    }

    private void installApp() {
        if (isApkDownloaded(appVersionName)) {
            AppUtils.installApp(getApkPath(appVersionName));
        } else {
            FileDownloader.getImpl()
                    .create(downloadUrl)
                    .setPath(getApkPath(appVersionName))
                    .setListener(new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            mbtnUpdate.setEnabled(false);
                            mbtnUpdate.setText("准备下载...");
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            mbtnUpdate.setEnabled(false);
                            mbtnUpdate.setText(String.format(Locale.CHINA,"已下载：%%%d", Math.round(soFarBytes* 100/totalBytes )));
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            mbtnUpdate.setEnabled(true);
                            mbtnUpdate.setText("下载完成，点击安装");
                            AppUtils.installApp(task.getTargetFilePath());
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            mbtnUpdate.setEnabled(true);
                            mbtnUpdate.setText("下载暂停,点击继续");

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            mbtnUpdate.setEnabled(true);
                            mbtnUpdate.setText("下载错误");
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    }).start();

        }

    }

    private String getApkNameByDownloadUrl(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return "temp.apk";
        } else {
            String appName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            if (!appName.endsWith(".apk")) {
                appName = "temp.apk";
            }
            return appName;
        }
    }

    private boolean isApkDownloaded(String versionName) {
        return new File(getApkPath(versionName)).exists();
    }

    private String getApkPath(String versionName) {
        String appName = getApkNameByDownloadUrl(downloadUrl);
        return getDiskCacheDir()
                .concat(File.separator + (versionName==null?"v":versionName))
                .concat(File.separator + appName);
    }

    private String getDiskCacheDir() {
        File cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cacheDir = getExternalCacheDir();
        } else {
            cacheDir = getCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = getCacheDir();
        }
        return cacheDir.getPath() + File.separator + "app_update";
    }


    @Override
    public void onBackPressed() {

    }
}

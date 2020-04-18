package com.glg.app.core_lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.liulishuo.filedownloader.FileDownloader;

public class UpdateHelper {
    private String downloadUrl;
    private String versionName;
    private String updateDes;
    private boolean isForce;
    private Context context;


    public static UpdateHelper build(Context context){
        return new UpdateHelper(context);
    }

    private UpdateHelper(Context context){
        this.context=context;
        FileDownloader.setup(context);
    }

    public UpdateHelper setDownloadUrl(String downloadUrl){
        this.downloadUrl=downloadUrl;
        return this;
    }

    public UpdateHelper setVersionName(String versionName){
        this.versionName=versionName;
        return this;
    }

    public UpdateHelper setUpdateDes(String updateDes){
        this.updateDes=updateDes;
        return this;
    }



    public UpdateHelper forceUpdate(boolean isForce){
        this.isForce=isForce;
        return this;
    }


    public void check(){
        Intent intent=new Intent(context, AppUpdateActivity.class);
        intent.putExtra("url",downloadUrl);
        intent.putExtra("force",isForce);
        intent.putExtra("app_version",versionName);
        intent.putExtra("update_info",updateDes);
        if(context instanceof Application){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

}

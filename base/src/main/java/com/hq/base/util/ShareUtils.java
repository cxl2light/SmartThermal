package com.hq.base.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class ShareUtils {
    public static void shareFile(File file, Context context) {
        if (file == null){
            ToastUtil.toast("获取视频文件失败，请返回重试");
            return;
        }
        //由文件得到uri
        Uri imageUri = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }

//        ArrayList<Uri> imageUris = new ArrayList<Uri>();//不需要多文件可以删掉
//        for (File f : files) {
//            imageUris.add(Uri.fromFile(f));
//        }
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUris);//多个文件
//        shareIntent.setType("image/*");//选择图片
        //shareIntent.setType(“audio/*”); //选择音频
        shareIntent.setType("video/*"); //选择视频
        //shareIntent.setType(“video/;image/”);//同时选择音频和视频
        context.startActivity(Intent.createChooser(shareIntent, "分享视频"));
    }

    public static void shareImage(File file, Context context) {
        if (file == null){
            ToastUtil.toast("获取图片文件失败，请返回重试");
            return;
        }
        //由文件得到uri
        Uri imageUri = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }

//        ArrayList<Uri> imageUris = new ArrayList<Uri>();//不需要多文件可以删掉
//        for (File f : files) {
//            imageUris.add(Uri.fromFile(f));
//        }
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUris);//多个文件
        shareIntent.setType("image/*");//选择图片
        //shareIntent.setType(“audio/*”); //选择音频
//        shareIntent.setType("video/*"); //选择视频
        //shareIntent.setType(“video/;image/”);//同时选择音频和视频
        context.startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }
}

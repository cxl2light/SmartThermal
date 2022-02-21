package com.hq.monitor.media;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created on 2020/5/28
 * author :
 * desc : 媒体中心，将图片和视频信息放在这里，方式特别多时，通过intent传递会太大导致崩溃
 */
public class MediaCenter {
    private static volatile MediaCenter instance;

    private List<File> localImageList;
    private List<File> localVideoList;

    private CopyOnWriteArraySet<OnMediaDataChanged> mMediaImageChangedSet = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<OnMediaDataChanged> mMediaVideoChangedSet = new CopyOnWriteArraySet<>();

    private MediaCenter() {

    }

    public static MediaCenter getInstance() {
        if (instance == null) {
            synchronized (MediaCenter.class) {
                if (instance == null) {
                    instance = new MediaCenter();
                }
            }
        }
        return instance;
    }

    public synchronized void registerImageDataChanged(OnMediaDataChanged onMediaDataChanged) {
        if (onMediaDataChanged == null) {
            return;
        }
        mMediaImageChangedSet.add(onMediaDataChanged);
    }

    public synchronized void registerVideoDataChanged(OnMediaDataChanged onMediaDataChanged) {
        if (onMediaDataChanged == null) {
            return;
        }
        mMediaVideoChangedSet.add(onMediaDataChanged);
    }

    @Nullable
    public File getLocalImage(int position) {
        if (position >= 0 && position < getLocalImageListSize()) {
            return localImageList.get(position);
        }
        return null;
    }

    @Nullable
    public File getLocalVideo(int position) {
        if (position >= 0 && position < getLocalVideoListSize()) {
            return localVideoList.get(position);
        }
        return null;
    }

    public List<File> getLocalImageList() {
        return localImageList;
    }

    public void setLocalImageList(List<File> localImageList) {
        this.localImageList = localImageList;
    }

    public List<File> getLocalVideoList() {
        return localVideoList;
    }

    public void setLocalVideoList(List<File> localVideoList) {
        this.localVideoList = localVideoList;
    }

    public int getLocalImageListSize() {
        return localImageList == null ? 0 : localImageList.size();
    }

    public int getLocalVideoListSize() {
        return localVideoList == null ? 0 : localVideoList.size();
    }

    private void onImgDataChanged() {
        for (OnMediaDataChanged dataChanged : mMediaImageChangedSet) {
            try {
                dataChanged.onDataChanged();
            } catch (Exception ignore) {
            }
        }
    }

    private void onVideoDataChanged() {
        for (OnMediaDataChanged dataChanged : mMediaVideoChangedSet) {
            try {
                dataChanged.onDataChanged();
            } catch (Exception ignore) {
            }
        }
    }

    public void releaseImage() {
        mMediaImageChangedSet.clear();
        localImageList = null;
    }

    public void releaseVideo() {
        mMediaVideoChangedSet.clear();
        localVideoList = null;
    }

    public boolean deleteImgFile(final int position) {
        final File model = getLocalImage(position);
        if (position >= 0 && position < getLocalImageListSize()) {
            localImageList.remove(position);
        }
        onImgDataChanged();
        return deleteFile(model);
    }

    public boolean deleteVideoFile(final int position) {
        if (position <  0 || position >= getLocalVideoListSize()) {
            return false;
        }
        final File model = localVideoList.remove(position);;
        onVideoDataChanged();
        return deleteFile(model);
    }

    private boolean deleteFile(final File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        return file.delete();
    }

    public void destroy() {
        releaseImage();
        releaseVideo();
        instance = null;
    }

    public interface OnMediaDataChanged {
        void onDataChanged();
    }

}

package com.hq.monitor.media.local;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.app.CommonExecutor;
import com.hq.base.ui.BaseFragment;
import com.hq.base.util.ExceptionToTip;
import com.hq.base.util.Logger;
import com.hq.base.widget.CommonErrorWidget;
import com.hq.commonwidget.item_decoration.GridSpacingItemDecoration;
import com.hq.monitor.R;
import com.hq.monitor.media.MediaCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Created on 2020/5/24
 * author :
 * desc :
 */
public class LocalVideoListFragment extends BaseFragment implements MediaCenter.OnMediaDataChanged {
    private static final String TAG = "LocalVideoListFragment";
    private static final String EXTRA_KEY_FILE_PATH = "extra_key_file_path";

    private static final TreeSet<String> TARGET_FILE_SUFFIX = new TreeSet<>();

    static {
        TARGET_FILE_SUFFIX.add("mp4");
    }

    private View progressWidget;
    private CommonErrorWidget mErrorWidget;
    private BaseQuickAdapter<File, BaseViewHolder> mMediaAdapter;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private String mFileDir;

    public static LocalVideoListFragment getInstance(@NonNull String filePath) {
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY_FILE_PATH, filePath);
        final LocalVideoListFragment fragment = new LocalVideoListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressWidget = view.findViewById(R.id.progress_widget);
        mErrorWidget = view.findViewById(R.id.error_widget);
        final RecyclerView recyclerView = view.findViewById(R.id.media_recycler_list);
        final int spanCount = 5;
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, spanCount));
//        final int itemPadding = (int) getResources().getDimension(R.dimen.media_recycler_item_padding);
        final int itemPadding = 2;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, itemPadding, true));
        mMediaAdapter = new BaseQuickAdapter<File, BaseViewHolder>(R.layout.item_media_video_info, null) {
            @Override
            protected void convert(@NonNull BaseViewHolder holder, File f) {
                final AppCompatImageView imageView = holder.getView(R.id.video_thumbnail);
                Glide.with(mContext)
                        .load(f)
                        .placeholder(R.drawable.common_default_image)
                        .into(imageView);
                holder.setText(R.id.media_name, f.getName());
            }

            @Override
            public void onViewRecycled(@NonNull BaseViewHolder holder) {
                super.onViewRecycled(holder);
                final AppCompatImageView imageView = holder.getView(R.id.media_img);
                Glide.with(mContext).clear(imageView);
            }
        };
        recyclerView.setAdapter(mMediaAdapter);
        mMediaAdapter.setOnItemClickListener((adapter, v, position) -> {
//          PlayLocalVideoActivity.startActivity(mActivity, position);
            Log.d(TAG,"position" + position);
            LocalVideoBannerActivity.startActivity(mActivity, position);
        });
        MediaCenter.getInstance().registerVideoDataChanged(this);
        if (getArguments() != null) {
            mFileDir = getArguments().getString(EXTRA_KEY_FILE_PATH);
        }
        CommonExecutor.getExecutorService().execute(this::loadFtpFile);
    }

    @WorkerThread
    private void loadFtpFile() {
        if (TextUtils.isEmpty(mFileDir)) {
            showError(getString(R.string.give_file_path));
            return;
        }
        final File dir = new File(mFileDir);
        if (!dir.exists() || !dir.isDirectory()) {
            showError(getString(R.string.empty_video_file_tip));
            return;
        }
        try {
            File[] pictureFileArr = dir.listFiles((dir1, name) ->
                    TARGET_FILE_SUFFIX.contains(name.substring(name.lastIndexOf(".") + 1).toLowerCase()));
            if (pictureFileArr == null) {
                pictureFileArr = new File[0];
            }
            final List<File> fileList = new ArrayList<>(Arrays.asList(pictureFileArr));
            mHandler.post(() -> {
                showFileList(fileList);
            });
        } catch (Exception e) {
            Logger.d(TAG, e.toString());
            mHandler.post(() -> {
                showError(ExceptionToTip.toTip(e));
            });
        }
    }

    private void showFileList(final List<File> fileList) {
        MediaCenter.getInstance().setLocalVideoList(fileList);
        mMediaAdapter.setNewInstance(fileList);
        if (fileList == null || fileList.isEmpty()) {
            showError(getString(R.string.empty_video_file_tip));
            return;
        }
        progressWidget.setVisibility(View.GONE);
        mErrorWidget.setVisibility(View.GONE);
    }

    private void showError(String error) {
        progressWidget.setVisibility(View.GONE);
        mErrorWidget.setVisibility(View.VISIBLE);
        mErrorWidget.setTip(error);
    }

    @Override
    public void onDataChanged() {
        if (mMediaAdapter == null) {
            return;
        }
        mMediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

}

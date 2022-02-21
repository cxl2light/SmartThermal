package com.hq.monitor.media.local;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
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
public class LocalPictureListFragment extends BaseFragment implements MediaCenter.OnMediaDataChanged {
    private static final String TAG = "LocalPictureListFragment";
    private static final String EXTRA_KEY_FILE_PATH = "extra_key_file_path";

    private static final TreeSet<String> PICTURE_SUFFIX = new TreeSet<>();

    static {
        PICTURE_SUFFIX.add("jpg");
        PICTURE_SUFFIX.add("jpeg");
        PICTURE_SUFFIX.add("png");
    }

    private View progressWidget;
    private CommonErrorWidget mErrorWidget;
    private BaseQuickAdapter<File, BaseViewHolder> mMediaAdapter;
    private String mFileDir;

    public static LocalPictureListFragment getInstance(@NonNull String filePath) {
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_KEY_FILE_PATH, filePath);
        final LocalPictureListFragment fragment = new LocalPictureListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_list, container, false);
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
        mMediaAdapter = new BaseQuickAdapter<File, BaseViewHolder>(R.layout.item_media_picture_info, null) {
            @Override
            protected void convert(@NonNull BaseViewHolder holder, File f) {
                final AppCompatImageView imageView = holder.getView(R.id.media_img);
                Glide.with(mContext).load(f).placeholder(R.drawable.common_default_image).into(imageView);
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
            if (position >= 0 && position < mMediaAdapter.getData().size()) {
//                LocalPictureViewActivity.startActivity(mContext, position);
                LocalPictureBannerActivity.startActivity(mContext, position);
            }
        });
        MediaCenter.getInstance().registerImageDataChanged(this);
        if (getArguments() != null) {
            mFileDir = getArguments().getString(EXTRA_KEY_FILE_PATH);
        }
        loadLocalFile();
    }

    private void loadLocalFile() {
        if (TextUtils.isEmpty(mFileDir)) {
            showError(getString(R.string.give_file_path));
            return;
        }
        final File dir = new File(mFileDir);
        if (!dir.exists() || !dir.isDirectory()) {
//            ToastUtil.toast("请传入正确的文件夹路径");
            showError(getString(R.string.empty_picture_file_tip));
            return;
        }
        try {
            File[] pictureFileArr = dir.listFiles((dir1, name) ->
                    PICTURE_SUFFIX.contains(name.substring(name.lastIndexOf(".") + 1).toLowerCase()));
            if (pictureFileArr == null) {
                pictureFileArr = new File[0];
            }
            final List<File> fileList = new ArrayList<>(Arrays.asList(pictureFileArr));
            showFileList(fileList);
        } catch (Exception e) {
            Logger.d(TAG, e.toString());
            showError(ExceptionToTip.toTip(e));
        }
    }

    private void showFileList(final List<File> fileList) {
        MediaCenter.getInstance().setLocalImageList(fileList);
        mMediaAdapter.setNewInstance(fileList);
        if (fileList == null || fileList.isEmpty()) {
            showError(getString(R.string.empty_picture_file_tip));
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

}

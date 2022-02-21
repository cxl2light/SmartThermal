package com.hq.monitor.media.local;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hq.base.dialog.CommonConfirmDialog;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ExceptionToTip;
import com.hq.base.util.ScreenUtils;
import com.hq.base.util.ShareUtils;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.TimeUtils;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.commonwidget.AspectRatioFrameLayout;
import com.hq.monitor.R;
import com.hq.monitor.device.selectdialog.OnSelectClickListener;
import com.hq.monitor.device.selectdialog.SelectDialog;
import com.hq.monitor.media.MediaCenter;
import com.to.aboomy.pager2banner.Banner;

import java.io.File;
import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.widget.IRenderView;
import tv.danmaku.ijk.media.player.widget.TextureRenderView;

public class LocalVideoBannerActivity extends BaseActivity {
    private static final String EXTRA_KEY_POSITION = "extra_key_position";

    private int mPosition = -1;

    private CommonTitleBar mTitleBar;
    private TextureRenderView mPlayerView;
    private View mProgressView;
    private ImageView play_pause_btn;
    private TextView played_time, total_length_time;
    private SeekBar video_progress_bar;

    File videoModel;

    RelativeLayout rlOne;
    RelativeLayout rlTwo;
    Banner banner;
    IRenderView.IRenderCallback renderCallback = null;

    AspectRatioFrameLayout ratioLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_banner);

        StatusBarUtil.setStatusBarWhiteMode(this);

        ratioLayout = findViewById(R.id.aspect_ratio_layout);
        mProgressView = findViewById(R.id.progress_view);
        mTitleBar = findViewById(R.id.title_bar);
        play_pause_btn = findViewById(R.id.play_pause_btn);
        played_time = findViewById(R.id.played_time);
        total_length_time = findViewById(R.id.total_length_time);
        video_progress_bar = findViewById(R.id.video_progress_bar);
        initTitleBar();
        mPosition = getIntent().getIntExtra(EXTRA_KEY_POSITION, mPosition);
        videoModel = MediaCenter.getInstance().getLocalVideo(mPosition);
        if (videoModel == null || !videoModel.exists()) {
            return;
        }

        initBanner();
    }

    private void initVideoStart() {
        video_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser || mIjkMediaPlayer == null) {
                    return;
                }
                //跳转到指定位置播放
                mIjkMediaPlayer.seekTo(progress);
                //更新进度
                updateProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIjkMediaPlayer == null) {
                    return;
                }
                if (playOver) {
                    mIjkMediaPlayer.start();
                    play_pause_btn.setImageResource(R.drawable.ic_pause);
                    //开启更新进度
                    mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
                    playOver = false;
                    return;
                }
                updatePlayState();
            }
        });
    }

    private void initBanner() {
        rlOne = findViewById(R.id.rlOne);
        rlTwo = findViewById(R.id.rlTwo);

        TextView tvTitleNum = findViewById(R.id.tvTitleNum);
        tvTitleNum.setText( mPosition + 1 + "/" + MediaCenter.getInstance().getLocalVideoListSize());

        banner = findViewById(R.id.banner);
        banner.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        //创建adapter
        LocalVideoBannerAdapter adapter = new LocalVideoBannerAdapter();

        banner.setAdapter(adapter);
        adapter.setNewInstance(MediaCenter.getInstance().getLocalVideoList());

        banner.setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("ZeOne:", "onPageScrolled=" + position);
                onPause();
//              mIjkMediaPlayer = null;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("ZeOne:", "onPageSelected=" + position);
                rlTwo.setVisibility(View.GONE);
                mPosition = position;
                tvTitleNum.setText( position + 1 + "/" + MediaCenter.getInstance().getLocalVideoListSize());

                release();
                mHandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d("ZeOne:", "onPageScrollStateChanged=" + state);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapte, @NonNull View view, int position) {
                  if (mIjkMediaPlayer != null){
                      updatePlayState();
                      return;
                  }
                    Log.d("ZeOne=","initPlayer=" + position + ",mPosition=" + mPosition + ",position=" + position + "," + adapter.getData().size());
                    rlTwo.setVisibility(View.VISIBLE);
                    initPlayer(MediaCenter.getInstance().getLocalVideo(mPosition));
                    initVideoStart();
            }
        });

        banner.setCurrentItem(mPosition);
        banner.setAutoPlay(false);
    }

    private void initTitleBar() {
        final int colorRes = R.color.text_color_normal_dark_bg;
        mTitleBar.setIconTintColor(colorRes);
        mTitleBar.setMenuTextColor(colorRes);
        mTitleBar.setTitleTextColor(colorRes);
        mTitleBar.setBackgroundResource(R.color.player_bg_trans);

//        mTitleBar.setShareTvTextColor(colorRes);
//        mTitleBar.setMenu(getString(R.string.operate_delete));
//        mTitleBar.setMenuClick(v -> showConfirmDialog());

        mTitleBar.setImageDeleteClick(v -> {
            final File model = MediaCenter.getInstance().getLocalVideo(mPosition);
            if (model == null) {
                return;
            }
            mDelPosition = mPosition;
            initSelectDialog(getString(R.string.delete_tip_placeholder, model.getName()));

//          showConfirmDialog();
        });

//        mTitleBar.setShareTv(getString(R.string.operate_share));
//        mTitleBar.setMenuShareClick(v -> ShareUtils.shareFile(videoModel,this));

        mTitleBar.setImageShareClick(v -> ShareUtils.shareFile(videoModel,this));
    }

    private IjkMediaPlayer mIjkMediaPlayer;

    private void initPlayer(File file) {
        /**
         * ijkPlayer切换视频源的时候，必须新建
         */
        if (mPlayerView != null){
            ratioLayout.removeView(mPlayerView);
            mPlayerView = null;
        }
        mPlayerView = new TextureRenderView(this);
        ratioLayout.addView(mPlayerView);
        mPlayerView.setLayoutParams(ScreenUtils.getVideoLayoutParamsFrame(this));
        if (renderCallback != null){
            mPlayerView.removeRenderCallback(renderCallback);
        }


        Uri fileUri = Uri.fromFile(file);
        mIjkMediaPlayer = new IjkMediaPlayer();
        Log.d("ZeVideo","duration=1001");
        renderCallback = new IRenderView.IRenderCallback() {
            @Override
            public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
                holder.bindToMediaPlayer(mIjkMediaPlayer);
                //开启异步准备
                try {
                    mIjkMediaPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    ToastUtil.toast(ExceptionToTip.toTip(e));
                }
            }

            @Override
            public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {

            }
        };
        Log.d("ZeVideo","duration=1002");
        mPlayerView.addRenderCallback(renderCallback);
        Log.d("ZeVideo","duration=1003");
//        final IjkMediaPlayer mediaPlayer = mIjkMediaPlayer;
        //mediaPlayer准备工作-------回调,onPrepared
        mIjkMediaPlayer.setOnPreparedListener(mp -> {
            mProgressView.setVisibility(View.GONE);
            mp.start();
            play_pause_btn.setImageResource(R.drawable.ic_pause);
            final int duration = (int) mp.getDuration(); //获取视频总时长
            Log.d("ZeVideo","duration=" + duration);
            video_progress_bar.setMax(duration);//设置进度条最大值
            total_length_time.setText(TimeUtils.parseDuration(duration));//设置总时长
            //开始更新进度
            startUpdateProgress();
        });
        //MediaPlayer完成---------回调,onCompletion
        mIjkMediaPlayer.setOnCompletionListener(mp -> {
            Log.d("ZeVideo","duration=1005");
            mProgressView.setVisibility(View.GONE);
            playOver = true;
            play_pause_btn.setImageResource(R.drawable.ic_play);
            mHandler.removeMessages(UPDATE_PROGRESS);

//          rlTwo.setVisibility(View.GONE);
        });
        Log.d("ZeVideo","duration=1004");
        try {
            mIjkMediaPlayer.setDataSource(mActivity.getApplicationContext(), fileUri);
        } catch (IOException e) {
            ToastUtil.toast(ExceptionToTip.toTip(e));
        }
    }

    private boolean playOver = false;

    private static final int UPDATE_PROGRESS = 300;

    //切换播放状态
    private void updatePlayState() {
        //获取当前播放状态
        final boolean playing = mIjkMediaPlayer.isPlaying();
        //修改播放状态
        if (playing) {
            //暂停
            mIjkMediaPlayer.pause();
            play_pause_btn.setImageResource(R.drawable.ic_play);
            //移除定时更新进度
            mHandler.removeMessages(UPDATE_PROGRESS);
        } else {
            //播放
            mIjkMediaPlayer.start();
            play_pause_btn.setImageResource(R.drawable.ic_pause);
            //开启更新进度
            mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
        }
    }

    private CommonConfirmDialog mCommonConfirmDialog;
    private int mDelPosition = -1;

    private void initSelectDialog(String content){
        SelectDialog mDialog = new SelectDialog();
        mDialog.show(getSupportFragmentManager(), false, "title", "", new OnSelectClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                delete();
            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
            }
        });
    }

    private void showConfirmDialog() {
        final File model = MediaCenter.getInstance().getLocalVideo(mPosition);
        if (model == null) {
            return;
        }
        mDelPosition = mPosition;
        if (mCommonConfirmDialog == null) {
            mCommonConfirmDialog = new CommonConfirmDialog(this);
            mCommonConfirmDialog.setConfirmListener(v -> {
                delete();
            });
        }
        mCommonConfirmDialog.setContent(getString(R.string.delete_tip_placeholder, model.getName()));
        mCommonConfirmDialog.show();
    }

    private void delete() {
        final File model = MediaCenter.getInstance().getLocalVideo(mDelPosition);
        if (model == null) {
            return;
        }
        try {
            release();
            MediaCenter.getInstance().deleteVideoFile(mDelPosition);
            ToastUtil.toast(getString(R.string.tip_delete_success));
            mDelPosition = -1;
            finish();
        } catch (Exception e) {
            ToastUtil.toast(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIjkMediaPlayer == null) {
            return;
        }
        mIjkMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIjkMediaPlayer == null) {
            return;
        }
        mIjkMediaPlayer.pause();
//        rlTwo.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void release() {
        if (mIjkMediaPlayer == null) {
            return;
        }
        mIjkMediaPlayer.stop();
        mIjkMediaPlayer.release();
        mIjkMediaPlayer = null;
    }

    //开始更新进度
    private void startUpdateProgress() {
        if (mIjkMediaPlayer == null) {
            return;
        }
        //获取当前进度
        final int progress = (int) mIjkMediaPlayer.getCurrentPosition();
        //设置进度
        updateProgress(progress);
        played_time.setText(TimeUtils.parseDuration((progress)));
        video_progress_bar.setProgress(progress);
        //定时更新
        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }

    //更新进度数值设置进度
    private void updateProgress(int progress) {
        played_time.setText(TimeUtils.parseDuration(progress));
        video_progress_bar.setProgress(progress);
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    startUpdateProgress();
                    break;
            }
        }
    };

    public static void startActivity(Context context, int position) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, LocalVideoBannerActivity.class);
//        intent.putExtra(EXTRA_KEY_VIDEO_URI, videoUri);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        context.startActivity(intent);
    }
}

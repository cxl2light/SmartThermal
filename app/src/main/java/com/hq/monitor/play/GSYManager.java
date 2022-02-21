package com.hq.monitor.play;

/*import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;*/

/**
 * Created on 2020/4/12.
 * author :
 * desc :
 */
public class GSYManager {

    private GSYManager() {

    }

    public static void init() {
//        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        initGSYVideoOptions();
    }

    /**
     * 配置文档：
     * https://github.com/bilibili/ijkplayer/blob/cced91e3ae3730f5c63f3605b00d25eafcf5b97b/ijkmedia/ijkplayer/ff_ffplay_options.h
     */
    private static void initGSYVideoOptions() {
        /*final List<VideoOptionModel> list = new ArrayList<>(40);
        // //不额外优化
        VideoOptionModel model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
        list.add(model);
        *//*
        是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
        pause output until enough packets have been read after stalling
         *//*
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        list.add(model);
        // 是否开启mediacodec硬解;1: 开启，0：不开启，使用软解
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        list.add(model);
        *//*
        丢帧  是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
        drop frames when cpu is too slow：0-120
         *//*
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 30);//丢帧,太卡可以尝试丢帧
        list.add(model);
        //automatically start playing on prepared
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        list.add(model);
        *//*
        //最大缓冲大小,单位kb
        //max buffer size should be pre-read：默认为15*1024*1024
         *//*
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024);//最大缓存数
        list.add(model);
        //默认最小帧数3
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3);//最大缓存时长
        list.add(model);
        //input buffer:don't limit the input buffer size (useful with realtime streams)
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);//是否限制输入缓存数,无限读
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 30);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 15);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV16);
        list.add(model);
        // //播放重连次数
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);
        list.add(model);

        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV16);
        list.add(model);

        // 播放前的探测Size，默认是1M, 改小一点会出画面更快
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024);//10240
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "4096");
        list.add(model);
        //设置播放前的探测时间 1,达到首屏秒开效果
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");
        list.add(model);
        // 如果是rtsp协议，可以优先用tcp(默认是用udp)
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "udp");//udp传输数据
        list.add(model);
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzedmaxduration", 100);//分析码流时长:默认1024*1000
        list.add(model);

        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小 ,默认值48
        model = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        list.add(model);

        GSYVideoManager.instance().setOptionModelList(list);*/

    }


}

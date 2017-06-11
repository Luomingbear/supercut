package com.slwb.supercut.activity.mainwork;

import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.slwb.supercut.ThumbnailView;

/**
 * Created by bear on 17-6-11.
 */
public interface MainWorkView {
    /**
     * 获取返回按钮
     *
     * @return
     */
    Button getBackButton();

    /**
     * 获取帮助按钮
     */
    Button getHelpButton();

    /**
     * 获取相册的按钮
     *
     * @return
     */
    Button getAlbumButton();

    /**
     * 获取导出按钮
     *
     * @return
     */
    Button getExportButton();

    /**
     * 获取帮助的布局
     *
     * @return
     */
    View getHelpLayout();


    /**
     * 获取显示视频的surfaceView
     *
     * @return
     */
    SurfaceView getSurfaceView();

    /**
     * 获取显示当前视频时间的TV
     *
     * @return
     */
    TextView getTimeIndexView();

    /**
     * 获取视频条控件
     *
     * @return
     */
    ThumbnailView getThumbnailView();

    /**
     * 跳转到选择视频
     */
    void intent2ChooseFile();

    /**
     * 跳转到工作室
     */
    void intent2WorkStation();
}

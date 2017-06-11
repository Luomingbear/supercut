package com.slwb.supercut.activity.mainwork;

/**
 * mvp
 * 视频编辑页面的功能接口
 * Created by bear on 17-6-11.
 */
public interface MainWorkPresenter {

    void initFolder();

    void showHelp();

    void exportFile();

    void importFile();

    void initSurfaceView();

    void play();
}

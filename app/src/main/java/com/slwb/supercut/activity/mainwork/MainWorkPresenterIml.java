package com.slwb.supercut.activity.mainwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.slwb.supercut.FinalCompose;
import com.slwb.supercut.Thumbnail;

import java.io.File;
import java.util.List;

/**
 * mvp
 * 编辑页面的逻辑实现
 * Created by bear on 17-6-11.
 */
public class MainWorkPresenterIml implements MainWorkPresenter {
    private MainWorkView mMainWorkView;
    private Context mContext;

    private boolean isHelp = false; //帮助正在显示吗
    private SurfaceHolder mSurfaceHolder; //
    private PlayThread mPlayThread; //播放线程
    private int mSurfaceWidth, mSurfaceHeight;

    public MainWorkPresenterIml(Context context, MainWorkView mainWorkView) {
        this.mContext = context;
        this.mMainWorkView = mainWorkView;
    }

    @Override
    public void initFolder() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/剪影/作品");
            if (!file.exists()) {
                file.mkdirs();
            }

            File file2 = new File(Environment.getExternalStorageDirectory() + "/剪影/视频池");
            if (!file2.exists()) {
                file2.mkdirs();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void showHelp() {
        if (isHelp) {
            mMainWorkView.getHelpLayout().setVisibility(View.GONE);
            isHelp = false;
        } else {
            mMainWorkView.getHelpLayout().setVisibility(View.VISIBLE);
            isHelp = true;
        }
    }

    @Override
    public void exportFile() {

        FinalCompose finalCompose;

        int n = mMainWorkView.getThumbnailView().getVideoFiles().size();
        List<String> listFile = mMainWorkView.getThumbnailView().getVideoFiles();
        finalCompose = new FinalCompose();
        finalCompose.setFinalCompose(mMainWorkView.getThumbnailView().getCutxml(), n);
        finalCompose.Merge();

				/*
                //
				FFmpegNative ffmpeg=new FFmpegNative();
				ffmpeg.Decode("/storage/emulated/0/剪影/作品/test.mp4","/storage/emulated/0/test.yuv");
				//
				*/
        //ewj.merge(listFile, FinalCompose.outsrc);
        updateGallery(finalCompose.getOutsrc());//更新媒体库

        /**
         *保存数据
         */
        SharedPreferences share = mContext.getSharedPreferences("persondata", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
        edit.putString("outSrc", finalCompose.getOutsrc());
        edit.apply();    //提交数据保存

        mMainWorkView.intent2WorkStation();
    }

    @Override
    public void importFile() {
        mMainWorkView.intent2ChooseFile();
    }

    @Override
    public void initSurfaceView() {
        mSurfaceHolder = mMainWorkView.getSurfaceView().getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mPlayThread = new PlayThread(surfaceHolder, mContext);
                mPlayThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                mSurfaceWidth = i1;
                mSurfaceHeight = i2;

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//                mPlayThread.stop();
            }
        });
    }

    @Override
    public void play() {

    }

    //filename是我们的文件全名，包括后缀
    private void updateGallery(String filename) {
        MediaScannerConnection.scanFile(mContext,
                new String[]{filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    class PlayThread extends Thread {
        SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        Paint paint;

        private Bitmap showBitmap, showBitmapCatch;
        private int viewWidth, viewHeight;
        private Rect rectmap, rectview;

        private int LineOnFrame = 20, LineOnFrameBefor = 10;

        public PlayThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
        }

        @Override
        public void run() {
            while (true) {
                Canvas c = mSurfaceHolder.lockCanvas();
                String showpath = mMainWorkView.getThumbnailView().getShowpath();
                //2.开画
                Paint p = new Paint();
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

                Paint text = new Paint();
                text.setColor(Color.BLUE);
                text.setTextSize(20);
                if (c != null && showpath != null) {
                    //显示在画布上
                    LineOnFrame = mMainWorkView.getThumbnailView().getLineOnFrame();

                    try {
                        Thumbnail thumbnail = new Thumbnail();
                        thumbnail.setPath(showpath);
                        showBitmap = thumbnail.getFrameOn(LineOnFrame);
                        showBitmapCatch = showBitmap;
                        rectmap = new Rect(0, 0, showBitmap.getWidth(), showBitmap.getHeight());
                        //预览视频的高度
                        int h = (int) ((showBitmap.getHeight() * mSurfaceWidth) / (float) showBitmap.getWidth());
                        //
                        rectview = new Rect(0, (int) ((mSurfaceHeight - h) / 2.0), mSurfaceWidth,
                                (int) ((mSurfaceHeight - h) / 2.0) + h);
                    } catch (NullPointerException e) {
                        showBitmap = showBitmapCatch;
                        rectmap = new Rect(0, 0, mSurfaceWidth, mSurfaceHeight);
                        rectview = rectmap;
                    }
                    //
                    c.drawPaint(p);
                    p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    c.drawBitmap(showBitmap, rectmap, rectview, p);
                    LineOnFrameBefor = LineOnFrame;

                    //3. 解锁画布   更新提交屏幕显示内容
                    mSurfaceHolder.unlockCanvasAndPost(c);
                    try {
                        Thread.sleep(1000 / 30);
                    } catch (Exception e) {
                    }
                } else if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}

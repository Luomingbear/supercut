
package com.slwb.supercut.activity.mainwork;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.slwb.mediachooser.MediaChooser;
import com.slwb.mediachooser.activity.HomeFragmentActivity;
import com.slwb.supercut.R;
import com.slwb.supercut.ThumbnailView;
import com.slwb.supercut.activity.WorkStation;

import java.util.ArrayList;
import java.util.List;

public class MainWork extends Activity implements MainWorkView, OnClickListener {

    //设置特效的图标显示
    private static final int[] ITEM_DRAWABLES = {R.drawable.cutwy,
            R.drawable.titlewy, R.drawable.lvjingwy, R.drawable.deletewy};
    private Button ok, back, get, help, button_up;
    private SurfaceView surface;
    private TextView timeShowTv;

    private int IT = 11;
    private int ITC[] = {15, 16, 17, 18};
    private String hits[] = {"剪切", "字幕", "滤镜", "删除"};
    private String filepath = null;
    private RelativeLayout tools;
    private List<String> listFile = new ArrayList<String>();   //最终视频List
    private ThumbnailView thumbnailView;
    private RelativeLayout helplayout;

    private MainWorkPresenter mPresenter; //视频编辑页面的逻辑实现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_edit);

        initView();

        mPresenter = new MainWorkPresenterIml(this, this);

        initEvent();
    }

    private void initView() {

		/*
        //向上弹出工具栏
		button_up=(Button)findViewById(R.id.button_up);
		button_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				pop();
				}
		});
		*/

        //帮助浮层
        helplayout = (RelativeLayout) findViewById(R.id.helpLayout);
        helplayout.setVisibility(View.GONE);
        help = (Button) findViewById(R.id.help);

        back = (Button) findViewById(R.id.back);

        //选择素材
        get = (Button) findViewById(R.id.get);

        timeShowTv = (TextView) findViewById(R.id.timeShow);

        ok = (Button) findViewById(R.id.ok);

        //播放预览
        surface = (SurfaceView) findViewById(R.id.surface_view);

        thumbnailView = (ThumbnailView) findViewById(R.id.thumbnailView);
    }

    private void initEvent() {
        IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        registerReceiver(videoBroadcastReceiver, videoIntentFilter);

        IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        registerReceiver(imageBroadcastReceiver, imageIntentFilter);

        back.setOnClickListener(this);

        help.setOnClickListener(this);

        get.setOnClickListener(this);

        ok.setOnClickListener(this);

        surface.setOnClickListener(this);

        mPresenter.initFolder();
        mPresenter.initSurfaceView();

    }

    BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String path;
            listFile = intent.getStringArrayListExtra("list");
            //获取略略缩图
            thumbnailView.addVideo(listFile);
            thumbnailView.invalidate();//刷新画面
        }
    };
    BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            filepath = intent.getStringArrayListExtra("list").toString();
            filepath = filepath.substring(1, filepath.length() - 1);

        }
    };

    //弹出菜单
    public void pop() {

        tools = (RelativeLayout) findViewById(R.id.tools);
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.tools, null);
        // 创建PopupWindow对象
        final PopupWindow pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, false);
        pop.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.AnimationFade);

        if (pop.isShowing()) {// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
            pop.dismiss();
        } else {
            // 显示窗口
            pop.showAtLocation(findViewById(R.id.tools), Gravity.BOTTOM, 0, 0);
        }

    }

    @Override
    public Button getBackButton() {
        return back;
    }

    @Override
    public Button getHelpButton() {
        return help;
    }

    @Override
    public Button getAlbumButton() {
        return get;
    }

    @Override
    public Button getExportButton() {
        return ok;
    }

    @Override
    public View getHelpLayout() {
        return helplayout;
    }

    @Override
    public SurfaceView getSurfaceView() {
        return surface;
    }

    @Override
    public TextView getTimeIndexView() {
        return timeShowTv;
    }

    @Override
    public ThumbnailView getThumbnailView() {
        return thumbnailView;
    }

    @Override
    public void intent2ChooseFile() {
        Intent intent = new Intent(MainWork.this, HomeFragmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void intent2WorkStation() {
        Intent f = new Intent();
        f.setClass(MainWork.this, WorkStation.class);
        startActivity(f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.help:
                mPresenter.showHelp();
                break;
            case R.id.get:
                mPresenter.importFile();
                break;
            case R.id.ok:
                mPresenter.exportFile();
                break;
            case R.id.surface_view:
                mPresenter.play();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(imageBroadcastReceiver);
        unregisterReceiver(videoBroadcastReceiver);
        super.onDestroy();
    }
}

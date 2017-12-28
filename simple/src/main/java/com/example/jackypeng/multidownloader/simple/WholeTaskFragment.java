package com.example.jackypeng.multidownloader.simple;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jackypeng.multidownloader.R;
import com.example.jackypeng.multidownloader.bean.MyBusinessInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jackypeng on 2017/12/26.
 */

public class WholeTaskFragment extends Fragment {

    private Unbinder binder;
    @BindView(R.id.fragment_whole_recycle_view)
    RecyclerView recyclerView;

    private WholeTaskRecycleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whole_task, container,false);
        binder = ButterKnife.bind(this,rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WholeTaskRecycleAdapter(getDownloadListData(), getContext());
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    private List<MyBusinessInfo> getDownloadListData() {
        ArrayList<MyBusinessInfo> myBusinessInfos = new ArrayList<>();
        myBusinessInfos.add(new MyBusinessInfo("QQ",
                "http://img.wdjimg.com/mms/icon/v1/4/c6/e3ff9923c44e59344e8b9aa75e948c64_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/e/b8/520c1a2208bf7724b96f538247233b8e.apk"));
        myBusinessInfos.add(new MyBusinessInfo("微信",
                "http://img.wdjimg.com/mms/icon/v1/7/ed/15891412e00a12fdec0bbe290b42ced7_256_256.png",
                "http://wdj-uc1-apk.wdjcdn.com/1/a3/8ee2c3f8a6a4a20116eed72e7645aa31.apk"));
        myBusinessInfos.add(new MyBusinessInfo("360手机卫士",
                "http://img.wdjimg.com/mms/icon/v1/d/29/dc596253e9e80f28ddc84fe6e52b929d_256_256.png",
                "http://www.qmtjr.com/App/qmtjr_2.8.0.apk"));
        myBusinessInfos.add(new MyBusinessInfo("陌陌",
                "http://img.wdjimg.com/mms/icon/v1/a/6e/03d4e21876706e6a175ff899afd316ea_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/b/0a/369eec172611626efff4e834fedce0ab.apk"));
        myBusinessInfos.add(new MyBusinessInfo("美颜相机",
                "http://img.wdjimg.com/mms/icon/v1/7/7b/eb6b7905241f22b54077cbd632fe87b7_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/a/e9/618d265197a43dab6277c41ec5f72e9a.apk"));
        myBusinessInfos.add(new MyBusinessInfo("Chrome",
                "http://img.wdjimg.com/mms/icon/v1/d/fd/914f576f9fa3e9e7aab08ad0a003cfdd_256_256.png",
                "http://wdj-qn-apk.wdjcdn.com/6/0d/6e93a829b97d671ee56190aec78400d6.apk"));
        myBusinessInfos.add(new MyBusinessInfo("网易云音乐",
                getResourceUri(R.mipmap.wangyi_music),
                "http://d1.music.126.net/dmusic/CloudMusic_official_4.3.1.319310.apk"));
        myBusinessInfos.add(new MyBusinessInfo("酷狗音乐",
                getResourceUri(R.mipmap.kugou_music),
                "http://downmobile.kugou.com/Android/KugouPlayer/8921/KugouPlayer_219_V8.9.2.apk"));
        myBusinessInfos.add(new MyBusinessInfo("喜马拉雅",
                getResourceUri(R.mipmap.ximalaya),
                "http://s1.xmcdn.com/apk/MainApp_v6.3.60.3_c148_release_proguard_171219_and-a1.apk"));
        myBusinessInfos.add(new MyBusinessInfo("优酷",
                getResourceUri(R.mipmap.youku),
                "http://p.gdown.baidu.com/d8bc950cfd68550ecac9bc0ba352c083966f150ff348be9a9fb64d2eb67a89394f8f08b69589a4cb8beb2ae29bdd0137696962e6215edfeb2093102d7e6c0d3e7d651b1d7af27d9787f5d1cf00ca6c867df1a8039a75cea140767eda085bbd5b3952d481312bcfc10d28168c15da8faca89d11e518fccaf51db3715c63e6609423ec8f9cfecd1f16"));
        return myBusinessInfos;
    }

    public String getResourceUri(int resId) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getContext().getPackageName() + "/" + getResources().getResourceTypeName(resId) + "/" + getResources().getResourceEntryName(resId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binder.unbind();
    }
}

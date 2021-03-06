package com.juntai.wisdom.basecomponent.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.juntai.wisdom.basecomponent.R;
import com.juntai.wisdom.basecomponent.utils.DisplayUtil;
import com.juntai.wisdom.basecomponent.utils.EventManager;
import com.juntai.wisdom.basecomponent.utils.ImageLoadUtil;
import com.juntai.wisdom.basecomponent.utils.LoadingDialog;
import com.juntai.wisdom.basecomponent.utils.ScreenUtils;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public abstract class BaseActivity extends RxAppCompatActivity implements Toolbar.OnMenuItemClickListener {
    public abstract int getLayoutView();

    public abstract void initView();

    public abstract void initData();

    public Context mContext;
    public Toast toast;
    private Toolbar toolbar;
    protected CoordinatorLayout mBaseRootCol;
    private boolean title_menu_first = true;
    private TextView mBackTv;
    public ImmersionBar mImmersionBar;
    private TextView titleName, titleRightTv;
    private boolean autoHideKeyboard = true;
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.getLibraryEvent().register(this);//??????
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ????????????
        mContext = this;
        mImmersionBar = ImmersionBar.with(this);
//        initWidows();
        setContentView(R.layout.activity_base);
        frameLayout = findViewById(R.id.base_content);
        if (0 != getLayoutView()) {
            frameLayout.addView(View.inflate(this, getLayoutView(), null));
        }
        toolbar = findViewById(R.id.base_toolbar);
        mBaseRootCol = findViewById(R.id.base_col);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBackTv = findViewById(R.id.back_tv);
        titleName = findViewById(R.id.title_name);
        titleRightTv = findViewById(R.id.title_rightTv);
        initToolbarAndStatusBar();
        initLeftBackTv(true);
        initView();
        initData();
    }
    /**
     * ????????????
     *
     * @param drawableId
     */
    public void setRightTvDrawable( int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, DisplayUtil.dp2px(mContext, 23), DisplayUtil.dp2px(mContext, 23));//????????? 0 ?????????????????????????????? 0 ?????????????????????40 ???????????????
        getTitleRightTv().setCompoundDrawables(drawable, null, null, null);//????????????
    }
    /**
     * ???????????????  view ????????????????????????view ???????????????edittext
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void hideKeyboardFromView(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * view????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public  void getViewFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        hideKeyboardFromView(view);
    }


    /**
     * ????????? ????????????????????? ?????????toolbar????????????
     */
    protected void initToolbarAndStatusBar() {
        getToolbar().setVisibility(View.VISIBLE);
        getToolbar().setNavigationIcon(null);
        getToolbar().setBackgroundResource(R.drawable.bg_accent_only_bottom_gray_shape_1px);
        //???????????????
        mBaseRootCol.setFitsSystemWindows(true);
        mImmersionBar.statusBarColor(R.color.colorAccent)
                .statusBarDarkFont(true)
                .init();
    }

    /**
     * ????????????????????? ???????????????
     *
     * @param isShow ????????????
     */
    protected void initLeftBackTv(boolean isShow) {
        if (isShow) {
            mBackTv.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.app_back);
            // ?????????????????????,??????????????????.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBackTv.setCompoundDrawables(drawable, null, null, null);
//            mBackTv.setText("??????");
            mBackTv.setCompoundDrawablePadding(-DisplayUtil.dp2px(this, 3));
            mBackTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else {
            mBackTv.setVisibility(View.GONE);
        }

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getTitleRightTv() {
        titleRightTv.setVisibility(View.VISIBLE);
        return titleRightTv;
    }

    /**
     * ??????????????????
     */
    public void showLoadingDialog(Context context) {
        LoadingDialog.getInstance().showProgress(context);
    }

    /**
     * ???????????????
     *
     * @return
     */
    public TextView getTitleLeftTv() {
        mBackTv.setVisibility(View.VISIBLE);
        return mBackTv;
    }

    /**
     * ??????
     *
     * @param title
     */
    public void setTitleName(String title) {
        titleName.setText(title);
        titleName.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    /**
     * title??????:?????????
     */
    private void setRightRes() {
        //??????menu
        toolbar.inflateMenu(R.menu.toolbar_menu);
        //????????????
        toolbar.setOnMenuItemClickListener(this);
    }

    /**
     * ??????????????????
     *
     * @param itemId
     */
    public void showTitleRes(int... itemId) {
        if (title_menu_first) {
            setRightRes();
            title_menu_first = false;
        }
        for (int item : itemId) {
            //??????
            toolbar.getMenu().findItem(item).setVisible(true);//??????id??????,????????????setIcon()????????????
            //            toolBar.getMenu().getItem(0).setVisible(true);//??????????????????
        }
    }

    /**
     * ??????title??????
     *
     * @param itemId :?????????????????????id
     */
    public void hindTitleRes(int... itemId) {
        //        if (titleBack != null)
        //            titleBack.setVisibility(View.GONE);
        for (int item : itemId) {
            //??????
            toolbar.getMenu().findItem(item).setVisible(false);
        }
    }

    /**
     * toolbar????????????---???activity??????onMenuItemClick()
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    /**
     * ??????webview
     *
     * @param webView
     */
    public void closeWebView(WebView webView) {
        if (webView != null) {
//            ViewGroup parent = webView.getParent();
//            if (parent != null) {
//                parent.re(webView);
//            }
            webView.removeAllViews();
            webView.destroy();
        }
    }
//    @Override
//    public void showLoadingFileDialog() {
//        showFileDialog();
//    }
//
//    @Override
//    public void hideLoadingFileDialog() {
//        hideFileDialog();
//    }

//    @Override
//    public void onProgress(long totalSize, long downSize) {
//        if (dialog != null) {
//            dialog.setProgress((int) (downSize * 100 / totalSize));
//        }
//    }

    /**
     * ????????????????????????
     *
     * @param event
     * @param view
     * @param activity
     */
    public static void hideKeyboard(MotionEvent event, View view,
                                    Activity activity) {
        try {
            if (view != null && view instanceof TextView) {
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bootom = top + view.getHeight();
                // ???????????????????????????????????????????????????????????????????????????????????????
                if (event.getRawX() < left || event.getRawX() > right
                        || event.getY() < top || event.getRawY() > bootom) {
                    // ????????????
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(token,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????  view ????????????????????????view ???????????????edittext
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * ??????????????????????????????- - - ??????
     *
     * @param autoHideKeyboard:false - ???????????????
     */
    public void setAutoHideKeyboard(boolean autoHideKeyboard) {
        this.autoHideKeyboard = autoHideKeyboard;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                if (autoHideKeyboard) {
                    hideKeyboard(ev, view, BaseActivity.this);//??????????????????????????????????????????
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        ToastUtils.info(mContext,"??????");
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventManager.getLibraryEvent().unregister(this);//??????
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();  //??????????????????????????????????????????????????????????????????????????????bar???????????????????????????app????????????????????????????????????????????????????????????bar???????????????
            mImmersionBar = null;
        }
        this.mContext = null;
        stopLoadingDialog();
    }

    /**
     * ??????????????????
     */
    public void stopLoadingDialog() {
        LoadingDialog.getInstance().dismissProgress();
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public List<String> getTestData() {
        return Arrays.asList(new String[]{"test1", "test2", "test3", "test4", "test5", "?????????????????????", "???????????????????????????????????????????????????????????????", "test2", "test3", "test4", "test5", "???????????????????????????????????????????????????????????????XXXXXXXXXXXXX", "???????????????????????????????????????????????????????????????"});
    }

    /**
     * ??????TextView??????
     *
     * @param textView
     * @return
     */
    public String getTextViewValue(TextView textView) {
        return textView.getText().toString().trim();
    }


    /**
     * ??????????????? ?????????????????????????????????
     */
    protected void initWidows() {
        //?????????????????? 360??????????????????px/2
        ScreenUtils screenUtils = ScreenUtils.getInstance(getApplicationContext());
        if (screenUtils.isPortrait()) {
            screenUtils.adaptScreen4VerticalSlide(this, 360);
        } else {
            screenUtils.adaptScreen4HorizontalSlide(this, 360);
        }

    }

    /**
     * ?????????recyclerview LinearLayoutManager
     */
    public void initRecyclerview(RecyclerView recyclerView, BaseQuickAdapter baseQuickAdapter, @RecyclerView.Orientation int orientation) {
        LinearLayoutManager managere = new LinearLayoutManager(this, orientation, false);
        baseQuickAdapter.setEmptyView(getAdapterEmptyView("?????????????????????", R.drawable.none_publish));
        recyclerView.setLayoutManager(managere);
        recyclerView.setAdapter(baseQuickAdapter);
    }
    /**
     * ???????????????
     *
     * @param text
     * @return
     */
    public View getAdapterEmptyView(String text, int imageId) {
        View view = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        TextView noticeTv = view.findViewById(R.id.none_tv);
        noticeTv.setText(text);
        ImageView imageView = view.findViewById(R.id.none_image);
        if (-1==imageId) {
            imageView.setVisibility(View.GONE);
        }else {
            imageView.setImageResource(imageId);
        }
        return view;
    }

    /**
     * ??????imageview????????????
     *
     * @param imageView
     */
    public void recycleImageView(ImageView imageView) {
        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bm.recycle();
        bm = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //???ui????????????
    public void receiveMsg(String test) {
//        if ("888888".equals(test)) {
//            LogUtil.e(test);
//        }
    }

    /**
     * ????????????  Invisible  gone
     *
     * @param isGone gone
     * @param views
     */
    protected void setViewsInvisible(boolean isGone, View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    if (isGone) {
                        view.setVisibility(View.GONE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    /**
     * ????????????  Invisible  gone
     *
     * @param views
     */
    protected void setViewsVisible(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}

package com.example.multi_downloader.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.multi_downloader.R;


/**
 * Created by pj on 2017/2/27.
 * 一个通用的确认取消对话框
 */
public class GeneralPositiveAndNegativeDialog extends Dialog {

    private TextView mTitleView;
    private View contentView;

    public GeneralPositiveAndNegativeDialog(Context context) {
        this(context, R.style.Theme_Transparent);
    }

    public GeneralPositiveAndNegativeDialog(Context context, int themeResId) {
        super(context, themeResId);
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_general_positive_negative, null);
        int dialog_width = (int) (DeviceInfo.getInstance().getDevice_width() * 2.0 / 3);
        setContentView(contentView, new ViewGroup.LayoutParams(dialog_width, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTitleView = (TextView) contentView.findViewById(R.id.dialog_general_positive_negative_title);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setPositiveAndNegativeListener(OnPositiveAndNegativeListener listener) {
        contentView.findViewById(R.id.dialog_general_positive_negative_btn_negative).setOnClickListener(listener);
        contentView.findViewById(R.id.dialog_general_positive_negative_btn_positive).setOnClickListener(listener);
    }

    public abstract static class OnPositiveAndNegativeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.dialog_general_positive_negative_btn_negative) {
                onNegativeButtonClicked();

            } else if (i == R.id.dialog_general_positive_negative_btn_positive) {
                onPositiveButtonClicked();
            }
        }

        public abstract void onPositiveButtonClicked();

        public abstract void onNegativeButtonClicked();
    }

}

package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.injector.component.DaggerReminderComponent;
import com.zack.enderplan.injector.module.ReminderPresenterModule;
import com.zack.enderplan.view.dialog.DateTimePickerDialogFragment;
import com.zack.enderplan.presenter.ReminderPresenter;
import com.zack.enderplan.view.contract.ReminderViewContract;
import com.zack.enderplan.common.Constant;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderActivity extends BaseActivity implements ReminderViewContract {

    @BindView(R.id.text_content)
    TextView mContentText;
    @BindView(R.id.switcher_delay)
    ViewAnimator mDelaySwitcher;

    @Inject
    ReminderPresenter mReminderPresenter;

    public static void start(Context context, int planListPosition) {
        context.startActivity(
                new Intent(context, ReminderActivity.class)
                        .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReminderPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerReminderComponent.builder()
                .reminderPresenterModule(new ReminderPresenterModule(this, getIntent().getIntExtra(Constant.PLAN_LIST_POSITION, -1)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReminderPresenter.detach();
    }

    @Override
    public void showInitialView(String content) {
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        mContentText.setText(content);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enterPlanDetail(int position) {
        //可能会有两个PlanDetailActivity同时存在
        PlanDetailActivity.start(this, position, false);
    }

    @OnClick({R.id.btn_delay, R.id.btn_detail, R.id.btn_complete, R.id.btn_back, R.id.btn_1_hour, R.id.btn_tomorrow, R.id.btn_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delay:
                mDelaySwitcher.showNext();
                break;
            case R.id.btn_detail:
                mReminderPresenter.notifyEnteringPlanDetail();
                break;
            case R.id.btn_complete:
                mReminderPresenter.notifyPlanCompleted();
                break;
            case R.id.btn_back:
                mDelaySwitcher.showPrevious();
                break;
            case R.id.btn_1_hour:
                mReminderPresenter.notifyDelayingReminder(Constant.ONE_HOUR);
                break;
            case R.id.btn_tomorrow:
                mReminderPresenter.notifyDelayingReminder(Constant.TOMORROW);
                break;
            case R.id.btn_more:
                DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(Util.getDateTimePickerDefaultTime(Constant.UNDEFINED_TIME));
                fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
                    @Override
                    public void onDateTimePicked(long timeInMillis) {
                        mReminderPresenter.notifyUpdatingReminderTime(timeInMillis);
                    }
                });
                fragment.show(getSupportFragmentManager(), Constant.REMINDER_TIME);
                break;
        }
    }

    @Override
    public void showToast(@StringRes int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exit() {
        finish();
    }
}

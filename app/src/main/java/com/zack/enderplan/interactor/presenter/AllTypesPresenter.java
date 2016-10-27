package com.zack.enderplan.interactor.presenter;

import android.view.View;

import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.interactor.adapter.TypeAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.AllTypesView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AllTypesPresenter extends BasePresenter implements Presenter<AllTypesView> {

    private AllTypesView mAllTypesView;
    private DataManager mDataManager;
    private TypeAdapter mTypeAdapter;
    private EventBus mEventBus;

    public AllTypesPresenter(AllTypesView allTypesView) {
        mEventBus = EventBus.getDefault();
        attachView(allTypesView);
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void attachView(AllTypesView view) {
        mAllTypesView = view;
        mEventBus.register(this);
    }

    @Override
    public void detachView() {
        mAllTypesView = null;
        mEventBus.unregister(this);
    }

    public void setInitialView() {
        mTypeAdapter = new TypeAdapter(mDataManager.getTypeList(), mDataManager.getUcPlanCountOfEachTypeMap());
        mAllTypesView.showInitialView(mTypeAdapter);
    }

    //储存类型列表的排序
    public void syncWithDatabase() {
        mDataManager.notifyTypeSequenceRearranged();
    }

    public void notifyTypeItemClicked(int position, View typeItem) {
        mAllTypesView.onTypeItemClicked(position, typeItem);
    }

    public void notifyTypeSequenceChanged(int fromPosition, int toPosition) {
        mDataManager.swapTypesInTypeList(fromPosition, toPosition);
        mTypeAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mTypeAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        mTypeAdapter.notifyItemInserted(mDataManager.getTypeCount() - 1);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        Plan newPlan = mDataManager.getPlan(event.getPosition());
        if (!newPlan.isCompleted()) {
            //新计划是一个未完成的计划
            mTypeAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(newPlan.getTypeCode()));
        }
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        mTypeAdapter.notifyItemChanged(event.getPosition());
    }

    @Subscribe
    public void onTypeDeleted(TypeDeletedEvent event) {
        mTypeAdapter.notifyItemRemoved(event.getPosition());
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS || event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN) {
            //类型或完成情况改变后的刷新（其他改变未在此界面上呈现）
            //因为可能有多个item需要刷新，比较麻烦，所以直接全部刷新了
            mTypeAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        Plan deletedPlan = event.getDeletedPlan();
        if (!deletedPlan.isCompleted()) {
            //删除的计划是一个未完成的计划
            mTypeAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(deletedPlan.getTypeCode()));
        }
    }
}

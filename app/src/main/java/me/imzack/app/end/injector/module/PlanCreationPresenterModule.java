package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.PlanCreationViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class PlanCreationPresenterModule {

    private final PlanCreationViewContract mPlanCreationViewContract;

    public PlanCreationPresenterModule(PlanCreationViewContract planCreationViewContract) {
        mPlanCreationViewContract = planCreationViewContract;
    }

    @Provides
    PlanCreationViewContract provideCreatePlanViewContract() {
        return mPlanCreationViewContract;
    }
}

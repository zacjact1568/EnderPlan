package me.imzack.app.end.injector.component

import dagger.Component
import me.imzack.app.end.injector.module.ReminderPresenterModule
import me.imzack.app.end.injector.scope.ActivityScope
import me.imzack.app.end.view.activity.ReminderActivity

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ReminderPresenterModule::class))
interface ReminderComponent {

    fun inject(reminderActivity: ReminderActivity)
}

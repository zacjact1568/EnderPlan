package me.imzack.app.end.injector.component

import android.content.Context
import dagger.Component
import me.imzack.app.end.injector.module.AppModule
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    val context: Context

    val eventBus: EventBus
}

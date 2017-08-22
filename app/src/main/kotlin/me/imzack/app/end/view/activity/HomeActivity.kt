package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.injector.component.DaggerHomeComponent
import me.imzack.app.end.injector.module.HomePresenterModule
import me.imzack.app.end.presenter.HomePresenter
import me.imzack.app.end.util.LogUtil
import me.imzack.app.end.view.contract.HomeViewContract
import me.imzack.app.end.view.fragment.AllTypesFragment
import me.imzack.app.end.view.fragment.MyPlansFragment
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.fab_create)
    lateinit var mCreateFab: FloatingActionButton
    @BindView(R.id.navigator)
    lateinit var mNavigator: NavigationView
    @BindView(R.id.layout_drawer)
    lateinit var mDrawerLayout: DrawerLayout

    @Inject
    lateinit var mHomePresenter: HomePresenter

    private lateinit var mPlanCountText: TextView
    private lateinit var mPlanCountDscptText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomePresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerHomeComponent.builder()
                .homePresenterModule(HomePresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHomePresenter.notifyStartingUpCompleted()
        //如果放到setInitialView中，toolbar的title不会改变
        showFragment(if (savedInstanceState == null) Constant.MY_PLANS else savedInstanceState.getString(Constant.CURRENT_FRAGMENT, Constant.MY_PLANS))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.CURRENT_FRAGMENT, if (isFragmentShowing(Constant.MY_PLANS)) Constant.MY_PLANS else Constant.ALL_TYPES)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHomePresenter.detach()
    }

    override fun onBackPressed() {
        mHomePresenter.notifyBackPressed(
                mDrawerLayout.isDrawerOpen(GravityCompat.START),
                isFragmentShowing(Constant.MY_PLANS)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu.findItem(R.id.action_search).icon.setTint(Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> enterActivity(if (isFragmentShowing(Constant.MY_PLANS)) Constant.PLAN_SEARCH else Constant.TYPE_SEARCH)
        }
        return super.onOptionsItemSelected(item)
    }

    // 参数data必须声明成可选类型，否则崩溃，因为若没有数据返回，data为null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            //正常结束引导
            RESULT_OK -> { }
            //中途退出引导
            RESULT_CANCELED -> exit()
        }
    }

    override fun showInitialView(planCount: String, textSize: Int, planCountDscpt: String) {
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)

        val navHeader = mNavigator.getHeaderView(0)
        mPlanCountText = ButterKnife.findById(navHeader, R.id.text_plan_count)
        mPlanCountDscptText = ButterKnife.findById(navHeader, R.id.text_plan_count_dscpt)

        setSupportActionBar(mToolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mNavigator.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_my_plans -> showFragment(Constant.MY_PLANS)
                R.id.nav_all_types -> showFragment(Constant.ALL_TYPES)
                R.id.nav_settings -> enterActivity(Constant.SETTINGS)
                R.id.nav_about -> enterActivity(Constant.ABOUT)
                else -> { }
            }
            mDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        changeDrawerHeaderDisplay(planCount, textSize, planCountDscpt)
    }

    override fun changePlanCount(planCount: String, textSize: Int) {
        mPlanCountText.text = planCount
        mPlanCountText.textSize = textSize.toFloat()
    }

    override fun changeDrawerHeaderDisplay(planCount: String, textSize: Int, planCountDscpt: String) {
        changePlanCount(planCount, textSize)
        mPlanCountDscptText.text = planCountDscpt
    }

    override fun closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun showFragment(tag: String) {
        if (!isFragmentShowing(tag)) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.layout_fragment,
                            when (tag) {
                                Constant.MY_PLANS -> MyPlansFragment()
                                Constant.ALL_TYPES -> AllTypesFragment()
                                else -> throw IllegalArgumentException("The argument tag cannot be " + tag)
                            },
                            tag
                    )
                    .commit()
        }
        //在切换相同fragment时，下面的语句是不必要的
        val titleResId: Int
        val navViewCheckedItemId: Int
        when (tag) {
            Constant.MY_PLANS -> {
                titleResId = R.string.title_fragment_my_plans
                navViewCheckedItemId = R.id.nav_my_plans
            }
            Constant.ALL_TYPES -> {
                titleResId = R.string.title_fragment_all_types
                navViewCheckedItemId = R.id.nav_all_types
            }
            else -> throw IllegalArgumentException("The argument tag cannot be " + tag)
        }
        mToolbar.setTitle(titleResId)
        mCreateFab.translationY = 0f
        mNavigator.setCheckedItem(navViewCheckedItemId)
    }

    override fun onPressBackKey() {
        super.onBackPressed()
    }

    override fun enterActivity(tag: String) {
        when (tag) {
            Constant.GUIDE -> GuideActivity.start(this)
            Constant.PLAN_SEARCH -> PlanSearchActivity.start(this)
            Constant.TYPE_SEARCH -> TypeSearchActivity.start(this)
            Constant.SETTINGS -> SettingsActivity.start(this)
            Constant.ABOUT -> AboutActivity.start(this)
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @OnClick(R.id.fab_create)
    fun onClick(view: View) {
        when (view.id) {
            R.id.fab_create -> if (isFragmentShowing(Constant.MY_PLANS)) PlanCreationActivity.start(this) else TypeCreationActivity.start(this)
        }
    }

    private fun isFragmentShowing(tag: String) = supportFragmentManager.findFragmentByTag(tag) != null
}

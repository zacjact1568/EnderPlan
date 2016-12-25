package com.zack.enderplan.view.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerTypeEditComponent;
import com.zack.enderplan.injector.module.TypeEditPresenterModule;
import com.zack.enderplan.view.dialog.TypeMarkColorPickerDialogFragment;
import com.zack.enderplan.view.dialog.EditorDialogFragment;
import com.zack.enderplan.view.dialog.TypeMarkPatternPickerDialogFragment;
import com.zack.enderplan.view.contract.EditTypeViewContract;
import com.zack.enderplan.presenter.EditTypePresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.widget.CircleColorView;
import com.zack.enderplan.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditTypeActivity extends BaseActivity implements EditTypeViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.item_type_name)
    ItemView mTypeNameItem;
    @BindView(R.id.item_type_mark_color)
    ItemView mTypeMarkColorItem;
    @BindView(R.id.item_type_mark_pattern)
    ItemView mTypeMarkPatternItem;

    @BindString(R.string.dscpt_unsettled)
    String mUnsettledDscpt;

    @Inject
    EditTypePresenter mEditTypePresenter;

    public static void start(Activity activity, int typeListPosition, boolean sharedElementTransition, View sharedElement, String sharedElementName) {
        Intent intent = new Intent(activity, EditTypeActivity.class);
        intent.putExtra(Constant.TYPE_LIST_POSITION, typeListPosition);
        ActivityOptions options;
        if (sharedElementTransition) {
            options = ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElementName);
        } else {
            options = ActivityOptions.makeSceneTransitionAnimation(activity);
        }
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditTypePresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeEditComponent.builder()
                .typeEditPresenterModule(new TypeEditPresenterModule(this, getIntent().getIntExtra(Constant.TYPE_LIST_POSITION, -1)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditTypePresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showInitialView(FormattedType formattedType) {
        setContentView(R.layout.activity_edit_type);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        onTypeNameChanged(formattedType.getTypeName(), formattedType.getFirstChar());
        onTypeMarkColorChanged(formattedType.getTypeMarkColorInt(), formattedType.getTypeMarkColorName());
        onTypeMarkPatternChanged(formattedType.isHasTypeMarkPattern(), formattedType.getTypeMarkPatternResId(), formattedType.getTypeMarkPatternName());
    }

    @Override
    public void showTypeNameEditorDialog(String originalEditorText) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(App.getContext().getString(R.string.title_dialog_type_name_editor), originalEditorText);
        fragment.setOnPositiveButtonClickListener(new EditorDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(String editorText) {
                mEditTypePresenter.notifyUpdatingTypeName(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_NAME);
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar) {
        mTypeMarkIcon.setInnerText(firstChar);
        mTypeNameText.setText(typeName);
        mTypeNameItem.setDescriptionText(typeName);
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        mTypeMarkIcon.setFillColor(colorInt);
        mTypeMarkColorItem.setDescriptionText(colorName);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId, String patternName) {
        mTypeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
        mTypeMarkPatternItem.setDescriptionText(hasPattern ? patternName : mUnsettledDscpt);
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnTypeMarkColorPickedListener(new TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener() {
            @Override
            public void onTypeMarkColorPicked(TypeMarkColor typeMarkColor) {
                mEditTypePresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_MARK_COLOR);
    }

    @Override
    public void showTypeMarkPatternPickerDialog(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment fragment = TypeMarkPatternPickerDialogFragment.newInstance(defaultPattern);
        fragment.setOnTypeMarkPatternPickedListener(new TypeMarkPatternPickerDialogFragment.OnTypeMarkPatternPickedListener() {
            @Override
            public void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern) {
                mEditTypePresenter.notifyTypeMarkPatternSelected(typeMarkPattern);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_MARK_PATTERN);
    }

    @OnClick({R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type_name:
                mEditTypePresenter.notifySettingTypeName();
                break;
            case R.id.item_type_mark_color:
                mEditTypePresenter.notifySettingTypeMarkColor();
                break;
            case R.id.item_type_mark_pattern:
                mEditTypePresenter.notifySettingTypeMarkPattern();
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

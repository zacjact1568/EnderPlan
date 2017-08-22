package me.imzack.app.end.view.contract

import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.view.adapter.TypeGalleryAdapter

interface PlanCreationViewContract : BaseViewContract {

    fun showInitialView(typeGalleryAdapter: TypeGalleryAdapter, formattedType: FormattedType)

    fun onContentChanged(isValid: Boolean)

    fun onStarStatusChanged(isStarred: Boolean)

    fun onTypeOfPlanChanged(formattedType: FormattedType)

    fun onTypeCreationItemClicked()

    fun showDeadlinePickerDialog(defaultDeadline: Long)

    fun onDeadlineChanged(deadline: CharSequence)

    fun showReminderTimePickerDialog(defaultReminderTime: Long)

    fun onReminderTimeChanged(reminderTime: CharSequence)
}

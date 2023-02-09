package com.houvven.guise.ui.routing

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import com.houvven.guise.db.Template
import com.houvven.guise.db.TemplateDBHelper
import com.houvven.guise.module.apps.AppInfo
import com.houvven.guise.module.apps.AppInfoProvider

@SuppressLint("MutableCollectionMutableState")
object LauncherState {

    private val templateDao = TemplateDBHelper.templateDao

    val apps = mutableStateOf<List<AppInfo>>(emptyList())

    val templates by lazy { mutableStateOf(templateDao.getAll()) }

    fun refreshApps() {
        apps.value = AppInfoProvider.getList()
    }

    private fun refreshTemplates() {
        templates.value = templateDao.getAll()
    }

    fun addTemplate(template: Template) {
        templateDao.insert(template)
        refreshTemplates()
    }

    fun addTemplates(templates: List<Template>) {
        templateDao.insertMany(templates)
        refreshTemplates()
    }

    fun deleteTemplate(template: Template) {
        templateDao.delete(template)
        refreshTemplates()
    }

    fun updateTemplate(template: Template) {
        templateDao.update(template)
        refreshTemplates()
    }


}
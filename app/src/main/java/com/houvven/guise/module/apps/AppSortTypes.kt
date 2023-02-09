package com.houvven.guise.module.apps

import com.houvven.guise.ContextAmbient
import com.houvven.guise.R

enum class AppSortTypes(val depict: String) {
    NAME(ContextAmbient.current.getString(R.string.apps_order_by_name)),
    PACKAGE_NAME(ContextAmbient.current.getString(R.string.apps_order_by_package_name)),
    INSTALL_TIME(ContextAmbient.current.getString(R.string.apps_order_by_install_time)),
    UPDATE_TIME(ContextAmbient.current.getString(R.string.apps_order_by_update_time))
}
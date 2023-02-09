package com.houvven.guise.ui.routing

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.houvven.guise.db.Template
import com.houvven.guise.ui.routing.editor.AddTemplateScreen
import com.houvven.guise.ui.routing.editor.DeployConfigEditScreen
import com.houvven.guise.ui.routing.editor.EditTemplateScreen
import com.houvven.guise.ui.routing.launcher.LauncherRoute
import com.houvven.guise.ui.routing.template.EnableTemplateScreen

@SuppressLint("StaticFieldLeak")
object LocalNavController {
    lateinit var current: NavHostController
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationRoute() {
    val navController = rememberAnimatedNavController()
    LocalNavController.current = navController

    AnimatedNavHost(navController, NavRoutingTypes.LAUNCHER.name) {
        composable(NavRoutingTypes.LAUNCHER.name) { LauncherRoute() }

        composable(
            route = "${NavRoutingTypes.DEPLOY_CONFIG_EDITOR.name}/{name}/{packageName}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments!!.getString("name")!!
            val packageName = backStackEntry.arguments!!.getString("packageName")!!
            DeployConfigEditScreen(name, packageName)
        }

        composable(NavRoutingTypes.ADD_TEMPLATE.name) { AddTemplateScreen() }

        composable(NavRoutingTypes.EDIT_TEMPLATE.name) {
            val template = it.arguments?.get("template") as Template
            EditTemplateScreen(template)
        }

        composable(NavRoutingTypes.ENABLE_TEMPLATE.name) {
            val template = it.arguments?.get("template") as Template
            EnableTemplateScreen(template)
        }
    }
}


fun NavHostController.navigateAndArgument(
    route: String,
    args: List<Pair<String, Any>>? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    navigate(route = route, navOptions = navOptions, navigatorExtras = navigatorExtras)

    if (args.isNullOrEmpty()) return

    val bundle = backQueue.lastOrNull()?.arguments
    bundle?.putAll(bundleOf(*args.toTypedArray()))
}
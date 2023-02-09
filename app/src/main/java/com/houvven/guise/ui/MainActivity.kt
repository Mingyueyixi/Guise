package com.houvven.guise.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.houvven.guise.ui.routing.NavigationRoute
import com.houvven.guise.ui.theme.GuiseTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuiseTheme {
                Scaffold(snackbarHost = {
                    SnackbarHost(
                        hostState = GlobalSnackbarHost.state,
                        modifier = Modifier.padding(bottom = 80.dp),
                    ) { data ->
                        val containerStateColor =
                            if (GlobalSnackbarHost.onError.value) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        Snackbar(
                            snackbarData = data,
                            containerColor = containerStateColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(
                            top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()
                        )
                    ) {
                        NavigationRoute()
                    }
                }
            }
        }
    }
}
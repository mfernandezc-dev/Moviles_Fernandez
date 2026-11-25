
package com.fernandez.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fernandez.avisoscampus.data.AvisosRepositoryMemoria
import com.fernandez.avisoscampus.ui.PantallaAvisos
import com.fernandez.avisoscampus.ui.theme.AvisosCampusTheme
import com.fernandez.avisoscampus.viewmodel.AvisosViewModel
import com.fernandez.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AvisosCampusTheme {
                AvisosCampusContenido()
            }
        }
    }
}

@Composable
private fun AvisosCampusContenido() {

    val repo = remember {
        AvisosRepositoryMemoria()
    }

    val factory = remember(repo) {
        AvisosViewModelFactory(repo)
    }

    val vm: AvisosViewModel = viewModel(
        factory = factory
    )

    PantallaAvisos(vm)
}

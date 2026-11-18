package com.fernandez.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fernandez.campusgps.ui.PantallaMapa
import com.fernandez.campusgps.ui.theme.CampusGPSTheme
import com.fernandez.campusgps.viewmodel.CampusViewModelFactory
import com.fernandez.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            CampusGPSTheme {

                val contexto = LocalContext.current

                val factory = remember(contexto) {
                    CampusViewModelFactory(contexto)
                }

                val mapaViewModel: MapaViewModel = viewModel(
                    factory = factory
                )

                PantallaMapa(
                    vm = mapaViewModel
                )
            }
        }
    }
}
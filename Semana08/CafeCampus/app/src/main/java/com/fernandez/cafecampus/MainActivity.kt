package com.fernandez.cafecampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.fernandez.cafecampus.data.prefs.PreferenciasStore
import com.fernandez.cafecampus.data.repository.CafeRepositoryRoom
import com.fernandez.cafecampus.ui.navigation.NavCafe
import com.fernandez.cafecampus.ui.theme.CafeCampusTheme
import com.fernandez.cafecampus.viewmodel.CafeViewModelFactory
import com.fernandez.cafecampus.viewmodel.SesionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels {
        val app = application as CafeApp

        CafeViewModelFactory(
            repository = CafeRepositoryRoom(app.database),
            preferencias = PreferenciasStore(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val perfil by sesionViewModel.perfilActivo.collectAsState()
            val modoOscuro by sesionViewModel.modoOscuro.collectAsState()

            CafeCampusTheme(
                rol = perfil ?: "CLIENTE",
                darkTheme = modoOscuro
            ) {
                NavCafe(sesionViewModel)
            }
        }
    }
}
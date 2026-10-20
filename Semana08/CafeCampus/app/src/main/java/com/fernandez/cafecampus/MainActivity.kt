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
import com.fernandez.cafecampus.viewmodel.MenuViewModel
import com.fernandez.cafecampus.viewmodel.PedidosClienteViewModel
import com.fernandez.cafecampus.viewmodel.PanelAdminViewModel
import com.fernandez.cafecampus.viewmodel.PedidosAdminViewModel
import com.fernandez.cafecampus.ui.admin.PanelScreen
import com.fernandez.cafecampus.ui.admin.PedidosAdminScreen
import com.fernandez.cafecampus.ui.components.AdminBottomBar
import com.fernandez.cafecampus.model.EstadoPedido

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels {
        val app = application as CafeApp

        CafeViewModelFactory(
            repository = CafeRepositoryRoom(app.database),
            preferencias = PreferenciasStore(applicationContext)
        )
    }

    private val menuViewModel: MenuViewModel by viewModels {
        val app = application as CafeApp

        CafeViewModelFactory(
            repository = CafeRepositoryRoom(app.database),
            preferencias = PreferenciasStore(applicationContext)
        )
    }

    private val pedidosClienteViewModel: PedidosClienteViewModel by viewModels {
        val app = application as CafeApp

        CafeViewModelFactory(
            repository = CafeRepositoryRoom(app.database),
            preferencias = PreferenciasStore(applicationContext)
        )
    }


    private val panelAdminViewModel: PanelAdminViewModel by viewModels {
        val app = application as CafeApp

        CafeViewModelFactory(
            repository = CafeRepositoryRoom(app.database),
            preferencias = PreferenciasStore(applicationContext)
        )
    }

    private val pedidosAdminViewModel: PedidosAdminViewModel by viewModels {
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
                NavCafe(
                    vm = sesionViewModel,
                    menuVm = menuViewModel,
                    pedidosVm = pedidosClienteViewModel,
                    panelAdminVm = panelAdminViewModel,
                    pedidosAdminVm = pedidosAdminViewModel
                )
            }
        }
    }
}
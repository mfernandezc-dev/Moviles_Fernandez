package com.fernandez.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.UiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fernandez.murocampus.data.PostRepositoryRemote
import com.fernandez.murocampus.data.remote.RetrofitClient
import com.fernandez.murocampus.ui.PantallaLista
import com.fernandez.murocampus.ui.PantallaDetalle
import com.fernandez.murocampus.ui.PantallaCrear
import com.fernandez.murocampus.ui.theme.MuroCampusTheme
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.PostsViewModelFactory
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            )
        )
        val repository = PostRepositoryRemote(RetrofitClient.api)
        val factory = PostsViewModelFactory(repository)
        val vm = ViewModelProvider(this, factory)[PostsViewModel::class.java]

        setContent {
            MuroCampusTheme {
                PantallaInicio(vm)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun PantallaInicio(vm: PostsViewModel) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val eliminacion by vm.eliminacion.collectAsStateWithLifecycle()
    LaunchedEffect(eliminacion) {
        if (eliminacion is UiState.Exito && idSeleccionado != null) {
            vm.cerrarDetalle()
            idSeleccionado = null
        }
    }
    BackHandler(enabled = idSeleccionado != null || creando) {
        if (creando) {
            vm.cerrarCreacion()
            creando = false
        } else {
            vm.cerrarDetalle()
            idSeleccionado = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (idSeleccionado == null && !creando) {
                FloatingActionButton(
                    onClick = {
                        vm.prepararCreacion()
                        creando = true
                    },
                    modifier = Modifier.semantics { contentDescription = "Crear publicación" },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text("+", fontSize = 32.sp, modifier = Modifier.clearAndSetSemantics {})
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        val contenido = Modifier.fillMaxSize().padding(innerPadding)
        val id = idSeleccionado
        if (creando) {
            PantallaCrear(vm = vm, modifier = contenido)
        } else if (id == null) {
            PantallaLista(
                vm = vm,
                modifier = contenido,
                listState = listState,
                onPostClick = { seleccionado ->
                    vm.abrir(seleccionado)
                    idSeleccionado = seleccionado
                }
            )
        } else {
            PantallaDetalle(vm = vm, idSeleccionado = id, modifier = contenido)
        }
    }
}

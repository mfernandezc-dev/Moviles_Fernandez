package com.fernandez.catalogo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.catalogo.ui.lista.ListaViewModel
import com.fernandez.catalogo.ui.navigation.AppNav
import com.fernandez.catalogo.ui.theme.CatalogoTheme

class MainActivity : ComponentActivity() {
    private val listaViewModel: ListaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(ListaViewModel::class.java)) {
                    "ViewModel no compatible: ${modelClass.name}"
                }
                @Suppress("UNCHECKED_CAST")
                return ListaViewModel(
                    AppContainer.itemRepository,
                    AppContainer.notificador
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContainer.inicializar(applicationContext)
        setContent {
            CatalogoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNav(
                        listaViewModel = listaViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatalogoTheme {
        Greeting("Android")
    }
}

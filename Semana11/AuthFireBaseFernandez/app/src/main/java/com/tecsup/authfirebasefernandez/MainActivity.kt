
package com.tecsup.authfirebasefernandez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.tecsup.authfirebasefernandez.ui.theme.AuthFirebaseFernandezTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Mostrar la aplicación
        setContent {
            AuthFirebaseFernandezTheme {
                AuthApp()
            }
        }
    }
}

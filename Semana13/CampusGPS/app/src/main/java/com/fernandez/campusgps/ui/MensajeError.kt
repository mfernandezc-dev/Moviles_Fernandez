package com.fernandez.campusgps.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.fernandez.campusgps.model.TipoError
import com.fernandez.campusgps.model.UbicacionUi
import com.fernandez.campusgps.ui.theme.Blanco

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit
) {

    val contexto = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer
        ) {

            Text(
                text = error.mensaje,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        when (error.tipo) {

            TipoError.PERMISO_DENEGADO -> {

                OutlinedButton(
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Blanco,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {

                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                contexto.packageName,
                                null
                            )
                        )

                        contexto.startActivity(intent)
                    }
                ) {
                    Text("Abrir ajustes")
                }
            }

            TipoError.GPS_APAGADO,
            TipoError.SIN_PLAY_SERVICES,
            TipoError.DESCONOCIDO -> {

                OutlinedButton(
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Blanco,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onReintentar
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

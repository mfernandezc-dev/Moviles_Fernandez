package com.fernandez.campusgps.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.fernandez.campusgps.model.LugarConDistancia
import com.fernandez.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(
            items = lugares,
            key = { it.lugar.id }
        ) { item ->

            val estaSeleccionado =
                item.lugar.id == seleccionado

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(item.lugar.id)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (estaSeleccionado) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = item.lugar.nombre,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = item.lugar.categoria,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = item.metros?.let {
                            formatearDistancia(it)
                        } ?: "Sin calcular",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
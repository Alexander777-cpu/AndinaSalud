package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    viewModel: SolicitudViewModel,
    onCitaCreada: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            viewModel.reiniciarEstadoGuardado()
            onCitaCreada()
        }
    }

    if (state.isLoadingCatalogos) {
        LoadingView(modifier = modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Solicitar Cita Médica",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Complete el formulario para programar su atención",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        state.errores.general?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        CampoSelector(
            label = "Especialidad",
            opciones = state.especialidadesDisponibles,
            seleccionado = state.especialidadSeleccionada,
            onSeleccionar = { viewModel.onEspecialidadCambiada(it) },
            error = state.errores.especialidad
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSelector(
            label = "Sede",
            opciones = state.sedesDisponibles,
            seleccionado = state.sedeSeleccionada,
            onSeleccionar = { viewModel.onSedeCambiada(it) },
            error = state.errores.sede
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoSelector(
            label = "Médico Asignado",
            opciones = state.medicosFiltrados.map { it.nombre },
            seleccionado = state.medicoSeleccionado,
            onSeleccionar = { viewModel.onMedicoCambiado(it) },
            error = state.errores.medico
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.fecha,
                onValueChange = { viewModel.onFechaCambiada(it) },
                label = { Text("Fecha (AAAA-MM-DD)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = state.errores.fecha != null,
                supportingText = state.errores.fecha?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = state.hora,
                onValueChange = { viewModel.onHoraCambiada(it) },
                label = { Text("Hora (HH:mm)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = state.errores.hora != null,
                supportingText = state.errores.hora?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.motivo,
            onValueChange = { viewModel.onMotivoCambiado(it) },
            label = { Text("Motivo de consulta") },
            placeholder = { Text("Describa sus síntomas o consulta (10-200 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 4,
            isError = state.errores.motivo != null,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.errores.motivo ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "${state.motivo.length}/200",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.guardarCita() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !state.isGuardando
        ) {
            if (state.isGuardando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.width(24.dp).height(24.dp)
                )
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Cita")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoSelector(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccionar: (String) -> Unit,
    error: String?
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido }
    ) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            isError = error != null,
            supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
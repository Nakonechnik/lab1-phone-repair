package com.example.phonerepair.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phonerepair.model.CatalogDevice
import com.example.phonerepair.ui.list.CatalogUiState
import com.example.phonerepair.ui.theme.TextSecondary

@Composable
fun CreateTicketDialog(
    catalog: CatalogUiState,
    onRetryCatalog: () -> Unit,
    onConfirm: (deviceName: String, catalogId: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var manualName by remember { mutableStateOf("") }
    val canCreateManual = manualName.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая заявка") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (catalog) {
                    CatalogUiState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Загружаем каталог поставщика...")
                        }
                    }
                    is CatalogUiState.Error -> {
                        Text(
                            text = if (catalog.canRetry) {
                                "Каталог недоступен. Можно ввести название вручную или повторить загрузку."
                            } else {
                                "Не удалось прочитать каталог. Введите название вручную."
                            }
                        )
                        if (catalog.canRetry) {
                            TextButton(onClick = onRetryCatalog) {
                                Text("Повторить загрузку каталога")
                            }
                        }
                        ManualNameField(
                            value = manualName,
                            onValueChange = { manualName = it }
                        )
                    }
                    is CatalogUiState.Ready -> {
                        Text(
                            text = "Выберите устройство из каталога или введите своё название",
                            color = TextSecondary
                        )
                        CatalogDeviceList(
                            devices = catalog.devices,
                            onSelect = { device ->
                                onConfirm(device.name, device.id)
                            }
                        )
                        HorizontalDivider()
                        ManualNameField(
                            value = manualName,
                            onValueChange = { manualName = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(manualName.trim(), null) },
                enabled = canCreateManual && catalog !is CatalogUiState.Loading
            ) {
                Text("Создать вручную")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ManualNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Название устройства") }
    )
}

@Composable
private fun CatalogDeviceList(
    devices: List<CatalogDevice>,
    onSelect: (CatalogDevice) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
    ) {
        items(devices, key = { it.id }) { device ->
            Text(
                text = device.name,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(device) }
                    .padding(vertical = 10.dp)
            )
            HorizontalDivider()
        }
    }
}

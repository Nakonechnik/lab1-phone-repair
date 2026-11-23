package com.example.phonerepair.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.model.CatalogDevice
import com.example.phonerepair.model.DeviceLoadException
import com.example.phonerepair.model.RepairTicket
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Ready(val devices: List<CatalogDevice>) : CatalogUiState
    data class Error(val canRetry: Boolean) : CatalogUiState
}

data class TicketListUiState(
    val tickets: List<RepairTicket> = emptyList(),
    val catalog: CatalogUiState = CatalogUiState.Loading
)

class TicketListViewModel(
    private val repo: RepairRepository
) : ViewModel() {
    private var catalog by mutableStateOf<CatalogUiState>(CatalogUiState.Loading)
    private var loadJob: Job? = null

    val uiState: TicketListUiState
        get() = TicketListUiState(
            tickets = repo.tickets,
            catalog = catalog
        )

    init {
        loadCatalog()
    }

    fun onRetryCatalog() = loadCatalog()

    fun onAdd(deviceName: String, catalogId: String? = null) {
        repo.addTicket(deviceName = deviceName, catalogId = catalogId)
    }

    fun onDelete(id: String) = repo.removeTicket(id)

    private fun loadCatalog() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            catalog = CatalogUiState.Loading
            catalog = try {
                CatalogUiState.Ready(repo.loadCatalog())
            } catch (e: DeviceLoadException) {
                CatalogUiState.Error(canRetry = e.canRetry)
            }
        }
    }
}

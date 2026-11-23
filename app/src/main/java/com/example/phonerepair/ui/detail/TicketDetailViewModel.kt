package com.example.phonerepair.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.model.DeviceDetails
import com.example.phonerepair.model.DeviceLoadException
import com.example.phonerepair.model.DeviceNotFoundException
import com.example.phonerepair.model.RepairTicket
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface SupplierInfoUiState {
    data object Loading : SupplierInfoUiState
    data class Content(val details: DeviceDetails) : SupplierInfoUiState
    data object MissingLink : SupplierInfoUiState
    data object NotFound : SupplierInfoUiState
    data class Error(val canRetry: Boolean) : SupplierInfoUiState
}

data class TicketDetailUiState(
    val ticket: RepairTicket? = null,
    val supplierInfo: SupplierInfoUiState = SupplierInfoUiState.Loading
)

class TicketDetailViewModel(
    private val ticketId: String,
    private val repo: RepairRepository
) : ViewModel() {
    private var supplierInfo by mutableStateOf<SupplierInfoUiState>(SupplierInfoUiState.Loading)
    private var loadJob: Job? = null

    val uiState: TicketDetailUiState
        get() = TicketDetailUiState(
            ticket = repo.ticket(ticketId),
            supplierInfo = supplierInfo
        )

    init {
        loadSupplierInfo()
    }

    fun onRetrySupplierInfo() = loadSupplierInfo()

    fun startDiagnostics() = repo.startDiagnostics(ticketId)

    fun finishDiagnostics() = repo.finishDiagnostics(ticketId)

    fun markPartsArrived() = repo.markPartsArrived(ticketId)

    fun approveByClient() = repo.approveByClient(ticketId)

    fun pay() = repo.pay(ticketId)

    fun toggleWarranty() = repo.toggleWarranty(ticketId)

    fun increaseSeverity() = repo.increaseSeverity(ticketId)

    fun decreaseSeverity() = repo.decreaseSeverity(ticketId)

    fun startRepair() = repo.startRepair(ticketId)

    fun completeRepair() = repo.completeRepair(ticketId)

    fun issueToClient() = repo.issueToClient(ticketId)

    fun reject() = repo.reject(ticketId)

    fun reset() = repo.reset(ticketId)

    fun renameDevice(deviceName: String) = repo.renameDevice(ticketId, deviceName)

    private fun loadSupplierInfo() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            val ticket = repo.ticket(ticketId) ?: return@launch
            val catalogId = ticket.catalogId
            if (catalogId == null) {
                supplierInfo = SupplierInfoUiState.MissingLink
                return@launch
            }
            supplierInfo = SupplierInfoUiState.Loading
            supplierInfo = try {
                SupplierInfoUiState.Content(repo.deviceDetails(catalogId))
            } catch (_: DeviceNotFoundException) {
                SupplierInfoUiState.NotFound
            } catch (e: DeviceLoadException) {
                SupplierInfoUiState.Error(canRetry = e.canRetry)
            }
        }
    }
}

package com.example.phonerepair.ui.detail

import androidx.lifecycle.ViewModel
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.model.RepairTicket

data class TicketDetailUiState(
    val ticket: RepairTicket? = null
)

class TicketDetailViewModel(
    private val ticketId: String,
    private val repo: RepairRepository
) : ViewModel() {
    val uiState: TicketDetailUiState
        get() = TicketDetailUiState(ticket = repo.ticket(ticketId))

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
}

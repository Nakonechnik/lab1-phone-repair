package com.example.phonerepair.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.ui.detail.TicketDetailScreen
import com.example.phonerepair.ui.detail.TicketDetailViewModel
import com.example.phonerepair.ui.list.TicketListScreen
import com.example.phonerepair.ui.list.TicketListViewModel
import kotlinx.serialization.Serializable

@Serializable
data object TicketListKey : NavKey

@Serializable
data class TicketDetailKey(val ticketId: String) : NavKey

@Composable
fun AppNavigation(
    repo: RepairRepository,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(TicketListKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<TicketListKey> {
                val viewModel = viewModel { TicketListViewModel(repo) }
                TicketListScreen(
                    state = viewModel.uiState,
                    onOpenTicket = { id ->
                        if (backStack.lastOrNull() == TicketListKey) {
                            backStack.add(TicketDetailKey(id))
                        }
                    },
                    onAdd = viewModel::onAdd,
                    onDelete = viewModel::onDelete
                )
            }
            entry<TicketDetailKey> { key ->
                val viewModel = viewModel { TicketDetailViewModel(key.ticketId, repo) }
                val close: () -> Unit = {
                    if (backStack.lastOrNull() == key) {
                        backStack.removeLastOrNull()
                    }
                }
                TicketDetailScreen(
                    state = viewModel.uiState,
                    onBack = close,
                    onRenameDevice = viewModel::renameDevice,
                    onStartDiagnostics = viewModel::startDiagnostics,
                    onFinishDiagnostics = viewModel::finishDiagnostics,
                    onMarkPartsArrived = viewModel::markPartsArrived,
                    onApproveByClient = viewModel::approveByClient,
                    onPay = viewModel::pay,
                    onToggleWarranty = viewModel::toggleWarranty,
                    onIncreaseSeverity = viewModel::increaseSeverity,
                    onDecreaseSeverity = viewModel::decreaseSeverity,
                    onStartRepair = viewModel::startRepair,
                    onCompleteRepair = viewModel::completeRepair,
                    onIssueToClient = viewModel::issueToClient,
                    onReject = viewModel::reject,
                    onReset = viewModel::reset
                )
            }
        }
    )
}

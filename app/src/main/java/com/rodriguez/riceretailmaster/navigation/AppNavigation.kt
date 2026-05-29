package com.rodriguez.riceretailmaster.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Outbox
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodriguez.riceretailmaster.auth.RecoveryEvents
import com.rodriguez.riceretailmaster.data.model.UserRole
import com.rodriguez.riceretailmaster.data.repository.AuthRepository
import com.rodriguez.riceretailmaster.ui.admin.alerts.LowStockAlertsScreen
import com.rodriguez.riceretailmaster.ui.admin.dashboard.LiveDashboardScreen
import com.rodriguez.riceretailmaster.ui.admin.history.MovementHistoryScreen
import com.rodriguez.riceretailmaster.ui.components.RrmBottomNav
import com.rodriguez.riceretailmaster.ui.components.RrmNavItem
import com.rodriguez.riceretailmaster.ui.login.LoginScreen
import com.rodriguez.riceretailmaster.ui.login.ResetPasswordScreen
import com.rodriguez.riceretailmaster.ui.staff.delivery.DeliveryLogScreen
import com.rodriguez.riceretailmaster.ui.staff.lookup.QuickCheckLookupScreen
import com.rodriguez.riceretailmaster.ui.staff.release.StockReleaseScreen
import com.rodriguez.riceretailmaster.ui.theme.Background
import com.rodriguez.riceretailmaster.ui.theme.Primary
import kotlinx.coroutines.launch

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val STAFF = "staff"
    const val ADMIN = "admin"
    const val RESET_PASSWORD = "reset-password"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val authRepo = remember { AuthRepository() }

    val signOut: () -> Unit = {
        scope.launch { authRepo.signOut() }
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        RecoveryEvents.events.collect {
            navController.navigate(Routes.RESET_PASSWORD) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                authRepo = authRepo,
                onResolved = { role ->
                    val destination = when (role) {
                        UserRole.OWNER -> Routes.ADMIN
                        UserRole.STAFF -> Routes.STAFF
                        null -> Routes.LOGIN
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = { role ->
                val destination = if (role == UserRole.OWNER) Routes.ADMIN else Routes.STAFF
                navController.navigate(destination) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(Routes.STAFF) { StaffScaffold(onSignOut = signOut) }
        composable(Routes.ADMIN) { AdminScaffold(onSignOut = signOut) }
        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(onDone = {
                scope.launch { authRepo.signOut() }
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
    }
}

@Composable
private fun SplashScreen(
    authRepo: AuthRepository,
    onResolved: (UserRole?) -> Unit,
) {
    LaunchedEffect(Unit) {
        val role = runCatching { authRepo.awaitInitialRole() }.getOrNull()
        onResolved(role)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

private val staffItems = listOf(
    RrmNavItem("release", "Release", Icons.Rounded.Outbox),
    RrmNavItem("delivery", "Delivery", Icons.Rounded.LocalShipping),
    RrmNavItem("lookup", "Lookup", Icons.Rounded.Search),
)

private val adminItems = listOf(
    RrmNavItem("dashboard", "Dashboard", Icons.Rounded.Dashboard),
    RrmNavItem("alerts", "Alerts", Icons.Rounded.Notifications),
    RrmNavItem("history", "History", Icons.Rounded.History),
)

@Composable
private fun StaffScaffold(onSignOut: () -> Unit) {
    RoleScaffold(items = staffItems, startTab = "release") { snackbar ->
        composable("release") { StockReleaseScreen(snackbar, onSignOut = onSignOut) }
        composable("delivery") { DeliveryLogScreen(snackbar, onSignOut = onSignOut) }
        composable("lookup") { QuickCheckLookupScreen(snackbar, onSignOut = onSignOut) }
    }
}

@Composable
private fun AdminScaffold(onSignOut: () -> Unit) {
    RoleScaffold(items = adminItems, startTab = "dashboard") { snackbar ->
        composable("dashboard") { LiveDashboardScreen(snackbar, onSignOut = onSignOut) }
        composable("alerts") { LowStockAlertsScreen(snackbar, onSignOut = onSignOut) }
        composable("history") { MovementHistoryScreen(snackbar, onSignOut = onSignOut) }
    }
}

@Composable
private fun RoleScaffold(
    items: List<RrmNavItem>,
    startTab: String,
    tabs: NavGraphBuilder.(SnackbarHostState) -> Unit,
) {
    val navController = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            RrmBottomNav(
                items = items,
                currentRoute = currentRoute,
                onSelect = { route -> navController.navigateTab(route) },
            )
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startTab,
            modifier = Modifier.padding(padding),
        ) {
            tabs(snackbar)
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

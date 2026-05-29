package com.rodriguez.riceretailmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rodriguez.riceretailmaster.auth.RecoveryEvents
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.navigation.AppNavigation
import com.rodriguez.riceretailmaster.ui.theme.Background
import com.rodriguez.riceretailmaster.ui.theme.RiceRetailMasterTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAuthDeepLink(intent)
        setContent {
            RiceRetailMasterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Background) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthDeepLink(intent)
    }

    private fun handleAuthDeepLink(intent: Intent?) {
        if (intent == null || !SupabaseService.isConfigured) return
        SupabaseService.client.handleDeeplinks(intent)
        if (intent.isRecoveryDeepLink()) RecoveryEvents.signal()
    }

    private fun Intent.isRecoveryDeepLink(): Boolean {
        val uri = data ?: return false
        return uri.scheme == "riceretailmaster" && uri.host == "reset-password"
    }
}

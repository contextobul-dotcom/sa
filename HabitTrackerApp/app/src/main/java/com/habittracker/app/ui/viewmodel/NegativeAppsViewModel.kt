package com.habittracker.app.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habittracker.app.data.entity.NegativeApp
import com.habittracker.app.data.repository.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InstalledAppInfo(val packageName: String, val label: String)

class NegativeAppsViewModel(
    private val app: Application,
    private val usageRepository: UsageRepository
) : ViewModel() {

    val trackedApps = usageRepository.observeNegativeApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val pm = app.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolvedApps = pm.queryIntentActivities(launcherIntent, 0)
            val seen = LinkedHashSet<String>()
            val result = mutableListOf<InstalledAppInfo>()
            for (resolveInfo in resolvedApps) {
                val packageName = resolveInfo.activityInfo.packageName
                if (seen.add(packageName)) {
                    val label = resolveInfo.loadLabel(pm).toString()
                    result.add(InstalledAppInfo(packageName, label))
                }
            }
            _installedApps.value = result.sortedBy { it.label.lowercase() }
        }
    }

    fun addNegativeApp(appInfo: InstalledAppInfo, pointsPerMinute: Int) {
        if (pointsPerMinute <= 0) return
        viewModelScope.launch {
            usageRepository.addNegativeApp(appInfo.packageName, appInfo.label, pointsPerMinute)
        }
    }

    fun removeNegativeApp(negativeApp: NegativeApp) {
        viewModelScope.launch { usageRepository.removeNegativeApp(negativeApp) }
    }

    class Factory(
        private val app: Application,
        private val usageRepository: UsageRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NegativeAppsViewModel(app, usageRepository) as T
        }
    }
}

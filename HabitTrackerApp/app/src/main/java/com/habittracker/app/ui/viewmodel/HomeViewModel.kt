package com.habittracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habittracker.app.data.entity.PositiveHabit
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.data.repository.UsageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val habitRepository: HabitRepository,
    private val usageRepository: UsageRepository
) : ViewModel() {

    val habits = habitRepository.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val positivePoints = habitRepository.observeTodayPositivePoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val negativePoints = usageRepository.observeTodayNegativePoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val netPoints = combine(
        habitRepository.observeTodayPositivePoints(),
        usageRepository.observeTodayNegativePoints()
    ) { positive, negative -> positive - negative }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        refreshUsage()
    }

    fun refreshUsage() {
        viewModelScope.launch {
            usageRepository.syncTodayUsage()
        }
    }

    fun toggleHabit(habit: PositiveHabit) {
        viewModelScope.launch {
            if (habit.activeSessionStart == null) {
                habitRepository.startHabit(habit.id)
            } else {
                habitRepository.stopHabit(habit.id)
            }
        }
    }

    class Factory(
        private val habitRepository: HabitRepository,
        private val usageRepository: UsageRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(habitRepository, usageRepository) as T
        }
    }
}

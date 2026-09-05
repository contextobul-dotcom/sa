package com.habittracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habittracker.app.data.entity.PositiveHabit
import com.habittracker.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitsViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    val habits = habitRepository.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHabit(name: String, pointsPerMinute: Int) {
        if (name.isBlank() || pointsPerMinute <= 0) return
        viewModelScope.launch { habitRepository.addHabit(name.trim(), pointsPerMinute) }
    }

    fun deleteHabit(habit: PositiveHabit) {
        viewModelScope.launch { habitRepository.deleteHabit(habit) }
    }

    class Factory(private val habitRepository: HabitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HabitsViewModel(habitRepository) as T
        }
    }
}

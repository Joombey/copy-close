package dev.farukh.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.internal.Provider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class UniversalViewModelFactory @Inject constructor(
    private val outerFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (outerFactories.containsKey(modelClass)) {
            return outerFactories[modelClass]?.get() as T
        }
        throw Exception("no such ViewModel in di graph: ${modelClass.simpleName}")
    }
}
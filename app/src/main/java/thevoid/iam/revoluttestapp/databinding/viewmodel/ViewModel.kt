package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.os.Bundle

/**
 * Created by alese_000 on 23.02.2018.
 */


/**
 * View model calls from activity lifecycle
 *
 * Each ViewModel child must override ViewModel lifecycle methods, but not
 * Activity lifecycle methods
 *
 * Restore and save state calls only if possible (bundle not null)
 */
abstract class ViewModel {

    internal abstract fun initialize();
    internal abstract fun deinitialize();

    internal abstract fun restoreState(savedInstantState: Bundle)
    internal abstract fun saveState(bundle: Bundle)

    fun onCreate(savedInstantState : Bundle?) {
        savedInstantState?.let { restoreState(it) }
    }

    fun onResume(){
        initialize()
    }

    fun onStart(){}
    fun onStop(){}
    fun onPause(){}

    fun onDestroy(){
        deinitialize()
    }

    fun onSaveInstantState(bundle: Bundle?) {
        bundle?.let { saveState(it) }
    }
}

package thevoid.iam.revoluttestapp.databinding.viewmodel

import android.os.Bundle

/**
 * Created by alese_000 on 23.02.2018.
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

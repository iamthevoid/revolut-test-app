package thevoid.iam.revoluttestapp.databinding.viewmodel

/**
 * Created by alese_000 on 23.02.2018.
 */
abstract class ViewModel {

    internal abstract fun initialize();
    internal abstract fun deinitialize();

    fun onCreate(){}
    fun onResume(){
        initialize()
    }
    fun onStart(){}
    fun onStop(){}
    fun onPause(){}
    fun onDestroy(){
        deinitialize()
    }
}

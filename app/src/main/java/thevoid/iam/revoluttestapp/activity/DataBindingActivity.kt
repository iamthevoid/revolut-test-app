package thevoid.iam.revoluttestapp.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import thevoid.iam.revoluttestapp.databinding.viewmodel.ViewModel

/**
 * Created by alese_000 on 23.02.2018.
 */
abstract class DataBindingActivity<out VM : ViewModel, Binding : android.databinding.ViewDataBinding> : AppCompatActivity(){

    abstract fun getViewModel() : VM
    abstract fun getLayout() : Int
    abstract fun getVariable() : Int

    private lateinit var viewModel: VM
    private lateinit var binding : Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        binding = DataBindingUtil.setContentView(this, getLayout())
        binding.setVariable(getVariable(), viewModel)
        viewModel.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveInstantState(outState)
    }
}
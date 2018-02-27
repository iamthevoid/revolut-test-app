package thevoid.iam.revoluttestapp.activity

import thevoid.iam.revoluttestapp.BR
import thevoid.iam.revoluttestapp.R
import thevoid.iam.revoluttestapp.databinding.ActivityRatesListBinding
import thevoid.iam.revoluttestapp.databinding.viewmodel.RatesListViewModel

class MainActivity : DataBindingActivity<RatesListViewModel, ActivityRatesListBinding>() {

    override fun getVariable(): Int {
        return BR.vm
    }

    override fun getViewModel(): RatesListViewModel {
        return RatesListViewModel()
    }

    override fun getLayout(): Int {
        return R.layout.activity_rates_list
    }
}

package com.android.sabsigan.wifi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.sabsigan.R
import com.android.sabsigan.databinding.FragmentWifiRateBinding
import com.android.sabsigan.viewModel.WifiSelectorViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WifiRateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WifiRateFragment : Fragment() {
    private var mBinding: FragmentWifiRateBinding? = null
    private val binding get() = mBinding!!
    private val viewModel by activityViewModels<WifiSelectorViewModel>()


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentWifiRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getwifiInfo().observe(requireActivity(), Observer {
            if (it != "mobileInfo.isConnected" && it != "wifiInfo.null") {
                binding.ssid.text = "SSID = $it"
                binding.bssid.text = "BSSID = ${viewModel.getBSSID().value}"
                binding.macAdress.text = "macAdress = ${viewModel.getMacAdress().value}"
                binding.ipAdress.text = "ipAdress = ${viewModel.getIpAdress().value}"
                binding.networkId.text = "networkId = ${viewModel.getNetworkId().value}"
                binding.linkSpeed.text = "linkSpeed = ${viewModel.getLinkSpeed().value}"
            } else {
                binding.ssid.text = "SSID = null"
                binding.bssid.text = "BSSID = null"
                binding.macAdress.text = "macAdress = null"
                binding.ipAdress.text = "ipAdress = null"
                binding.networkId.text = "networkId = null"
                binding.linkSpeed.text = "linkSpeed = null"            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WifiRateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WifiRateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
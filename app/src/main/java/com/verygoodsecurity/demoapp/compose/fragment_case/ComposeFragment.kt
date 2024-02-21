package com.verygoodsecurity.demoapp.compose.fragment_case

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.verygoodsecurity.demoapp.compose.components.BaseCollect
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse

private const val PATH = ""
private const val VAULT_ID = ""
private const val ENVIRONMENT = ""

class ComposeFragment : Fragment(), VgsCollectResponseListener {

    private val collect: VGSCollect by lazy {
        VGSCollect(
            requireContext(),
            VAULT_ID,
            ENVIRONMENT
        ).also {
            it.addOnResponseListeners(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Content(collect = collect)
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        Toast.makeText(
            requireContext(),
            when (response) {
                is VGSResponse.SuccessResponse -> "Success"
                is VGSResponse.ErrorResponse -> "Error"
                else -> throw IllegalStateException()
            },
            Toast.LENGTH_SHORT
        ).show()

        Log.d("VGS", "response = ${response.body}")
    }
}

@Composable
private fun Content(collect: VGSCollect?) {
    MaterialTheme {
        BaseCollect(
            collect = collect,
            path = PATH
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(collect = null)
}

package ani.saikou.offline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import ani.saikou.R
import ani.saikou.databinding.FragmentOfflineBinding
import ani.saikou.isOnline
import ani.saikou.navBarHeight
import ani.saikou.settings.saving.PrefManager
import ani.saikou.settings.saving.PrefName
import ani.saikou.startMainActivity
import ani.saikou.statusBarHeight

class OfflineFragment : Fragment() {
    private var offline = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOfflineBinding.inflate(inflater, container, false)
        binding.refreshContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = statusBarHeight
            bottomMargin = navBarHeight
        }
        offline = PrefManager.getVal(PrefName.OfflineMode)
        binding.noInternet.text =
            if (offline) "Offline Mode" else getString(R.string.no_internet)
        binding.refreshButton.visibility = if (offline) View.GONE else View.VISIBLE
        binding.refreshButton.setOnClickListener {
            if (isOnline(requireContext())) {
                startMainActivity(requireActivity())
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        offline = PrefManager.getVal(PrefName.OfflineMode)
    }
}
package ani.saito.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ani.saito.BottomSheetDialogFragment
import ani.saito.MainActivity
import ani.saito.profile.ProfileActivity
import ani.saito.R
import ani.saito.connections.anilist.Anilist
import ani.saito.databinding.BottomSheetSettingsBinding
import ani.saito.download.anime.OfflineAnimeFragment
import ani.saito.download.manga.OfflineMangaFragment
import ani.saito.home.AnimeFragment
import ani.saito.home.HomeFragment
import ani.saito.home.LoginFragment
import ani.saito.home.MangaFragment
import ani.saito.home.NoInternet
import ani.saito.incognitoNotification
import ani.saito.loadImage
import ani.saito.profile.activity.NotificationActivity
import ani.saito.offline.OfflineFragment
import ani.saito.profile.activity.FeedActivity
import ani.saito.setSafeOnClickListener
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName
import ani.saito.startMainActivity
import java.util.Timer
import kotlin.concurrent.schedule

class SettingsDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageType: PageType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageType = arguments?.getSerializable("pageType") as? PageType ?: PageType.HOME
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog?.window
        window?.statusBarColor = Color.CYAN
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
        window?.navigationBarColor = typedValue.data
        val notificationIcon = if (Anilist.unreadNotificationCount > 0) {
            R.drawable.ic_round_notifications_active_24
        } else {
            R.drawable.ic_round_notifications_none_24
        }
        binding.settingsNotification.setImageResource(notificationIcon)

        if (Anilist.token != null) {
            binding.settingsLogin.setText(R.string.logout)
            binding.settingsLogin.setOnClickListener {
                val alertDialog = AlertDialog.Builder(requireContext(), R.style.MyPopup)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    Anilist.removeSavedToken()
                    dismiss()
                    startMainActivity(requireActivity())
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                alertDialog.window?.setDimAmount(0.8f)
                alertDialog.show()
            }
            binding.settingsUsername.text = Anilist.username
            binding.settingsUserAvatar.loadImage(Anilist.avatar)
        } else {
            binding.settingsUsername.visibility = View.GONE
            binding.settingsLogin.setText(R.string.login)
            binding.settingsLogin.setOnClickListener {
                dismiss()
                Anilist.loginIntent(requireActivity())
            }
        }
        binding.settingsNotificationCount.visibility = if (Anilist.unreadNotificationCount > 0) View.VISIBLE else View.GONE
        binding.settingsNotificationCount.text = Anilist.unreadNotificationCount.toString()
        binding.settingsUserAvatar.setOnClickListener{
            ContextCompat.startActivity(
                requireContext(), Intent(requireContext(), ProfileActivity::class.java)
                    .putExtra("userId", Anilist.userid), null
            )
        }

        binding.settingsIncognito.isChecked = PrefManager.getVal(PrefName.Incognito)
        binding.settingsIncognito.setOnCheckedChangeListener { _, isChecked ->
            PrefManager.setVal(PrefName.Incognito, isChecked)
            incognitoNotification(requireContext())
        }

        binding.settingsExtensionSettings.setSafeOnClickListener {
            startActivity(Intent(activity, ExtensionsActivity::class.java))
            dismiss()
        }

        binding.settingsSettings.setSafeOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
            dismiss()
        }

        binding.settingsActivity.setSafeOnClickListener {
            startActivity(Intent(activity, FeedActivity::class.java))
            dismiss()
        }

        binding.settingsNotification.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
            dismiss()
        }
        binding.settingsDownloads.isChecked = PrefManager.getVal(PrefName.OfflineMode)
        binding.settingsDownloads.setOnCheckedChangeListener { _, isChecked ->
            Timer().schedule(300) {
                when (pageType) {
                    PageType.MANGA -> {
                        val intent = Intent(activity, NoInternet::class.java)
                        intent.putExtra(
                            "FRAGMENT_CLASS_NAME",
                            OfflineMangaFragment::class.java.name
                        )
                        startActivity(intent)
                    }

                    PageType.ANIME -> {
                        val intent = Intent(activity, NoInternet::class.java)
                        intent.putExtra(
                            "FRAGMENT_CLASS_NAME",
                            OfflineAnimeFragment::class.java.name
                        )
                        startActivity(intent)
                    }

                    PageType.HOME -> {
                        val intent = Intent(activity, NoInternet::class.java)
                        intent.putExtra("FRAGMENT_CLASS_NAME", OfflineFragment::class.java.name)
                        startActivity(intent)
                    }

                    PageType.OfflineMANGA -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("FRAGMENT_CLASS_NAME", MangaFragment::class.java.name)
                        startActivity(intent)
                    }

                    PageType.OfflineHOME -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra(
                            "FRAGMENT_CLASS_NAME",
                            if (Anilist.token != null) HomeFragment::class.java.name else LoginFragment::class.java.name
                        )
                        startActivity(intent)
                    }

                    PageType.OfflineANIME -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("FRAGMENT_CLASS_NAME", AnimeFragment::class.java.name)
                        startActivity(intent)
                    }
                }

                dismiss()
                PrefManager.setVal(PrefName.OfflineMode, isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        enum class PageType {
            MANGA, ANIME, HOME, OfflineMANGA, OfflineANIME, OfflineHOME
        }

        fun newInstance(pageType: PageType): SettingsDialogFragment {
            val fragment = SettingsDialogFragment()
            val args = Bundle()
            args.putSerializable("pageType", pageType)
            fragment.arguments = args
            return fragment
        }
    }
}

package ani.saito.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ani.saito.R
import ani.saito.connections.anilist.ProfileViewModel
import ani.saito.connections.anilist.api.Query
import ani.saito.databinding.FragmentProfileBinding
import ani.saito.media.Author
import ani.saito.media.AuthorAdapter
import ani.saito.media.Character
import ani.saito.media.CharacterAdapter
import ani.saito.media.Media
import ani.saito.media.MediaAdaptor
import ani.saito.setSlideIn
import ani.saito.setSlideUp
import ani.saito.util.AniMarkdown.Companion.getFullAniHTML
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var activity: ProfileActivity
    private lateinit var user: Query.UserProfile
    private val favStaff = arrayListOf<Author>()
    private val favCharacter = arrayListOf<Character>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    val model: ProfileViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as ProfileActivity

        user = arguments?.getSerializable("user") as Query.UserProfile
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            model.setData(user.id)
        }
        binding.profileUserBio.settings.loadWithOverviewMode = true
        binding.profileUserBio.settings.useWideViewPort = true
        binding.profileUserBio.setInitialScale(1)
        val styledHtml = getFullAniHTML(
            user.about ?: "",
            ContextCompat.getColor(activity, R.color.bg_opp)
        )
        binding.profileUserBio.loadDataWithBaseURL(
            null,
            styledHtml,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )
        binding.profileUserBio.setBackgroundColor(
            ContextCompat.getColor(
                activity,
                android.R.color.transparent
            )
        )
        binding.profileUserBio.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.profileUserBio.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.profileUserBio.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        android.R.color.transparent
                    )
                )
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }
        }

        binding.userInfoContainer.visibility =
            if (user.about != null) View.VISIBLE else View.GONE


        binding.statsEpisodesWatched.text = user.statistics.anime.episodesWatched.toString()
        binding.statsDaysWatched.text =
            (user.statistics.anime.minutesWatched / (24 * 60)).toString()
        binding.statsAnimeMeanScore.text = user.statistics.anime.meanScore.toString()
        binding.statsChaptersRead.text = user.statistics.manga.chaptersRead.toString()
        binding.statsVolumeRead.text = (user.statistics.manga.volumesRead).toString()
        binding.statsMangaMeanScore.text = user.statistics.manga.meanScore.toString()
        initRecyclerView(
            model.getAnimeFav(),
            binding.profileFavAnimeContainer,
            binding.profileFavAnimeRecyclerView,
            binding.profileFavAnimeProgressBar,
            binding.profileFavAnime
        )

        initRecyclerView(
            model.getMangaFav(),
            binding.profileFavMangaContainer,
            binding.profileFavMangaRecyclerView,
            binding.profileFavMangaProgressBar,
            binding.profileFavManga
        )

        user.favourites?.characters?.nodes?.forEach { i ->
            favCharacter.add(Character(i.id, i.name.full, i.image.large, i.image.large, "", true))
        }

        user.favourites?.staff?.nodes?.forEach { i ->
            favStaff.add(Author(i.id, i.name.full, i.image.large , "" ))
        }

        setFavPeople()
    }

    override fun onResume() {
        super.onResume()
        if (this::binding.isInitialized) {
            binding.root.requestLayout()
            setFavPeople()
            model.refresh()
        }
    }

    private fun setFavPeople() {
        if (favStaff.isEmpty()) {
            binding.profileFavStaffContainer.visibility = View.GONE
        }
        binding.profileFavStaffRecycler.adapter = AuthorAdapter(favStaff)
        binding.profileFavStaffRecycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        if (favCharacter.isEmpty()) {
            binding.profileFavCharactersContainer.visibility = View.GONE
        }
        binding.profileFavCharactersRecycler.adapter = CharacterAdapter(favCharacter)
        binding.profileFavCharactersRecycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun initRecyclerView(
        mode: LiveData<ArrayList<Media>>,
        container: View,
        recyclerView: RecyclerView,
        progress: View,
        title: View
    ) {
        container.visibility = View.VISIBLE
        progress.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        title.visibility = View.INVISIBLE

        mode.observe(viewLifecycleOwner) {
            recyclerView.visibility = View.GONE
            if (it != null) {
                if (it.isNotEmpty()) {
                    recyclerView.adapter = MediaAdaptor(0, it, activity, fav=true)
                    recyclerView.layoutManager = LinearLayoutManager(
                        activity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.layoutAnimation =
                        LayoutAnimationController(setSlideIn(), 0.25f)

                } else {
                    container.visibility = View.GONE
                }
                title.visibility = View.VISIBLE
                title.startAnimation(setSlideUp())
                progress.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance(query: Query.UserProfile): ProfileFragment {
            val args = Bundle().apply {
                putSerializable("user", query)
            }
            return ProfileFragment().apply {
                arguments = args
            }
        }
    }

}
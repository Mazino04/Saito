package ani.saito.profile


import android.view.View
import ani.saito.R
import ani.saito.blurImage
import ani.saito.databinding.ItemFollowerBinding
import ani.saito.loadImage
import com.xwray.groupie.viewbinding.BindableItem

class FollowerItem(
    private val id: Int,
    private val name: String,
    private val avatar: String?,
    private val banner: String?,
    val clickCallback: (Int) -> Unit
): BindableItem<ItemFollowerBinding>() {
    private lateinit var binding: ItemFollowerBinding

    override fun bind(viewBinding: ItemFollowerBinding, position: Int) {
        binding = viewBinding
        binding.profileUserName.text = name
        avatar?.let { binding.profileUserAvatar.loadImage(it) }
        blurImage(binding.profileBannerImage, banner ?: avatar)
        binding.root.setOnClickListener { clickCallback(id) }
    }

    override fun getLayout(): Int {
        return R.layout.item_follower
    }

    override fun initializeViewBinding(view: View): ItemFollowerBinding {
        return ItemFollowerBinding.bind(view)
    }
}
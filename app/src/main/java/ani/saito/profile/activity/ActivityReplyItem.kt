package ani.saito.profile.activity

import android.view.View
import androidx.core.content.ContextCompat
import ani.saito.R
import ani.saito.buildMarkwon
import ani.saito.connections.anilist.api.ActivityReply
import ani.saito.databinding.ItemActivityReplyBinding
import ani.saito.loadImage
import ani.saito.util.AniMarkdown.Companion.getBasicAniHTML
import com.xwray.groupie.viewbinding.BindableItem

class ActivityReplyItem(
    private val reply: ActivityReply,
    private val clickCallback: (Int, type: String) -> Unit
) : BindableItem<ItemActivityReplyBinding>() {
    private lateinit var binding: ItemActivityReplyBinding

    override fun bind(viewBinding: ItemActivityReplyBinding, position: Int) {
        binding = viewBinding

        binding.activityUserAvatar.loadImage(reply.user.avatar?.medium)
        binding.activityUserName.text = reply.user.name
        binding.activityTime.text = ActivityItemBuilder.getDateTime(reply.createdAt)
        binding.activityLikeCount.text = reply.likeCount.toString()
        val likeColor = ContextCompat.getColor(binding.root.context, R.color.yt_red)
        val notLikeColor = ContextCompat.getColor(binding.root.context, R.color.bg_opp)
        binding.activityLike.setColorFilter(if (reply.isLiked) likeColor else notLikeColor)
        val markwon = buildMarkwon(binding.root.context)
        markwon.setMarkdown(binding.activityContent, getBasicAniHTML(reply.text))
        binding.activityAvatarContainer.setOnClickListener {
            clickCallback(reply.userId, "USER")
        }
        binding.activityUserName.setOnClickListener {
            clickCallback(reply.userId, "USER")
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_activity_reply
    }

    override fun initializeViewBinding(view: View): ItemActivityReplyBinding {
        return ItemActivityReplyBinding.bind(view)
    }
}
package ani.saikou.profile.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ani.saikou.databinding.ItemFollowerBinding
import ani.saikou.loadImage
import ani.saikou.profile.ProfileActivity
import ani.saikou.profile.User
import ani.saikou.setAnimation


class UsersAdapter(private val user: ArrayList<User>) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    inner class UsersViewHolder(val binding: ItemFollowerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                ContextCompat.startActivity(
                    binding.root.context, Intent(binding.root.context, ProfileActivity::class.java)
                        .putExtra("userId", user[bindingAdapterPosition].id), null
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            ItemFollowerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val b = holder.binding
        setAnimation(b.root.context, b.root)
        val user = user[position]
        b.profileUserAvatar.loadImage(user.pfp)
        b.profileBannerImage.loadImage(user.banner)
        b.profileUserName.text = user.name
    }

    override fun getItemCount(): Int = user.size
}

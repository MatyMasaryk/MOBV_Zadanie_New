package eu.mcomputing.mobv.mobvzadanie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.mobvzadanie.R
import eu.mcomputing.mobv.mobvzadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.mobvzadanie.utils.ItemDiffCallback

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<UserEntity> = listOf()

    var onItemClick: ((UserEntity) -> Unit)? = null

    // ViewHolder poskytuje odkazy na zobrazenia v každej položke
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card_user)
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val textView: TextView = itemView.findViewById(R.id.item_text)
    }

    // Táto metóda vytvára nový ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    // Táto metóda prepojí dáta s ViewHolderom
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = items[position]

        val urlBase = "https://upload.mcomputing.eu/"
        val urlPhoto = item.photo

        if (urlPhoto != ""){
            Picasso.get().load(urlBase + urlPhoto).into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_account_box_24)
        }

        holder.textView.text = item.name

        holder.cardView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    // Vracia počet položiek v zozname
    override fun getItemCount() = items.size

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}
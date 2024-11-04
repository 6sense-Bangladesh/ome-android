package com.ome.app.presentation.views.camera

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.databinding.ItemPhotoBinding
import com.ome.app.presentation.stove.StoveSetupPhotoViewModel
import com.ome.app.utils.setBounceClickListener

class PhotoAdapter(
    private val items: List<Bitmap>,
    private val viewModel: StoveSetupPhotoViewModel
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount()= items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(items[position], position)
    }

    inner class ViewHolder(
        private val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(photo: Bitmap, position: Int) {
            binding.apply {
                photoView.setImageBitmap(photo)
                btnRemove.setBounceClickListener {
                    viewModel.removePhoto(position)
                }

                itemView.setBounceClickListener {
                    onItemClickListener?.invoke(position)
                }
            }
        }

    }

}

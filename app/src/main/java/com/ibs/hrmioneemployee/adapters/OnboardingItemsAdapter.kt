package com.ibs.hrmioneemployee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.databinding.OnboardingItemContainerBinding
import com.ibs.hrmioneemployee.models.other_models.OnboardingItemModel

class OnboardingItemsAdapter(private val context: Context, private val onboardingItems: List<OnboardingItemModel>) :
    RecyclerView.Adapter<OnboardingItemsAdapter.OnboardingItemViewHolder>() {

    inner class OnboardingItemViewHolder(binding: OnboardingItemContainerBinding) : RecyclerView.ViewHolder(binding.root) {

        private val backgroundBigTitle = binding.backgroundBigTitle
        private val onboardingSmallTitle = binding.onboardingSmallTitle
        private val onboardingImage = binding.onboardingImage
        private val onboardingDescription = binding.onboardingDescription

        fun bind(onboardingItemModel: OnboardingItemModel) {
            onboardingImage.setImageResource(onboardingItemModel.onboardingImage)
            backgroundBigTitle.text = onboardingItemModel.backgroundBigTitle
            onboardingSmallTitle.text = onboardingItemModel.onboardingSmallTitle
            onboardingDescription.text = onboardingItemModel.onboardingDescription
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {

        val binding = OnboardingItemContainerBinding.inflate(LayoutInflater.from(context), parent, false)
        return OnboardingItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }
}
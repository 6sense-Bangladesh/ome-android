package com.ome.app.ui.views


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import com.ome.app.R
import com.ome.app.databinding.ViewBotttomNavBarBinding


class OmeNavBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : BottomNavbar, ConstraintLayout(context, attrs) {
    private val binding: ViewBotttomNavBarBinding =
        ViewBotttomNavBarBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    private val viewsList = listOf(
        binding.settingsContainer,
        binding.myStoveContainer,
        binding.memberContainer,
        binding.profileContainer
    )

    val enabledStateTabList = mutableMapOf(
        BottomItem.SETTINGS to true,
        BottomItem.MY_STOVE to true,
        BottomItem.MEMBERS to true,
        BottomItem.PROFILE to true,
    )

    override var currentActiveTab = BottomItem.MY_STOVE

    override var destinationListener: ((activeBottomItem: BottomItem, commitNow: Boolean) -> Unit)? =
        { activeBottomItem, commitNow -> }

    private fun setActiveTabsColor() {

    }

    fun setEnabledTabState(tab: BottomItem, enabled: Boolean) {
        enabledStateTabList[tab] = enabled
    }

    init {
        setActiveTabsColor()
        setActiveTab(BottomItem.MY_STOVE)


        binding.settingsContainer.setOnClickListener {
            enabledStateTabList[BottomItem.SETTINGS]?.let {
                if(it){
                    setActiveTab(BottomItem.SETTINGS)
                }
            } ?: run {
                setActiveTab(BottomItem.SETTINGS)
            }

        }
        binding.myStoveContainer.setOnClickListener {
            enabledStateTabList[BottomItem.MY_STOVE]?.let {
                if(it){
                    setActiveTab(BottomItem.MY_STOVE)
                }
            } ?: run {
                setActiveTab(BottomItem.MY_STOVE)
            }
        }
        binding.memberContainer.setOnClickListener {
            enabledStateTabList[BottomItem.MEMBERS]?.let {
                if(it){
                    setActiveTab(BottomItem.MEMBERS)
                }
            } ?: run {
                setActiveTab(BottomItem.MEMBERS)
            }
        }
        binding.profileContainer.setOnClickListener {
            enabledStateTabList[BottomItem.PROFILE]?.let {
                if(it){
                    setActiveTab(BottomItem.PROFILE)
                }
            } ?: run {
                setActiveTab(BottomItem.PROFILE)
            }
        }


        doOnLayout {
            (parent as? ViewGroup)?.clipChildren = false
            clipChildren = false
        }
    }

    override fun setActiveTab(activeBottomItem: BottomItem) {
        destinationListener?.invoke(activeBottomItem, true)
        currentActiveTab = activeBottomItem

        viewsList.forEach {
            val imageView = (it.getChildAt(0) as ImageView)
            val textView = (it.getChildAt(1) as TextView)
            val text = textView.text
            if (imageView.id == activeBottomItem.viewId) {

                imageView.setImageResource(activeBottomItem.iconResActive)


                textView.setTextColor(
                    ContextCompat.getColor(
                        context, (
                                when (activeBottomItem) {
                                    BottomItem.SETTINGS -> {
                                        R.color.menu_item_blue_color
                                    }
                                    BottomItem.MY_STOVE -> {
                                        R.color.white
                                    }
                                    BottomItem.MEMBERS -> {
                                        R.color.menu_item_blue_color
                                    }
                                    BottomItem.PROFILE -> {
                                        R.color.menu_item_blue_color
                                    }
                                }
                                )
                    )
                )
            } else {
//                it.setBackgroundColor(Color.TRANSPARENT)
                when (textView.text) {
                    context.getString(R.string.menu_settings) -> {
                        when (activeBottomItem) {
                            BottomItem.SETTINGS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_blue_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_settings_blue_pressed)
                            }
                            BottomItem.MY_STOVE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_settings_white)
                            }
                            BottomItem.MEMBERS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_settings_blue)
                            }
                            BottomItem.PROFILE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_settings_blue)
                            }
                        }
                    }
                    context.getString(R.string.menu_my_stove) -> {
                        when (activeBottomItem) {
                            BottomItem.SETTINGS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_my_stove)
                            }
                            BottomItem.MY_STOVE -> {
                                textView.setTextColor(
                                    Color.WHITE
                                )
                                imageView.setImageResource(R.drawable.ic_my_stove_pressed)
                            }
                            BottomItem.MEMBERS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_my_stove)
                            }
                            BottomItem.PROFILE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_my_stove)
                            }
                        }
                    }
                    context.getString(R.string.menu_members) -> {
                        when (activeBottomItem) {
                            BottomItem.SETTINGS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_members_blue)
                            }
                            BottomItem.MY_STOVE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_members_white)
                            }
                            BottomItem.MEMBERS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_blue_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_members_pressed)
                            }
                            BottomItem.PROFILE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_members_blue)
                            }
                        }
                    }
                    context.getString(R.string.menu_profile) -> {
                        when (activeBottomItem) {
                            BottomItem.SETTINGS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_profile_blue)
                            }
                            BottomItem.MY_STOVE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_profile_white)
                            }
                            BottomItem.MEMBERS -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_gray_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_profile_blue)
                            }
                            BottomItem.PROFILE -> {
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.menu_item_blue_color
                                    )
                                )
                                imageView.setImageResource(R.drawable.ic_profile_pressed)
                            }
                        }
                    }
                }

            }
        }
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putInt(SELECTED_TAB_STATE, currentActiveTab.viewId)
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val activeTab = state.getInt(SELECTED_TAB_STATE, R.id.ivMyStove)
            setActiveTab(BottomItem.findByViewID(activeTab))
            @Suppress("DEPRECATION")
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE))
        }
    }

    companion object {
        private const val SELECTED_TAB_STATE = "selectedTab"
        private const val SUPER_STATE = "superState"
    }
}

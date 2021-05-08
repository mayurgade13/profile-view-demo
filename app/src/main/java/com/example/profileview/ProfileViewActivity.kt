package com.example.profileview

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.profileview.databinding.ActivityProfileViewBinding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs
import kotlin.math.roundToInt

class ProfileViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileViewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var profileImage: AppCompatImageView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileImageTitle: AppCompatTextView
    private lateinit var toolbarTitle: AppCompatTextView
    private lateinit var workaroundView: AppCompatTextView
    private lateinit var profileBackground: FrameLayout

    private var toolbarHorizontalMargin = 0F
    private var toolbarVerticalMargin = 0F
    private var cashCollapseState: Pair<Int, Int>? = null
    private var profileImageAnimateStartPointY: Float = 0F
    private var profileCollapseAnimationChangeWeight: Float = 0F
    private var areAnimationPropertiesInitialized = false

    companion object {
        const val SWITCH_BOUND = 0.8f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1

        private var PROFILE_IMAGE_COLLAPSED_SIZE: Float = 0F
        private var PROFILE_IMAGE_EXPANDED_SIZE: Float = 0F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization
        recyclerView = binding.recyclerView
        PROFILE_IMAGE_COLLAPSED_SIZE = resources.getDimension(R.dimen.profile_picture_collapsed_size)
        PROFILE_IMAGE_EXPANDED_SIZE = resources.getDimension(R.dimen.profile_picture_expanded_size)
        toolbarHorizontalMargin = resources.getDimension(R.dimen.profile_title_margin)
        toolbar = binding.toolbar
        appBarLayout = binding.appBar
        profileImage = binding.profileImage
        profileImageTitle = binding.profileTitle
        toolbarTitle = binding.toolbarTitle
        profileBackground = binding.background
        workaroundView = binding.titleWorkaround

        // Setup recycler view with list items
        setupRecyclerView()

        // Setup app bar and perform animation
        setupAppBar()
    }

    private fun setupAppBar() {
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
                if (areAnimationPropertiesInitialized.not()) {
                    profileImageAnimateStartPointY =
                            abs((appBarLayout.height - (PROFILE_IMAGE_EXPANDED_SIZE + toolbarHorizontalMargin))
                                    / appBarLayout.totalScrollRange) / 2
                    profileCollapseAnimationChangeWeight = 1 / (1 - profileImageAnimateStartPointY)
                    toolbarVerticalMargin = (toolbar.height - PROFILE_IMAGE_COLLAPSED_SIZE) * 2
                    areAnimationPropertiesInitialized = true
                }
                updateViews(this, abs(offset / appBarLayout.totalScrollRange.toFloat()))
            }
        )
    }

    private fun updateViews(context: Context, offset: Float) {
        // Control the visibility of toolbar title
        when (offset) {
            in 0.15F..1F -> {
                profileImageTitle.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
            }

            in 0F..0.15F -> {
                profileImageTitle.alpha = (1f)
                profileImage.alpha = 1f
            }
        }

        // toggle collapse and expand
        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED ->  {
                            // Set profile picture's initial position
                            profileImage.translationX = 0F
                            // Initially hide title on toolbar
                            toolbarTitle.visibility = View.INVISIBLE
                        }
                        TO_COLLAPSED -> profileBackground.apply {
                            // Show title on toolbar with animation
                            toolbarTitle.apply {
                                visibility = View.VISIBLE
                                alpha = 0F
                                animate().setDuration(500).alpha(1.0f)
                            }
                        }
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }

            // Collapse profile image
            profileImage.apply {
                when {
                    offset > profileImageAnimateStartPointY -> {
                        val profileImageCollapseAnimateOffset =
                                (offset - profileImageAnimateStartPointY) * profileCollapseAnimationChangeWeight
                        val profileImageSize =
                                PROFILE_IMAGE_EXPANDED_SIZE - (PROFILE_IMAGE_EXPANDED_SIZE - PROFILE_IMAGE_COLLAPSED_SIZE) * profileImageCollapseAnimateOffset
                        this.layoutParams.also {
                            it.height = profileImageSize.roundToInt()
                            it.width = profileImageSize.roundToInt()
                        }
                        workaroundView.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset)

                        this.translationX =
                                (appBarLayout.width - toolbarHorizontalMargin - profileImageSize) * profileImageCollapseAnimateOffset
                        this.translationY =
                                ((toolbar.height  - toolbarVerticalMargin - profileImageSize ) / 2) * profileImageCollapseAnimateOffset
                    }
                    else -> this.layoutParams.also {
                        if (it.height != PROFILE_IMAGE_EXPANDED_SIZE.toInt()) {
                            it.height = PROFILE_IMAGE_EXPANDED_SIZE.toInt()
                            it.width = PROFILE_IMAGE_EXPANDED_SIZE.toInt()
                            this.layoutParams = it
                        }
                        translationX = 0f
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter(getListItems())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun getListItems(): MutableList<String> {
        val list = mutableListOf<String>();
        for (i in 0..20) {
            list.add("Item$i")
        }
        return list
    }
}

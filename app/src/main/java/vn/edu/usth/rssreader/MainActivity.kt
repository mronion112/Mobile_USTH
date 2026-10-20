package vn.edu.usth.rssreader

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val navigationItems = intArrayOf(
        R.id.nav_home,
        R.id.nav_explore,
        R.id.nav_saved,
        R.id.nav_rss,
    )

    private var selectedItemId = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedItemId = savedInstanceState?.getInt(KEY_SELECTED_ITEM, R.id.nav_home)
            ?: R.id.nav_home

        navigationItems.forEach { itemId ->
            findViewById<LinearLayout>(itemId).setOnClickListener {
                selectNavigationItem(itemId)
            }
        }

        selectNavigationItem(selectedItemId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_ITEM, selectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun selectNavigationItem(selectedId: Int) {
        selectedItemId = selectedId

        navigationItems.forEach { itemId ->
            val item = findViewById<LinearLayout>(itemId)
            val isSelected = itemId == selectedId
            val color = getColor(
                if (isSelected) R.color.nav_active else R.color.nav_inactive,
            )

            item.isSelected = isSelected
            item.findViewById<ImageView>(R.id.nav_icon).setColorFilter(color)
            item.findViewById<TextView>(R.id.nav_label).apply {
                setTextColor(color)
                setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            }
        }
    }

    companion object {
        private const val KEY_SELECTED_ITEM = "selected_navigation_item"
    }
}

package forpdateam.ru.forpda.fragments.news.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import forpdateam.ru.forpda.R
import forpdateam.ru.forpda.base.BaseParentFragment
import forpdateam.ru.forpda.ext.gone
import forpdateam.ru.forpda.ext.logger
import forpdateam.ru.forpda.fragments.news.details.comments.main.NewsDetailsCommentsFragment
import forpdateam.ru.forpda.fragments.news.details.content.NewsDetailsContentFragment

/**
 * Created by isanechek on 7/30/17.
 */
class NewsDetailsParentFragment : BaseParentFragment() {
    override fun getMenuId(): Int = R.menu.news_details_bn_menu

    private var prevMenuItem: MenuItem? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pager.adapter = PagerAdapter(childFragmentManager)
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_details_content -> pager.currentItem = 0
                R.id.action_details_comments -> pager.currentItem = 1
            }
            false
        }

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) prevMenuItem!!.isChecked = false
                else navigation.menu.getItem(0).isChecked = false
                navigation.menu.getItem(position).isChecked = true
                prevMenuItem = navigation.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    internal inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> NewsDetailsContentFragment.createInstance()
            1 -> NewsDetailsCommentsFragment.createInstance()
            else -> NewsDetailsContentFragment.createInstance()
        }

        override fun getCount(): Int = 2

    }

    companion object {
        private val TAG = NewsDetailsParentFragment::class.java.simpleName

    }

    init {
        configuration.isAlone = true
        configuration.isUseCache = true

    }
}
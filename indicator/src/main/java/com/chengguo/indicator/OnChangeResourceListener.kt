package com.chengguo.indicator

/**
 * @author ChengGuo
 * @date 2019/1/14
 */
interface OnChangeResourceListener {
    companion object {
        const val CHANGE_CLICK = 0
        const val CHANGE_VIEW_PAGER = 1
    }

    /**
     * @param resource 标题变化的来源，点击/viewPager changeListener
     * @param position 最终的位置
     */
    fun clickResource(resource: Int, position: Int)
}
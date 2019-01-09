package com.chengguo.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.view.ViewPager
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * 自定义tab导航title
 *
 * @author ChengGuo
 * @date 2019/01/10
 */
class TabLayoutTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val ANIM_MODE_START = 0
        const val ANIM_MODE_MIDDLE = 1

        const val DEFAULT_COLOR = 0x9f6def

        const val DEFAULT_NORMAL_SIZE = 14
        const val DEFAULT_SELECT_SIZE = 20
        /**
         * 手指点击的默认位置为第一个
         */
        const val DEFAULT_TOUCH_POSITION = 0
    }

    /**
     * title的宽度和颜色
     */
    private var mTextColor: Int = DEFAULT_COLOR

    private val mPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var mPosition = 0
    private var mPositionOffset = 0f
    private var mTotalTabCount = 1
    private var mItemWith = 0
    /**
     * 文字普通大小（px）
     * 文字选中大小（px）
     */
    private var mTextNormalSize = DEFAULT_NORMAL_SIZE
    private var mTextSelectSize = DEFAULT_SELECT_SIZE
    /**
     * 选中和普通文字大小的差值
     */
    private var mDiffSize = 0
    /**
     * 动画模式
     */
    private var mAnimMode = ANIM_MODE_START
    /**
     * 是否为粗体
     */
    private var mIsBoldText = false
    /**
     * 标题
     */
    private val mTitles: MutableList<String> = ArrayList()
    /**
     * 手指点击之前的位置
     */
    private var mTouchBeforePosition = DEFAULT_TOUCH_POSITION
    /**
     * 手指点击的位置
     */
    private var mTouchPosition = DEFAULT_TOUCH_POSITION
    /**
     * 是否手动点击title
     */
    private var mIsTouched = false

    var mViewPager: ViewPager? = null
        set(value) {
            field = value
            mTotalTabCount = field!!.adapter?.count ?: 1
            for (i in 0 until mTotalTabCount) {
                mTitles.add(field!!.adapter?.getPageTitle(i).toString())
            }

            field!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                   if (state == ViewPager.SCROLL_STATE_IDLE){
                       //ViewPager停止了滑动
                       mIsTouched = false
                   }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    mPosition = position
                    mPositionOffset = positionOffset
                    invalidate()
                }

                override fun onPageSelected(position: Int) {
                }
            })
        }

    init {
        if (attrs != null) {
            val attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.TabLayoutTitle)
            mTextColor = attrsArray.getColor(R.styleable.TabLayoutTitle_title_color, DEFAULT_COLOR)
            mTextNormalSize = attrsArray.getInt(R.styleable.TabLayoutTitle_title_text_normal_size, DEFAULT_NORMAL_SIZE)
            mTextNormalSize = attrsArray.getInt(R.styleable.TabLayoutTitle_title_text_normal_size, DEFAULT_NORMAL_SIZE)
            mIsBoldText = attrsArray.getBoolean(R.styleable.TabLayoutTitle_title_is_Bold, false)
            mAnimMode = attrsArray.getInt(R.styleable.TabLayoutTitle_title_anim_mode, ANIM_MODE_MIDDLE)
            attrsArray.recycle()
        }

        //设置文字paint
        mPaint.run {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mTextColor
            textSize = dp2px(mTextNormalSize)
            isFakeBoldText = mIsBoldText
            //居中对齐
            textAlign = Paint.Align.CENTER
        }

        mTextNormalSize = dp2px(mTextNormalSize).toInt()
        mTextSelectSize = dp2px(mTextSelectSize).toInt()
        mDiffSize = mTextSelectSize - mTextNormalSize
    }

    private fun initItemWidth() {
        if (mItemWith == 0 && mTitles.isNullOrEmpty().not()) {
            //说明还没有进行初始化
            mItemWith = width / mTitles.size
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initItemWidth()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            if (mIsTouched){
                mTitles.forEachIndexed { index, title ->
                    mPaint.run {
                        textSize = when (index) {
                            mTouchBeforePosition -> {
                                if (mAnimMode == ANIM_MODE_START) {
                                    textAlign = Paint.Align.LEFT
                                }
                                when {
                                    Math.abs(mPosition - mTouchPosition) > 1 -> mTextSelectSize.toFloat()
                                    mPosition == mTouchPosition -> mTextNormalSize.toFloat()
                                    else -> mTextSelectSize - mDiffSize * mPositionOffset
                                }
                            }
                            mTouchPosition -> {
                                if (mAnimMode == ANIM_MODE_START) {
                                    textAlign = Paint.Align.RIGHT
                                }
                                when {
                                    Math.abs(mPosition - mTouchPosition) > 1 -> mTextNormalSize.toFloat()
                                    mPosition == mTouchPosition -> mTextSelectSize.toFloat()
                                    else -> mTextNormalSize + mDiffSize * mPositionOffset
                                }
                            }
                            else -> {
                                textAlign = Paint.Align.CENTER
                                mTextNormalSize.toFloat()
                            }
                        }
                    }
                    drawText(title, mItemWith * index + mItemWith / 2.toFloat(), height / 2 + mPaint.fontMetrics.bottom, mPaint
                   )
                }
            }else{
                mTitles.forEachIndexed { index, title ->
                    mPaint.run {
                        textSize = when (index) {
                            mPosition -> {
                                if (mAnimMode == ANIM_MODE_START) {
                                    textAlign = Paint.Align.LEFT
                                }
                                mTextSelectSize - mDiffSize * mPositionOffset
                            }
                            mPosition + 1 -> {
                                if (mAnimMode == ANIM_MODE_START) {
                                    textAlign = Paint.Align.RIGHT
                                }
                                mTextNormalSize + mDiffSize * mPositionOffset
                            }
                            else -> {
                                textAlign = Paint.Align.CENTER
                                mTextNormalSize.toFloat()
                            }
                        }
                    }
                    drawText(title, mItemWith * index + mItemWith / 2.toFloat(), height / 2 + mPaint.fontMetrics.bottom, mPaint)
                }
            }
        }
    }

    /**
     * dp转换成px
     */
    private fun dp2px(dpValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchBeforePosition = mViewPager?.currentItem ?: DEFAULT_TOUCH_POSITION
                // 根据手指点击的范围，确定当前点击的第几个title
                mTouchPosition = (event.x / mItemWith).toInt()

                mIsTouched = true
                mViewPager?.currentItem = mTouchPosition
            }
            else -> {
            }
        }
        return true
    }

}


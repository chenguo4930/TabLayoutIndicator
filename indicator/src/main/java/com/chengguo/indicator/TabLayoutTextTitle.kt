package com.chengguo.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.viewpager.widget.ViewPager
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.chengguo.indicator.TabLayoutTitle.Companion.ANIM_MODE_START
import com.chengguo.indicator.TabLayoutTitle.Companion.DEFAULT_TOUCH_POSITION

/**
 * 自定义tab导航title
 *
 * @author ChengGuo
 * @date 2019/01/10
 */
class TabLayoutTextTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

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
        /**
         * 颜色改变的阈值
         */
        const val CHANGE_THRESHOLD = 0.5f
    }

    /**
     * title的宽度、普通颜色、选中后的颜色
     */
    private var mTextNormalColor: Int = DEFAULT_COLOR
    private var mTextSelectColor: Int = DEFAULT_COLOR

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
     * 文本间的margin
     */
    private var mTextMargin = 0
    /**
     * 是否为粗体
     */
    private var mIsBoldText = false
    /**
     * 是否只有选中的为粗体
     */
    private var mOnlySelectBoldText = false
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
    /**
     * 指示器的宽度和颜色
     */
    private var mIndicatorWidth = 0f
    private var mIndicatorHeight = 2f
    private var mIndicatorColor: Int = 0x9f6def
    private val mIndicatorPaint = Paint()
    /**
     * 导航条是否自动变换长度
     */
    private var mIndicatorAutoLength = false
    /**
     * 是否和标题一样的长度
     */
    private var mMatchTitleWidth = false
    /**
     * 设置了drawable就回执drawable，否则就绘制线条
     */
    private var mIndicatorDrawable: Drawable? = null
    private var mBitmapPaint: Paint? = null
    private var mIndicatorBitmap: Bitmap? = null
    /**
     * title变化的方式
     */
    var changeResourceListener: OnChangeResourceListener? = null

    var mViewPager: ViewPager? = null
        set(value) {
            field = value
            mTotalTabCount = field!!.adapter?.count ?: 1

            //默认第一个为选中状态
            for (i in 0 until mTotalTabCount) {
                addView(TextView(context).apply {
                    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        marginStart = (0.5 * dp2px(mTextMargin)).toInt()
                        marginEnd = marginStart
                    }
                    text = field!!.adapter?.getPageTitle(i).toString()
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, if (i == 0) mTextSelectSize.toFloat() else mTextNormalSize.toFloat())
                    setTextColor(if (i == 0) mTextSelectColor else mTextNormalColor)
                    typeface =  if (mIsBoldText) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                })
            }

            field!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        //ViewPager停止了滑动
                        if (mIsTouched) {
                            changeResourceListener?.clickResource(
                                OnChangeResourceListener.CHANGE_CLICK,
                                mViewPager?.currentItem ?: DEFAULT_TOUCH_POSITION
                            )
                        } else {
                            changeResourceListener?.clickResource(
                                OnChangeResourceListener.CHANGE_VIEW_PAGER,
                                mViewPager?.currentItem ?: DEFAULT_TOUCH_POSITION
                            )
                        }
                        mIsTouched = false
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    mPosition = position
                    mPositionOffset = when {
                        positionOffset > 0.99 -> 1f
                        positionOffset < 0.01 -> 0f
                        else -> positionOffset
                    }
                    invalidate()
                    for (i in 0 until childCount) {
                        if (mIsTouched) {
                            when (i) {
                                mTouchBeforePosition -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(mTextNormalColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = Typeface.DEFAULT
                                        }
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_PX,
                                            when {
                                                Math.abs(mPosition - mTouchPosition) > 1 -> mTextSelectSize.toFloat()
                                                mPosition == mTouchPosition -> mTextNormalSize.toFloat()
                                                else -> {
                                                    if (mTouchPosition > mTouchBeforePosition) {
                                                        //向右
                                                        mTextSelectSize - mDiffSize * mPositionOffset
                                                    } else {
                                                        //向左
                                                        mTextNormalSize + mDiffSize * mPositionOffset
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                mTouchPosition -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(mTextSelectColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = Typeface.DEFAULT_BOLD
                                        }
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_PX,
                                            when {
                                                Math.abs(mPosition - mTouchPosition) > 1 -> mTextNormalSize.toFloat()
                                                mPosition == mTouchPosition -> mTextSelectSize.toFloat()
                                                else -> {
                                                    if (mTouchPosition > mTouchBeforePosition) {
                                                        //向右
                                                        mTextNormalSize + mDiffSize * mPositionOffset
                                                    } else {
                                                        //向左
                                                        mTextSelectSize - mDiffSize * mPositionOffset
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                else -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(mTextNormalColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = Typeface.DEFAULT
                                        }
                                        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextNormalSize.toFloat())
                                    }
                                }
                            }
                        } else {
                            when (i) {
                                mPosition -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(if (mPositionOffset < TabLayoutTitle.COLOR_CHANGE_THRESHOLD) mTextSelectColor else mTextNormalColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = if (mPositionOffset < TabLayoutTitle.COLOR_CHANGE_THRESHOLD) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                        }
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_PX,
                                            mTextSelectSize - mDiffSize * mPositionOffset
                                        )
                                    }
                                }
                                mPosition + 1 -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(if (mPositionOffset < TabLayoutTitle.COLOR_CHANGE_THRESHOLD) mTextNormalColor else mTextSelectColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = if (mPositionOffset > TabLayoutTitle.COLOR_CHANGE_THRESHOLD) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                        }
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_PX,
                                            mTextNormalSize + mDiffSize * mPositionOffset
                                        )
                                    }
                                }
                                else -> {
                                    (getChildAt(i) as TextView).apply {
                                        setTextColor(mTextNormalColor)
                                        if (mOnlySelectBoldText) {
                                            typeface = Typeface.DEFAULT
                                        }
                                        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextNormalSize.toFloat())
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                }
            })
        }

    init {
        if (attrs != null) {
            val attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.TabLayoutTextTitle)
            //-----------------title--------------
            mTextNormalColor = attrsArray.getColor(R.styleable.TabLayoutTextTitle_text_normal_color, DEFAULT_COLOR)
            mTextSelectColor = attrsArray.getColor(R.styleable.TabLayoutTextTitle_text_select_color, DEFAULT_COLOR)
            mTextNormalSize = attrsArray.getInt(R.styleable.TabLayoutTextTitle_text_normal_size, DEFAULT_NORMAL_SIZE)
            mTextSelectSize = attrsArray.getInt(R.styleable.TabLayoutTextTitle_text_select_size, DEFAULT_NORMAL_SIZE)
            mIsBoldText = attrsArray.getBoolean(R.styleable.TabLayoutTextTitle_text_is_Bold, false)
            mOnlySelectBoldText = attrsArray.getBoolean(R.styleable.TabLayoutTextTitle_text_only_select_Bold, false)
            mTextMargin = attrsArray.getInt(R.styleable.TabLayoutTextTitle_text_margin, DEFAULT_NORMAL_SIZE)
            //-----------------indicator-------------
            mIndicatorWidth = attrsArray.getDimension(R.styleable.TabLayoutTextTitle_text_indicator_width, 20f)
            mIndicatorHeight = attrsArray.getDimension(R.styleable.TabLayoutTextTitle_text_indicator_height, 2f)
            mIndicatorColor = attrsArray.getColor(R.styleable.TabLayoutTextTitle_text_indicator_color, 0x9f6def)
            mIndicatorAutoLength =
                attrsArray.getBoolean(R.styleable.TabLayoutTextTitle_text_indicator_length_auto_transformation, false)
            mMatchTitleWidth =
                attrsArray.getBoolean(R.styleable.TabLayoutTextTitle_text_indicator_match_text_width, false)
            mIndicatorDrawable = attrsArray.getDrawable(R.styleable.TabLayoutTextTitle_text_indicator_drawable)
            attrsArray.recycle()
        }

        mTextMargin = dp2px(mTextMargin).toInt()
        mTextNormalSize = dp2px(mTextNormalSize).toInt()
        mTextSelectSize = dp2px(mTextSelectSize).toInt()
        mDiffSize = mTextSelectSize - mTextNormalSize

        //---------indicator-----------------
        mIndicatorPaint.run {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mIndicatorHeight
            color = mIndicatorColor
        }

        if (mIndicatorDrawable != null) {
            mIndicatorBitmap =
                Util.drawableToBitmap(mIndicatorDrawable!!, mIndicatorWidth.toInt(), mIndicatorHeight.toInt())
            mBitmapPaint = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
            }
        }

        //水平摆放、垂直居中
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mIndicatorAutoLength) {
            //indicator长度动态变换和标题文字一样宽 length_auto_transformation
            val startX: Float
            val endX: Float
            if (mPositionOffset <= CHANGE_THRESHOLD) {
                if (mMatchTitleWidth) {
                    startX = getChildAt(mPosition).x
                    //动态计算结束点
                    endX = if (getChildAt(mPosition + 1) != null)
                        startX + getChildAt(mPosition).width +
                                (getChildAt(mPosition + 1).width + getChildAt(mPosition + 1).x
                                        - getChildAt(mPosition).x - getChildAt(mPosition).width) * mPositionOffset * 2
                    else
                        startX + getChildAt(mPosition).width
                } else {
                    startX = getChildAt(mPosition).x + getChildAt(mPosition).width / 2 - mIndicatorWidth / 2
                    endX = if (getChildAt(mPosition + 1) != null)
                        startX + mIndicatorWidth +
                                (getChildAt(mPosition + 1).width + 2 * getChildAt(mPosition + 1).x
                                        - getChildAt(mPosition).width - 2 * getChildAt(mPosition).x) * mPositionOffset
                    else
                        startX + mIndicatorWidth
                }
            } else {
                if (mMatchTitleWidth) {
                    endX = getChildAt(mPosition + 1).width + getChildAt(mPosition + 1).x
                    //动态计算开始点
                    startX = getChildAt(mPosition).x + (getChildAt(mPosition + 1).x - getChildAt(mPosition).x) * (mPositionOffset * 2 - 1)
                } else {
                    endX = getChildAt(mPosition + 1).x + getChildAt(mPosition + 1).width * 0.5f + mIndicatorWidth * 0.5f
                    startX = getChildAt(mPosition).x + getChildAt(mPosition).width * 0.5f - mIndicatorWidth * 0.5f +
                            (getChildAt(mPosition + 1).x + getChildAt(mPosition + 1).width / 2 -
                                    getChildAt(mPosition).x - getChildAt(mPosition).width / 2) * (2 * mPositionOffset - 1)
                }
            }
            if (mIndicatorBitmap != null) {
                mIndicatorBitmap = Util.drawableToBitmap(mIndicatorDrawable!!, (endX - startX).toInt(), measuredHeight)
                canvas?.drawBitmap(mIndicatorBitmap!!, startX, height - mIndicatorHeight, mBitmapPaint)
            } else {
                canvas?.drawLine(
                    startX,
                    height - mIndicatorHeight / 2,
                    endX,
                    height - mIndicatorHeight / 2,
                    mIndicatorPaint
                )
            }
        } else {
            //indicator长度固定
            //绘制导航条
            canvas?.run {
                val middle1X = getChildAt(mPosition).x + getChildAt(mPosition).width / 2
                val middle2X = if (getChildAt(mPosition + 1) != null) {
                    getChildAt(mPosition + 1).x + getChildAt(mPosition + 1).width / 2
                } else {
                    0f
                }

                val indicatorStartX = middle1X - mIndicatorWidth / 2 + (middle2X - middle1X) * mPositionOffset
                if (mIndicatorBitmap != null) {
                    drawBitmap(mIndicatorBitmap!!, indicatorStartX, height - mIndicatorHeight, mBitmapPaint)
                } else {
                    drawLine(
                        indicatorStartX,
                        height - mIndicatorHeight / 2,
                        indicatorStartX + mIndicatorWidth,
                        height - mIndicatorHeight / 2,
                        mIndicatorPaint
                    )
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
                for (i in 0 until childCount) {
                    if (event.x > getChildAt(i).x - 0.5 * mTextMargin && event.x < getChildAt(i).x + getChildAt(i).width + 0.5 * mTextMargin) {
                        mTouchPosition = i
                        break
                    }
                }

                mIsTouched = true
                mViewPager?.currentItem = mTouchPosition
            }
            else -> {
            }
        }
        return true
    }

}



package com.chengguo.zhenai.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.chengguo.zhenai.indicator.R


/**
 * 自定义tabLayout指示器
 *
 * @author ChengGuo
 * @date 2018/12/10
 */
class TabLayoutIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    /**
     * 指示器的宽度和颜色
     */
    private var mIndicatorWith = 0f
    private var mIndicatorColor: Int = 0x9f6def

    private val mPaint = Paint()
    private var mPosition = 0
    private var mPositionOffset = 0f
    private var mTotalTabCount = 1
    private var mItemWith = 0
    private var mMarginLeft = 0f
    /**
     * 是否变换长度
     */
    private var mCanScanAnim = false
    /**
     * 设置了drawable就回执drawable，否则就绘制线条
     */
    private var mIndicatorDrawable: Drawable? = null
    private var mBitmapPaint: Paint? = null
    private var mIndicatorBitmap: Bitmap? = null

    var mViewPager: ViewPager? = null
        set(value) {
            field = value
            mTotalTabCount = field!!.adapter?.count?:1
            field!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
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
            val attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.TabLayoutIndicator)
            mIndicatorWith = attrsArray.getDimension(R.styleable.TabLayoutIndicator_indicator_with, 20f)
            mIndicatorColor = attrsArray.getColor(R.styleable.TabLayoutIndicator_indicator_color, 0x9f6def)
            mIndicatorDrawable = attrsArray.getDrawable(R.styleable.TabLayoutIndicator_indicator_drawable)
            mCanScanAnim = attrsArray.getBoolean(R.styleable.TabLayoutIndicator_indicator_can_anim, false)
            attrsArray.recycle()
        }

        mPaint.run {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = mIndicatorColor
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mItemWith = width / mTotalTabCount
        mMarginLeft = mItemWith / 2 - mIndicatorWith / 2
        mPaint.strokeWidth = height.toFloat()
        if (mIndicatorDrawable != null) {
            mIndicatorBitmap = drawableToBitmap(mIndicatorDrawable!!, mIndicatorWith.toInt(), measuredHeight)
            mBitmapPaint = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (mCanScanAnim) {
            //indicator长度动态变换
            val startX: Float
            val endX: Float
            if (mPositionOffset <= 0.5) {
                startX = mItemWith * mPosition + mMarginLeft
                endX = startX + mIndicatorWith + mItemWith * 2 * mPositionOffset
            } else {
                endX = mItemWith * (mPosition + 1) + mMarginLeft + mIndicatorWith
                startX = mMarginLeft + mItemWith * mPosition + (mPositionOffset - 0.5f) * 2 * mItemWith
            }
            if (mIndicatorBitmap != null) {
                mIndicatorBitmap = drawableToBitmap(mIndicatorDrawable!!, (endX - startX).toInt(), measuredHeight)
                canvas?.drawBitmap(mIndicatorBitmap!!, startX, 0f, mBitmapPaint)
            } else {
                canvas?.drawLine(startX, height / 2.toFloat(), endX, height / 2.toFloat(), mPaint)
            }
        } else {
            //indicator长度固定
            val startX = mItemWith * mPosition + mMarginLeft + mItemWith * mPositionOffset
            if (mIndicatorBitmap != null) {
                canvas?.drawBitmap(mIndicatorBitmap!!, startX, 0f, mBitmapPaint)
            } else {
                canvas?.drawLine(startX, height / 2.toFloat(), startX + mIndicatorWith, height / 2.toFloat(), mPaint)
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}
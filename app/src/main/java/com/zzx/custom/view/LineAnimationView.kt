package com.zzx.custom.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import android.view.animation.Animation
import android.view.animation.LinearInterpolator


class LineAnimationView : View {
    private var paint = Paint()
    private var bgPaint = Paint()
    private var path = Path()

    private var radius = 50f
    private var strokeWidth = 10f
    private var totalDuration = 5000
    private var strokeColor = Color.BLACK
    private var bgColor = Color.WHITE

    private var rectBg = RectF()
    private var rectRight = RectF()
    private var rectLeft = RectF()

    private var step = 1

    private var lineDuration = 0L
    private var arcDuration = 0L

    private var topLineLeft = 0f
    private var topLineRight = 0f
    private var rightArcSweepAngle = 0f
    private var bottomLineLeft = 0f
    private var leftArcSweepAngle = 0f

    private var set: AnimatorSet? = null

    var onCountDownListener: OnCountDownListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineAnimationView)
        radius = typedArray.getDimension(R.styleable.LineAnimationView_round_radius, 50f)
        strokeWidth = typedArray.getDimension(R.styleable.LineAnimationView_stroke_width, 10f)
        totalDuration = typedArray.getInteger(R.styleable.LineAnimationView_duration, 5000)
        strokeColor = typedArray.getInteger(R.styleable.LineAnimationView_stroke_color, Color.BLACK)
        bgColor = typedArray.getInteger(R.styleable.LineAnimationView_bg_color, Color.WHITE)
        typedArray.recycle()
        paint.color = strokeColor
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        bgPaint.color = bgColor
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
    }

    private fun updateRectRightLocation() {
        rectRight.left = width - radius * 2
        rectRight.top = strokeWidth / 2
        rectRight.right = width - strokeWidth / 2
        rectRight.bottom = radius * 2 - strokeWidth / 2
    }

    private fun updateRectLeftLocation() {
        rectLeft.left = strokeWidth / 2
        rectLeft.top = strokeWidth / 2
        rectLeft.right = radius * 2
        rectLeft.bottom = radius * 2 - strokeWidth / 2
    }

    private fun updateRectBgLocation() {
        rectBg.left = 0f
        rectBg.top = 0f
        rectBg.right = width.toFloat()
        rectBg.bottom = height.toFloat()
    }

    fun startCountDown() {
        val lineSize = width - radius * 2
        val arcSize = Math.PI.toFloat() * radius
        val totalSize = lineSize * 2 + arcSize * 2
        lineDuration = (lineSize / totalSize * 5000).toLong()
        arcDuration = (arcSize / totalSize * 5000).toLong()
        release()
        set = AnimatorSet()
        val animateLineTopRight = animateLineTopRight()
        val animateArcRight = animateArcRight()
        val animateLineBottom = animateLineBottom()
        val animateArcLeft = animateArcLeft()
        val animateLineTopLeft = animateLineTopLeft()
        set?.play(animateLineTopRight)?.before(animateArcRight)
        set?.play(animateArcRight)?.before(animateLineBottom)
        set?.play(animateLineBottom)?.before(animateArcLeft)
        set?.play(animateArcLeft)?.before(animateLineTopLeft)
        set?.start()
        set?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                onCountDownListener?.onCountDown()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    fun release() {
        step = 1
        set?.cancel()
        set = null
    }

    //画上面右边的横线
    private fun animateLineTopRight(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(width / 2f, width - radius)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = lineDuration / 2
        valueAnimator.addUpdateListener {
            topLineRight = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                step = 1
            }

        })
        return valueAnimator
    }

    //画右边的弧线
    private fun animateArcRight(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 180f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = arcDuration
        valueAnimator.addUpdateListener {
            rightArcSweepAngle = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                step = 2
            }

        })
        return valueAnimator
    }

    //画下边的横线
    private fun animateLineBottom(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(width - radius, radius)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = lineDuration
        valueAnimator.addUpdateListener {
            bottomLineLeft = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                step = 3
            }

        })
        return valueAnimator
    }

    //画左边的弧线
    private fun animateArcLeft(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 180f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = arcDuration
        valueAnimator.addUpdateListener {
            leftArcSweepAngle = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                step = 4
            }

        })
        return valueAnimator
    }

    //画上面左边的横线
    private fun animateLineTopLeft(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(radius, width / 2f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = lineDuration / 2
        valueAnimator.addUpdateListener {
            topLineLeft = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                step = 5
            }

        })
        return valueAnimator
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (width == 0 || topLineRight == 0f) {
            return
        }
        updateRectBgLocation()
        canvas?.drawRoundRect(rectBg, radius, radius, bgPaint)
        path.reset()
        path.moveTo(width / 2f, strokeWidth / 2)
        path.lineTo(topLineRight, strokeWidth / 2)
        if (step > 1) {
            updateRectRightLocation()
            path.arcTo(rectRight, 270f, rightArcSweepAngle)
        }
        if (step > 2) {
            path.lineTo(bottomLineLeft, radius * 2 - strokeWidth / 2)
        }

        if (step > 3) {
            updateRectLeftLocation()
            path.arcTo(rectLeft, 90f, leftArcSweepAngle)
        }
        if (step > 4) {
            path.lineTo(topLineLeft, strokeWidth / 2)
        }
        canvas?.drawPath(path, paint)
    }

    interface OnCountDownListener {
        fun onCountDown()
    }
}

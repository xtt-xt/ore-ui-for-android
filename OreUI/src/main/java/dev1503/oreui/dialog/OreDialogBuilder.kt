package dev1503.oreui.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import dev1503.oreui.StyleSheet
import dev1503.oreui.widgets.OreButton
import dev1503.oreui.widgets.OrePanel
import dev1503.oreui.widgets.OreTextView

class OreDialogBuilder(context: Context) : AlertDialog.Builder(context) {

    private var userView: View? = null
    val rootLayout: OrePanel
    private var headerPanel: OrePanel? = null
    private var headerPanelTextView: OreTextView? = null
    private var containerScrollView: ScrollView? = null
    private var messageTextView: OreTextView? = null
    private var footerPanel: OrePanel? = null
    var positiveButton: OreButton? = null
    var negativeButton: OreButton? = null
    var neutralButton: OreButton? = null
    private var dialog: AlertDialog? = null

    // 额外自定义按钮列表
    private val customButtons = mutableListOf<OreButton>()

    private var buttonOrientation = LinearLayout.VERTICAL

    // 默认按钮样式（可为 null，表示不强制改变按钮原有样式）
    var defaultButtonStyle: StyleSheet? = null

    companion object {
        const val ANIMATION_DISABLED = true
    }

    init {
        rootLayout = OrePanel(context).apply {
            styleSheet = StyleSheet.STYLE_DIALOG
            orientation = LinearLayout.VERTICAL
            val p = styleSheet.pixelSize.toInt()
            setPadding(p, p, p, p)
        }
        super.setView(rootLayout)
    }

    fun setButtonOrientation(orientation: Int): OreDialogBuilder {
        this.buttonOrientation = orientation
        footerPanel?.orientation = orientation
        refreshFooterButtons()
        return this
    }

    override fun setView(view: View?): OreDialogBuilder {
        this.userView = view
        if (containerScrollView == null) {
            containerScrollView = ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
                rootLayout.addView(this)
            }
        }
        containerScrollView?.removeAllViews()
        containerScrollView?.addView(view)
        return this
    }

    override fun setTitle(title: CharSequence?): OreDialogBuilder {
        if (headerPanel == null) {
            headerPanel = OrePanel(context).apply {
                outlineEnabled = false
                borderEnabled = true
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                gravity = Gravity.CENTER
                rootLayout.addView(this, 0)
            }

            headerPanelTextView = OreTextView(context).apply {
                headerPanel?.addView(this)
            }
        }
        headerPanelTextView?.text = title
        return this
    }

    override fun setMessage(message: CharSequence?): OreDialogBuilder {
        if (containerScrollView == null) {
            containerScrollView = ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
                rootLayout.addView(this)
            }
            val container = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                gravity = Gravity.CENTER
            }
            containerScrollView?.addView(container)
            messageTextView = OreTextView(context).apply {
                val padding = styleSheet.calcPixelSize(7.5f).toInt()
                setPadding(padding, padding, padding, padding)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, styleSheet.calcTextSize(0.85f))
                container.addView(this)
            }
        }
        messageTextView?.text = message
        return this
    }

    private fun initFooter() {
        if (footerPanel == null) {
            val style = rootLayout.styleSheet
            val spacing = style.calcPixelSize(2f).toInt()
            footerPanel = OrePanel(context).apply {
                outlineEnabled = false
                borderEnabled = true
                sideBorderEnabled = false
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                orientation = buttonOrientation
                gravity = Gravity.CENTER
                dividerDrawable = GradientDrawable().apply {
                    if (buttonOrientation == LinearLayout.VERTICAL) {
                        setSize(0, spacing)
                    } else {
                        setSize(spacing, 0)
                    }
                }
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                rootLayout.addView(this)
            }
        }
    }

    /**
     * 按指定样式创建一个按钮
     */
    private fun createButton(text: CharSequence?, style: StyleSheet?, listener: DialogInterface.OnClickListener?): OreButton {
        return OreButton(context).apply {
            this.text = text
            setOnClickListener { listener?.onClick(dialog, DialogInterface.BUTTON_NEUTRAL) }
            // 如果传入了样式，优先使用；否则使用默认样式（可为空）
            (style ?: defaultButtonStyle)?.let { styleSheet = it }
        }
    }

    private fun refreshFooterButtons() {
        val panel = footerPanel ?: return
        panel.removeAllViews()

        val lp = if (buttonOrientation == LinearLayout.HORIZONTAL) {
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        } else {
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val allButtons = mutableListOf<OreButton>()
        if (buttonOrientation == LinearLayout.HORIZONTAL) {
            negativeButton?.let { allButtons.add(it) }
            neutralButton?.let { allButtons.add(it) }
            positiveButton?.let { allButtons.add(it) }
        } else {
            positiveButton?.let { allButtons.add(it) }
            neutralButton?.let { allButtons.add(it) }
            negativeButton?.let { allButtons.add(it) }
        }
        allButtons.addAll(customButtons)

        for (button in allButtons) {
            button.layoutParams = lp
            panel.addView(button)
        }
    }

    // ---------- 固定三个按钮的原始方法（无样式版） ----------
    override fun setPositiveButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        return setPositiveButton(text, null, listener)
    }

    override fun setNegativeButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        return setNegativeButton(text, null, listener)
    }

    override fun setNeutralButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        return setNeutralButton(text, null, listener)
    }

    // ---------- 带样式的版本 ----------
    fun setPositiveButton(text: CharSequence?, style: StyleSheet?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        initFooter()
        positiveButton = createButton(text, style, listener)
        refreshFooterButtons()
        return this
    }

    fun setNegativeButton(text: CharSequence?, style: StyleSheet?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        initFooter()
        negativeButton = createButton(text, style, listener)
        refreshFooterButtons()
        return this
    }

    fun setNeutralButton(text: CharSequence?, style: StyleSheet?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        initFooter()
        neutralButton = createButton(text, style, listener)
        refreshFooterButtons()
        return this
    }

    // ---------- 自定义按钮（无限数量） ----------
    fun addButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        return addButton(text, null, listener)
    }

    fun addButton(text: CharSequence?, style: StyleSheet?, listener: DialogInterface.OnClickListener?): OreDialogBuilder {
        initFooter()
        val button = createButton(text, style, listener)
        customButtons.add(button)
        refreshFooterButtons()
        return this
    }

    override fun create(): AlertDialog {
        dialog = super.create()
        dialog?.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.decorView.setPadding(0, 0, 0, 0)
            if (ANIMATION_DISABLED) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.setWindowAnimations(0)
                window.attributes.windowAnimations = 0
                window.setDimAmount(0.5f)
            }
            val lp = window.attributes
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
        }
        return dialog!!
    }

    override fun show(): AlertDialog {
        val dialog = create()
        if (ANIMATION_DISABLED) {
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.show()
            dialog.window?.let { window ->
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                val lp = window.attributes
                lp.windowAnimations = 0
                window.attributes = lp
            }
        } else {
            dialog.show()
        }
        return dialog
    }
}
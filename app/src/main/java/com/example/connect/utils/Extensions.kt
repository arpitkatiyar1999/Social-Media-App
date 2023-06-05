package com.example.connect.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.connect.R
import com.example.connect.ui.authentication.AuthenticationActivity
import com.example.connect.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt


fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    val snackBar = Snackbar.make(this, message, duration)
    snackBar.show()
}

@Suppress("DEPRECATION")
fun vibrateDevice(context: Context, time: Long = 300) {
    if (Build.VERSION.SDK_INT >= 26) {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
            .vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(time)
    }
}

fun animateView(
    view: View,
    duration: Long = 300,
    repeat: Int = 0,
    techniques: Techniques = Techniques.Shake
) {
    YoYo.with(techniques).duration(duration).repeat(repeat).playOn(view)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun getMonthNameFromMonthNumber(monthNumber: Int): String {
    val monthList =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return if (monthNumber in 0..11)
        monthList[monthNumber]
    else ""
}

fun View.setAlpha() {
    this.alpha = 0.5f
}

fun View.removeAlpha() {
    this.alpha = 1f
}

fun View.focusScreen() {
    this.setOnApplyWindowInsetsListener { _, windowInsets ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
            this.setPadding(0, 0, 0, imeHeight)
        }
        windowInsets
    }
}

fun toast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}

fun changeDpToPx(dp: Int, context: Context): Int {
    return dp * context.resources.displayMetrics.density.roundToInt()
}

fun Fragment.navigateUsingAction(action: NavDirections) {
    findNavController().navigate(action)
}

fun Fragment.checkCurrentUser(firebaseAuth: FirebaseAuth) {
    if ((requireActivity() as MainActivity).currentUser == null) {
        firebaseAuth.signOut()
        toast(requireContext(), getString(R.string.some_error_occurred))
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
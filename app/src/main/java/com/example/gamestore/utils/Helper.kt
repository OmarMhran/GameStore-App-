package com.example.gamestore.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}
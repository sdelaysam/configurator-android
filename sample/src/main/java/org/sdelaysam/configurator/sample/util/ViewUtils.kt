package org.sdelaysam.configurator.sample.util

import android.content.res.Resources

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

fun dp(value: Int): Int = (value * Resources.getSystem().displayMetrics.density).toInt()

fun sp(value: Int): Int = (value * Resources.getSystem().displayMetrics.scaledDensity).toInt()
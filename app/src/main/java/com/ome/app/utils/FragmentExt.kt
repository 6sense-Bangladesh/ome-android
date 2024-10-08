package com.ome.app.utils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

fun NavHostFragment.getCurrentFragment(): Fragment? = childFragmentManager.primaryNavigationFragment

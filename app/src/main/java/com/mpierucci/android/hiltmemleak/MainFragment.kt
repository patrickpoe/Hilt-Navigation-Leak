package com.mpierucci.android.hiltmemleak

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment(R.layout.main_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()

        view.findViewById<Button>(R.id.leakyFragment).setOnClickListener {
            controller.navigate(R.id.action_mainFragment_to_leakFragment)
        }

        view.findViewById<Button>(R.id.navigationOnlyFragment).setOnClickListener {
            controller.navigate(R.id.action_mainFragment_to_navigationOnlyFragment)
        }

        view.findViewById<Button>(R.id.hiltOnlyFragment).setOnClickListener {
            controller.navigate(R.id.action_mainFragment_to_hiltOnlyFragment)
        }
    }
}
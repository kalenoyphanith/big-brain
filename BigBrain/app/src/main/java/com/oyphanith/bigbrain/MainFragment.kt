package com.oyphanith.bigbrain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.*
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.Navigation

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // star animation
        val rotate = RotateAnimation(0f, 359f, RELATIVE_TO_SELF, .5f,
                RELATIVE_TO_SELF, .5f)

        rotate.repeatCount = INFINITE
        rotate.repeatCount = RESTART
        rotate.duration = 10000
        rotate.interpolator = LinearInterpolator()

        val rollingStar = view.findViewById<ImageView>(R.id.rolling_star)
        rollingStar.startAnimation(rotate)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.fragmentOneButton).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_fragmentOne)
        }
    }
} // Fragment
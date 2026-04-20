package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.platform.ViewControllerHolder
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val vc = ComposeUIViewController { App() }
    ViewControllerHolder.viewController = vc
    return vc
}

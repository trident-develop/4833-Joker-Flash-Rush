package org.example.project.platform

import platform.UIKit.UIViewController

object ViewControllerHolder {
    var viewController: UIViewController? = null

    fun topViewController(): UIViewController? {
        var vc = viewController
        while (vc?.presentedViewController != null) {
            vc = vc.presentedViewController
        }
        return vc
    }
}

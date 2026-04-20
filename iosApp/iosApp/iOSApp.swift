import SwiftUI
import UIKit

@main
struct iOSApp: App {
    init() {
        let bg = UIColor(red: 0x1A / 255.0, green: 0x0F / 255.0, blue: 0x0A / 255.0, alpha: 1)
        UIWindow.appearance().backgroundColor = bg
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
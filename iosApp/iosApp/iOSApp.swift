import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
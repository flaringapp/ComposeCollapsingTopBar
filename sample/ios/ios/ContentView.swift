//
//  ContentView.swift
//  ios
//
//  Created by Andrii Shpek on 23.02.2026.
//

import SampleShared
import SwiftUI

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    var body: some View {
        ComposeView()
            .ignoresSafeArea(edges: .all)
            .ignoresSafeArea(.keyboard)  // Compose has own keyboard handler
    }
}

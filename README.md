# AI Builder - Android Native Studio

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![MinSdk](https://img.shields.io/badge/minSdk-26-green.svg)](https://developer.android.com)
[![TargetSdk](https://img.shields.io/badge/targetSdk-35-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

**AI Builder** is a modern, high-performance native Android application that turns natural language prompts into live, interactive web applications and games. Equipped with **High Thinking Mode**, multi-model AI routing (NVIDIA NIM & Google Gemini), a sandboxed Puter.js runtime environment, multi-file code editing, and full version history, AI Builder brings desktop-class AI code generation directly to mobile devices.

---

## 📸 Key Capabilities & Highlights

- 🧠 **Deep Reasoning Engine (High Thinking Mode)**:
  - Visual reasoning timeline displaying step-by-step architectural planning before code synthesis.
  - Dedicated thinking tab to inspect reasoning tokens, design decisions, and system constraints.

- ⚡ **Multi-Model Engine Selection**:
  - **NVIDIA Nemotron 3 Super (120B)** (`nvidia/nemotron-3-super-120b-a12b`): Advanced reasoning, deep planning, and high-quality code generation via NVIDIA NIM.
  - **Google Gemini 3.1 Pro** (`gemini-3.1-pro-preview`): Native structured reasoning with high thinking budget.
  - **Meta Llama 3.3 70B Instruct** (`meta/llama-3.3-70b-instruct`): Fast open-weights instruction model.
  - In-app engine switcher and custom endpoint / API key configuration dialog.

- 🌐 **Interactive Sandboxed Puter.js Preview**:
  - Live execution of generated HTML5, CSS3, and modern ES6 JavaScript.
  - Full support for Puter.js cloud storage, key-value data, hosting, and AI extensions.
  - Viewport switcher supporting **Mobile (375px)**, **Tablet (768px)**, and **Responsive Desktop (100%)** testing.

- 🛠️ **Developer Tooling & Diagnostics**:
  - **Code Inspector**: Formatted multi-file code viewer with copy and quick export.
  - **Live Console Logs**: In-app interception of JavaScript `console.log`, `warn`, and `error` messages.
  - **Version Control & Rollback**: Room-backed version snapshots; compare versions and revert with one tap.
  - **Quick Prompts**: Pre-configured task templates (Retro Synth Drum Machine, Pomodoro Matrix, Canvas Pong, Puter Cloud Notes).

---

## 🏗️ Architecture & Tech Stack

AI Builder is crafted strictly adhering to modern Android development best practices and Clean Architecture:

- **UI & Design**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3), dynamic theming, and edge-to-edge support.
- **State Management**: Android `ViewModel`, Kotlin `StateFlow`, and `asStateFlow()` reactive streams.
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + [OkHttp 3](https://square.github.io/okhttp/) with custom interceptors for OpenAI-compatible and Google Gemini REST endpoints.
- **Serialization**: [Moshi](https://github.com/square/moshi) with Kotlin reflection and codegen adapters.
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) powered by [KSP](https://kotlinlang.org/docs/ksp-overview.html) for instantaneous versioning and offline state storage.
- **Runtime Sandboxing**: Android `WebView` with hardware acceleration and bidirectional `JavascriptInterface` bridging.
- **Testing**: Comprehensive JUnit 4 + [Robolectric](https://robolectric.org/) testing suite covering UI components, ViewModel state transitions, and API response deserialization.

---

## 🚀 Getting Started & Installation

### Prerequisites

1. **Android Studio**: Android Studio Jellyfish (2023.3.1), Koala (2024.1.1), or newer.
2. **Java Development Kit (JDK)**: JDK 17 or JDK 21 configured as your Gradle JDK.
3. **Android SDK**:
   - `compileSdk = 35`
   - `minSdk = 26` (Android 8.0 Oreo or higher)
   - `targetSdk = 35` (Android 15)

### Clone & Open

```bash
git clone https://github.com/bageltrade/ai-builder-android.git
cd ai-builder-android
```

Open the cloned folder in Android Studio:
1. Select **File > Open...**
2. Choose the root directory of the repository.
3. Allow Gradle to sync dependencies automatically.

### Build and Run via Gradle CLI

To compile the debug build:
```bash
gradle assembleDebug
```

To install directly to a connected device or emulator:
```bash
gradle installDebug
```

To run the local JVM unit and Robolectric test suite:
```bash
gradle testDebugUnitTest
```

---

## 🔑 API Key Configuration

AI Builder allows setting your API credentials either in the app UI or via environment configuration:

### Option 1: In-App Settings Dialog (Recommended)
1. Open the app on your device or emulator.
2. Tap the **Settings (⚙️)** icon in the top app bar.
3. Enter your **NVIDIA API Key** (e.g. `nvapi-...`) and **Base URL** (`https://integrate.api.nvidia.com/v1`).
4. Optionally configure your **Google Gemini API Key**.
5. Tap **Save Settings**. Preferences are securely stored in private application storage.

### Option 2: Build Environment
You can configure default credentials in `.env`:
```properties
NVIDIA_API_KEY=your_nvidia_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here
```

---

## 📂 Project Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Main entry point & Compose root
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/                      # Retrofit services, Gemini & OpenAI data models
│   │   │   │   │   ├── local/                    # Room entities, DAOs, and AppDatabase
│   │   │   │   │   ├── model/                    # AiModel, PuterTemplates, and SupportedModels
│   │   │   │   │   └── repository/               # ProjectRepository (API routing & prompt engine)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── builder/                  # Main builder screens & header controls
│   │   │   │   │   ├── dialogs/                  # ModelSelectorDialog & SettingsDialog
│   │   │   │   │   ├── preview/                  # Sandboxed PuterWebView & Console log sheet
│   │   │   │   │   ├── prompt/                   # Prompt bar with model selector chips
│   │   │   │   │   ├── theme/                    # Material 3 ColorScheme & typography
│   │   │   │   │   └── viewmodel/                # BuilderViewModel & UI State
│   │   │   ├── res/                              # Adaptive icons, vectors, and string resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/example/                # Unit, ViewModel, UI, and Model test suites
│   └── build.gradle.kts                          # App-level Gradle build configuration
├── gradle/
│   └── libs.versions.toml                        # Version Catalog
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

---

## 🧪 Testing

The repository includes a comprehensive Robolectric and JVM unit test suite:

- `ModelIntegrationTest.kt`: Tests model definitions, serialization/deserialization of reasoning tokens, and multi-model routing.
- `ProjectRepositoryTest.kt`: Tests repository fallbacks, template loading, and output parsing (HTML/CSS/JS file blocks).
- `BuilderViewModelTest.kt`: Tests UI state transitions, tab switching, and console logging.
- `BuilderUiTest.kt`: Tests Compose UI dialogs, model cards, and interactive chips.

Run all tests with:
```bash
gradle :app:testDebugUnitTest
```

---

## 📄 License

```
Copyright 2026 AI Builder Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# 📝 Personal Note (KMP)

A simple **cross-platform notes app** built with **Kotlin Multiplatform** and **Jetpack Compose Multiplatform**.  
The app lets you create, edit, and view notes. It also supports **HTML content rendering** and **PDF viewing** across Android, Desktop, and Web (Wasm).

---

## 📂 Project Structure

```
Personal-Note-KMP/
 ├── composeApp/              # Main KMP module
 │   ├── androidMain/         # Android specific code
 │   ├── commonMain/          # Shared UI + business logic
 │   ├── jvmMain/             # Desktop (JVM) specific code
 │   ├── wasmJsMain/          # Web/Wasm specific code
 │   └── commonTest/          # Shared tests
 ├── gradle/                  # Gradle wrapper & versions catalog
 ├── kotlin-js-store/         # Web/Wasm build cache
 ├── build.gradle.kts         # Root build config
 ├── settings.gradle.kts      # Gradle settings
 └── README.md
```

---

## 🏗️ Architecture

- **Kotlin Multiplatform (KMP):**  
  Common code shared across Android, Desktop, and Web.  

- **Jetpack Compose Multiplatform:**  
  UI is written once and runs everywhere.  

- **MVVM Pattern:**  
  - `ViewModel` → Handles state and business logic  
  - `View` (Compose Screens) → Displays data & reacts to state  

- **Platform-specific implementations:**  
  - Android: `MainActivity.kt`, platform viewers for HTML/PDF  
  - Desktop: `main.kt`, uses PDFBox for PDFs  
  - Web: Wasm + HTML/CSS + Compose  

---

## 📚 Dependencies (Extra Used)

Besides default Compose/KMP setup, these extra libraries are included:

- **OkHttp (4.12.0):** For networking  
- **kotlinx-datetime (0.7.1):** Date & time utilities  
- **Compose Material Icons Extended (1.7.3):** Extra material icons  
- **Apache PDFBox (2.0.29):** PDF rendering on desktop  

---

## 🖱️ Handling HTML & PDF

- **HTML Viewer:**  
  Each platform (`androidMain`, `jvmMain`, `wasmJsMain`) has its own `HtmlView` implementation.  
  Click events inside HTML content trigger navigation or open external links.  

- **PDF Viewer:**  
  - **Android:** Uses native PDF viewing (via intent or embedded view).  
  - **Desktop:** Uses **PDFBox** to load and render PDFs in Compose.  
  - **Web (Wasm):** Displays PDFs via browser-supported viewer.  

---

## 🎥 Walkthrough  

https://github.com/user-attachments/assets/67a9fb26-1c09-415e-830c-a80c655efad0


https://github.com/user-attachments/assets/529365dc-f149-4113-bf92-c4cfa8de7a58


---

## 🚀 How to Run

### Android
1. Open the project in **Android Studio**  
2. Select `composeApp` → `androidMain`  
3. Run on emulator or device  

### Desktop (JVM)
```bash
./gradlew :composeApp:run
```

### Web (Wasm/JS)
```bash
./gradlew :composeApp:wasmJsBrowserRun
```

---

## ✅ Features
- Create, edit, delete notes  
- View notes in a clean Compose UI  
- Clickable HTML inside notes  
- Cross-platform PDF support  
- in future we can add sqdelight db 
---





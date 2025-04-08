# 🔄 Android ↔ Windows Chat App via WebSocket

A lightweight real-time chat system that allows **bi-directional messaging between an Android app and a Windows web interface** using WebSockets. Built for seamless communication across platforms on the same Wi-Fi network.

---

## 🚀 Features

- 📡 Real-time messaging between Android and browser
- 🔄 Automatic WebSocket reconnection on disconnect
- ✅ Connection status indicators
- 📜 Auto-scrolling chat views
- 📲 Built in **Kotlin (Android)** and **HTML/JavaScript (Web)**
- 🛠️ Lightweight WebSocket backend powered by Python (FastAPI)

---

## 📱 Android App

- Built in Kotlin using OkHttp WebSocket client
- Auto reconnect on disconnection
- UI updates status (`Connected`, `Disconnected`, `Reconnecting...`)
- Messages tagged as `Me:` and `Windows:` for clarity

### Android UI Features:
- `TextView` for chat messages
- `EditText` for input
- `Button` to send
- Colored status indication (green/red)
- Smooth message appending and scrolling

---

## 💻 Web App

- HTML, CSS, JavaScript
- Auto-scrolls on new messages
- Shows messages from Android and sent by user
- Handles connection events and auto-reconnects

---

## 🌐 WebSocket Server (FastAPI + Python)

- Lightweight backend for real-time communication
- Two endpoints:
  - `/ws/android` – for Android device
  - `/ws/windows` – for Web app
- Broadcasts messages between the two

> ⚠️ Both Android and Web must be on the same Wi-Fi network.

---

## 🛠️ Tech Stack

| Platform | Language     | Library/Tool       |
|----------|--------------|--------------------|
| Android  | Kotlin       | OkHttp WebSocket   |
| Web      | HTML/CSS/JS  | WebSocket API      |
| Backend  | Python       | FastAPI + WebSocket |
| Network  | Local Wi-Fi  | IPv4-based comm    |

---

## ⚙️ Setup Instructions

### 1. Clone the Repo 
```bash / cmd
git clone https://github.com/NaveenMiskin/NotifyMe.git
cd NotifyMe
```
### 2. Run WebSocket Server (Python + FastAPI)
```
pip install fastapi uvicorn
uvicorn server:app --host 0.0.0.0 --port 8000
```
###3. Android Setup
```Open the Android project in Android Studio, Replace IP in MainActivity.kt:

private val serverUrl = "ws://<your-pc-ip>:8000/ws/android"

Run the app on a physical device connected to same Wi-Fi
```
4. Web App Setup
``` Open index.html, Replace server IP:

const serverIp = "192.168.xx.xx";
Open in browser (double click or run via local server)
```

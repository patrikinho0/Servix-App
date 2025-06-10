<h1 align="center">Servix - On-Demand Service Marketplace 🚀</h1>

<p align="center">
  <img src="https://github.com/user-attachments/assets/a829717b-2993-48fd-bf77-8e97d9043ae8" alt="SERVIX NO-SLOGAN">
</p>

<p align="center">
  <i>Connecting local professionals with people who need services — fast, secure, and reliable.</i>
</p>

---

## 🔥 Overview

**Servix** is your go-to mobile app for instantly linking individuals with nearby professionals for a broad range of tasks — from moving furniture and house repairs to hiring certified experts.

---

## ✨ Features

Built with a strong focus on **usability**, **trust**, and **scalability**.

### ⚙️ Core Functionality
- 🔐 **Firebase Auth**: Email/password authentication with seamless login/register flow.
- 📝 **Service Listings**: Create, edit, and remove service posts with ease.
- 👤 **Expert Profiles**: Professionals can showcase their skills, ratings, and experience.
- 🔎 **Search & Filter**: Find exactly what you need by category, location, or rating.
- ❤️ **Liked Services**: Like service posts to boost visibility and save them for later.
- ❌ **Account Deletion**: Full user control to delete their data at any time.

### 🚧 Planned Features
- ⭐ **Ratings & Reviews** *(Planned)*: Leave feedback after service completion.
- 🚩 **Reporting System** *(Planned)*: Flag inappropriate behavior or content.
- 🔔 **Push Notifications** *(Coming Soon)*: Stay informed via Firebase Cloud Messaging.
- 🗺️ **Google Maps Integration** *(Planned)*: Better location handling and navigation.

---

## 💻 Tech Stack

Built with modern tools for a fast and responsive user experience:

- 🛠️ **Language**: Kotlin (Android)
- 🔥 **Backend**: Firebase
  - **Authentication**: Secure user sessions
  - **Firestore**: Real-time NoSQL cloud database
  - **Storage**: Image uploads and retrieval

---

## 🗄️ Firestore Database Structure

| 📂 Collection | 🔑 Key Fields | 🧠 Description |
|--------------|---------------|----------------|
| `Users` | `email`, `name`, `profilePictureUrl`, `likedServices`, `uid`, `role` | Stores user data, profile info, and liked services |
| `Services` | `title`, `description`, `category`, `images`, `likes`, `uid`, `location`, `comments`, `date` | All service listings with related info |
| `Experts` | `name`, `expertise`, `rating`, `numberOfRatings`, `uid`, `profilePictureUrl`, `description`, `date` | Expert profiles with detailed qualifications |

---

## 📊 Data Model Diagram

Visualizing the structure:
<p align="center">
  <img src="https://github.com/user-attachments/assets/4c3d1512-81bc-4796-976e-355e7a4116fd" alt="Servix_UML">
</p>

---

## 🛠️ Getting Started

Want to try out Servix locally? Follow these steps.

### ✅ Prerequisites
- Android Studio (latest)
- A Firebase Project with:
  - Email/Password Auth
  - Firestore enabled

### 🚀 Setup
```bash
git clone https://github.com/patrikinho0/Servix-App.git
```
```bash
cd Servix-App
```
- Open in Android Studio
- Add your google-services.json to /app
- Sync Gradle and Run the project on a device/emulator

## 📸 Screenshots (Coming Soon)
- Login Screen
- Service Feed
- Expert Profile

## 🤝 Contributing
We ❤️ contributions! Here’s how to jump in:

- Fork the project
```bash
git checkout -b feature/your-feature-name
```

- Make your changes
```bash
git commit -m "feat: your feature summary"
```

- Push to your fork
```bash
git push origin feature/your-feature-name
```

- Open a Pull Request
Please follow Conventional Commits for clarity.

## ❓ Questions or Feedback?
- 📧 Reach out to me at [patryk61222@gmail.com]
- 🐛 Or open an issue

<p align="center"> Made with ❤️ by <b>patrikinho</b> </p>

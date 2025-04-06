# Servix - On-Demand Service Marketplace 🚀

![Servix Logo](https://github.com/user-attachments/assets/13af8dfc-801f-4df1-b2a9-726d01103cef)

Servix connects users with local professionals for all kinds of services! Whether you need help moving furniture, fixing something around the house, or hiring a skilled expert, Servix makes it easy to find and hire someone nearby.

---

## Features 🌟

### Core Functionality 🛠️
- **User Authentication**  
  Secure sign-up/login with **Firebase Authentication** (Email/Password).
  
- **Service Listings**  
  Post, edit, or delete your service requests (e.g., "Need a plumber").

- **Expert Profiles**  
  Experts can create detailed profiles showcasing their skills, expertise, and ratings.

- **Search & Filter**  
  Quickly find services or experts by **category**, **location**, or **rating**.

- **Reviews & Ratings**  
  After service completion, rate and review experts to build trust within the community.

- **Liked Services**  
  Boost visibility by "liking" service posts.

### Additional Features 🚀
- **Reporting System**  
  Flag inappropriate content or users to maintain a safe platform.

- **Account Deletion**  
  Permanently delete accounts with ease.

- **Notifications** *(Coming Soon)*  
  Stay up to date with Firebase Cloud Messaging (FCM) alerts.

- **Google Maps Integration** *(Planned)*  
  Use maps for location-based searches and navigation.

---

## Tech Stack 🖥️

- **Programming Language**: Kotlin  
- **Backend**: Firebase  
  - Authentication  
  - Firestore (NoSQL database)  
  - Storage (for user and service images)  

---

## Firestore Database Structure 🗂️

| Collection       | Key Fields                                                                 |
|------------------|---------------------------------------------------------------------------|
| **Users**        | `id`, `name`, `email`, `profilePictureUrl`, `role` ("user" or "expert")   |
| **Experts**      | `userId`, `expertise`, `rating`, `profileDescription`                     |
| **Announcements**| `userId`, `title`, `description`, `location`, `status`, `imageUrls`       |
| **Reviews**      | `announcementId`, `expertId`, `reviewerId`, `rating`, `comment`          |
| **Notifications**| `recipientId`, `message`, `timestamp` *(Planned)*                        |

---

## UML Diagram (Data Model) 📊  
![SERVIX-UML-DIAGRAM-PNG](https://github.com/user-attachments/assets/1af5f6ae-49a2-4b37-94e6-ac6863d9d0bc)

---

## Installation ⚙️

### Prerequisites 📋
- Android Studio (latest version)
- Firebase project with **Authentication** and **Firestore** enabled.

### Setup Steps 📝
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/servix-android.git

2. Open the project in Android Studio.
3. Add your **google-services.json** file (from Firebase) to **app/**.
4. Build and run the app on an emulator or device.

---

### **Part 4: Screenshots & Contributing**

## Screenshots 📸 *(Coming soon)*
| Login Screen | Service Feed | Expert Profile |
|--------------|--------------|----------------|
| ![Login](https://via.placeholder.com/200) | ![Feed](https://via.placeholder.com/200) | ![Profile](https://via.placeholder.com/200) |

---

## Contributing 🤝
1. Fork the project.  
2. Create a branch (`git checkout -b feature/your-feature`).  
3. Commit changes (`git commit -m 'Add some feature'`).  
4. Push to the branch (`git push origin feature/your-feature`).  
5. Open a Pull Request.  

---

**Questions?** 🤔
Contact [patryk61222@gmail.com] or open an issue in the repository.

<br><br>
<p align="center">Made by patrikinho with ❤️</p>

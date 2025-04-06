# Servix - On-Demand Service Marketplace

![Servix Logo](https://github.com/user-attachments/assets/13af8dfc-801f-4df1-b2a9-726d01103cef)

Servix is an Android application that connects users with local experts for various services. Whether you need help moving furniture, repairing appliances, or hiring a professional, Servix allows users to post service requests or browse expert profiles directly. Experts can showcase their skills and get hired through the platform.

---

## Features

### Core Functionality
- **User Authentication**  
  Secure sign-up/login using **Firebase Authentication** (Email/Password).
- **Service Listings**  
  Users can post, edit, or delete service requests (e.g., "Need a plumber").
- **Expert Profiles**  
  Experts create profiles with descriptions, expertise, and ratings.
- **Search & Filter**  
  Find services/experts by **category**, **location**, or **rating**.
- **Reviews & Ratings**  
  Rate and review completed services to build trust.
- **Liked Services**  
  Users can "like" posts to boost visibility.

### Additional Features
- **Reporting System**  
  Flag inappropriate content or users.
- **Account Deletion**  
  Users can permanently delete accounts.
- **Notifications** *(Planned)*  
  Firebase Cloud Messaging (FCM) for alerts.
- **Google Maps Integration** *(Planned)*  
  Location-based searches and navigation.

---

## Tech Stack
- **Language**: Kotlin  
- **Backend**: Firebase  
  - Authentication  
  - Firestore (NoSQL database)  
  - Storage (for user/announcement images)  

---

## Firestore Database Structure
| Collection       | Key Fields                                                                 |
|------------------|---------------------------------------------------------------------------|
| **Users**        | `id`, `name`, `email`, `profilePictureUrl`, `role` ("user" or "expert")   |
| **Experts**      | `userId`, `expertise`, `rating`, `profileDescription`                     |
| **Announcements**| `userId`, `title`, `description`, `location`, `status`, `imageUrls`       |
| **Reviews**      | `announcementId`, `expertId`, `reviewerId`, `rating`, `comment`          |
| **Notifications**| `recipientId`, `message`, `timestamp` *(Planned)*                        |

---

## UML Diagram (Data Model)
![SERVIX-UML-DIAGRAM-PNG](https://github.com/user-attachments/assets/1af5f6ae-49a2-4b37-94e6-ac6863d9d0bc)

---

## Installation
### Prerequisites
- Android Studio (latest version)
- Firebase project with **Authentication** and **Firestore** enabled.

### Steps
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/servix-android.git
2. Open the project in Android Studio.
3. Add your **google-services.json** file (from Firebase) to **app/**.
4. Build and run the app on an emulator or device.

---

### **Part 4: Screenshots & Contributing**

## Screenshots *(To be implemented)*
| Login Screen | Service Feed | Expert Profile |
|--------------|--------------|----------------|
| ![Login](https://via.placeholder.com/200) | ![Feed](https://via.placeholder.com/200) | ![Profile](https://via.placeholder.com/200) |

---

## Contributing
1. Fork the project.  
2. Create a branch (`git checkout -b feature/your-feature`).  
3. Commit changes (`git commit -m 'Add some feature'`).  
4. Push to the branch (`git push origin feature/your-feature`).  
5. Open a Pull Request.  

---

**Questions?**  
Contact [patryk61222@gmail.com] or open an issue in the repository.

<br><br>
<p align="center">Made by patrikinho with ❤️</p>
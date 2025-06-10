# Servix - On-Demand Service Marketplace 🚀

![Servix Logo](https://github.com/user-attachments/assets/13af8dfc-801f-4df1-b2a9-726d01104cef)

**Servix** is your go-to mobile application for seamlessly connecting individuals with local professionals for a wide array of services. Whether you're looking for help moving furniture, need a quick fix around the house, or require a highly skilled expert for a specialized task, Servix simplifies the process of finding and hiring reliable assistance right in your neighborhood.

---

## Features ✨

Servix is built with a focus on ease of use, security, and community trust.

### Core Functionality 🛠️
* **User Authentication**: Enjoy secure and straightforward sign-up and login experiences powered by **Firebase Authentication** (Email/Password).
* **Service Listings**: Effortlessly **post, edit, or delete** your service requests, such as "Need a plumber" or "Help with garden maintenance."
* **Expert Profiles**: Professionals can create comprehensive profiles, detailing their **skills, areas of expertise, and accumulated ratings**, helping users make informed decisions.
* **Search & Filter**: Quickly discover the perfect service or expert using intuitive search and filter options based on **category, location, or rating**.
* **Liked Services**: Boost the visibility of service posts by "liking" them, ensuring popular requests get the attention they deserve.
* **Account Deletion**: Users have full control with a simple option to permanently delete their accounts.

### Planned Features 🚀
* **Reviews & Ratings** *(Planned)*: After service completion, users will be able to **rate and review experts**, fostering transparency and building a trustworthy community.
* **Reporting System** *(Planned)*: A system to maintain a safe and respectful platform by easily flagging inappropriate content or user behavior.
* **Notifications** *(Coming Soon)*: Stay updated with real-time alerts for new requests, messages, and service updates via Firebase Cloud Messaging (FCM).
* **Google Maps Integration** *(Planned)*: Enhance location-based searches and provide seamless navigation for service providers and users.

---

## Tech Stack 💻

Servix is developed using modern and robust technologies to ensure a smooth and scalable experience.

* **Programming Language**: **Kotlin** – A concise, safe, and interoperable language for Android development.
* **Backend**: **Firebase** – Google's comprehensive mobile development platform, providing:
    * **Authentication**: Secure user identity management.
    * **Firestore**: A flexible, scalable NoSQL cloud database for storing and syncing data.
    * **Storage**: For reliable storage and retrieval of user and service-related images.

---

## Firestore Database Structure 🗄️

Understanding the underlying data model is crucial for contributors. Other needed collections will be added in the future. 
Here's how Servix currently organizes its data in Firestore:

| Collection        | Key Fields                                                                                             | Description                                                                                             |
| :---------------- | :----------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------ |
| `Users`           | `email`, `likedServices` (array of service UIDs), `name`, `profilePictureUrl`, `role`, `uid`           | Stores essential user information, including their email, display name, profile picture, role (`user`), and a list of liked services. |
| `Services`        | `comments` (array), `category`, `date` (timestamp), `description`, `images` (array of URLs), `likes`, `location`, `title`, `uid` (of poster) | Represents individual service requests or announcements. Includes details like content, associated images, location, and tracking for likes and comments. |
| `Experts`         | `category` (e.g., "expert"), `date` (timestamp), `description`, `expertise`, `name`, `numberOfRatings`, `profilePictureUrl`, `rating`, `uid` | Contains specific details for users who have registered as experts, including their specialized skills, self-description, and current rating. |

---

## Data Model (UML Diagram) 📊

For a visual representation of the Servix data relationships, refer to the UML diagram below:

![SERVIX-UML-DIAGRAM-PNG](https://github.com/user-attachments/assets/1af5f6ae-49a2-4b37-94e6-ac6863d3d0bc)

---

## Installation Guide ⚙️

Ready to get Servix up and running on your local machine? Follow these simple steps.

### Prerequisites 📋
Before you begin, ensure you have the following:

* **Android Studio (latest version)**: Download and install the latest stable release.
* **Firebase Project**: A configured Firebase project with **Authentication** (Email/Password provider enabled) and **Firestore** database activated.

### Setup Steps 📝
1.  **Clone the repository**:
    ```sh
    git clone [https://github.com/yourusername/servix-android.git](https://github.com/yourusername/servix-android.git)
    cd servix-android
    ```
2.  **Open in Android Studio**:
    Launch Android Studio and open the cloned `servix-android` project.
3.  **Add `google-services.json`**:
    Download your `google-services.json` file from your Firebase project settings and place it directly into the **`app/`** directory of your cloned project. This file is crucial for connecting your app to Firebase.
4.  **Build and Run**:
    Sync your Gradle files, then build and run the application on an Android emulator or a physical device.

---

## Screenshots 📸

*(Coming soon!)* Here's a glimpse of the Servix user interface:

| Login Screen | Service Feed | Expert Profile |
| :----------- | :----------- | :------------- |
| ![Login](https://via.placeholder.com/200/0000FF/FFFFFF?text=Login+Screen) | ![Feed](https://via.placeholder.com/200/008000/FFFFFF?text=Service+Feed) | ![Profile](https://via.placeholder.com/200/FF0000/FFFFFF?text=Expert+Profile) |

---

## Contributing 🤝

We welcome contributions to Servix! If you're interested in making this project even better, please follow these guidelines:

1.  **Fork the project**.
2.  **Create a new branch**:
    ```sh
    git checkout -b feature/your-awesome-feature
    ```
3.  **Commit your changes**:
    ```sh
    git commit -m 'feat: Add a concise description of your new feature'
    ```
    *(Tip: Use conventional commits for clear history, e.g., `feat:`, `fix:`, `docs:`, `chore:`)*
4.  **Push to your branch**:
    ```sh
    git push origin feature/your-awesome-feature
    ```
5.  **Open a Pull Request**:
    Submit a detailed Pull Request to the `main` branch of this repository. Please describe your changes thoroughly.

---

**Questions or Feedback?** 🤔
Feel free to reach out to [patryk61222@gmail.com] or open an issue directly in this repository. Your input is highly valued!

<br><br>
<p align="center">Made by patrikinho with ❤️</p>
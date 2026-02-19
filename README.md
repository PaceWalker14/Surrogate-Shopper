# 🛒 Surrogate-Shopper

Surrogate-Shopper is an Android-based community support application designed to bridge the gap between vulnerable individuals and local volunteers. Whether it's grocery shopping, running errands, or general assistance, the app provides a centralized platform for community-driven aid.

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)
![Backend](https://img.shields.io/badge/Backend-PHP%20%2F%20MySQL-777BB4?style=flat&logo=php&logoColor=white)
![Status](https://img.shields.io/badge/Status-Coursework-blue)

## 📺 Project Walkthrough
For a detailed look at the app's features and implementation:
* **Video Demo:** [Watch on YouTube](https://youtu.be/GE-FuoZRA1I)
* **Technical Documentation:** [View PDF Report](https://github.com/user-attachments/files/20646432/DBF.Surrogate.Shopper.pdf)

---

## 🌟 Key Features

* **User Roles:** Distinct interfaces for "Requesters" and "Volunteers."
* **Request Management:** Users can post specific errands (e.g., grocery lists) that volunteers can browse and accept.
* **Real-time Messaging:** Integrated chat system for volunteers and requesters to coordinate details safely.
* **Secure Authentication:** User registration and login system powered by PHP/MySQL.
* **Location-Based Support:** Designed to facilitate help within local community clusters.

---

## 🏗️ Technical Architecture

The project follows a **Client-Server architecture** utilizing a LAMP stack:

### Frontend
* **Framework:** Android Studio (Java/XML).
* **Communication:** Handles data retrieval and storage via HTTP requests to the backend API.

### Backend
* **Database:** MySQL (Hosted on WITS University LAMP server).
* **API Layer:** Custom PHP scripts that process SQL queries to handle user accounts, help requests, and messaging history.

---

## 🚀 Installation & Usage

### Option 1: Quick Install (Recommended)
1. Download the `app-release.apk` file from this repository.
2. Transfer the file to your Android device.
3. Open the file to install (ensure "Install from Unknown Sources" is enabled in your settings).

### Option 2: Build from Source
1. Clone the repository.
2. Open the `SurrogateShopper` folder in **Android Studio**.
3. Build and run the project on an emulator or physical device.

> [!CAUTION]
> **Server Dependency:** This application relies on a university-hosted LAMP server. If the WITS server is offline, login and data-fetching features will be unavailable. An active internet connection is required.

---

## 🎓 About the Project
This app was developed as university coursework to explore the intersection of mobile technology and social problem-solving. It demonstrates the use of mobile UI/UX design, database management, and network communication to foster community assistance.

---

## 📝 License
This project is for educational purposes. All rights reserved.

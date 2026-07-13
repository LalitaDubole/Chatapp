# Java Socket Programming Chat App

A real-time, multi-user desktop chat application built with Java Socket Programming, Multithreading, and MySQL. It features a Java Swing GUI, a multithreaded server capable of handling multiple simultaneous clients, secure login/registration, persistent chat history, and a live online users list.

---

## 🚀 Key Features

- **Real-time Messaging**: Instant message broadcast between all connected clients over a TCP socket connection.

- **Multithreaded Server**: Every connected client is handled on its own dedicated thread (`ClientHandler`), allowing multiple users to chat simultaneously without blocking each other.

- **User Authentication**: Secure login and registration system backed by a MySQL database.

- **Persistent Chat History**: The last 50 messages are automatically loaded and displayed when a user logs in.

- **Live Online Users List**: See who's currently online in real time, updated instantly as users join or leave.

- **Event Logging**: User join and leave events are logged to the database for tracking activity.

---

## 🛠️ Tech Stack

- **Language**: Java
- **GUI**: Java Swing
- **Networking**: Socket Programming (TCP)
- **Concurrency**: Multithreading
- **Database**: MySQL
- **DB Connectivity**: JDBC (MySQL Connector/J 9.7.0)

---

## 📁 Project Structure

```
Chatapp/
├── src/
│   ├── client/
│   │   ├── Client.java              # Client-side socket connection logic
│   │   └── MessageListener.java     # Listens for incoming messages
│   ├── server/
│   │   ├── Server.java              # Accepts and manages client connections
│   │   └── ClientHandler.java       # Handles each client on a separate thread
│   ├── gui/
│   │   ├── LoginFrame.java          # Login screen
│   │   └── ChatFrame.java           # Main chat window
│   ├── db/
│   │   └── DBConnection.java        # MySQL connection setup
│   └── dao/
│       ├── UserDAO.java             # User-related database operations
│       └── MessageDAO.java          # Message-related database operations
├── sql/
│   └── schema.sql                   # Database schema
├── lib/
│   └── mysql-connector-j-9.7.0.jar  # MySQL JDBC driver
├── run_server.bat
├── run_client.bat
└── build.bat
```

---

## ⚙️ Getting Started

### Prerequisites
- Java JDK installed
- MySQL Server (or XAMPP) running

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/LalitaDubole/Chatapp.git
cd Chatapp
```

**2. Set up the database**
```bash
mysql -u root -p < sql/schema.sql
```

**3. Configure the connection**

Update the credentials in `src/db/DBConnection.java` to match your MySQL setup.

**4. Build the project**
```bash
build.bat
```

**5. Start the server**
```bash
run_server.bat
```

**6. Start a client** (repeat in a new terminal for each user)
```bash
run_client.bat
```

## 🧩 How It Works

The server starts and listens for incoming client connections on a fixed port. Every client that connects gets its own `ClientHandler` thread, which is what allows multiple people to chat at the same time without blocking each other. The client connects via a socket and sends/receives messages using the protocol above, while all messages and user data are persisted in MySQL through the DAO layer.

---

## 👩‍💻 Author

**Lalita Dubole**
   MCA Student 

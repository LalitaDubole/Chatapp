# 💬 Java Socket Programming Chat App

A desktop chat application built with **Java Swing GUI**, **Socket Programming**, and **Multithreading**, using **JDBC + MySQL** for persistent storage of users and messages.

## 🚀 Features

- Real-time messaging between client and server using TCP sockets
- Multithreaded server (`ClientHandler`) to support multiple simultaneous clients
- User authentication (Login) backed by MySQL database
- Message history stored and retrieved via JDBC
- Simple, intuitive Swing-based desktop GUI

## 🛠️ Tech Stack

- **Language:** Java
- **GUI:** Java Swing
- **Networking:** Java Socket Programming (TCP)
- **Concurrency:** Multithreading
- **Database:** MySQL
- **Database Connectivity:** JDBC (MySQL Connector/J 9.7.0)

## 📁 Project Structure
Chatapp/
├── src/
│   ├── client/
│   │   ├── Client.java              # Client-side socket connection logic
│   │   └── MessageListener.java     # Listens for incoming messages
│   ├── server/
│   │   ├── Server.java              # Server that accepts client connections
│   │   └── ClientHandler.java       # Handles each client on a separate thread
│   ├── gui/
│   │   ├── LoginFrame.java          # Login screen
│   │   └── ChatFrame.java           # Main chat window
│   ├── db/
│   │   └── DBConnection.java        # MySQL database connection setup
│   └── dao/
│       ├── UserDAO.java             # User-related database operations
│       └── MessageDAO.java          # Message-related database operations
├── sql/
│   └── schema.sql                   # Database schema
├── lib/
│   └── mysql-connector-j-9.7.0.jar  # MySQL JDBC driver
├── run_server.bat                   # Script to run the server
├── run_client.bat                   # Script to run the client
└── build.bat                        # Build script
## ⚙️ Setup & Installation

### Prerequisites
- Java JDK installed
- MySQL Server installed and running

### Steps

1. **Clone the repository**
```bash
   git clone https://github.com/LalitaDubole/Chatapp.git
   cd Chatapp
```

2. **Set up the database**
    - Open MySQL and run the schema file:
```bash
   mysql -u root -p < sql/schema.sql
```

3. **Configure database connection**
    - Update credentials (URL, username, password) in `src/db/DBConnection.java` as per your MySQL setup.

4. **Build the project**
```bash
   build.bat
```

5. **Run the server**
```bash
   run_server.bat
```

6. **Run the client** (in a new terminal, repeat for multiple clients)
```bash
   run_client.bat
```

## 🧩 How It Works

1. The **Server** starts and listens for incoming client connections on a designated port.
2. Each connecting client is handled by a new **ClientHandler** thread, enabling concurrent multi-user chat.
3. The **Client** app connects to the server via sockets and sends/receives messages in real time.
4. User credentials and chat messages are persisted in **MySQL** through the DAO layer.

## 👩‍💻 Author

**Lalita Dubole**  
MCA Student 

## 📄 License

This project is open source and available for educational purposes.
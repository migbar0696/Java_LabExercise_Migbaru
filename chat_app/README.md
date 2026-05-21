# Java Swing Chat Application

Step 1: Project setup.

## Project structure

- src/com/chatapp/Main.java

## How to compile and run

1. Open a terminal in `c:\Users\hp\Desktop\chat_app`
2. Compile:
   `javac -d out src/com/chatapp/Main.java`
3. Run:
   `java -cp out com.chatapp.Main`

## Notes

This is the initial setup for the chat application. After verification, we will add the database, login, registration, and socket modules step by step.

## Step 2: Database setup

1. Open MySQL and run the SQL in `db/schema.sql`.
2. The script creates `chat_app_db` plus `users` and `messages` tables.
3. If you use custom credentials, note them for the JDBC connection step.

Common commands:

- Login to MySQL:
  `mysql -u root -p`
- Run script:
  `source C:/Users/hp/Desktop/chat_app/db/schema.sql`

## Step 3: JDBC connection

1. Download the MySQL Connector/J JAR and place it in `lib/`.
2. Update `src/com/chatapp/config/DBConfig.java` with your MySQL username and password.
3. Compile with the connector JAR on the classpath:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java`
4. Run the test:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.DatabaseTest`

What should happen:

- The test prints `Database connection OK.` if JDBC connects successfully.
- If the connector JAR is missing, you will see a driver not found error.

Common errors and fixes:

- `ClassNotFoundException: com.mysql.cj.jdbc.Driver` — ensure the MySQL Connector/J JAR is in `lib/` and on the classpath.
- `Access denied for user` — update `DBConfig.USER` and `DBConfig.PASSWORD` with valid MySQL credentials.
- `Unknown database 'chat_app_db'` — run the SQL script in `db/schema.sql` first.

## Step 4: Registration form

1. Open `src/com/chatapp/ui/RegistrationForm.java`.
2. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java`
3. Run the registration form:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.ui.RegistrationForm`

What should happen:

- A registration window opens.
- You can enter username, password, confirm password, display name.
- Successful registration displays a success message and closes the window.

How to verify:

- After registering, check the `users` table in MySQL.
- Use: `SELECT username, display_name FROM users;`
- Your new username should appear in the results.

## Step 5: Login form

1. Open `src/com/chatapp/ui/LoginForm.java`.
2. Compile with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java`
3. Run the login window:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.ui.LoginForm`

What should happen:

- A login window opens.
- You can enter username and password.
- Clicking **Login** authenticates the user.
- Clicking **Register** opens the registration form.

How to verify:

- If credentials are correct, a success message appears.
- If credentials are incorrect, an error is shown.
- Use `SELECT username, display_name FROM users;` in MySQL to confirm the account exists.

## Step 6: GUI navigation

1. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
2. Run the application start screen:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`

What should happen:

- The login window opens.
- After successful login, the chat window opens.
- The chat window shows:
  - welcome header
  - online users list
  - chat display area
  - message input field
  - send button and attach image button
  - logout button

How to verify:

- Login with a valid registered account.
- The login window should close and the chat window should appear.
- Clicking **Logout** should return you to the login screen.

## Step 7: Socket server

1. Create `src/com/chatapp/socket/ChatServer.java` and `src/com/chatapp/socket/ClientHandler.java`.
2. Compile the server with:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/socket/*.java`
3. Run the server:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.socket.ChatServer`

What should happen:

- The terminal prints `Chat server started on port 5000`.
- The server waits for client connections.

How to verify:

- If the server starts without exception, the socket module is ready.
- In later steps, client windows will connect to this server.

## Step 8: Client connection

1. Ensure the chat server is running: `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.socket.ChatServer`
2. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
3. Run the application:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`

What should happen:

- The login window opens.
- After successful login, the chat window opens and connects to the server.
- The chat window shows `Connected to chat server.` in the chat area.
- Messages sent in this window are transmitted to the server and displayed back in chat.

How to verify:

- Start the server first.
- Run the client and log in.
- The chat area should display the connection message.
- Sending a message should display it in the chat area.

## Step 9: Real-time messaging

1. Ensure the chat server is running.
2. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
3. Run the application:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`

What should happen:

- The chat window connects to the server and adds `Connected to chat server.` to the chat area.
- Send a text message using the message box and **Send** button.
- The message appears immediately in the chat area.
- If another client is connected, the message is broadcast to the other client(s) as well.

How to verify:

- Start two client windows and log in with separate accounts.
- Send a message from one client.
- The other client should receive the same message in real time.
- Real-time updates confirm the messaging layer works.

## Step 10: Multiple clients

1. Run the chat server once. Do not start it twice if it is already running.
2. Open two separate terminals.
3. In the first terminal, run the server:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.socket.ChatServer`
4. In each additional terminal, run the client:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`

What should happen:

- Both clients connect to the same server.
- Each client can log in with a different registered account.
- When one client sends a message, the other client receives it.
- The server can serve multiple connections concurrently.

How to verify:

- Start the server once.
- Run two instances of `com.chatapp.Main`.
- Log in with different usernames.
- Send a message from one client and confirm it appears in both chat windows.
- If the server reports `Address already in use: bind`, that means a server instance is already running and you should use the existing one.

## Step 11: Store messages in database

1. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
2. Run the server and the client:
   - `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.socket.ChatServer`
   - `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`
3. Use two clients to send messages between accounts.

What should happen:

- Each sent message is saved in the `messages` table in MySQL.
- Broadcast messages still appear in the chat windows.

How to verify:

- In MySQL, run:
  `USE chat_app_db;`
  `SELECT sender_id, message_text, sent_at FROM messages ORDER BY sent_at DESC LIMIT 10;`
- Confirm the messages you sent are stored.

## Step 12: Image upload/send

1. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
2. Run the server and the client:
   - `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.socket.ChatServer`
   - `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`
3. In the chat window, click **Attach Image** and choose an image file.

What should happen:

- The image is copied into the local `uploads/` folder.
- The chat window sends a message with image information.
- The message is saved in MySQL with the image path.
- The chat display shows the image filename in the message.

How to verify:

- Send an image from the chat window.
- In MySQL, run:
  `USE chat_app_db;`
  `SELECT sender_id, message_text, image_path, sent_at FROM messages WHERE image_path IS NOT NULL ORDER BY sent_at DESC LIMIT 10;`
- Confirm the uploaded image path is stored.

## Step 13: UI improvements

1. Compile all source files with the connector JAR:
   `javac -d out -cp lib/mysql-connector-j-9.7.0.jar src/com/chatapp/*.java src/com/chatapp/db/*.java src/com/chatapp/config/*.java src/com/chatapp/model/*.java src/com/chatapp/dao/*.java src/com/chatapp/ui/*.java src/com/chatapp/service/*.java src/com/chatapp/util/*.java src/com/chatapp/socket/*.java`
2. Run the application:
   `java -cp "out;lib/mysql-connector-j-9.7.0.jar" com.chatapp.Main`

What should happen:

- The login form and registration screen use a modern Nimbus theme.
- Buttons are larger and easier to click.
- The chat window shows a styled header and cleaner text area.
- Pressing Enter sends messages in the chat window.

How to verify:

- Open the app and observe the updated visual style.
- The login and registration forms should look more polished.
- In the chat window, type a message and press Enter to send.
- The app should still behave as before with socket messaging and database storage.

# ASESE Integrated Management System (PepeLink)

Desktop integrated management system built in Java (Swing) to support a construction company workflow. The application provides role-based access (ADMIN/USER), project and operational data management backed by MySQL, and integrations for FTP file management and Gmail (SMTP/IMAP).

## Key features

- **Authentication and roles**
  - Login/logout with role-based dashboards (**ADMIN** and **USER**).
- **Project management (CRUD)**
  - Manage **Projects**, **Stages**, **Deliveries**, **Permissions**, **Contractors**, **Crew**, **Users**, **Whitelist**, and **Logs**.
  - User mode restricts visibility to projects assigned via `project_access`.
- **FTP module**
  - Browse and preview files on an FTP server (connects using the logged-in user’s FTP username derived from email).
- **Email module (Gmail)**
  - Send email via SMTP (with optional attachments).
  - Read/mark/delete messages via IMAP (embedded inbox view).

## Tech stack

- **Language/runtime**: Java (JDK **25**)
- **Build tool**: Maven
- **UI**: Swing
- **Database**: MySQL (via **MySQL Connector/J**) + schema bootstrap via **Spring JDBC ScriptUtils**
- **FTP**: Apache Commons Net
- **Email**: JavaMail (`javax.mail`)

## Project structure

- `proyectoASESE/`
  - `src/main/java/`
    - `ui/` — Swing views and controllers
    - `db/` — repositories and DB bootstrap (`Db`)
    - `entities/` — domain entities (User, Project, Stage, etc.)
    - `ftp/` — FTP service and helpers
    - `smtp/` — SMTP/IMAP services
    - `Main.java` — application entry point
  - `src/main/resources/db/`
    - `schema.sql` — database schema
    - `data.sql` — optional sample data

## Prerequisites

- **JDK 25** (required by `pom.xml` compiler settings)
- **Maven 3.9+**
- **MySQL server** reachable from your machine
  - The application uses a JDBC connection defined in `proyectoASESE/src/main/java/db/Db.java`.
- Optional:
  - **FTP server** reachable from your machine (configured in `proyectoASESE/src/main/java/ftp/FtpService.java`)
  - **Gmail SMTP/IMAP** access (requires an app password/token stored per user in the database)

## Configuration

### Database (MySQL)

Database connectivity is currently configured as constants in:

- `proyectoASESE/src/main/java/db/Db.java`

Update these values to match your environment:

- `DATABASE_CONNECTION_STRING` (host/port/database)
- `DATABASE_USER`
- `DATABASE_PASSWORD`

On first connection, the application executes `src/main/resources/db/schema.sql` automatically (via `ScriptUtils.executeSqlScript`).

**Optional sample data**

- `src/main/resources/db/data.sql` contains sample users/projects/etc.
- Seeding is **disabled by default** (see `Db.getConnection()` where `insertData()` is commented out).
- If you choose to use `data.sql`, review and sanitize it first (it may contain development/test-only values).

### FTP

FTP is configured in:

- `proyectoASESE/src/main/java/ftp/FtpService.java` (`FTP_ADDRESS`, `FTP_PORT`)

The app logs into FTP using:

- **Username**: derived from the logged-in user’s email (everything before `@`)
- **Password**: the same password used for app login

### Gmail (SMTP/IMAP)

Email credentials are taken from the logged-in user record:

- **Email**: `users.email`
- **Token**: `users.email_token` (used as the SMTP/IMAP password/token)

Gmail typically requires an **App Password** (and IMAP enabled) for SMTP/IMAP access.

## Build and run

All commands below are run from the Maven module directory:

```bash
cd proyectoASESE
```

### Build (without tests)

Use this when MySQL is not available locally (tests require a DB connection):

```bash
mvn -DskipTests package
```

### Run the application

- **Recommended (IDE)**: open `proyectoASESE` as a Maven project and run `Main` (`src/main/java/Main.java`).
- **Command line (Maven Exec Plugin)**:

```bash
mvn -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=Main
```

### Run tests

Tests hit the database through repository initialization, so MySQL must be reachable and correctly configured in `db/Db.java`.

```bash
mvn test
```

## Troubleshooting

- **“Can’t connect to the database” / test failures**
  - Confirm MySQL is running and reachable.
  - Verify the JDBC settings in `db/Db.java` (host, port, user, password).
  - Ensure your server supports the SQL used in the schema and queries (including password hashing strategy).
- **FTP tab shows connection/login errors**
  - Confirm the FTP host/port in `ftp/FtpService.java`.
  - Confirm the FTP server has users matching the email-prefix usernames.
- **Gmail send/IMAP inbox fails**
  - Use a Gmail App Password and enable IMAP in the Gmail account settings.
  - Ensure the user record has a valid `email_token`.

## Documentation

`proyectoASESE/tools/generate_test_docs_pdf.py` can generate a QA/testing document PDF (requires Python + `reportlab`):

```bash
python proyectoASESE/tools/generate_test_docs_pdf.py
```


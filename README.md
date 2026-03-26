# Security Access Control

Java Maven project demonstrating a capability-based role and scope access-control system.

## Project structure

- `src/main/java`: application main code
  - `com.coursework.accesscontrol.auth`: authorizer interfaces and implementations
  - `com.coursework.accesscontrol.auth.rules`: role-based access rules (Guest, Student, Staff, Admin)
  - `com.coursework.accesscontrol.capability`: read/write capability model
  - `com.coursework.accesscontrol.log`: audit logging of decisions
  - `com.coursework.accesscontrol.model`: domain model (User, Role, Resource, Operation, AccessScope)
  - `com.coursework.accesscontrol.service`: secure service wrapper for resources
  - `com.coursework.accesscontrol.demo`: CLI demo runner

- `src/test/java`: unit tests for policy, rules, authorizers, logging, service

- `pom.xml`: Maven build configuration

## Prerequisites

- Java 17+ installed
- Maven 3.8+ installed
- `JAVA_HOME` pointing to JDK home
- `M2_HOME` pointing to Maven root (not bin path)
- `PATH` includes `%M2_HOME%\bin` and `%JAVA_HOME%\bin`

## Setup

1. Clone or download repository into `D:\Hassan\java` (your workspace root).
2. Verify Java and Maven in terminal:
   ```powershell
   java --version
   mvn -v
   ```

3. If Maven is not present, install from https://maven.apache.org/download.cgi and set environment variables:
   - `JAVA_HOME`: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot`
   - `M2_HOME`: `C:\Program Files\apache-maven-3.9.14`
   - `PATH`: `%JAVA_HOME%\bin;%M2_HOME%\bin` (plus existing values)

4. Reload terminal (important to load new env vars).

## Build

From project root:

```powershell
mvn clean compile
```

## Tests

```powershell
mvn test
```

## Run Demo

### Option 1: Maven exec (recommended)

```powershell
mvn exec:java -Dexec.mainClass="com.coursework.accesscontrol.demo.AccessControlDemo"
```

`pom.xml` is configured to use exec plugin with main class, so this is enough:

```powershell
mvn exec:java
```

### Option 2: Package and run jar

```powershell
mvn clean package
java -cp target/security-access-control-1.0.0-SNAPSHOT.jar com.coursework.accesscontrol.demo.AccessControlDemo
```

## Behaviour

The demo runs a scenario that exercises:
- guest, student, staff, admin role-based access rules
- public/internal/confidential resource scopes
- read/write operation enforcement
- decision logging with timestamp and audit output

Output includes example allow/refuse results and log entries.

## Troubleshooting

- If `mvn` fails as unknown command: confirm `C:\Program Files\apache-maven-3.9.14\bin` exists and is in PATH.
- If Java class not found: ensure compile is successful and run from project root with correct jar path.
- If tests fail, inspect output in `target/surefire-reports`.

---

## Contact

For enhancement requests, add new rules under `com.coursework.accesscontrol.auth.rules` and wire through `RuleChainAccessAuthorizer` in `AccessControlDemo`.

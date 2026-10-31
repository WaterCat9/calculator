# ===== Stage 1: builder - compile and package with Maven =====
# FROM specifies the base image. maven:3.8-openjdk-17 bundles Maven 3.8 and JDK 17,
# which matches the maven.compiler.target in our pom.xml.
FROM maven:3.8-openjdk-17 AS builder

# WORKDIR creates and switches to /app inside the container.
WORKDIR /app

# COPY copies files from the build context (./backend, set by docker-compose.yml)
# into the image. We copy pom.xml and src only - target/ and .git/ are not needed.
COPY pom.xml .
COPY src ./src

# RUN executes a command during the build. mvn package compiles, runs the 9 JUnit
# tests and produces the shaded fat jar that contains all dependencies.
RUN mvn -B package

# ===== Stage 2: runtime - only the JDK and the jar are needed =====
# A second FROM starts a fresh image, so the final image only carries the jar.
FROM maven:3.8-openjdk-17

WORKDIR /app

# COPY --from=builder copies the jar produced in stage 1 into this new image.
COPY --from=builder /app/target/calculator-1.0.jar calculator-1.0.jar

# EXPOSE documents which port the service listens on inside the container.
# The API server binds to port 8000 (see App.java).
EXPOSE 8000

# ENTRYPOINT defines the fixed command run when the container starts.
# Note: no command-line arguments this time - the jar starts the HTTP server itself.
ENTRYPOINT ["java", "-jar", "calculator-1.0.jar"]

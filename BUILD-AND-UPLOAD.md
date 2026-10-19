# Build and Upload Guide

## 1. Local Build Instructions

Requirements:
- JDK 17 or higher
- Android SDK with Platform 35 installed

### Building with Gradle Wrapper:
```bash
# Make gradlew executable
chmod +x gradlew

# Run unit tests
./gradlew testDebugUnitTest

# Assemble debug APK
./gradlew assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 2. Uploading to Your GitHub Repository

To push the clean project directly to your fork (`https://github.com/anbv-dev/Rem`):

```bash
# Initialize git if not already present
git init
git checkout -b main

# Add files
git add .
git commit -m "feat: complete transformation of reReminder into one-time Reminder app"

# Set remote and push
git remote add origin https://github.com/anbv-dev/Rem.git
git push -u origin main --force
```

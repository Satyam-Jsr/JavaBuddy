# 🎓 JavaBuddy - Interactive Java Learning App

<div align="center">

**A comprehensive Android application designed to help users learn Java programming through interactive lessons, quizzes, hands-on coding practice, and AI-powered features.**

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![GitHub](https://img.shields.io/badge/GitHub-Satyam--Jsr-blue.svg)](https://github.com/Satyam-Jsr/JavaBuddy)

</div>

---

## 📱 About

JavaBuddy is a feature-rich mobile learning platform that makes Java programming accessible and engaging for beginners and intermediate learners. With a built-in IDE, interactive lessons, AI-powered assistance, and gamified learning elements, JavaBuddy provides everything you need to master Java programming on the go.

## ✨ Key Features

### 📚 Comprehensive Learning System
- **15 Structured Lessons**: Progressive curriculum from basics to advanced topics
  - Variables, Data Types, Operators
  - Control Flow (if-else, loops)
  - Object-Oriented Programming (Classes, Inheritance, Polymorphism)
  - Exception Handling, Collections, Multithreading
  - File I/O, Networking, and more
- **Interactive Quizzes**: Test knowledge with instant feedback
- **Progress Tracking**: Monitor your learning journey
- **Bookmark System**: Save and revisit favorite lessons
- **Three-Tab Interface**: Content, code examples, and practice

### 💻 Built-in Java IDE
- **Full-featured Code Editor**: Write and execute Java code on mobile
- **Multiple Compiler Modes**: Basic Interpreter, Advanced Interpreter, Real Java Compiler
- **Syntax Highlighting**: Color-coded syntax with Sora-Editor
- **Example Programs**: 20+ pre-built programs to learn from
- **File Management**: Save, load, and organize code files
- **Real-time Execution**: See code results instantly

### 🎯 Practice & Assessment
- **80+ Practice Problems**: Hands-on coding challenges with difficulty levels
- **Hint System**: Get helpful hints when stuck
- **Solution Verification**: Automatic code checking
- **Timed Mastery Tests**: Time-limited coding tests
- **Performance Analytics**: Track coding speed and accuracy

### 🤖 AI-Powered Features (Groq API)
- **AI Quiz Generator**: Create unlimited custom quizzes
- **AI Programming Challenges**: Personalized coding problems
- **AI Help Assistant**: 24/7 conversational help
- **Smart Recommendations**: AI-suggested topics based on progress

### 🎨 Modern User Experience
- **Material Design 3**: Beautiful, intuitive interface
- **Dark Mode Support**: Easy on the eyes
- **Lottie Animations**: Smooth, engaging animations
- **Offline Capable**: Learn anytime (AI requires internet)
- **Navigation Drawer**: Quick access to all features

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| **Lines of Code** | 15,573 |
| **Activities** | 19 |
| **Fragments** | 4 |
| **Database Tables** | 4 |
| **Total Files** | 146 |
| **Java Lessons** | 15 |
| **Practice Problems** | 80+ |

## 🛠️ Technical Stack

### Core Technologies
- **Language**: Java
- **Platform**: Android (Min API 21 / Android 5.0+)
- **Build System**: Gradle
- **Architecture**: Three-Layer (Presentation, Business, Data)

### Key Libraries
- **Room Database**: SQLite with compile-time verification
- **Material Components**: Modern UI components
- **Lottie**: Vector animations
- **Sora Code Editor**: Feature-rich code editing
- **Groq API**: AI features (llama-3.1-70b-versatile)
- **OkHttp**: HTTP client for API calls
- **ViewPager2**: Smooth page transitions
- **RecyclerView**: Efficient list rendering

### Database Schema
```
Room Database (Version 6)
├── Lesson (id, title, content, codeExample, videoUrl)
├── Quiz (id, lessonId, question, options, correctAnswer)
├── UserProgress (id, lessonId, completed, score, timestamp)
└── LessonBookmark (id, lessonId, timestamp)
```

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8+
- Android SDK API 21+
- Groq API Key (optional, for AI features)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/Satyam-Jsr/JavaBuddy.git
   cd JavaBuddy
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to cloned directory

3. **Configure Groq API (Optional)**
   - Get API key from [console.groq.com](https://console.groq.com)
   - Open `app/src/main/java/com/example/javabuddy/ai/GroqApiService.java`
   - Replace `YOUR_GROQ_API_KEY_HERE` with your actual key
   - **⚠️ Never commit your actual API key!**

4. **Sync & Build**
   - Sync Gradle files
   - Build → Make Project

5. **Run**
   - Connect device or start emulator
   - Click Run button

## 📚 Documentation

Comprehensive documentation for understanding and contributing to JavaBuddy:

### 🔍 Code Explanation Guides
- [Person 1 - Navigation & Startup](docs/CODE_SCRIPT_PERSON_1.md) - SplashActivity, MainActivity, HomeFragment
- [Person 2 - Learning System](docs/CODE_SCRIPT_PERSON_2.md) - LessonActivity, QuizActivity, RecyclerView patterns
- [Person 3 - IDE & Practice](docs/CODE_SCRIPT_PERSON_3.md) - IDEActivity, compilers, practice features
- [Person 4 - Database Architecture](docs/CODE_SCRIPT_PERSON_4.md) - Room entities, DAOs, database patterns

### 🛠️ Technical Guides
- [Tech Guide 1 - Architecture & AI](docs/TECH_PERSON_1_GUIDE.md) - System architecture, Groq API integration
- [Tech Guide 2 - UI/UX Implementation](docs/TECH_PERSON_2_GUIDE.md) - Activities, Material Design, animations

### 📖 Additional Resources
- [Groq API Setup Guide](docs/GROQ_API_SETUP.md) - How to configure AI features
- [Master Index](docs/MASTER_INDEX.md) - Complete documentation index
- [Quick Reference](docs/QUICK_REFERENCE_CARD.md) - Quick feature overview
- [Team Documentation Package](docs/TEAM_CODE_EXPLANATION_PACKAGE.md) - Complete team guide

## 🎯 Learning Path

1. Introduction to Java & Programming Basics
2. Variables and Data Types
3. Operators and Expressions
4. Control Structures (if-else, switch)
5. Loops (for, while, do-while)
6. Arrays and Collections
7. Methods and Functions
8. Object-Oriented Programming
9. Inheritance and Polymorphism
10. Exception Handling
11. File I/O Operations
12. Java Generics
13. Lambda Expressions
14. Java Streams API
15. Multithreading and Concurrency

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 🗺️ Roadmap

- [ ] Multi-language support (Python, JavaScript, C++)
- [ ] Social features & code sharing
- [ ] Achievement system with badges
- [ ] Advanced AI tutor
- [ ] Video lessons integration
- [ ] Web version

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

Copyright (c) 2025 Satyam Kumar

## 👥 Team

| Role | Responsibilities |
|------|-----------------|
| Person 1 | Navigation & User Journey |
| Person 2 | Learning System & Quizzes |
| Person 3 | IDE & Practice Features |
| Person 4 | Database Architecture |
| Tech Lead 1 | Architecture & AI Integration |
| Tech Lead 2 | UI/UX Implementation |

## 🙏 Acknowledgments

- [Groq](https://groq.com/) - Fast AI inference
- [Material Design](https://material.io/) - Design guidelines
- [Lottie](https://airbnb.design/lottie/) - Animations
- [Sora Editor](https://github.com/Rosemoe/sora-editor) - Code editing
- [Android Room](https://developer.android.com/training/data-storage/room) - Database

## 📞 Contact

- **GitHub**: [@Satyam-Jsr](https://github.com/Satyam-Jsr)
- **Repository**: [JavaBuddy](https://github.com/Satyam-Jsr/JavaBuddy)
- **Issues**: [Report Bug](https://github.com/Satyam-Jsr/JavaBuddy/issues)

---

<div align="center">

**⭐ Star this repo if you find it helpful! ⭐**

**Made with ❤️ by the JavaBuddy Team**

</div>
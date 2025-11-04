## DATABASE TROUBLESHOOTING GUIDE

### Current Issue: Lessons not showing, quizzes not available

### What I've implemented to fix this:

1. **Database Version Update**: Changed from version 1 to 2
2. **Fallback Migration**: Added `.fallbackToDestructiveMigration()` to force database recreation  
3. **Auto-Population**: Database checks if empty and auto-populates on startup
4. **Debug Method**: MainActivity now shows toast with lesson count when database is populated

### Expected Behavior:
- When you start the app, you should see a toast saying "Database populated with 15 lessons!"
- HomeFragment should show "Progress: 0/15 lessons completed"
- LessonActivity should display 15 lessons
- Each lesson should have 6 quiz questions

### If you're still not seeing lessons:

**Option 1: Clear App Data (Recommended)**
1. Go to Android Settings > Apps > JavaBuddy
2. Storage > Clear Data
3. Restart the app

**Option 2: Uninstall and Reinstall**
1. Uninstall the app completely
2. Install the new APK
3. The database will be created fresh with all 15 lessons

**Option 3: Check Device Logs**
- The app will show toast messages indicating database status
- Look for "Database populated with X lessons!" message

### Verification Steps:
1. Open app - should see toast with lesson count
2. Navigate to Lessons - should see 15 lessons
3. Click any lesson - should see lesson content
4. Take quiz - should see 6 questions per lesson
5. Check Practice - should see 15 problems

The database content is definitely there (1,400+ lines of lessons, quizzes, and problems).
The issue is likely that the old database file is still present and needs to be cleared.
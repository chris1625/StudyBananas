<p align="center">
  <img src="https://github.com/chris1625/StudyBananas/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>
</p> 

# Study Bananas
An Android study group app, created by students, for students.

### What does it do, and who is it for?
This application was created as a group project for the CSE 110 class at UCSD. It was created to bring students together to study. It is
built with anonymity in mind, so that strangers in the same class might come together to study, and yet remain anonymous to other users of
the application. At the moment it is only available to UCSD students, but may be expanded in the future. For a basic demo of the
application's features and functionality, check out our [demo](https://www.youtube.com/watch?v=QmaaHrmI9vk).

### Installation
If you have an Android device or emulator with Android 5.0+ (Lollipop and above), follow the below instructions to install this application:
1. Go to the device’s Settings > Security
2. Scroll down to the “Device administration” section and enable the “Unknown sources” setting.
3. Go to https://github.com/chris1625/StudyBananas/releases and download the latest release with the .apk extension.
4. Go to the device’s downloads and tap the file named “study-bananas-[version #].apk” that was just downloaded.
5. An installation screen shall appear. Tap the “Install” button.
6. When the installer is finished, tap the “Open” button to open the app.

### Application Design Decisions

#### Data storage and retrieval
We used [Firebase](https://firebase.google.com/) as the backend of this project, which provided a simple interface that suited our needs. 
Firebase stores all of the user information (passwords hashed of course), course information, and dynamic data, such as groups and group 
details, in a protected database. Live updates to the database trigger listeners in our application, allowing for real-time updates in 
the application.

#### Locations and Maps
To allow users to search physical locations at which they might study, we used the 
[Google Places API](https://developers.google.com/places/). It provides a simple interface for Autocomplete search functionality as well
as addresses and location names, which we used as part of study groups' identities and information. To allow users to view these locations
in a map view, we used the [Google Maps API](https://developers.google.com/maps/) which simply allows users to view those stored locations
on the Google Maps application.

#### MVP architectural pattern
We designed our app using the MVP architectural pattern, which is a derivation of the MVC pattern. We
used MVP because it is more commonly used in Android software. Google suggests Android developers use
the MVP pattern, so we used the MVP template that they provide 
[here](https://github.com/googlesamples/android-architecture/tree/todo-mvp/) as a reference.

MVP breaks down applications into three components, the Model, View, and Presenter. The Presenter
functions very similar to the Controller in the MVC pattern. For more information, view 
[this](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) article.

In our code, you will find code for each page of the app in folders under the directory:
[StudyBananas/app/src/main/java/com/bananabanditcrew/studybananas/ui](https://github.com/chris1625/StudyBananas/tree/master/app/src/main/java/com/bananabanditcrew/studybananas/ui)

In each of these folders, you will generally find files with names ending in “Fragment”, “Contract”, and
“Presenter”. The “Fragment” files implement the View interface found in the “Contract”. These files are the
View of MVP. The “Presenter” files implement the Presenter interface found in the “Contract”. These files are
the Presenter of MVP. The “Contract” files outline the way in which the Views and Presenters are able talk to
one another. In other words, the Contract shows which Presenter functions the View can call, and which
View functions the Presenter can call. The Model functions are not a part of this contract. Instead, the
“Presenter” files have their own helper functions that communicate with the Model.

### The Team
The people involved in the project and our "roles", in no particular order:
- David Chang - Algorithm Specialist
- Christopher Harris - Software Development Lead
- John Murcko - Senior System Analyst
- Tarun Pasumarthi - Second Software Development Lead
- Harley Saxton - User Interface Specialist
- Ryan Shim - Quality Assurance Lead
- Gokul Suresh - Database Specialist
- Andrew Yoo - Business Analyst
- William Yu - Software Architect
- Julia Li - Project Manager

### Graphics and Icons
The app's logo and navigation drawer background were created by Harley, and the buttons on the home page were created by Julia. All other
icons and/or vector drawables were provided by Google and the Material Design project.

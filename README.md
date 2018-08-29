Take Free
======

> Android App for submitting redundant goods, providing for other users

## Overview

### 1. Main Features
- User management  
Signup, Login, Profile, Setting, ...

#### 1.1 Admin Version
- User management  
User list, Ban/Unban User ...
- Report management  
Report list, Delete Report

#### 1.2 User Version
- Item management  
Submit item, list & filter items, item detail  
- Take management  
Contact to owner, chatting ...  
 
### 2. Techniques 
#### 2.1 UI Implementation
- All UI are based on ConstraintLayout  
- Using Font Awesome  
- Bottom navigation bar  
- switching tabs using supportFragmentManager  
- CollapsingToolbarLayout for smooth toolbar effect  
- Spannable for style on specific text in TextView  
  - Notifications page  
  
#### 2.2 Function Implementation
- Getting location using Google play service location APIs  
  - [FusedLocationProviderClient](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient)  
  - [LocationCallback](https://developers.google.com/android/reference/com/google/android/gms/location/LocationCallback)  
- Google Firebase for backend  
  - [GeoFire for location saving & querying](https://github.com/firebase/geofire-java)  
- Implemented Parcelable for all model for passing between activities  
- Push notification using Firebase Clound Messaging(FCM)
  - Sending push notification from App  
[https://firebase.google.com/docs/cloud-messaging/send-message?authuser=0#send_messages_using_the_legacy_app_server_protocols](https://firebase.google.com/docs/cloud-messaging/send-message?authuser=0#send_messages_using_the_legacy_app_server_protocols)  
[https://firebase.google.com/docs/cloud-messaging/auth-server?authuser=0](https://firebase.google.com/docs/cloud-messaging/auth-server?authuser=0)

#### 2.3 Code tricks  
- Copying object using ``copy()`` function in Kotlin data class  
[https://kotlinlang.org/docs/reference/data-classes.html](https://kotlinlang.org/docs/reference/data-classes.html)  
- Passing function as parameter using lambda expression, replacing traditional Interface
``BaseModel.readFromDatabaseChild()``  

#### 2.4 Third-Party Libraries
- [Google Firebase](https://mvnrepository.com/artifact/com.google.firebase) v15.0.0  
  - Firebase Auth  
  - Firebase Database  
  - Firebase Storage  
  - Firebase Messaging  
- [Firebase Geofire](https://github.com/firebase/geofire-java) v2.3.1  
Filtering items based on distance  
- [CircleIndicator](https://github.com/ongakuer/CircleIndicator) v1.2.2  
- [BottomNavigationViewEx](https://github.com/ittianyu/BottomNavigationViewEx) v1.2.4    
  - Main Tabbar in home page  
- [CircleImageView](https://github.com/hdodenhof/CircleImageView) v2.2.0  
- [AndroidSwipeLayout](https://github.com/daimajia/AndroidSwipeLayout) v1.2.0  
  - Messages page  
- [AndPermission](https://github.com/yanzhenjie/AndPermission) v2.0.0-rc4  
Real Time permission checking module  
- [Glide](https://github.com/bumptech/glide) v4.6.1  
  - Showing photo for items & users   
- [Android-Image-Cropper](https://github.com/ArthurHub/Android-Image-Cropper) v2.7.1   
  - Taking photo from camera in all cases  
- [OkHttp](https://github.com/square/okhttp) v3.10.0  
Calling & Fetching data from Rest Api  
  - Send push notification to FCM server  
- [BadgeView](https://github.com/qstumn/BadgeView) v1.1.3  
  - Show/Hide badge on bottom navigation bar

## Need to Improve
- Add and improve features
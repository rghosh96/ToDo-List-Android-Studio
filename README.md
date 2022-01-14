# To-Do List App (Android)
This android application is a to do list that can operate as reminder app for users to complete tasks. The main functionalities are creating, updating, deleting, marking a to do item as complete, and setting alarm reminders. The underlying architecture of the app is Model-View Presenter (MVP). The app utilizes a local SQL database and Room in order to keep track of to do items. The final outcome of the application is a functioning to do list app, that can store and modify tasks, as well as set set reminders and notify the user (in app or out) that a task is due.

## Description
In this project, Android Studio and the Java programming language was used to create a to do list app. There were a number of purposes for this assignment: (1) to gain an understanding of the Model-View Presenter (MVP) design architecture, and (2) how to use this architecture for an app that requires storing/accessing/manipulating data in a database. Alongside orchestrating how the model, view, presenter, and database communicated with each other, notifications and and an alarm manager was implemented to extend the functionality of the app. The result is a to do list utilizing a Model-View Presenter structural design in order to perform tasks. It has the following seven core functionalities:
1) Create a new to do item
2) Select a previously created to do item
3) Update the title and content of a to do item
4) Update the due date of a to do item
5) Update the completion status of a to do item
6) Delete a to do item
7) Notify user when to do item is due via a status bar
notification

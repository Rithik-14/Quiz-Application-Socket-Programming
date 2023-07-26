# Quiz-Application-Socket-Programming

## Features
* Login
* SignUp/Registration
* Timer
* File sharing between host and participants regarding the type of quiz (Quiz Guidelines)
* Automated Email result for each student

## Requirements
* Eclipse IDE
* MySQL and XAMPP
* JavaMail

## MySQL Table : Columns
```
Username
Email
Password
Score
FinishTime
```
## Workflow
* The server updates quiz data i.e questions from a file provided by the quiz host.
* Participants can create an account and login.
* Every participant recieves a file regarding the Quiz guidelines from the host using FTP.
* Quiz is timed.
* After the quiz, the server calculates scores, updates the database and sends result to participants in the form of email using SMTP.

## Screenshots
![Login](Screenshots/Login.JPG?raw=true)
![SignUp](Screenshots/SignUp.JPG?raw=true)
![Signup_confirm](Screenshots/Signup_Confirm.JPG?raw=true)
![quiz1](Screenshots/Question_1.JPG?raw=true)
![quiz2](Screenshots/Question_2.JPG?raw=true)
![Result](Screenshots/Result.JPG?raw=true)
![Mail_Result](Screenshots/Mail_Result.png?raw=true)


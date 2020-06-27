
	GET STARTING (INSTRUCTION):
	===========================


   1. Download Folder „READY_TO_START“ with file 	“GoogleDriveSynchronizatorJava.jar”
   
   2. run application with command: “ java -jar 	GoogleDriveSynchronizatorJava.jar  ”
   
   3. set settings (only onse):
   	
	- Important: add “ credentials.json ” in folder “Secret” (The folder will 	appear beside file “GoogleDriveSynchronizatorJava.jar”);
   	
	- choose your folder in PC,which will be as “root” for Drive service 		  (necessary only once)
   	
	- write and save your e-mail for Drive-disc (not necessary), in order to
	  recognize active synchronization
   
   4. Choose option (radio buttons) and start program.
   
   5. If You run the program for the first time:
  	
	1.confirm access by clicking on the entire long link from google that 	  	  appeared either in console or in the program text Area
	
	2. confirm two-factor authentication in your smartphone, check the E-		   mail.
   
   6.  Done.

    

    
=====================================
	
If you have been denied access to the operation, there may be the following reasons:
-------------------------------------
	
    • your credential file is expired
    • google still need time for proof credentials, maybe more then one-three 	hours
    • you have forgotten to add this app (according to the name of the program) 	to Google Drive Projects .

===================================== 


	============================================
	How You can get an credentials?
	============================================

 (Names Depend on Google WEB GUI version) authentication Oauth:
 
   1. Sign in with your Google account in the reserved area where you can 	configure Google APIs,
     from this URL: https://console.developers.google.com/apis/library
   
   2. Google drive Api → anable
   
   3. Dashboard. New Project → Create a project
   
   4. Project Settings:
    	
	◦ Name: GoogleDriveSynchronizatorJava
      
	◦ Location → No Organisation
      
	◦ In the  “API and Services”, click on “OAuth consent screen“→

	◦ Select “External” and enter your email address and the name of the 		  application „GoogleDriveSynchronizatorJava “.

 	This is the name that will be shown in the “Consent screen” when making 	authentication from account. Leave all the other options to their 		default values.
   
   5. Click on the button “SAVE“.
   
   6. In the left menu “API and Services”, click on “Credentials” and 	choose “OAuth Client ID” from the “Create credentials” menu:
   
   7. Now select “Desktop App” as Application Type, enter the User App name (for  	instance your E-Mail) then click on “Create “.
   
   8. Immediately, CLIENT ID and CLIENT SECRET will be shown on the right. Copy 	these credentials and paste them into save place, in the window 	where 	you’re creating the cloud account for Google Drive.
   9. Download Json file with credentials.json (in Credentials→ OAuth 2.0 Client I	dentifiers → name(click)->Download  Json ).
	Rename file on “ credentials.json ”
   
   10. Go to : GET STARTING (INSTRUCTION) 
       

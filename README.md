# Buddy Chat Sample

This application demonstrates a simple chat application using the following Buddy features:

* User creation, login, logout
* Message send and receive
* Push notifications 

Buddy Chat is fully functional, but it's a sample so it's rough around the edges and needs work to be production ready.

## Running the Sample

To run the sample, you need to copy Google Cloud Messaging (GCM) information into both the source code of the sample, and the Buddy Dashboard. And you need to copy your Buddy app's ID and Key into the source code as well.

First, open or create your [Google Developers Console](https://console.developers.google.com/project) Project:

- Create or open your Google Project in the GDC. Ensure that you have enabled the "Google Cloud Messaging for Android" API under "APIs & auth > APIs".

Next, copy the Google Project's Project Number into the sample's source code:

- Open the Chat Sample project in [Android Studio](http://developer.android.com/sdk/index.html).
- Open BuddyChatApplication.java.
- Replace `MY_SENDER_ID` with the GCM [Sender ID](https://developers.google.com/cloud-messaging/gcm#senderid), which is same value as the Google Developers Console Project Number. Go to the GDC, then select your project name. Copy the Project Number, which is an 11-digit number, and replace `MY_SENDER_ID` in BuddyChatApplication.java.

Next, copy your Buddy app's ID and Key into the sample's source:

- Choose your application at the [Buddy Dashboard](https://buddyplatform.com) (or create one if you haven't already). Click the 'App Keys' button, and replace `MY_APP_ID` and `MY_APP_KEY` with the appropriate values from the Dashboard.

Next, copy the Project's Server key into the Buddy Dashboard:

- To get an API key for GCM, go to the GDC, then under your project, find "APIs & Auth > Credentials". Click on "Create new key" under "Public API access" if one doesn't exist, click on "Server key", and then copy the value next to "Key for server applications > API key". You will now paste this value into the Buddy Dashboard.
- Log in to the Dashboard, click on the name of your app in the left-hand column, and then click on "Push". Scroll down the "Push" page, then click "Add GCM API Key", and paste in the GCM API Key.

You're now ready to run the sample. 

## Key Points

The sample uses the Buddy Message APIs to conduct the chat, and save chat messages. However, the sample uses push notifications to communicate chat state information back-and-forth between the chat apps. This frees the application from having to poll the message API, which can be bad for battery life.

When the sample receives a push notification, it checks the notification for which chat action it should perform, such as updating the chatting state with the other client, or fetching new messages. This also allows the chat to be launched from a notification in the notification manager.

The code for the chat scenario can primarily be found in Chat.java.
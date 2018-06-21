const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// general notification to function
exports.createUpdate = functions.firestore
  .document('notifications/{documentId}')
  .onCreate(event => {
    var newValue = event.data();

	// Create a DATA notification
    const payload = {
       data: {
        title: newValue.mTitle,
        msg: newValue.mMessage
      }
    };
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };
    return admin.messaging().sendToTopic("generalNotifications", payload, options);
});


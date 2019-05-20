/* eslint-disable */

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.firestore.document("notifications/{userEmail}/userNotifications/{notificationId}").onWrite((change, context) => {
	const userEmail = change.after.userEmail;
	const notificationId = change.after.notificationId;

	return admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).get().then(queryResult => {
		const senderUserEmail = queryResult.data().senderUserEmail;
		const notificationMessage = queryResult.data().notificationMessage;

		const fromUser = admin.firestore().collection("users").doc(senderUserEmail).get();
		const toUser = admin.firestore().collection("users").doc(userEmail).get();

		return Promise.all([fromUser, toUser]).then(result => {
			const fromUserName = result[0].data().userName;
			const toUserName = result[1].data().userName;
			const tokenId = result[1].data().tokenId;

			const notificationContent = {
				notification: {
					title: fromUserName + " is Completed",
					body: notificationMessage,
					icon: "default"
				}
			};

			return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
				console.log("Notification sent!");
				admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
			});
		});
	});
});
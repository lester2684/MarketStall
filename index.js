//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// Listens for new messages added to messages/:pushId
exports.updateForecast = functions.database.ref('/items/{itemid}').onWrite( event => {
	return calculateForecast(event);
});

function calculateForecast(event){
    var daysNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
	var daysItemCount = [0, 0, 0, 0, 0, 0, 0];
	var daysItemtotalPrice = [0, 0, 0, 0, 0, 0, 0];
	var daysItemPriceAverage = [0, 0, 0, 0, 0, 0, 0];
	
	var item = event.data.val();
	
	if(item == null)
	{
		item = event.data.previous.val()
	}
	console.log(item);
	
	var rootRef  = admin.database().ref();
	return rootRef.child('items').once('value', function(snapshot) {
		var items = snapshotToArray(snapshot);
	
		for(var i = 0; i < items.length; i++)
		{
			if(items[i].name == item.name)
			{
				var date = new Date(items[i].dateCreated);
				var dayNumber =  date.getDay();
				daysItemCount[dayNumber]++;
				daysItemtotalPrice[dayNumber]+= items[i].price;
			}
		}		
		for(i = 0; i < daysNames.length; i++)
		{
			if(daysItemtotalPrice[i] == 0 && daysItemCount[i] == 0)
			{
				daysItemPriceAverage[i] = 0;
			}
			else{
				daysItemPriceAverage[i] = daysItemtotalPrice[i]/daysItemCount[i];
			}
		}
	}).then(function(snapshot) {
		var dataArray = [];
		for(var i = 0; i < daysNames.length; i++)
		{
			dataArray.push(
			{
				day: daysNames[i], 
				count: daysItemCount[i],
				average: daysItemPriceAverage[i]
			});
		}
		
		console.log(dataArray);
		rootRef.child('forecasts').child(item.name.toLowerCase()).set(dataArray);
	}).catch(reason => {
		console.log(reason);
	});
}

function snapshotToArray(snapshot){
    var returnArr = [];

    snapshot.forEach(childSnapshot => {
        var item = childSnapshot.val(); 
        item.key = childSnapshot.key;
        returnArr.push(item);
    });

    return returnArr;
}

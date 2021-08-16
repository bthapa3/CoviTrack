// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access Firestore.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
var db = admin.database();

//reference to the users collection where user class is stored.
var usersref = db.ref("/Users/");

var twilio = require('twilio');
var m_accountSid = 'ACaf9bf636f4bccf4af6fdb53068331b3f'; //  Account SID from www.twilio.com/console
var m_authToken = 'e2be4c5bffe8e85fef7cc264bc2bfea0';   // Auth Token from www.twilio.com/console
var m_client;//connection object for the twillio account



    /**/
    /*
    NAME

        exports.checkTransmission

    SYNOPSIS

        exports.checkTransmission = functions.database
        .ref('/Users/{userid}/')
        .onUpdate(async(change, context)
        
        .ref('/Users/{userid}/')  --> reference to the user objects in the database.
        .onUpdate(async(change, context) --> OnUpdate is the trigger requirement whereas the change is the object before
                                            and after the change. Context is the context in which event occured.

    DESCRIPTION

            This function runs everytime there is update made on user object on database. This function
            helps to check if the change made on database is related to covid status change. If so
            this function makes calculations based on the change and modifies other user values and 
            alerts them through sms using twillio.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
    /**/

// listens to new updates for changes in covid status and updates as needed.
exports.checkTransmission = functions.database
    .ref('/Users/{userid}/')
    .onUpdate(async(change, context) => {
     //Setting an 'uppercase' field in Firestore document returns a Promise.
     
        const after=  await change.after.val();
        const before= await change.before.val();
        
        //await is needed in order to receive snapshot properly before comparing
        //If async function is not used could cause race conditions as data
        m_client =await new twilio(m_accountSid, m_authToken); 
        
        //if condition is true if the user clicks he got covid positive result.
        // His past status was negative but it is positive now.
        if(after.infected===true && before.infected===false){


            //totalpositive holds the document with count total positive people.
            var totalpositive= await admin.firestore().collection("stats").doc("totalpositive").get();

            //increasing the total number of covid positive number in the list and storing the value in database
            var countpositive=totalpositive.data().count;
            countpositive=countpositive+1;
            admin.firestore().collection("stats").doc("totalpositive").set({count : countpositive});
           
            //snapshot stores all the users objects
            usersref.once('value',async (snapshot)=>{
                
                //goes through each user in snapshot to check location cordinates
                for(child in snapshot.val()){
                    

                    //checks if the userID of the positive person is not same as the person we are comparing it with.
                    //this prevents the same person(covid positive) who triggered the function from getting notification again.
                    //child gives the userID here as child is named after userID.
                    if(after.userID!==child){
                        
                        //this function sends the positive person's userID and random person's ID from snapshot
                        //where child=random person & after.userID= userID of covid positive person.
                        //using for loop allows us to use await but for each doesnot
                        await checkcordinates(child,after.userID);
                    }
                }

                //goes through each user to check if there is common groups with the covid positive user.
                snapshot.forEach((child) => {
                
                       
                    // Comparing all the user values with covid positive person.
                    // after.userID!==child.val().userID prevents comparing and marking himself in risk as he is already positive 
                    // child.val().userID is needed in for-each loop unlike for loop as child is an object in this case
                    if(after.userID!==child.val().userID) {
                      
                        //checking all the possible work or class groups where they might be in the same room.
                        //an user can have upto 4 groups so comparison of 2 people needs 4*4= 16 comparisions. 
                        if(  (after.group1==child.val().group1) || (after.group1==child.val().group2) 
                        || (after.group1==child.val().group3) || (after.group1==child.val().group4) || (after.group2==child.val().group1) 
                        || (after.group2==child.val().group2) || (after.group2==child.val().group3) || (after.group2==child.val().group4) 
                        || (after.group3==child.val().group1) || (after.group3==child.val().group2) || (after.group3==child.val().group3)
                        || (after.group3==child.val().group4) || (after.group4==child.val().group1) || (after.group4==child.val().group2)
                        || (after.group4==child.val().group3) ||(after.group4==child.val().group4) )
                            
                        {
                             
                            
                            //this generates the present date
                            var datetime = new Date();
                            datatime=datetime.toDateString();
                            
                            //storing a latest date in which system finds they had contact with someone positive.
                            admin.firestore().collection("latest-dates").doc(child.val().userID).set({
                                timestamp: datetime
                            }); 
                            
                            //updating the transfer risk to true if the group of any child are common with the groups of positive person.
                            child.ref.update({transferrisk:true});
                            
                            //sending message notification to user with saved phone number
                            try{
                                m_client.messages.create({
                                    body: 'Hi,This message is from groups Covid Tracker Application! Our record shows someone close to you tested positive recently',
                                    to: child.val().phone_number,  // Text this number
                                    from: '+16462332146' // From a valid Twilio number
                                });
                            }
                            catch(err){
                                console.log("ERROR occured with code:"+ err);
                            }
                        }  
                    }    
                });   
            });
        }

        //if the user covid state is false.ie user had covid in past but he is no more positive. 
        //this means the number of covid positive person decreased by 1.

        if(after.infected===false && before.infected===true)
        {
            //totalpositive holds the document with count of total positive people.
            var totalpositive= await admin.firestore().collection("stats").doc("totalpositive").get();

            //deacreasing the number of covid positive people and storing the value on the database
            var countpositive=totalpositive.data().count;
            countpositive=countpositive-1;
            admin.firestore().collection("stats").doc("totalpositive").set({count : countpositive});

        }
    
    });  
         

    /**/
    /*
    NAME

        checkcordinates

    SYNOPSIS

        async function checkcordinates(childuserID,posuserID)

        childuserID  --> childuserID is the userID of random user for which we are determining risk.
        posuserID    --> It is the userID of the user who has tested positive for covid. we use his 
                        userID to lookup for all the locations that is stored on the database.

    DESCRIPTION

            This function helps to determine if two users have stayed on close contact by using the location
            cordinates stored on the database. This function matches all the cordinates of both the users
            received as parameters. 

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
    /**/
    
    async function checkcordinates(childuserID,posuserID) {

        //retreiving a user object based on the childs user ID received as parameter.
        var user;
        usersref.child(childuserID).once('value',async data=>{
             user=await data.val();
        })

        //childlocdoc contains the document that holds the arraylist with location cordinates of child user.
        childlocdoc = await admin.firestore().collection("userlocation").doc(childuserID).get();
        //Getting arraylist of locations from document 
        childlocarray= await childlocdoc.data().locations;
        
        //getting the document that holds the arraylist with location cordinates of positive user. 
        await admin.firestore().collection("userlocation").doc(posuserID).get()
        .then(async doc => {
           
            //Getting arraylist of locations from document 
            //contains all the cordinates of the positive recorded person.
            var locationsarray= await doc.data().locations;

            //going through each cordinates of locationarray where positivepoint=(latitude,longitude)
            locationsarray.forEach((positivepoint)=>{
               
               //going through each test co-ordinates for the 
                childlocarray.forEach((testpoint)=>{
                    

                    //if test point and positive point are equal users have been on close contact
                    //person who has not yet tested positive needs to be sent an alert message.
                    if(testpoint._latitude===positivepoint._latitude  && testpoint._longitude===positivepoint._longitude){
                        
                        
                        //getting new data time object for current date
                        var datetime = new Date();
    
                        //storing latest date for the user who received alert message.
                        admin.firestore().collection("latest-dates").doc(childuserID).set({
                            timestamp: datetime
                        }); 
    
                        //Updating their transfer risk to true as there was a close contact.
                        usersref.child(childuserID).update({transferrisk:true});  
                       
                        //sending message notification to user with saved phone number    
                        try{
                            m_client.messages.create({
                            body: 'Hi,This message is from Covid Tracker Application! Our record shows someone close to you tested positive recently',
                            to: user.phone_number,  // Text this number
                            from: '+16462332146' // From a valid Twilio number created  by twillio itself
                            });
                        }
                        catch(err){
                            console.log("ERROR occured with code:"+ err);
                        }
                       
                    }
               });

              
            })

        });
          
    }



    
    /**/
    /*
    NAME

        timerUpdate

    SYNOPSIS

        exports.timerUpdate=functions.pubsub.schedule('every 1440 minutes').onRun(async (context)

        schedule('every 1440 minutes')  --> time gap allocated between running the function next time in seconds.
                                            this function runs once a day: 1*24*60*60

    DESCRIPTION

            This function helps to collect and maintain the weekly count of the covid positive patients.
            Each day this function runs once to collect the total positive count for that day. It than modifies
            the weekly count array and removes the oldest date. This function also stores the modified array
            on database so it persists.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
    /**/
    


//this function updates the weekly positive count of the employees stored in firestore database.
//scheduled function that runs every day to record the number of positive patients for that day.
exports.timerUpdate=functions.pubsub.schedule('every 1440 minutes').onRun(async (context)=>{
    
    //positive holds the total number of all the positive people using common group.
    var totalpositive= await admin.firestore().collection("stats").doc("totalpositive").get();
    var countpositive=totalpositive.data().count;

    //weekly numbers holds the daily number of positive people for last seven days
    var weeklylist=await admin.firestore().collection("stats").doc("weekly").get();
    const weeklynumbers=weeklylist.data().positive_count;

    //Removing the count of days outside a range of a week and insertinng a new day value in a weeklynumber array
    var day;
    for (day = 0; day < weeklynumbers.length-1; day++) {
        weeklynumbers[day]=weeklynumbers[day+1];  
    }

    //adding the latest count to the end of weekly count array
    weeklynumbers[weeklynumbers.length-1]=countpositive;

    //replacing the weekly count array stored on the database.
    admin.firestore().collection("stats").doc("weekly").set({
       positive_count: weeklynumbers
    }); 
   
    return null;
});



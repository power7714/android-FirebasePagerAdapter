# FirebasePagerAdapter

Populate an Android ViewPager in real-time using the Google Firebase API.
This has been tested and working 100%. I persued this because I was wanting a real-time ViewPager but couldn't find any examples or solutions. I found others wanting to know how to do this but no answers. I felt it only right to make this public. I hope you find this useful in your current or future projects.
# Required Dependencies
Per the Firebase Documentation, add the following dependencies. firebase-ui is needed because the user needs to be authenticated before they can add entries to the database. If your users won't be writing any data to the database then that won't be needed. Be sure to check the Firebase Documentation for what is needed for your specific needs.

```
    compile 'com.google.firebase:firebase-core:11.0.1'
    compile 'com.firebaseui:firebase-ui:2.0.1'
```

#### How to use

You can use any standard or custom ViewPager you like. It will still work.

Create a POJO/Model class defining the get/set methods for each item.
Define your FirebasePagerAdapter variable

```
    private FirebasePagerAdapter adapter;
```

Then you can load the date when/how you please. I used a method to load the data once the user successfully logged in.
```
    	private void loadFirebaseData(){
		ViewPager viewpager = (ViewPager)findViewById(R.id.yourviewpager);

		adapter = new FirebasePagerAdapter<YourModel>(this, YourModel.class,
				R.layout.pager_view_item, FirebaseDatabase.getInstance().getReference("your_firebase_node")) {
			@Override
			protected void populateView(View v, YourModel model, int position) {
				// Get references to the views of pager_view_item.xml
				TextView sampleText = (TextView)v.findViewById(R.id.sampleTVOne);
				TextView sampleUser = (TextView)v.findViewById(R.id.sampleTVTwo);

				// Set their text
				sampleText.setText(model.getSampleText());
				sampleUser.setText(model.getUserName());
			}
		};
		viewpager.setAdapter(adapter);
	}
```

And that's it! Any changes to the referenced Firebase node would instantly be added to the ViewPager.

package com.example.wishlist4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class MainFragment extends Fragment {
	
	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private ProfilePictureView profilePictureView;
	private TextView welcomeTextView;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main, container, false );
		
		//allows the fragment to receive the onActivityResult() call rather than the activity
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		
		//authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
		//Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		//assign the text view
		welcomeTextView = (TextView) view.findViewById(R.id.welcome);
		return view;
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception)
	{
		if (state.isOpened())
		{
			//change visibility of user data
			profilePictureView.setVisibility(View.VISIBLE);
			welcomeTextView.setVisibility(View.VISIBLE);
			
			//Request user data and show the results
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (session == Session.getActiveSession())
					{
						if (user != null)
						{
							//Set the id for the PRofilePictureView
							//view that in turn displays the profile picture
							profilePictureView.setProfileId(user.getId());
							//Display the parsed user info
							welcomeTextView.setText("Welcome " + buildUserInfoDisplay(user));
						}
					}
					if (response.getError() != null)
					{
						//Handle errors, will do so later.
					}
					
				}
			});
			Log.i(TAG, "Logged in...");
			
		}
		else if (state.isClosed())
		{
			profilePictureView.setVisibility(View.INVISIBLE);
			welcomeTextView.setVisibility(View.INVISIBLE);
			Log.i(TAG, "Logged Out...");
		}
	}
	
	/*
	 * Helper method to extract and parse 
	 * user information using GRAPHUSER API
	 */
	private String buildUserInfoDisplay(GraphUser user)
	{
		StringBuilder userInfo = new StringBuilder("");
		
		//Example: typed access (name)
		//- no special permission required
		userInfo.append(String.format("%s\n\n", user.getName()));
		
		//Birthday - typed access - requires user_birthday permission
		//user.getBirthday()
		
		//Location - partially typed access, to location field, name key 
		//requires user_location permission: user.getLocation().getProperty("name")
		
		//Locale - access via property name - no special permission required
		//user.getProperty("locale")
		
		//Languages - access via key for array - requires user_likes permission
		/**
		 * JSONArray Languages = (JSONArray) user.getProperty(languages);
		 * if (languages.length() > 0)
		 * {
		 * ArrayList<string> languageNames = new ArrayList<String>();
		 * for (int i = 0; i < languages.length(); i++)
		 * {
		 * JSONObject language = languages.optJSONObject(i);
		 *add the language name to a list and use JSON methods to get access to name field
		 *languageNames.add(language.optString(name)
		 *}
		 *Now languagesNames.toString()
		 * 
		 */
		
		return userInfo.toString();
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		//For scenarios where the main activity is launched
		//and user session is not null , the session state change notification
		//may not be triggered. Trigger it if it's open/closed.
		
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()))
		{
			onSessionStateChange(session, session.getState(), null);
		}
		
		uiHelper.onResume();
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
	
	
}

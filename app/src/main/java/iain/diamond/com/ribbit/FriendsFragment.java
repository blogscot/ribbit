package iain.diamond.com.ribbit;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends ListFragment {

  private static final String TAG = FriendsFragment.class.getSimpleName();

  protected ParseRelation<ParseUser> mFriendsRelation;
  protected ParseUser mCurrentUser;
  protected List<ParseUser> mFriends;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();

    mCurrentUser = ParseUser.getCurrentUser();
    mFriendsRelation = mCurrentUser.getRelation(ParseUtils.KEY_FRIENDS_RELATION);

    // Update fragment with the user's list of friends
    mFriendsRelation.getQuery()
            .addDescendingOrder(ParseUtils.KEY_USERNAME)
            .findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> friends, ParseException e) {
        if (e == null) {
          mFriends = friends;
          ArrayList<String> usernames = new ArrayList<>();
          for (ParseUser user : friends) {
            usernames.add(user.getUsername());
          }
          ArrayAdapter<String> adapter =
                  new ArrayAdapter<String>(getListView().getContext(),
                          android.R.layout.simple_list_item_1,
                          usernames);
          setListAdapter(adapter);
        } else {
          ParseUtils.displayMessage(getListView().getContext(), TAG, e.getMessage());
        }
      }
    });
  }
}

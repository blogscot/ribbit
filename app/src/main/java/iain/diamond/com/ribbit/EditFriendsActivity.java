package iain.diamond.com.ribbit;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class EditFriendsActivity extends ListActivity {

  public static final String TAG = EditFriendsActivity.class.getSimpleName();

  protected List<ParseUser> mUsers;
  protected ParseRelation<ParseUser> mFriendsRelation;
  protected ParseUser mCurrentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_friends);

    // TODO: Figure out how to set up the action bar.

    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
  }

  @Override
  protected void onResume() {
    super.onResume();

    mCurrentUser = ParseUser.getCurrentUser();
    mFriendsRelation = mCurrentUser.getRelation(ParseUtils.KEY_FRIENDS_RELATION);

    // Display the user's friends
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.orderByAscending(ParseUtils.KEY_USERNAME);
    query.setLimit(1000);
    query.findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> list, ParseException e) {
        if (e == null) {
          // success
          mUsers = list;
          ArrayList<String> usernames = new ArrayList<>();
          for (ParseUser user : list) {
            usernames.add(user.getUsername());
          }
          ArrayAdapter<String> adapter =
                  new ArrayAdapter<String>(EditFriendsActivity.this,
                          android.R.layout.simple_list_item_checked,
                          usernames);
          setListAdapter(adapter);
          addFriendCheckMarks();
        } else {
          ParseUtils.displayMessage(EditFriendsActivity.this, getString(R.string.generic_error_message), getString(R.string.server_commication_error));
        }
      }
    });
  }

  private void addFriendCheckMarks() {
    mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> friends, ParseException e) {
        if (e == null) {
          // friends list returned - look for match
          for (int i = 0; i < mUsers.size(); i++) {
            ParseUser user = mUsers.get(i);
            for (ParseUser friend: friends) {
              if (friend.getObjectId().equals(user.getObjectId())) {
                getListView().setItemChecked(i, true);
              }
            }
          }
        } else {
          Log.e(TAG, e.getMessage());
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    // Add clicked user as a friend
    if (getListView().isItemChecked(position)) {
      // add friend
      mFriendsRelation.add(mUsers.get(position));
    }
    else {
      // remove friend
      mFriendsRelation.remove(mUsers.get(position));
    }
    mCurrentUser.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, e.getMessage());
        }
      }
    });
  }
}

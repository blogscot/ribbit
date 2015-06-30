package iain.diamond.com.ribbit;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class EditFriendsActivity extends ActionBarActivity {

  public static final String TAG = EditFriendsActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_friends);
  }

  @Override
  protected void onResume() {
    super.onResume();

    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.orderByAscending(ParseUtils.KEY_USERNAME);
    query.setLimit(1000);
    query.findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> list, ParseException e) {
        if (e == null) {
          // success
        } else {
          ParseUtils.displayMessage(EditFriendsActivity.this, "Error", "Problem communicating with server");
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
}

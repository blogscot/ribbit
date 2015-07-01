package iain.diamond.com.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  public static final int TAKE_PHOTO_REQUEST = 0;
  public static final int TAKE_VIDEO_REQUEST = 1;
  public static final int CHOOSE_PHOTO_REQUEST = 2;
  public static final int CHOOSE_VIDEO_REQUEST = 3;

  public static final int MEDIA_TYPE_IMAGE = 4;
  public static final int MEDIA_TYPE_VIDEO = 5;

  public static final int FILE_SIZE_LIMIT = 1024*1024*10;  // 10 MB

  protected Uri mMediaUri;

  private void displayToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

      switch (which) {
        case 0:
          take_picture();
          break;
        case 1: // Take video
          take_video();
          break;
        case 2: // Choose picture
          choose_picture();
          break;
        case 3: // Choose video
          choose_video();
          break;
        default:
      }
    }

    private void choose_video() {
      Toast.makeText(MainActivity.this, R.string.video_length_warning, Toast.LENGTH_LONG).show();
      Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
      chooseVideoIntent.setType("video/*");
      startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
    }

    private void choose_picture() {
      Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
      choosePhotoIntent.setType("image/*");
      startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
    }

    private void take_video() {
      Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
      if (mMediaUri == null) {
        // display an error
        Toast.makeText(MainActivity.this,
                getString(R.string.external_storage_error), Toast.LENGTH_LONG).show();
      } else {
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // 0 = 3gp, 1 = mp4 formats
        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
      }
    }

    private void take_picture() {
      Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
      if (mMediaUri == null) {
        // display an error
        Toast.makeText(MainActivity.this,
                getString(R.string.external_storage_error), Toast.LENGTH_LONG).show();
      } else {
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
      }
    }

      // To be safe, you should check that the SDCard is mounted
      // using Environment.getExternalStorageState() before doing this.
    private Uri getOutputMediaFileUri(int mediaType) {

      if (isExternalStorageAvailable()) {
        // get external storage directory
        String appName = MainActivity.this.getString(R.string.app_name);
        File mediaStorageDirectory =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);

        // create subdirectory
        if (!mediaStorageDirectory.exists()) {
          if (!mediaStorageDirectory.mkdirs()) {
            Log.e(TAG, getString(R.string.storage_filesystem_failure));
            return null;
          }
        }

        // create file
        File mediaFile;
        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(now);
        String path = mediaStorageDirectory.getPath() + File.separator;

        if (mediaType == MEDIA_TYPE_IMAGE) {
          mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
        } else if (mediaType == MEDIA_TYPE_VIDEO) {
          mediaFile = new File(path + "VID_" + timestamp + ".mp4");
        } else {
          return null;
        }

        Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
        // return the file's uri
        return Uri.fromFile(mediaFile);
      }
      return null;
    }

    private boolean isExternalStorageAvailable() {
      String state = Environment.getExternalStorageState();
      if (state.equals(Environment.MEDIA_MOUNTED)) {
        return true;
      }
      return false;
    }
  };

  

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser == null) {
      navigateToLogin();
    } else {
      Log.i(TAG, "User "+ currentUser.getUsername() + " is logged in." );
    }


    // Set up the action bar.
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    // When swiping between different sections, select the corresponding
    // tab. We can also use ActionBar.Tab#select() to do this if we have
    // a reference to the Tab.
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
      // Create a tab with text corresponding to the page title defined by
      // the adapter. Also specify this Activity object, which implements
      // the TabListener interface, as the callback (listener) for when
      // this tab is selected.
      actionBar.addTab(
              actionBar.newTab()
                      .setText(mSectionsPagerAdapter.getPageTitle(i))
                      .setTabListener(this));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {

      if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == CHOOSE_VIDEO_REQUEST) {
        if (data == null) {
          displayToast(getString(R.string.general_error));
        } else {
          mMediaUri = data.getData();
          Log.i(TAG, "Media URI: " + mMediaUri);

          // Check file size is under 10 MB
          if (requestCode == CHOOSE_VIDEO_REQUEST) {
            int fileSize = 0;
            InputStream stream = null;

            try {
              stream = getContentResolver().openInputStream(mMediaUri);
              fileSize = stream.available();
            } catch (FileNotFoundException e) {
              displayToast(getString(R.string.video_request_error));
              return;
            } catch (IOException e) {
              displayToast(getString(R.string.video_request_error));
              return;
            } finally {
              try {
                stream.close();
              } catch (IOException e) {
                displayToast(getString(R.string.video_request_error));
              }
            }
            if (fileSize > FILE_SIZE_LIMIT) {
              displayToast(getString(R.string.file_size_too_large));
              return;
            }
          }
        }
      } else {
        // add to the gallery
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mMediaUri);
        sendBroadcast(mediaScanIntent);
      }
    } else if (resultCode != RESULT_CANCELED) {
      Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
    }
  }


  private void navigateToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch(id) {
      case R.id.action_logout:
        ParseUser.logOut();
        navigateToLogin();
        break;
      case R.id.action_edit_friends:
        Intent intent = new Intent(this, EditFriendsActivity.class);
        startActivity(intent);
        break;
      case R.id.action_camera:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        break;
      default:
    }


    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in
    // the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.

      switch(position) {
        case 0:
          return new InboxFragment();
        case 1:
          return new FriendsFragment();
      }

      return null;
    }

    @Override
    public int getCount() {
      // Show 2 total pages.
      return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      Locale l = Locale.getDefault();
      switch (position) {
        case 0:
          return getString(R.string.title_section1).toUpperCase(l);
        case 1:
          return getString(R.string.title_section2).toUpperCase(l);
      }
      return null;
    }
  }

}

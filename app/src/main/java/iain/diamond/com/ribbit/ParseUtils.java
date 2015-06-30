package iain.diamond.com.ribbit;

import android.app.AlertDialog;

public final class ParseUtils {


  public static void displayMessage(android.content.Context instance, String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(instance);
    builder.setMessage(message)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok, null);
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  // Class name
  public static final String CLASS_MESSAGES = "Messages";

  // Field names
  public static final String KEY_USERNAME = "username";
  public static final String KEY_FRIENDS_RELATION = "friendsRelation";
  public static final String KEY_RECIPIENT_IDS = "recipientIds";
  public static final String KEY_SENDER_ID = "senderId";
  public static final String KEY_SENDER_NAME = "senderName";
  public static final String KEY_FILE = "file";
  public static final String KEY_FILE_TYPE = "fileType";
  public static final String KEY_CREATED_AT = "createdAt";

  public static final String TYPE_IMAGE = "image";
  public static final String TYPE_VIDEO = "video";
}

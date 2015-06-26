package iain.diamond.com.ribbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, "DdpHrIQOjdZUyvgto0O4S3iW3SWsLMtFUGaMaQ1e", "bahhzBlQL4U48uA0vh8LdwizHohlwWbBVBchycRe");
  }
}

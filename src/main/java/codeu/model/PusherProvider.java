package codeu.model;

import com.pusher.rest.Pusher;

public class PusherProvider {

  /** The string identifier on web sockets for chat channel */
  public static final String CHAT_CHANNEL= "chat";

  /** The string identifier on web sockets for activity channel */
  public static final String ACTIVITY_CHANNEL = "activity";

  /** The string identifier on web sockets for new activity event */
  public static final String NEW_ACTIVITY = "new-activity";

  /** The string identifier on web sockets for message sent event*/
  public static final String MESSAGE_SENT = "message-sent";

  /** The key for out pusher app */
  public static final String PUSHER_KEY = System.getenv("PUSHER_APP_KEY");
  public static final String CLUSTER = System.getenv("PUSHER_CLUSTER");

  private static final String APP_ID = System.getenv("PUSHER_APP_ID");
  private static final String APP_SECRET = System.getenv("PUSHER_APP_SECRET");

  private static Pusher instance;

  static Pusher getService() {
    if (instance != null) {
      return instance;
    } // Instantiate a pusher
    Pusher pusher = new Pusher(APP_ID, PUSHER_KEY, APP_SECRET);
    pusher.setCluster(CLUSTER); // required, if not default mt1 (us-east-1)
    pusher.setEncrypted(true); // optional, ensure subscriber also matches these settings
    instance = pusher;
    return pusher;
  }
}

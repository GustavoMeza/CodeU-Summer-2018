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

  public static final String PUSHER_CLUSTER = System.getenv("PUSHER_CLUSTER");
  public static final String PUSHER_ID = System.getenv("PUSHER_APP_ID");
  public static final String PUSHER_SECRET = System.getenv("PUSHER_APP_SECRET");

  /** The singleton Pusher instance*/
  private static Pusher pusher;

  /**
   * Static method that returns the singleton instance of Pusher
   * @return A Pusher instance
   */
  public static Pusher getService() {
    if(pusher != null) return pusher;
    pusher = new Pusher(PUSHER_ID, PUSHER_KEY, PUSHER_SECRET);
    pusher.setCluster(PUSHER_CLUSTER);
    pusher.setEncrypted(true);
    return pusher;
  }
}

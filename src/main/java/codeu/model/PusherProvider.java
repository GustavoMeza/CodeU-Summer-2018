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
  public static final String PUSHER_KEY = "1845b37e6fec77ec1a14";

  /** The singleton Pusher instance*/
  private static final Pusher pusher;

  /** Initialization of the Pusher instance */
  static {
    pusher = new Pusher("547303", PUSHER_KEY, "922690b9fa9b5664364c");
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
  }

  /**
   * Static method that returns the singleton instance of Pusher
   * @return A Pusher instance
   */
  public static Pusher getService() {
    return pusher;
  }
}

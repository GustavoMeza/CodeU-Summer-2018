package codeu.model.data;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by GustavoMG on 22/05/2018.
 */
public class ActivityTest {

    @Test
    public void TestCreate() {
        UUID objectId = UUID.randomUUID();
        Activity.Type type = Activity.Type.MessageSent;
        Instant firingTime = Instant.now();

        Activity activity = new Activity(type, objectId, firingTime);

        Assert.assertEquals(objectId, activity.getObjectId());
        Assert.assertEquals(type, activity.getType());
        Assert.assertEquals(firingTime, activity.getFiringTime());
    }
}

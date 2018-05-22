package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.store.basic.ActivityStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by GustavoMG on 17/05/2018.
 */
public class ActivityServlet extends HttpServlet{

    /** Amount of activities requested to the store. */
    public final static int maxActivitiesLoaded = 25;
    /** Store class that gives access to Activities. */
    private ActivityStore activityStore;

    /**
     * This method is only called when running in a server, not when running in a test.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        setActivityStore(ActivityStore.getInstance());
    }

    /**
     * Sets the ActivityStore used by this servlet. This function provides a common setup method for use
     * by the test framework or the servlet's init() function.
     */
    void setActivityStore(ActivityStore activityStore) {
        this.activityStore = activityStore;
    }

    /**
     * This function fires when a user requests the /activity URL.
     * Loads a list of activities from store, and forwards the request to activities.jsp
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Activity> activities = activityStore.getActivities(0, maxActivitiesLoaded);
        // Can be null if activities are not loaded correctly, handled on view.
        request.setAttribute("activities", activities);
        request.getRequestDispatcher("/WEB-INF/view/activities.jsp").forward(request, response);
    }
}

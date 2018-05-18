package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.store.basic.ActivityStore;
import com.google.appengine.repackaged.com.google.gson.Gson;

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

    /** Store class that gives access to Users. */
    private ActivityStore activityStore;

    /**
     * Set up state for handling login-related requests. This method is only called when running in a
     * server, not when running in a test.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        setActivityStore(activityStore.getInstance());
    }

    /**
     * Sets the UserStore used by this servlet. This function provides a common setup method for use
     * by the test framework or the servlet's init() function.
     */
    void setActivityStore(ActivityStore activityStore) {
        this.activityStore = activityStore;
    }

    /**
     * This function fires when a user requests the /login URL. It simply forwards the request to
     * login.jsp.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Activity> activities = activityStore.getActivities(0, 10);
        request.setAttribute("activities", activities);
        request.getRequestDispatcher("/WEB-INF/view/activities.jsp").forward(request, response);
    }
}

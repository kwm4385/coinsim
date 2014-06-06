package controllers;

import static play.data.Form.form;
import models.User;
import controllers.Authentication.Login;
import controllers.Authentication.Register;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result GO_HOME = redirect(
        routes.Application.index()
    );

    public static Result GO_DASHBOARD = redirect(
        routes.Dashboard.index()
    );

    /**
     * Display the login page or dashboard if connected
     *
     * @return login page or dashboard
     */
    public static Result index() {
    	return ok(index.render(User.findByEmail(ctx().session().get("email"))));
    }

}
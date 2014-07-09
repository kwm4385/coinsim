package controllers;

import java.util.Map;

import models.User;
import models.utils.Mail;
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
    
    /**
     * Takes post data from the contact form and sends it to info@coinsim.net
     * @return a redirect to the home page
     */
    public static Result submitContact() {
    	Map<String, String[]> data = request().body().asFormUrlEncoded();
    	String email = data.get("email")[0];
    	String message = data.get("message")[0];
    	String m = "Message from <a href='mailto:" + email + "'>" + email + 
    			"</a>:<br/>============================================<br/>" + message;
    	Mail.Envelop envelop = new Mail.Envelop("Coinsim contact from " + email, m, "info@coinsim.net");
        Mail.sendMail(envelop);
        flash("level", "success");
    	flash("message", "<b>Success!</b> Your message has been sent. Thank you for your feedback!");
    	return redirect("/#contact");
    }

}
package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

	public static Result index() {
    	flash("Hello!");
        return ok(index.render());
    }
    

}

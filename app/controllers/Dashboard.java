package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.libs.F.Promise;
import play.libs.F.Function;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.PriceData;
import views.html.dashboard.index;

@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

	/**
	 * Returns the dashboard index.
	 * @return
	 */
    public static Result index() {
        return ok(index.render(User.findByEmail(request().username())));
    }
    
    /**
     * Returns a JSON response with the current bitcoin price.
     * @return
     */
    public static Promise<Result> price() {
    	return PriceData.getPrice().map(new Function<Double, Result>() {
			@Override
			public Result apply(Double price) throws Throwable {
				Map<String, String> response = new HashMap<String, String>();
				response.put("price", Double.toString(price));
				response.put("last_updated", PriceData.lastUpdated().toString());
				response.put("exchange", PriceData.getExchange());
				return ok(Json.toJson(response));
			}
    	});
    }
   
    
}

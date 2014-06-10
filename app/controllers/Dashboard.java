package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Expr;

import models.Simulation;
import models.User;
import play.libs.F.Promise;
import play.libs.F.Function;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.PriceData;
import views.html.dashboard.*;

@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

	/**
	 * Returns the dashboard index.
	 * @return
	 */
    public static Result index() {
    	return resultOrNoSims(ok(index.render(User.findByEmail(request().username()))));
    }
    
    /**
     * Returns the simulations management page.
     * @return
     */
    public static Result simulations() {
    	User user = User.findByEmail(request().username());
    	List<Simulation> sims = Simulation.find.where(Expr.eq("userId", user.id)).findList();
    	return ok(simulations.render(user, sims));
    }
    
    /**
     * Creates a new simulation for the current user and sets it as their active.
     * @return
     */
    public static Result newSimulation() {
    	Map<String, String[]> data = request().body().asFormUrlEncoded();
    	Double bank, fee;
    	String name = data.get("name")[0];
    	try {
    		bank = Double.parseDouble(data.get("bank")[0]);
        	fee = Double.parseDouble(data.get("fee")[0]);
    	} catch(NumberFormatException e) {
    		flash("level", "danger");
        	flash("message", "<b>Whoops.</b> Simulation '" + name + "' could not be created.");
        	return redirect(routes.Dashboard.simulations());
    	}
    	Simulation newSim = new Simulation();
    	User user = User.findByEmail(request().username());
    	newSim.name = name;
    	newSim.dollars = bank;
    	newSim.tradingFee = fee;
    	newSim.userId = user.id;
    	newSim.save();
    	user.activeSimulation = newSim.id;
    	user.save();
    	
    	flash("level", "success");
    	flash("message", "<b>Success!</b> Simulation '" + name + "' has been created.");
    	return redirect(routes.Dashboard.simulations());
    }
    
    /**
     * Changes the user's active simulation.
     * @return
     */
    public static Result changeActiveSimulation() {
    	Map<String, String[]> data = request().body().asFormUrlEncoded();
    	Long sid;
    	try {
    		sid = Long.parseLong(data.get("simulation")[0]);
    	} catch(NumberFormatException e) {
    		flash("level", "danger");
        	flash("message", "<b>Whoops.</b> An error occured with your request.");
        	return redirect(routes.Dashboard.simulations());
    	}
    	Simulation sim = Simulation.find.byId(sid);
    	User user = User.findByEmail(request().username());
    	if(sim.userId == user.id) {
    		user.activeSimulation = sid;
    		user.save();
    		flash("level", "success");
        	flash("message", "<b>Success!</b> You are now working in simulation '" + sim.name + "'.");
    		return redirect(routes.Dashboard.simulations());
    	} else {
    		return forbidden();
    	}
    }
    
    /**
     * Returns a static page with embedded price charts.
     * @return
     */
    public static Result charts() {
    	return ok(charts.render(User.findByEmail(request().username())));
    }
    
    /**
     * Returns the given result or a redirect to the simulations page if the user has none.
     * @param r The primary result.
     * @return r or a redirect to simulations()
     */
    private static Result resultOrNoSims(Result r) {
    	User user = User.findByEmail(request().username());
    	if(user.activeSimulation == null) {
    		flash("level", "warning");
    		flash("message", "<b>Hey!</b> You need to set up your first simulation.");
    		return redirect(routes.Dashboard.simulations());
    	} else {
    		return r;
    	} 
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

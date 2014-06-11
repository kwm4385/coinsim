package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Expr;

import models.Simulation;
import models.Trade;
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
	
	/*
     * ========================================================================================
     *                                        Views
     * ========================================================================================
     */
	
	//-------------------------------------- Overview --------------------------------------

	/**
	 * Returns the dashboard index.
	 * @return
	 */
    public static Result index() {
    	User user = User.findByEmail(request().username());
    	List<Simulation> sims = Simulation.find.where(Expr.eq("userId", user.id)).findList();
    	List<Trade> trades;
    	if(user.activeSimulation != null) {
    		trades = Simulation.find.byId(user.activeSimulation).getTrades();
    	} else {
    		trades = new ArrayList<Trade>();
    	}
    	return resultOrNoSims(ok(index.render(user, sims, trades)));
    }
    
    //------------------------------------ Simulations ------------------------------------
    
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
    	User user = User.findByEmail(request().username());
    	if(Simulation.find.where(Expr.eq("userId", user.id)).findList().size() >= 10) {
    		flash("level", "danger");
        	flash("message", "<b>Whoops!</b> You can only have a maximum of 10 simulations.");
    		return redirect(routes.Dashboard.simulations());
    	}
    	Map<String, String[]> data = request().body().asFormUrlEncoded();
    	Double bank, fee;
    	String name = data.get("name")[0];
    	try {
    		bank = Double.parseDouble(data.get("bank")[0]);
        	fee = Double.parseDouble(data.get("fee")[0]);
    	} catch(NumberFormatException e) {
    		flash("level", "danger");
        	flash("message", "<b>Whoops!</b> Simulation '" + name + "' could not be created.");
        	return redirect(routes.Dashboard.simulations());
    	}
    	Simulation newSim = new Simulation();
    	newSim.name = name;
    	newSim.dollars = bank;
    	newSim.starting = bank;
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
     * Deletes a simulation belonging to the current user.
     * @return
     */
    public static Result deleteSimulation() {
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
    		String name = sim.name;
    		if(user.activeSimulation == sim.id) {
    			List<Simulation> userSims = Simulation.find.where(Expr.eq("userId", user.id)).findList();
    			if(userSims.size() - 1 == 0) {
    				user.activeSimulation = null;
    			} else {
    				user.activeSimulation = userSims.get(0).id;
    			}
    			user.save();
    		}
    		sim.delete();
    		flash("level", "success");
        	flash("message", "<b>Success!</b> '" + name + "' has been deleted.");
    		return redirect(routes.Dashboard.simulations());
    	} else {
    		return forbidden();
    	}
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
        	
        	if(data.get("from") != null) {
        		return redirect(data.get("from")[0]);
        	}
        	return redirect(routes.Dashboard.simulations());
    	} else {
    		return forbidden();
    	}
    }
    
  //-------------------------------------- Buy -------------------------------------
    
    public static Result buyView() {
    	User user = User.findByEmail(request().username());
    	List<Simulation> sims = Simulation.find.where(Expr.eq("userId", user.id)).findList();
    	List<Trade> trades;
    	if(user.activeSimulation != null) {
    		trades = Simulation.find.byId(user.activeSimulation).getTrades();
    	} else {
    		trades = new ArrayList<Trade>();
    	}
    	return resultOrNoSims(ok(buy.render(user, sims, trades)));
    }
    
    
  //-------------------------------------- Sell ------------------------------------
    
    
  //------------------------------------ Charts ------------------------------------
    
    /**
     * Returns a static page with embedded price charts.
     * @return
     */
    public static Result charts() {
    	User user = User.findByEmail(request().username());
    	List<Simulation> sims = Simulation.find.where(Expr.eq("userId", user.id)).findList();
    	return ok(charts.render(user, sims));
    }
    
    /*
     * ========================================================================================
     *                                        Utilities
     * ========================================================================================
     */
    
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
    
    /*
     * ========================================================================================
     *                                        JSON
     * ========================================================================================
     */
    
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

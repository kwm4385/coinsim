package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

@Entity
public class Simulation extends Model {
	
	@Id
	public Long id;
	
	@Constraints.Required
	public Long userId;
	
	@Constraints.Required
	public String name;
	
	@Constraints.Required
	@Constraints.Min(0)
	public Double tradingFee;
	
	@Constraints.Required
	@Constraints.Min(0)
	public Double dollars;
	
	@Constraints.Required
	@Constraints.Min(0)
	public Double coins;
	
	@Constraints.Required
	public String trades;
	
	@Transient
	private List<String> tradesList;

	@Transient
	public List<Trade> getTrades() {
		tradesList = Arrays.asList(trades.replace("{", "").replace("}", "").split(","));
		List<Trade> result = new ArrayList<Trade>();
		for(String id : tradesList) {
			result.add(Trade.find.byId(Long.parseLong(id)));
		}
		return result;
	}
	
	@Transient
	public void addTrade(Trade trade) {
		tradesList.add(trade.id.toString());
		trades = tradesList.toString();
		this.save();
	}
	
	public static Finder<Long, Simulation> find = new Finder<Long, Simulation>(Long.class, Simulation.class); 
}

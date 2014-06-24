package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import models.Trade.Type;
import play.Logger;
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
	public Double starting;
	
	@Constraints.Required
	@Constraints.Min(0)
	public Double coins = 0.0;
	
	@Constraints.Required
	public String trades;
	
	@Transient
	private List<Long> tradesList = new ArrayList<Long>();

	@Transient
	public List<Trade> getTrades() {
		if(trades == null || trades.equals("")) {
			return new ArrayList<Trade>();
		}
		tradesList.clear();
		for(String s : Arrays.asList(trades.replace("[", "").replace("]", "").split(","))) {
			s = s.trim();
			tradesList.add(Long.parseLong(s));
		}
		List<Trade> result = new ArrayList<Trade>();
		for(Long id : tradesList) {
			result.add(Trade.find.byId(id));
		}
		tradesList.clear();
		return result;
	}
	
	@Transient
	public void addTrade(Trade trade) {
		tradesList.clear();
		if(!(trades == null || trades.equals(""))) {
			for(String s : Arrays.asList(trades.replace("[", "").replace("]", "").split(","))) {
				s = s.trim();
				tradesList.add(Long.parseLong(s));
			}
		}
		tradesList.add(trade.id);
		trades = tradesList.toString();
		this.save();
		tradesList.clear();
	}
	
	public static Model.Finder<Long, Simulation> find = new Model.Finder<Long, Simulation>(Long.class, Simulation.class); 

}

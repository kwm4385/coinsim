package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class Trade extends Model {

	@Id
	public Long id;
	
	@Constraints.Required
	public Type type;
	
	@Constraints.Required
	@Constraints.Min(0)
	public Double amount;
	
	@Constraints.Required
	@Formats.DateTime(pattern="d MMM yyyy HH:mm:ss")
	public Date date = new Date();
	
	public static Finder<Long, Trade> find = new Finder<Long, Trade>(Long.class, Trade.class); 
	
	public enum Type {
		BUY, SELL;
	}
}

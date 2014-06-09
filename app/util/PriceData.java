package util;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import play.Logger;
import play.libs.F.Function0;
import play.libs.F.Promise;


/**
 * Class holding up to date bitcoin price data. \
 * Always a minute old at the latest.
 * @author kevin
 */
public class PriceData {
	
	// URL of the prices api
	public static final String API_URL = "https://api.bitcoinaverage.com/exchanges/USD";
	
	// How long prices are cached in seconds
	public static final int CACHE_LIFE = 30;
	
	private static final PriceUpdater PRICE_UPDATER = new PriceUpdater();
	
	private static double price = -1;
	private static Date lastUpdated = new Date(0L);
	private static String exchange = "bitstamp";
	
	/**
	 * Gets the last fetched price.
	 * @return
	 */
	public static Promise<Double> getPrice() {
		return Promise.promise(PRICE_UPDATER);
	}
	
	/**
	 * Gets the time the price was last updated.
	 * @return
	 */
	public static Date lastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * Gets the name of the exchange being used for price quotes.
	 * @return
	 */
	public static String getExchange() {
		return exchange;
	}
	
	/**
	 * Updates the price and last updated time. **Blocking**
	 * TODO: Prevent concurrent requests.
	 */
	private static class PriceUpdater implements Function0<Double> {
		@Override
		public Double apply() throws Throwable {
			if((((new Date().getTime() - lastUpdated.getTime()) / 1000) > CACHE_LIFE) || price == -1) {
				Logger.debug("Fetching new price");
				lastUpdated = new Date();
				try {
					URLConnection api = new URL(API_URL).openConnection();
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> jsonMap = mapper.readValue(api.getInputStream(), Map.class);
					price = Double.parseDouble(((Map)((Map)jsonMap.get(exchange)).get("rates")).get("ask").toString());
					Logger.debug(Double.toString(price));
				} catch (Exception e) {
					Logger.error("Error fetching price data");
				}
			} else {
				Logger.debug("Using cached price");
			}
			return price;
		}
	}
}
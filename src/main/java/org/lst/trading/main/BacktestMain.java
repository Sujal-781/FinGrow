package org.lst.trading.main;

import org.lst.trading.lib.backtest.Backtest;
import org.lst.trading.lib.model.ClosedOrder;
import org.lst.trading.lib.model.TradingStrategy;
import org.lst.trading.lib.series.MultipleDoubleSeries;
import org.lst.trading.lib.util.AlphaVantageHistoricalPriceService;
import org.lst.trading.lib.util.HistoricalPriceService;
import org.lst.trading.lib.util.Util;
import org.lst.trading.lib.chart.BacktestChartGenerator;
import org.lst.trading.main.strategy.kalman.CointegrationTradingStrategy;

import java.util.Locale;

import static java.lang.String.format;

public class BacktestMain {
    static String alphaVantageApiKey = "demo"; // Using demo key for testing

    public static void main(String[] args) throws Exception {
        findApiKey();

        String x = "GLD";
        String y = "GDX";

        // initialize the trading strategy
        TradingStrategy strategy = new CointegrationTradingStrategy(x, y);

        // download historical prices
        HistoricalPriceService finance = new AlphaVantageHistoricalPriceService(alphaVantageApiKey);
        MultipleDoubleSeries priceSeries = new MultipleDoubleSeries(finance.getHistoricalAdjustedPrices(x).toBlocking().first(), finance.getHistoricalAdjustedPrices(y).toBlocking().first());

        // initialize the backtesting engine
        int deposit = 15000;
        Backtest backtest = new Backtest(deposit, priceSeries);
        backtest.setLeverage(4);

        // do the backtest
        Backtest.Result result = backtest.run(strategy);

        // show results
        StringBuilder orders = new StringBuilder();
        orders.append("id,amount,side,instrument,from,to,open,close,pl\n");
        for (ClosedOrder order : result.getOrders()) {
            orders.append(format(Locale.US, "%d,%d,%s,%s,%s,%s,%f,%f,%f\n", order.getId(), Math.abs(order.getAmount()), order.isLong() ? "Buy" : "Sell", order.getInstrument(), order.getOpenInstant(), order.getCloseInstant(), order.getOpenPrice(), order.getClosePrice(), order.getPl()));
        }
        System.out.print(orders);

        int days = priceSeries.size();

        System.out.println();
        System.out.println("Backtest result of " + strategy.getClass() + ": " + strategy);
        System.out.println("Prices: " + priceSeries);
        System.out.println(format(Locale.US, "Simulated %d days, Initial deposit %d, Leverage %f", days, deposit, backtest.getLeverage()));
        System.out.println(format(Locale.US, "Commissions = %f", result.getCommissions()));
        System.out.println(format(Locale.US, "P/L = %.2f, Final value = %.2f, Result = %.2f%%, Annualized = %.2f%%, Sharpe (rf=0%%) = %.2f", result.getPl(), result.getFinalValue(), result.getReturn() * 100, result.getReturn() / (days / 251.) * 100, result.getSharpe()));

        String ordersFilePath = Util.writeStringToTempFile(orders.toString()).toString();
        System.out.println("Orders: " + ordersFilePath);
        System.out.println("Statistics: " + Util.writeCsv(new MultipleDoubleSeries(result.getPlHistory(), result.getMarginHistory())));
        
        // Generate and display P/L chart
        try {
            System.out.println("\nGenerating P/L chart...");
            BacktestChartGenerator.plotPLChartFromCsv(ordersFilePath);
        } catch (Exception e) {
            System.err.println("Error generating chart: " + e.getMessage());
        }
    }

    private static void findApiKey() {
        String key = System.getProperty("alphavantage.apikey");
        if (key != null) {
            alphaVantageApiKey = key;
        }

        if (alphaVantageApiKey.isEmpty() || alphaVantageApiKey.equals("demo")) {
            System.out.println("WARNING: Using demo API key. For production use, claim free alphavantage API key at https://www.alphavantage.co/support/#api-key and set via system property: -Dalphavantage.apikey=YOUR_KEY");
        }
    }
}

# FinGrow - Statistical Pairs Trading Backtester

A robust, Java-based platform to **backtest cointegration-driven trading strategies** using **Kalman filters** and real-world historical data. Built for quantitative finance enthusiasts, FinGrow emphasizes statistical rigor, modularity, and real trade simulation.

---

## 🚀 Key Features

* **Cointegration-Based Strategy**: Includes implementation of a dynamic hedge ratio using Kalman Filter for pairs trading.
* **Real Market Data Integration**: Pulls historical price data via [Alpha Vantage API](https://www.alphavantage.co).
* **Trade Simulation Engine**: Executes long/short trades with leverage, P/L tracking, and commission modeling.
* **Performance Metrics**: Calculates returns, Sharpe ratio, and final value; stores full trading logs.
* **Interactive Charting**: Visualizes cumulative profit/loss over time using XChart.
* **CSV Exports**: Outputs detailed trade and performance logs to CSV files.

---

## 🧪 Quick Start Guide

### ✅ Requirements

* Java 11 or newer
* Gradle (or use wrapper `./gradlew`)
* Free [Alpha Vantage API Key](https://www.alphavantage.co/support/#api-key)

### 🔧 Running the Backtest

```bash
# Build the project
./gradlew build

# Run the backtest with your Alpha Vantage API key
./gradlew run -Dalphavantage.apikey=YOUR_API_KEY
```

> The program will fetch data for pre-defined pairs (e.g., GLD & GDX), simulate trades, and open a P/L graph.

---

## 🖼 Sample Output

* 📈 Real-time chart: Cumulative Profit/Loss over time
* 📁 Files:

   * `orders-*.csv` – Executed trades and P/L breakdown
   * `statistics-*.csv` – Strategy statistics like Sharpe, drawdown

---

## 🏗 Project Structure

| Module                          | Purpose                                    |
| ------------------------------- | ------------------------------------------ |
| `org.lst.trading.lib.backtest`  | Core backtesting engine                    |
| `org.lst.trading.main.strategy` | Strategy definitions (e.g., Kalman-based)  |
| `org.lst.trading.lib.series`    | Time series representation                 |
| `org.lst.trading.lib.util`      | Utility classes: HTTP, data parsers, stats |
| `org.lst.trading.lib.model`     | Trading context and order execution        |

### 🔑 Notable Classes

* `Backtest`: Executes strategies, tracks portfolio P/L
* `TradingStrategy`: Interface for custom trading logic
* `CointegrationTradingStrategy`: Kalman-filter-driven pair trading logic
* `AlphaVantageHistoricalPriceService`: Fetches price data via API
* `DoubleSeries`, `MultipleDoubleSeries`: Time series containers

---

## 📊 Performance Metrics Computed

* **Total Return** & **Annualized Return**
* **Sharpe Ratio** (assumes zero risk-free rate)
* **Maximum Drawdown**
* **Win Rate** and **Commission Costs**

---

## ⚙️ Custom Strategy Example

```java
public class MyStrategy implements TradingStrategy {
    TradingContext context;

    public void onStart(TradingContext context) {
        this.context = context;
    }

    public void onTick() {
        double price = context.getLastPrice("AAPL");
        if (price < 100) context.order("AAPL", true, 50);  // Buy condition
    }

    public void onEnd() {
        // Final calculations
    }
}
```

### Backtest It:

```java
Backtest bt = new Backtest(10000, priceSeries);
bt.setLeverage(2.0);
Result result = bt.run(new MyStrategy());
```

---

## 🔌 Customizing Data Sources

You can replace the default data provider by implementing:

```java
public interface HistoricalPriceService {
    Observable<DoubleSeries> getHistoricalAdjustedPrices(String symbol);
}
```

---

## 🧠 Advanced Capabilities

### 📐 Kalman Filter

Built-in to model time-varying hedge ratio between cointegrated pairs:

```java
KalmanFilter kf = new KalmanFilter(2, 1);
kf.setUpdateMatrix(A);
kf.step(observedSpread);
```

### 🔍 Cointegration Model

```java
Cointegration coint = new Cointegration(delta, r);
coint.step(priceA, priceB);
double spread = coint.getError();
```

---

## 🧪 Testing

Run test suite:

```bash
./gradlew test
```

---

## 🤝 Contributing

1. Fork this repo
2. Create your branch (`git checkout -b feature-X`)
3. Add meaningful changes and tests
4. Create a PR with a clear description

---

## 📝 License

MIT License. Free for commercial and academic use. Contributions welcome!

---

## ⚠️ Disclaimer

This tool is for **educational and research purposes** only. It simulates past performance, which is not indicative of future results. Use with caution in real trading environments.

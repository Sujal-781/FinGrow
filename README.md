# FinGrow - Java Trading Backtest Engine

A comprehensive Java-based backtesting framework for algorithmic trading strategies with support for multiple instruments and advanced statistical analysis.

## Features

- **Backtest Engine**: Complete backtesting framework with P&L tracking, commission calculation, and performance metrics
- **Trading Strategies**: Includes cointegration-based pairs trading strategy using Kalman filters
- **Data Integration**: Alpha Vantage API integration for historical price data
- **Statistical Analysis**: Built-in statistics including Sharpe ratio, drawdown analysis, and returns calculation
- **CSV Support**: Import/export functionality for data analysis
- **Time Series Processing**: Comprehensive time series manipulation and analysis tools

## Quick Start

### Prerequisites

- Java 11 or higher
- Gradle 8.4+
- Alpha Vantage API key (free at https://www.alphavantage.co/support/#api-key)

### Building and Running

1. **Clone and build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run the backtest with demo data:**
   ```bash
   ./gradlew run
   ```

3. **Run with your own API key:**
   ```bash
   ./gradlew run -Dalphavantage.apikey=YOUR_API_KEY
   ```

### Example Output

The backtest will output:
- Individual trade details (CSV format)
- Performance metrics (returns, Sharpe ratio, drawdown)
- Strategy-specific statistics
- File paths to detailed CSV reports

## Architecture

### Core Components

- **Backtest Engine** (`org.lst.trading.lib.backtest`): Main backtesting framework
- **Trading Context** (`org.lst.trading.lib.model`): Interface for strategy execution
- **Time Series** (`org.lst.trading.lib.series`): Data structures for price and indicator data
- **Strategies** (`org.lst.trading.main.strategy`): Trading strategy implementations
- **Utilities** (`org.lst.trading.lib.util`): Data fetching, statistics, and helper functions

### Key Classes

- `Backtest`: Main backtesting engine
- `TradingStrategy`: Interface for implementing trading strategies
- `CointegrationTradingStrategy`: Pairs trading strategy using Kalman filters
- `DoubleSeries`/`MultipleDoubleSeries`: Time series data structures
- `AlphaVantageHistoricalPriceService`: Market data provider

## Strategy Development

### Creating a Custom Strategy

```java
public class MyStrategy implements TradingStrategy {
    private TradingContext context;
    
    @Override
    public void onStart(TradingContext context) {
        this.context = context;
        // Initialize strategy
    }
    
    @Override
    public void onTick() {
        // Execute trading logic on each price update
        double price = context.getLastPrice("AAPL");
        if (shouldBuy(price)) {
            context.order("AAPL", true, 100); // Buy 100 shares
        }
    }
    
    @Override
    public void onEnd() {
        // Cleanup and final calculations
    }
}
```

### Running Custom Strategies

```java
TradingStrategy strategy = new MyStrategy();
Backtest backtest = new Backtest(10000, priceSeries); // $10,000 initial capital
Backtest.Result result = backtest.run(strategy);
```

## Configuration

### System Properties

- `-Dalphavantage.apikey=KEY`: Set Alpha Vantage API key
- `-Djava.util.logging.config.file=logging.properties`: Configure logging

### Backtest Parameters

```java
Backtest backtest = new Backtest(initialCapital, priceSeries);
backtest.setLeverage(2.0); // Set leverage multiplier
```

## Performance Metrics

The framework calculates comprehensive performance metrics:

- **Returns**: Total and annualized returns
- **Sharpe Ratio**: Risk-adjusted returns (assuming 0% risk-free rate)
- **Maximum Drawdown**: Largest peak-to-trough decline
- **Win Rate**: Percentage of profitable trades
- **Commission Costs**: Total transaction costs

## Data Sources

### Alpha Vantage Integration

The framework includes built-in support for Alpha Vantage market data:

```java
HistoricalPriceService dataService = new AlphaVantageHistoricalPriceService(apiKey);
Observable<DoubleSeries> prices = dataService.getHistoricalAdjustedPrices("AAPL");
```

### Custom Data Sources

Implement the `HistoricalPriceService` interface for custom data providers:

```java
public class CustomDataService implements HistoricalPriceService {
    @Override
    public Observable<DoubleSeries> getHistoricalAdjustedPrices(String symbol) {
        // Your data fetching logic
    }
}
```

## Advanced Features

### Kalman Filter Implementation

The framework includes a complete Kalman filter implementation for state estimation in trading strategies:

```java
KalmanFilter filter = new KalmanFilter(stateCount, sensorCount);
filter.setUpdateMatrix(transitionMatrix);
filter.step(measurement);
```

### Cointegration Analysis

Built-in cointegration analysis for pairs trading:

```java
Cointegration coint = new Cointegration(delta, r);
coint.step(priceX, priceY);
double spread = coint.getError();
```

## Testing

Run the test suite:

```bash
./gradlew test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This software is for educational and research purposes only. Past performance does not guarantee future results. Always conduct thorough testing before using any trading strategy with real money.
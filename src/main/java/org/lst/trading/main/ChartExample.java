package org.lst.trading.main;

import org.lst.trading.lib.chart.BacktestChartGenerator;

import java.io.IOException;

/**
 * Example class demonstrating how to use the BacktestChartGenerator
 * with the output from BacktestMain.
 */
public class ChartExample {
    
    public static void main(String[] args) {
        // Example usage - replace with actual path from BacktestMain output
        String csvFilePath = "/tmp/out-example.csv";
        
        try {
            System.out.println("Generating P/L chart from: " + csvFilePath);
            BacktestChartGenerator.plotPLChartFromCsv(csvFilePath);
            System.out.println("Chart displayed successfully!");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Utility method to generate chart from the most recent backtest run.
     * This can be called after running BacktestMain to visualize results.
     */
    public static void showLastBacktestResults() {
        // This would need to be integrated with BacktestMain to get the actual file path
        System.out.println("To use this feature, modify BacktestMain to save the CSV file path");
        System.out.println("and call BacktestChartGenerator.plotPLChartFromCsv(filePath)");
    }
}
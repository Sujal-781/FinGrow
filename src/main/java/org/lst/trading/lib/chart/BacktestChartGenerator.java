package org.lst.trading.lib.chart;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating P/L charts from backtest CSV output files.
 * 
 * The CSV file is expected to have the following format:
 * id,amount,side,instrument,from,to,open,close,pl
 * 1,329,Sell,GDX,2025-03-27T00:00:00Z,2025-03-28T00:00:00Z,45.76,45.57,62.51
 */
public class BacktestChartGenerator {
    
    /**
     * Data class to hold parsed trade information
     */
    private static class TradeData {
        private final Date date;
        private final double profitLoss;
        
        public TradeData(Date date, double profitLoss) {
            this.date = date;
            this.profitLoss = profitLoss;
        }
        
        public Date getDate() {
            return date;
        }
        
        public double getProfitLoss() {
            return profitLoss;
        }
    }
    
    /**
     * Plots P/L over time from a CSV file and displays it in a Swing window.
     * 
     * @param filePath Path to the CSV file containing backtest results
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the CSV format is invalid
     */
    public static void plotPLChartFromCsv(String filePath) throws IOException {
        List<TradeData> trades = parseCsvFile(filePath);
        
        if (trades.isEmpty()) {
            throw new IllegalArgumentException("No valid trade data found in CSV file: " + filePath);
        }
        
        // Create chart
        XYChart chart = createChart(trades);
        
        // Display chart in Swing window
        new SwingWrapper<>(chart).displayChart();
    }
    
    /**
     * Creates and returns an XYChart with P/L data over time.
     * 
     * @param trades List of trade data
     * @return Configured XYChart
     */
    public static XYChart createChart(List<TradeData> trades) {
        // Prepare data for chart
        List<Date> dates = new ArrayList<>();
        List<Double> cumulativePL = new ArrayList<>();
        
        double runningTotal = 0.0;
        for (TradeData trade : trades) {
            dates.add(trade.getDate());
            runningTotal += trade.getProfitLoss();
            cumulativePL.add(runningTotal);
        }
        
        // Create chart
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(600)
                .title("Profit/Loss Over Time")
                .xAxisTitle("Date")
                .yAxisTitle("Cumulative P/L ($)")
                .build();
        
        // Customize chart style
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDatePattern("yyyy-MM-dd");
        chart.getStyler().setDecimalPattern("#,###.##");
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setXAxisTickMarkSpacingHint(100);
        
        // Add data series
        chart.addSeries("Cumulative P/L", dates, cumulativePL);
        
        return chart;
    }
    
    /**
     * Parses the CSV file and extracts trade data.
     * 
     * @param filePath Path to the CSV file
     * @return List of TradeData objects
     * @throws IOException if the file cannot be read
     */
    private static List<TradeData> parseCsvFile(String filePath) throws IOException {
        List<TradeData> trades = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header
            
            if (line == null) {
                throw new IllegalArgumentException("CSV file is empty: " + filePath);
            }
            
            // Validate header format
            if (!line.toLowerCase().contains("id") || !line.toLowerCase().contains("pl") || !line.toLowerCase().contains("to")) {
                throw new IllegalArgumentException("Invalid CSV header format. Expected columns: id, pl, to");
            }
            
            // Find column indices
            String[] headers = line.split(",");
            int toColumnIndex = findColumnIndex(headers, "to");
            int plColumnIndex = findColumnIndex(headers, "pl");
            
            // Parse data rows
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                try {
                    String[] columns = line.split(",");
                    
                    if (columns.length <= Math.max(toColumnIndex, plColumnIndex)) {
                        System.err.println("Skipping malformed line: " + line);
                        continue;
                    }
                    
                    // Parse date from 'to' column (ISO format: 2025-03-28T00:00:00Z)
                    String dateStr = columns[toColumnIndex].trim();
                    Date date = parseIsoDate(dateStr);
                    
                    // Parse profit/loss
                    String plStr = columns[plColumnIndex].trim();
                    double profitLoss = Double.parseDouble(plStr);
                    
                    trades.add(new TradeData(date, profitLoss));
                    
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        }
        
        return trades;
    }
    
    /**
     * Finds the index of a column by name (case-insensitive).
     * 
     * @param headers Array of header names
     * @param columnName Name of the column to find
     * @return Index of the column
     * @throws IllegalArgumentException if column is not found
     */
    private static int findColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().toLowerCase().equals(columnName.toLowerCase())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found in CSV header");
    }
    
    /**
     * Parses an ISO date string to a Date object.
     * 
     * @param dateStr ISO date string (e.g., "2025-03-28T00:00:00Z")
     * @return Date object
     */
    private static Date parseIsoDate(String dateStr) {
        try {
            // Parse ISO instant and convert to Date
            Instant instant = Instant.parse(dateStr);
            return Date.from(instant);
        } catch (Exception e) {
            // Fallback: try parsing as local date
            try {
                LocalDate localDate = LocalDate.parse(dateStr.substring(0, 10));
                return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (Exception e2) {
                throw new IllegalArgumentException("Unable to parse date: " + dateStr, e2);
            }
        }
    }
    
    /**
     * Example usage and testing method.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java BacktestChartGenerator <csv-file-path>");
            System.exit(1);
        }
        
        try {
            plotPLChartFromCsv(args[0]);
        } catch (Exception e) {
            System.err.println("Error generating chart: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
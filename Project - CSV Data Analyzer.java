import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CSVDataAnalyzer {

    // -----------------------------------------------------------------
    // Fields: hold the parsed CSV data
    // -----------------------------------------------------------------
    private String[] headers;                       // column names
    private List<String[]> rows;                    // raw string rows
    private String fileName;

    public CSVDataAnalyzer() {
        rows = new ArrayList<>();
    }

    // -----------------------------------------------------------------
    // Method: loadCSV
    // Reads the CSV file line by line, splits by comma, stores headers
    // and data rows separately.
    // -----------------------------------------------------------------
    public boolean loadCSV(String path) {
        this.fileName = path;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            rows.clear();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip blank lines
                }
                String[] tokens = line.split(",");

                // Trim whitespace from every token
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].trim();
                }

                if (firstLine) {
                    headers = tokens;
                    firstLine = false;
                } else {
                    rows.add(tokens);
                }
            }
            System.out.println("\n[OK] File \"" + path + "\" loaded successfully.");
            System.out.println("[OK] Columns found: " + headers.length +
                    " | Data rows found: " + rows.size());
            return true;

        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------------
    // Method: displayHeaders
    // Prints the column names with their index so the user can pick one.
    // -----------------------------------------------------------------
    public void displayHeaders() {
        System.out.println("\n--- Available Columns ---");
        for (int i = 0; i < headers.length; i++) {
            System.out.println("  [" + i + "]  " + headers[i]);
        }
    }

    // -----------------------------------------------------------------
    // Method: displayData
    // Prints the first N rows of the loaded data in a table-like format.
    // -----------------------------------------------------------------
    public void displayData(int limit) {
        System.out.println("\n--- Data Preview (" + Math.min(limit, rows.size()) +
                " of " + rows.size() + " rows) ---");

        // print header row
        StringBuilder sb = new StringBuilder();
        for (String h : headers) {
            sb.append(String.format("%-15s", h));
        }
        System.out.println(sb.toString());
        System.out.println("-".repeat(sb.length()));

        for (int i = 0; i < Math.min(limit, rows.size()); i++) {
            String[] row = rows.get(i);
            StringBuilder line = new StringBuilder();
            for (String cell : row) {
                line.append(String.format("%-15s", cell));
            }
            System.out.println(line.toString());
        }
    }

    // -----------------------------------------------------------------
    // Method: extractColumn
    // Extracts a numeric column as a List<Double>. Non-numeric or
    // missing values are skipped with a warning.
    // -----------------------------------------------------------------
    public List<Double> extractColumn(int colIndex) {
        List<Double> values = new ArrayList<>();
        for (int r = 0; r < rows.size(); r++) {
            String[] row = rows.get(r);
            if (colIndex >= row.length) continue;
            try {
                double val = Double.parseDouble(row[colIndex]);
                values.add(val);
            } catch (NumberFormatException e) {
                System.out.println("[WARN] Skipping non-numeric value at row " +
                        (r + 2) + ": \"" + row[colIndex] + "\"");
            }
        }
        return values;
    }

    // -----------------------------------------------------------------
    // Statistical Methods
    // -----------------------------------------------------------------

    public double calculateSum(List<Double> data) {
        double sum = 0;
        for (double d : data) sum += d;
        return sum;
    }

    public double calculateAverage(List<Double> data) {
        if (data.isEmpty()) return 0;
        return calculateSum(data) / data.size();
    }

    public double calculateMin(List<Double> data) {
        double min = Double.MAX_VALUE;
        for (double d : data) if (d < min) min = d;
        return min;
    }

    public double calculateMax(List<Double> data) {
        double max = Double.MIN_VALUE;
        for (double d : data) if (d > max) max = d;
        return max;
    }

    public double calculateRange(List<Double> data) {
        return calculateMax(data) - calculateMin(data);
    }

    public double calculateMedian(List<Double> data) {
        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n == 0) return 0;
        if (n % 2 == 0) {
            return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        } else {
            return sorted.get(n / 2);
        }
    }

    public List<Double> calculateMode(List<Double> data) {
        Map<Double, Integer> freqMap = new HashMap<>();
        for (double d : data) {
            freqMap.put(d, freqMap.getOrDefault(d, 0) + 1);
        }

        int maxFreq = 0;
        for (int freq : freqMap.values()) {
            if (freq > maxFreq) maxFreq = freq;
        }

        List<Double> modes = new ArrayList<>();
        if (maxFreq <= 1) return modes; // no repeating value -> no mode

        for (Map.Entry<Double, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() == maxFreq) {
                modes.add(entry.getKey());
            }
        }
        Collections.sort(modes);
        return modes;
    }

    public double calculateVariance(List<Double> data) {
        double mean = calculateAverage(data);
        double sumSquaredDiff = 0;
        for (double d : data) {
            sumSquaredDiff += Math.pow(d - mean, 2);
        }
        return data.isEmpty() ? 0 : sumSquaredDiff / data.size();
    }

    public double calculateStdDeviation(List<Double> data) {
        return Math.sqrt(calculateVariance(data));
    }

    // -----------------------------------------------------------------
    // Method: sortColumn
    // Returns a sorted copy of the column values (ascending or
    // descending based on the boolean flag).
    // -----------------------------------------------------------------
    public List<Double> sortColumn(List<Double> data, boolean ascending) {
        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }

    // -----------------------------------------------------------------
    // Method: filterGreaterThan / filterLessThan
    // Returns values from the column matching the condition.
    // -----------------------------------------------------------------
    public List<Double> filterGreaterThan(List<Double> data, double threshold) {
        List<Double> result = new ArrayList<>();
        for (double d : data) {
            if (d > threshold) result.add(d);
        }
        return result;
    }

    public List<Double> filterLessThan(List<Double> data, double threshold) {
        List<Double> result = new ArrayList<>();
        for (double d : data) {
            if (d < threshold) result.add(d);
        }
        return result;
    }

    // -----------------------------------------------------------------
    // Method: printStatistics
    // Prints a full statistical breakdown of a numeric column.
    // -----------------------------------------------------------------
    public void printStatistics(String columnName, List<Double> data) {
        if (data.isEmpty()) {
            System.out.println("[ERROR] No numeric data found in column \"" +
                    columnName + "\".");
            return;
        }

        System.out.println("\n===================================================");
        System.out.println(" STATISTICS FOR COLUMN: " + columnName);
        System.out.println("===================================================");
        System.out.printf("  Count               : %d%n", data.size());
        System.out.printf("  Sum                 : %.4f%n", calculateSum(data));
        System.out.printf("  Average (Mean)      : %.4f%n", calculateAverage(data));
        System.out.printf("  Minimum             : %.4f%n", calculateMin(data));
        System.out.printf("  Maximum             : %.4f%n", calculateMax(data));
        System.out.printf("  Range               : %.4f%n", calculateRange(data));
        System.out.printf("  Median              : %.4f%n", calculateMedian(data));

        List<Double> modes = calculateMode(data);
        if (modes.isEmpty()) {
            System.out.println("  Mode                : No repeating value");
        } else {
            System.out.println("  Mode                : " + modes);
        }

        System.out.printf("  Variance            : %.4f%n", calculateVariance(data));
        System.out.printf("  Standard Deviation  : %.4f%n", calculateStdDeviation(data));
        System.out.println("===================================================");
    }

    // -----------------------------------------------------------------
    // Method: exportSummaryReport
    // Writes a summary of the numeric column statistics to a new file.
    // -----------------------------------------------------------------
    public void exportSummaryReport(String columnName, List<Double> data, String outFile) {
        try (FileWriter fw = new FileWriter(outFile)) {
            fw.write("CSV DATA ANALYZER - SUMMARY REPORT\n");
            fw.write("Source File : " + fileName + "\n");
            fw.write("Column      : " + columnName + "\n");
            fw.write("-------------------------------------------\n");
            fw.write("Count       : " + data.size() + "\n");
            fw.write(String.format("Sum         : %.4f%n", calculateSum(data)));
            fw.write(String.format("Average     : %.4f%n", calculateAverage(data)));
            fw.write(String.format("Minimum     : %.4f%n", calculateMin(data)));
            fw.write(String.format("Maximum     : %.4f%n", calculateMax(data)));
            fw.write(String.format("Median      : %.4f%n", calculateMedian(data)));
            fw.write(String.format("Std Dev     : %.4f%n", calculateStdDeviation(data)));
            fw.write("-------------------------------------------\n");
            System.out.println("[OK] Summary report exported to \"" + outFile + "\"");
        } catch (IOException e) {
            System.out.println("[ERROR] Could not write report: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Getter helpers used by main()
    // -----------------------------------------------------------------
    public String[] getHeaders() {
        return headers;
    }

    public int getRowCount() {
        return rows.size();
    }

    // ===================================================================
    //  MAIN METHOD - Menu Driven Interface
    // ===================================================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CSVDataAnalyzer analyzer = new CSVDataAnalyzer();

        System.out.println("=====================================================");
        System.out.println("            CSV DATA ANALYZER - JAVA PROJECT         ");
        System.out.println("=====================================================");

        // ---- Step 1: Load the CSV file ----
        boolean loaded = false;
        while (!loaded) {
            System.out.print("\nEnter the path of the CSV file to load: ");
            String path = sc.nextLine();
            loaded = analyzer.loadCSV(path);
            if (!loaded) {
                System.out.println("Please try again with a valid file path.");
            }
        }

        // ---- Step 2: Menu Loop ----
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n----------------- MAIN MENU -----------------");
            System.out.println(" 1. View Column Headers");
            System.out.println(" 2. Preview Data (first N rows)");
            System.out.println(" 3. Full Statistics of a Column");
            System.out.println(" 4. Sort a Column (Ascending/Descending)");
            System.out.println(" 5. Filter a Column (Greater / Less Than)");
            System.out.println(" 6. Export Summary Report to File");
            System.out.println(" 0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    analyzer.displayHeaders();
                    break;

                case 2:
                    System.out.print("How many rows to preview? ");
                    int n = Integer.parseInt(sc.nextLine().trim());
                    analyzer.displayData(n);
                    break;

                case 3: {
                    analyzer.displayHeaders();
                    System.out.print("Enter column index to analyze: ");
                    int col = Integer.parseInt(sc.nextLine().trim());
                    List<Double> data = analyzer.extractColumn(col);
                    analyzer.printStatistics(analyzer.getHeaders()[col], data);
                    break;
                }

                case 4: {
                    analyzer.displayHeaders();
                    System.out.print("Enter column index to sort: ");
                    int col = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Sort ascending? (yes/no): ");
                    boolean asc = sc.nextLine().trim().equalsIgnoreCase("yes");
                    List<Double> data = analyzer.extractColumn(col);
                    List<Double> sorted = analyzer.sortColumn(data, asc);
                    System.out.println("\nSorted Values: " + sorted);
                    break;
                }

                case 5: {
                    analyzer.displayHeaders();
                    System.out.print("Enter column index to filter: ");
                    int col = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Filter type - 1: Greater Than, 2: Less Than: ");
                    int type = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Enter threshold value: ");
                    double threshold = Double.parseDouble(sc.nextLine().trim());
                    List<Double> data = analyzer.extractColumn(col);

                    List<Double> filtered;
                    if (type == 1) {
                        filtered = analyzer.filterGreaterThan(data, threshold);
                    } else {
                        filtered = analyzer.filterLessThan(data, threshold);
                    }
                    System.out.println("\nMatching Values (" + filtered.size() +
                            " found): " + filtered);
                    break;
                }

                case 6: {
                    analyzer.displayHeaders();
                    System.out.print("Enter column index for report: ");
                    int col = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Enter output file name (e.g. report.txt): ");
                    String outFile = sc.nextLine().trim();
                    List<Double> data = analyzer.extractColumn(col);
                    analyzer.exportSummaryReport(analyzer.getHeaders()[col], data, outFile);
                    break;
                }

                case 0:
                    System.out.println("\nExiting CSV Data Analyzer. Goodbye!");
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice. Please select from the menu.");
            }
        }

        sc.close();
    }
}

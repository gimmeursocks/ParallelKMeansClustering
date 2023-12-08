package com.parallelkmeansclustering;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.List;

public class OpenCSVReader {

    private final static String absLoc = "src/main/resources/";

    public static void readDataLineByLine(String file) {

        try {
            // add absolute location
            file = absLoc + file;

            // Create an object of filereader
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            String[] nextRecord;

            // read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }

            // close when done
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CSVFile readAllData(String file) {

        try {
            // add absolute location
            file = absLoc + file;

            // Create an object of filereader
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            CSVReader csvReader = new CSVReader(filereader);
            // create data objects
            List<String[]> allData = csvReader.readAll();
            String[] colNames = allData.get(0);
            
            double[][] data = new double[allData.size()-1][allData.get(0).length];

            // read data line by line
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = Double.parseDouble(allData.get(i+1)[j]);
                }
            }
            
            // close when done
            csvReader.close();
            
            return new CSVFile(colNames, data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class CSVFile {
        public double[][] data;
        public String[] colNames;
    
        public CSVFile(String[] colNames, double[][] data){
            this.colNames = colNames;
            this.data = data;
        }
    }
    
}

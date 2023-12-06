package com.parallelkmeansclustering;

public class CSVFile {
    public double[][] data;
    public String[] colNames;

    public CSVFile(String[] colNames, double[][] data){
        this.colNames = colNames;
        this.data = data;
    }
}

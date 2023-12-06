package com.parallelkmeansclustering;

public class CSVTest {
    public static void main(String[] args) {
        CSVFile file = OpenCSVReader.readAllData("sample_data.csv");

        if (file.data != null) {
            KMeansAlgo model = new KMeansAlgo(4);

            model.fit(file.data);
        }
    }
}

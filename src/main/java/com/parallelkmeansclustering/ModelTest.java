package com.parallelkmeansclustering;

public class ModelTest {
    public static void main(String[] args) {
        OpenCSVReader.CSVFile file = OpenCSVReader.readAllData("20000_encoded_large_data.csv");

        if (file.data != null) {

            for (int i = 1; i < 10; i++) {
                KMeansAlgo model = new KMeansAlgo(i, 10, 30);
                ParallelKMeansAlgo pmodel = new ParallelKMeansAlgo(i, 10, 30);

                System.out.println("Number of Clusters: " + i);

                model.fit(file.data);
                pmodel.fit(file.data);

                System.out.print("WCSS = ");
                System.out.printf("%.1f \n", model.getWCSS());

                System.out.print("pWCSS = ");
                System.out.printf("%.1f \n", pmodel.getWCSS());

                // int[] ans = model.getAssignments();

                // for (int j = 0; j < 25; j++) {
                // System.out.print(ans[j]+" ");
                // }
                // System.out.println();

                // for (double[] centroid : model.getCentroids()) {
                // for (double col : centroid) {
                // System.out.print(col+" ");
                // }
                // System.out.println();
                // }
            }
        }
    }
}

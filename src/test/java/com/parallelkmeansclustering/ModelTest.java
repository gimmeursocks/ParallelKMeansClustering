package com.parallelkmeansclustering;

public class ModelTest {
    public static void main(String[] args) {
        CSVFile file = OpenCSVReader.readAllData("scaled_sample_data.csv");

        if (file.data != null) {

            for (int i = 1; i < 10; i++) {
                KMeansAlgo model = new KMeansAlgo(i, 10, 30);
    
                model.fit(file.data);
                
                System.out.print("WCSS = ");
                System.out.printf("%.1f \n",model.getWCSS());

                int[] ans = model.getAssignments();

                for (int j = 0; j < 25; j++) {
                    System.out.print(ans[j]+" ");
                }
                System.out.println();

                // for (double[] centroid : model.getCentroids()) {
                //     for (double col : centroid) {
                //         System.out.print(col+" ");
                //     }
                //     System.out.println();
                // }
            }
        }
    }
}

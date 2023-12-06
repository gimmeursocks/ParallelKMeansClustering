package com.parallelkmeansclustering;

import java.util.Random;

public class KMeansAlgo {
    private int k;
    private double[][] data;
    private int rows;
    private int cols;
    private int[] assignments;
    private double[][] centroids;

    public KMeansAlgo(int k) {
        this.k = k;
    }

    public void fit(double[][] data) {
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
        this.assignments = new int[rows];
        this.centroids = new double[k][cols];

        cluster();
    }

    private void cluster() {
        placeInitialCentroids();
        int iter = 30;

        do {
            assign();

            update();

            iter--;
        } while (iter > 0);

        for (int i : assignments) {
            System.out.println(i);
        }
    }

    private void assign() {
        double tempDist;
        double minDist;
        int minInd;

        for (int i = 0; i < rows; i++) {
            minDist = Double.MAX_VALUE;
            minInd = 0;
            for (int j = 0; j < k; j++) {
                tempDist = distance(data[i], centroids[j]);
                if (tempDist < minDist) {
                    minDist = tempDist;
                    minInd = j;
                }
            }
            assignments[i] = minInd;
        }
    }

    private void update() {
        for (double[] centroid : centroids) {
            java.util.Arrays.fill(centroid, 0);
        }

        int[] centroidCount = new int[k];

        for (int i = 0; i < rows; i++) {
            centroidCount[assignments[i]]++;
            for (int j = 0; j < cols; j++) {
                centroids[assignments[i]][j] += data[i][j];
            }
        }

        for (int i = 0; i < k; i++) {
            if (centroidCount[i] != 0) {
                for (int j = 0; j < cols; j++) {
                    centroids[i][j] /= centroidCount[i];
                }
            }
        }
    }

    private void placeInitialCentroids() {
        double[] maxj = new double[cols];
        double[] minj = new double[cols];

        java.util.Arrays.fill(maxj, Double.MIN_VALUE);
        java.util.Arrays.fill(minj, Double.MAX_VALUE);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (data[i][j] > maxj[j])
                    maxj[j] = data[i][j];

                if (data[i][j] < minj[j])
                    minj[j] = data[i][j];
            }
        }

        Random r = new Random();
        for (double[] centroid : centroids) {
            for (int i = 0; i < cols; i++) {
                double rand = minj[i] + (r.nextDouble() * (maxj[i] - minj[i]));
                centroid[i] = Math.round(rand * 10.0) / 10.0;
            }
        }
    }

    public static double distance(double[] x, double[] y) {
        double dist = 0;

        for (int i = 0; i < x.length; i++) {
            dist += Math.pow(x[i] - y[i], 2);
        }

        return Math.sqrt(dist);
    }
}
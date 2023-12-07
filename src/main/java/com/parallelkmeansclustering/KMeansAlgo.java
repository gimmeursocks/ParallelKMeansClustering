package com.parallelkmeansclustering;

import java.util.Random;

public class KMeansAlgo {
    private int k;
    private int maxInit;
    private int maxIter;
    private double WCSS;
    private double[][] data;
    private int rows;
    private int cols;
    private double[] maxCol;
    private double[] minCol;
    private int[] assignments;
    private double[][] centroids;

    public KMeansAlgo(int k, int maxInit, int maxIter) {
        this.k = k;
        this.maxInit = maxInit;
        this.maxIter = maxIter;
    }

    public void fit(double[][] data) {
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
        this.assignments = new int[rows];
        this.centroids = new double[k][cols];
        this.maxCol = new double[cols];
        this.minCol = new double[cols];

        java.util.Arrays.fill(maxCol, Double.MIN_VALUE);
        java.util.Arrays.fill(minCol, Double.MAX_VALUE);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (data[i][j] > maxCol[j])
                    maxCol[j] = data[i][j];

                if (data[i][j] < minCol[j])
                    minCol[j] = data[i][j];
            }
        }

        double bestWCSS = Double.MAX_VALUE;
        double[][] bestCentroids = new double[k][cols];
        int[] bestAssignments = new int[rows];

        for (int i = 0; i < maxInit; i++) {
            cluster();

            if (WCSS < bestWCSS) {
                bestWCSS = WCSS;
                bestCentroids = centroids;
                bestAssignments = assignments;
            }
        }

        WCSS = bestWCSS;
        centroids = bestCentroids;
        assignments = bestAssignments;
    }

    private void cluster() {
        placeInitialCentroids();

        WCSS = Double.MAX_VALUE;
        double prevWCSS;
        int iter = maxIter;
        do {
            assign();

            update();

            prevWCSS = WCSS;
            calcWCSS();

            iter--;
        } while (iter > 0 && (prevWCSS != WCSS));
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
            } else {
                placeInitialCentroid(i);
            }
        }
    }

    private void placeInitialCentroids() {
        Random r = new Random();
        for (double[] centroid : centroids) {
            for (int i = 0; i < cols; i++) {
                double rand = minCol[i] + (r.nextDouble() * (maxCol[i] - minCol[i]));
                centroid[i] = Math.round(rand * 10.0) / 10.0;
            }
        }
    }

    private void placeInitialCentroid(int clusterInd) {
        Random r = new Random();
        for (int i = 0; i < cols; i++) {
            double rand = minCol[i] + (r.nextDouble() * (maxCol[i] - minCol[i]));
            centroids[clusterInd][i] = Math.round(rand * 10.0) / 10.0;
        }
    }

    public static double distance(double[] x, double[] y) {
        double dist = 0;

        for (int i = 0; i < x.length; i++) {
            dist += Math.pow(x[i] - y[i], 2);
        }

        return Math.sqrt(dist);
    }

    private void calcWCSS() {
        double WCSS = 0;

        for (int i = 0; i < rows; i++) {
            WCSS += Math.pow(distance(data[i], centroids[assignments[i]]), 2);
        }

        this.WCSS = WCSS;
    }

    public int[] getAssignments() {
        return assignments;
    }

    public double getWCSS() {
        return WCSS;
    }

    public double[][] getCentroids() {
        return centroids;
    }
}
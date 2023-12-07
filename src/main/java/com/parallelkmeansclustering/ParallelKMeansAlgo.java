package com.parallelkmeansclustering;

import java.util.Random;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ParallelKMeansAlgo {
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

    public ParallelKMeansAlgo(int k, int maxInit, int maxIter) {
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

            centroidInitialize();
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            forkJoinPool.invoke(new KMeansTask(0, rows));

            calcWCSS(0, rows);

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

    private class KMeansTask extends RecursiveAction {
        private static final int THRESHOLD = 100;
        private int start;
        private int end;

        public KMeansTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                int iter = maxIter;

                do {
                    parallelAssign(start, end);

                    parallelUpdate(start, end);

                    iter--;
                } while (iter > 0);

            } else {
                int mid = start + (end - start) / 2;
                invokeAll(
                        new KMeansTask(start, mid),
                        new KMeansTask(mid, end));
            }
        }

        private void parallelAssign(int start, int end) {
            for (int i = start; i < end; i++) {
                double tempDist;
                double minDist = Double.MAX_VALUE;
                int minInd = 0;

                for (int j = 0; j < k; j++) {
                    tempDist = distance(data[i], centroids[j]);
                    if (tempDist < minDist) {
                        minDist = tempDist;
                        minInd = j;
                    }
                }

                synchronized(assignments){
                    assignments[i] = minInd;
                }
            }
        }

        private void parallelUpdate(int start, int end) {
            for (double[] centroid : centroids) {
                java.util.Arrays.fill(centroid, 0);
            }

            int[] centroidCount = new int[k];

            synchronized(centroids){

                for (int i = start; i < end; i++) {
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
        }
    }

    private void centroidInitialize() {
        for (int i = 0; i < k; i++) {
            placeInitialCentroid(i);
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

    private void calcWCSS(int start, int end) {
        double WCSS = 0;

        for (int i = start; i < end; i++) {
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
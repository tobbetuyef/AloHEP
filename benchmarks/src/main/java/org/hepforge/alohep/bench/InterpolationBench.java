package org.hepforge.alohep.bench;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class InterpolationBench {

    @Param({"30", "100", "200"})
    public int gridSize;

    private double[][] grid;
    private double[][] bicubicGrid;
    private double testX;
    private double testY;
    private int testI;
    private int testJ;
    private static final int EXTRA_BOUNDARY = 2;
    private Random rand;

    @Setup
    public void setup() {
        rand = new Random(42);
        int gSize = gridSize + 2 * EXTRA_BOUNDARY + 1;
        grid = new double[gSize][gSize];
        bicubicGrid = new double[gSize][gSize];
        for (int m = 0; m < gSize; m++) {
            for (int n = 0; n < gSize; n++) {
                grid[m][n] = rand.nextDouble();
                bicubicGrid[m][n] = rand.nextDouble();
            }
        }
        testI = 1 + rand.nextInt(gridSize - 2);
        testJ = 1 + rand.nextInt(gridSize - 2);
        testX = rand.nextDouble();
        testY = rand.nextDouble();
    }

    @Benchmark
    public double bicubic() {
        return bicubicInterpolation(grid, testI, testJ, testX, testY);
    }

    @Benchmark
    public double bilinear() {
        return bilinearInterpolation(grid, testI, testJ, testX, testY);
    }

    private static double bicubicInterpolation(double[][] F, int i, int j, double x, double y) {
        double[] arr = new double[4];
        arr[0] = cubicInterpolation(F[i - 1][j - 1], F[i][j - 1], F[i + 1][j - 1], F[i + 2][j - 1], y);
        arr[1] = cubicInterpolation(F[i - 1][j],     F[i][j],     F[i + 1][j],     F[i + 2][j],     y);
        arr[2] = cubicInterpolation(F[i - 1][j + 1], F[i][j + 1], F[i + 1][j + 1], F[i + 2][j + 1], y);
        arr[3] = cubicInterpolation(F[i - 1][j + 2], F[i][j + 2], F[i + 1][j + 2], F[i + 2][j + 2], y);
        return cubicInterpolation(arr[0], arr[1], arr[2], arr[3], x);
    }

    private static double cubicInterpolation(double p0, double p1, double p2, double p3, double x) {
        return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3 + x * (3.0 * (p1 - p2) + p3 - p0)));
    }

    private static double bilinearInterpolation(double[][] F, int i, int j, double x, double y) {
        double v00 = F[i][j];
        double v10 = F[i + 1][j];
        double v01 = F[i][j + 1];
        double v11 = F[i + 1][j + 1];
        double top = lip(v00, v10, x);
        double bot = lip(v01, v11, x);
        return lip(top, bot, y);
    }

    private static double lip(double v1, double v2, double w) {
        return v1 * (1 - w) + w * v2;
    }
}

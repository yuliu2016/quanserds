package io.quanserds.comm.struct;

public class Matrix44 {
    private final double[] M;

    public Matrix44(double[] m) {
        if (m.length != 16) {
            throw new IllegalArgumentException();
        }
        M = m;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public Matrix44 mult(Matrix44 other) {
        double[] A = M;
        double[] B = other.M;
        double[] C = new double[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Compute the dot product of row i of A
                // and column j of B
                double dot = 0;

                for (int k = 0; k < 4; k++) {
                    dot += A[i * 4 + k] * B[k * 4 + j];
                }

                C[i * 4 + j] = dot;
            }
        }

        return new Matrix44(C);
    }

    public double get(int row, int col) {
        return M[row * 4 + col];
    }
}

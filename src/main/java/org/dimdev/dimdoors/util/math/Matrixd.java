package org.dimdev.dimdoors.util.math;

public class Matrixd extends MatrixdImpl<Matrixd> {
	public static Matrixd identity(int i, int j) {
		double[][] identityMatrix = new double[i][j];
		for (int n = 0; n < i && n < j; n++) {
			identityMatrix[n][n] = 1;
		}
		return new Matrixd(identityMatrix);
	}

	public static Matrixd diag(double... diagEntries) {
		double[][] diagMatrix = new double[diagEntries.length][diagEntries.length];
		for (int i = 0; i < diagEntries.length; i++) {
			diagMatrix[i][i] = diagEntries[i];
		}
		return new Matrixd(diagMatrix);
	}

	public Matrixd(double[][] matrix) {
		super(matrix);
	}

	public Matrixd(MatrixdImpl<? extends MatrixdImpl<?>> matrixd) {
		super(matrixd);
	}

	public Matrixd(Vectord... vectors) {
		super(vectors);
	}

	@Override
	public Matrixd construct(double[][] matrix) {
		return new Matrixd(matrix);
	}

	@Override
	public Matrixd construct(MatrixdImpl<? extends MatrixdImpl<?>> matrixd) {
		return new Matrixd(matrixd);
	}

	@Override
	public Matrixd construct(Vectord... vectors) {
		return new Matrixd(vectors);
	}
}

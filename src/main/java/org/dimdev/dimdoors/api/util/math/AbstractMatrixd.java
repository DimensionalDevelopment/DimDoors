package org.dimdev.dimdoors.api.util.math;

public abstract class AbstractMatrixd<T extends AbstractMatrixd<T>> {
	private final int dimensionX;
	private final int dimensionY;

	protected double[][] matrix;

	public AbstractMatrixd(double[][] matrix) {
		if (matrix.length > 0) { // Allow matrices of dimension 0x0. Why? No reason not to.
			int length = matrix[0].length;
			for (int i = 1; i < matrix.length; i++) {
				if (length != matrix[i].length) throw new UnsupportedOperationException("Cannot create Matrix from 2D array consisting of non equal length arrays.");
			}
		}
		this.matrix = matrix;

		this.dimensionX = matrix.length;
		this.dimensionY = matrix[0].length;
	}

	public AbstractMatrixd(AbstractMatrixd<?> matrixd) {
		this.matrix = matrixd.getMatrix();

		this.dimensionX = matrixd.getDimensionX();
		this.dimensionY = matrixd.getDimensionY();
	}

	public AbstractMatrixd(Vectord... vectors) {
		double[][] matrix = new double[vectors.length][vectors[0].size()];
		for (int i = 0; i < vectors.length; i++) {
			matrix[i] = vectors[i].getVec();
		}

		int length = matrix[0].length;
		for (int i = 1; i < matrix.length; i++) {
			if (length != matrix[i].length) throw new UnsupportedOperationException("Cannot create Matrix from 2D array consisting of non equal length arrays.");
		}

		this.matrix = matrix;

		this.dimensionX = matrix.length;
		this.dimensionY = matrix[0].length;
	}

	public abstract T construct(double[][] matrix);

	public abstract T construct(AbstractMatrixd<?> matrixd);

	public abstract T construct(Vectord... vectors);

	public int getDimensionX() {
		return dimensionX;
	}

	public int getDimensionY() {
		return dimensionY;
	}

	private double[][] getMatrix() {
		return matrix;
	}

	public double get(int column, int row) {
		return matrix[column][row];
	}

	public Vectord getColumn(int column) {
		return new Vectord(matrix[column]);
	}

	public Vectord getRow(int row) {
		double[] rowArray = new double[dimensionX];
		for (int i = 0; i < dimensionX; i++) {
			rowArray[i] = matrix[i][row];
		}
		return new Vectord(rowArray);
	}

	public T set(int column, int row, double value) {
		T updated = construct(this);
		updated.matrix[column][row] = value;
		return updated;
	}

	public T setColumn(int column, Vectord vector) {
		if (vector.size() != this.dimensionY) throw new UnsupportedOperationException("Cannot replace column with one of non matching length");
		T updated = construct(this);
		updated.matrix[column] = vector.getVec();
		return updated;
	}

	public T setRow(int row, Vectord vector) {
		if (vector.size() != this.dimensionX) throw new UnsupportedOperationException("Cannot replace row with one of non matching length");
		T updated = construct(this);
		for (int i = 0; i < vector.size(); i++) {
			updated.matrix[i][row] = vector.get(i);
		}
		return updated;
	}

	public T dropColumn(int column) {
		double[][] matrix = new double[this.dimensionX - 1][this.dimensionY];
		for (int i = 0; i < this.dimensionX; i++) {
			if (i == column) continue;
			matrix[i < column? i : i - 1] = this.matrix[i];
		}
		return construct(matrix);
	}

	public T dropRow(int row) {
		double[][] matrix = new double[this.dimensionX - 1][this.dimensionY];
		for (int i = 0; i < this.dimensionX; i++) {
			for (int j = 0; j < this.dimensionY; j++) {
				if (j == row) continue;
				matrix[i][j < row? j : j - 1] = this.matrix[i][j];
			}
		}
		return construct(matrix);
	}

	public T dropColumnAndRow(int column, int row) {
		double[][] matrix = new double[this.dimensionX - 1][this.dimensionY];
		for (int i = 0; i < this.dimensionX; i++) {
			if (i == column) continue;
			for (int j = 0; j < this.dimensionY; j++) {
				if (j == row) continue;
				matrix[i < column? i : i - 1][j < row? j : j - 1] = this.matrix[i][j];
			}
		}
		return construct(matrix);
	}

	public Vectord asVector() {
		if (dimensionX != 1 && dimensionY != 1) throw new UnsupportedOperationException("Cannot get Matrix of non vector dimensions as vector.");
		if (dimensionX == 1) return getColumn(0);
		else return getRow(0);
	}

	public T transpose() {
		double[][] transposed = new double[this.dimensionY][this.dimensionX];
		for (int i = 0; i < dimensionX; i++) {
			for (int j = 0; j < dimensionY; j++) {
				transposed[j][i] = matrix[j][i];
			}
		}
		return construct(transposed);
	}

	public double determinant() {
		if (dimensionY != dimensionX) throw new UnsupportedOperationException("Cannot get the determinant of matrix with differing dimensions.");
		return det();
	}

	// determinant as per https://en.wikipedia.org/wiki/Determinant#Laplace's_expansion_and_the_adjugate_matrix
	protected double det() {
		if (dimensionX == 0) return 1;
		double sum = 0;
		for (int i = 0; i < dimensionX; i++) {
			if (i % 2 == 0) {
				sum += matrix[i][0] * this.dropColumnAndRow(i, 0).det();
			} else {
				sum -= matrix[i][0] * this.dropColumnAndRow(i, 0).det();
			}
		}
		return sum;
	}

	public T product(AbstractMatrixd<?> matrix) {
		if (dimensionX != matrix.dimensionY) throw new UnsupportedOperationException("Cannot perform matrix product on matrices of non matching row length and column length");
		double[][] result = new double[matrix.dimensionX][dimensionY];
		for (int i = 0; i < matrix.dimensionX; i++) {
			for (int j = 0; j < dimensionY; j++) {
				result[i][j] = matrix.getColumn(i).dot(getRow(j));
			}
		}
		return construct(result);
	}

	public Matrixd universalProduct(AbstractMatrixd<?> matrix) {
		if (dimensionX != matrix.dimensionY) throw new UnsupportedOperationException("Cannot perform matrix product on matrices of non matching row length and column length");
		double[][] result = new double[matrix.dimensionX][dimensionY];
		for (int i = 0; i < matrix.dimensionX; i++) {
			for (int j = 0; j < dimensionY; j++) {
				result[i][j] = matrix.getColumn(i).dot(getRow(j));
			}
		}
		return new Matrixd(result);
	}

	public Vectord product(Vectord vector) {
		AbstractMatrixd<?> matrix = new Matrixd(vector);
		return universalProduct(matrix).asVector();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (int j = 0; j < matrix[0].length; j++) {
			stringBuilder.append("[");
			for (int i = 0; i < matrix.length; i++) {
				stringBuilder.append(matrix[i][j]);
				if (i < matrix.length - 1)
					stringBuilder.append(",");
			}
			stringBuilder.append("]");
			if (j < matrix[0].length - 1)
				stringBuilder.append(",\n");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractMatrixd)) return false;
		AbstractMatrixd<?> matrixd = (AbstractMatrixd<?>) o;
		if (matrixd.dimensionX != dimensionX || matrixd.dimensionY != dimensionY) return false;

		for (int i = 0; i < dimensionX; i++) {
			for (int j = 0; j < dimensionY; j++) {
				if (matrixd.matrix[i][j] != matrix[i][j]) return false;
			}
		}
		return true;
	}
}

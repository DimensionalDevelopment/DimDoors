package org.dimdev.dimdoors.api.util.math;

public class Vectord {
	private final double[] vec;

	public Vectord(int size) {
		vec = new double[size];
	}

	public Vectord(double... vec) {
		this.vec = vec;
	}

	protected double[] getVec() {
		return vec;
	}

	public double get(int index) {
		return vec[index];
	}

	public Vectord set(int index, double value) {
		double[] vec = this.vec;
		vec[index] = value;
		return new Vectord(vec);
	}

	public Vectord drop(int index) {
		double[] vec = new double[size() - 1];
		for (int i = 0; i < size(); i++) {
			if (i == index) continue;
			vec[i < index? i : i - 1] = this.vec[i];
		}
		return new Vectord(vec);
	}

	public Vectord append(double value) {
		double[] extended = new double[size() + 1];
		for (int i = 0; i < size(); i++) {
			extended[i] = vec[i];
		}
		extended[size()] = value;
		return new Vectord(extended);
	}

	public int size() {
		return vec.length;
	}

	public Vectord invert() {
		return mult(-1);
	}

	public Vectord mult(double value) {
		double[] vec = this.vec;
		for (int i = 0; i < vec.length; i++) {
			vec[i] *= value;
		}
		return new Vectord(vec);
	}

	public double dot(Vectord vector) {
		if (vector.size() != this.size()) throw new UnsupportedOperationException("Cannot apply dot product to vectors of different size.");
		double sum = 0;
		for (int i = 0; i < this.size(); i++) {
			sum += this.get(i) * vector.get(i);
		}
		return sum;
	}

	// TODO: cross product as per https://en.wikipedia.org/wiki/Cross_product#Multilinear_algebra
	public Vectord cross(Vectord... vectors) {
		if (vectors.length != size() - 2) throw new UnsupportedOperationException("Cannot perform " + size() +"D vector cross product with " + (vectors.length + 1) + " vectors.");
		Vectord[] allVectors = new Vectord[vectors.length + 1];
		allVectors[0] = this;
		for (int i = 0; i < vectors.length; i++) {
			allVectors[i + 1] = vectors[i];
		}
		Matrixd matrix = new Matrixd(allVectors).transpose();

		double[] vector = new double[size()];
		for (int i = 0; i < size(); i++) {
			if ((i + size()) % 2 == 0) {
				vector[i] = matrix.dropColumn(i).determinant();
			} else {
				vector[i] = -matrix.dropColumn(i).determinant();
			}
		}
		return new Vectord(vector);
	}
}

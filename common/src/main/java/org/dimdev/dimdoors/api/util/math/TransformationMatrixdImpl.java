package org.dimdev.dimdoors.api.util.math;

import java.util.ArrayList;
import java.util.List;

public abstract class TransformationMatrixdImpl<T extends AbstractMatrixd<T>> extends AbstractMatrixd<T> {
	public TransformationMatrixdImpl(double[][] matrix) {
		super(matrix);
		if (getDimensionX() != getDimensionY()) {
			throw new UnsupportedOperationException("Cannot create TransformationMatrixd from non square 2D array.");
		}
	}

	public TransformationMatrixdImpl(AbstractMatrixd<?> matrix) {
		super(matrix);
		if (getDimensionX() != getDimensionY()) {
			throw new UnsupportedOperationException("Cannot create TransformationMatrixd from non square matrix.");
		}
	}

	public TransformationMatrixdImpl(Vectord... vectors) {
		super(vectors);
		if (getDimensionX() != getDimensionY()) {
			throw new UnsupportedOperationException("Cannot create TransformationMatrixd from non square vector array.");
		}
	}

	public int getBase() {
		return getDimensionX() - 1;
	}

	public Vectord transform(Vectord vector) {
		if (vector.size() != getBase()) throw new UnsupportedOperationException("Cannot transform vector of non matching base");

		return product(vector.append(1)).drop(vector.size());
	}

	public static abstract class TransformationMatrixdBuilderImpl<V extends TransformationMatrixdBuilderImpl<V, U>, U extends TransformationMatrixdImpl<U>> {
		private final int base;
		private final U instance;
		private final List<TransformationMatrixdImpl<?>> transformers = new ArrayList<>();
		private final List<TransformationMatrixdImpl<?>> reverseTransformers = new ArrayList<>();

		protected TransformationMatrixdBuilderImpl(int base, U instance) {
			this.base = base;
			this.instance = instance;
		}

		public abstract V getSelf();

		public U build() {
			TransformationMatrixd transformer = TransformationMatrixd.identity(base);
			for (TransformationMatrixdImpl<?> transformationMatrix : transformers) {
				transformer = transformer.product(transformationMatrix);
			}
			return instance.construct(transformer);
		}

		public U buildReverse() {
			TransformationMatrixd transformer = TransformationMatrixd.identity(base);
			for (TransformationMatrixdImpl<?> transformationMatrix : reverseTransformers) {
				transformer = transformer.product(transformationMatrix);
			}
			return instance.construct(transformer);
		}

		public V translate(Vectord translation) {
			TransformationMatrixdImpl<?> transformer = TransformationMatrixd.identity(base).setColumn(base, translation.append(1));
			transformers.add(0, transformer);
			TransformationMatrixdImpl<?> reverseTransformer = TransformationMatrixd.identity(base).setColumn(base, translation.invert().append(1));
			reverseTransformers.add(reverseTransformer);
			return getSelf();
		}

		public V rotateAroundBasePlane(double angle, int planeBaseVectorIndex1, int planeBaseVectorIndex2) {
			Vectord column1 = new Vectord(base + 1).set(planeBaseVectorIndex1, Math.cos(angle)).set(planeBaseVectorIndex2, Math.sin(angle));
			Vectord column2 = new Vectord(base + 1).set(planeBaseVectorIndex1, -Math.sin(angle)).set(planeBaseVectorIndex2, Math.cos(angle));
			TransformationMatrixdImpl<?> transformer = TransformationMatrixd.identity(base).setColumn(planeBaseVectorIndex1, column1).setColumn(planeBaseVectorIndex2, column2);
			transformers.add(0, transformer);

			column1 = new Vectord(base + 1).set(planeBaseVectorIndex1, Math.cos(angle)).set(planeBaseVectorIndex2, -Math.sin(angle));
			column2 = new Vectord(base + 1).set(planeBaseVectorIndex1, Math.sin(angle)).set(planeBaseVectorIndex2, Math.cos(angle));
			TransformationMatrixdImpl<?> reverseTransformer = TransformationMatrixd.identity(base).setColumn(planeBaseVectorIndex1, column1).setColumn(planeBaseVectorIndex2, column2);
			reverseTransformers.add(reverseTransformer);
			return getSelf();
		}
	}
}

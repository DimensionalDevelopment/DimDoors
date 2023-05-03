package org.dimdev.dimdoors.api.util.math;

public class TransformationMatrixd extends TransformationMatrixdImpl<TransformationMatrixd> {
	public TransformationMatrixd(double[][] matrix) {
		super(matrix);
	}

	public TransformationMatrixd(AbstractMatrixd<? extends AbstractMatrixd<?>> matrix) {
		super(matrix);
	}

	public TransformationMatrixd(Vectord... vectors) {
		super(vectors);
	}


	public static TransformationMatrixd identity(int base) {
		return new TransformationMatrixd(Matrixd.identity(base + 1, base + 1));
	}

	public static TransformationMatrixdBuilder builder(int base) {
		return new TransformationMatrixdBuilder(base, identity(base));
	}

	@Override
	public TransformationMatrixd construct(double[][] matrix) {
		return new TransformationMatrixd(matrix);
	}

	@Override
	public TransformationMatrixd construct(AbstractMatrixd<? extends AbstractMatrixd<?>> matrixd) {
		return new TransformationMatrixd(matrixd);
	}

	@Override
	public TransformationMatrixd construct(Vectord... vectors) {
		return new TransformationMatrixd(vectors);
	}

	public static class TransformationMatrixdBuilder extends TransformationMatrixdBuilderImpl<TransformationMatrixdBuilder, TransformationMatrixd> {
		protected TransformationMatrixdBuilder(int base, TransformationMatrixd instance) {
			super(base, instance);
		}

		@Override
		public TransformationMatrixdBuilder getSelf() {
			return this;
		}
	}
}

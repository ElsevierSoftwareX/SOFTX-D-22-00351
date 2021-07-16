package mklab.JGNN.core.matrix;

import java.util.Iterator;

import mklab.JGNN.core.Matrix;
import mklab.JGNN.core.Tensor;
import mklab.JGNN.core.util.Range;

/**
 * Accesses a column of a {@link Matrix} as if it were a dense {@link Tensor}.
 * Prefer using {@link mklab.JGNN.core.Martix#getCol(long)}, which wraps usage
 * of this class. Instances of this class share elements with the matrix which
 * they access and do <i>not</i> allocate new memory.
 * 
 * @author Emmanouil Krasanakis
 * @see AccessRow
 */
public class AccessCol extends Tensor {
	private Matrix matrix;
	private long col;
	
	public AccessCol(Matrix matrix, long col) {
		super(matrix.getRows());
		this.matrix = matrix;
		this.col = col;
	}
	
	@Override
	protected void allocate(long size) {
	}

	@Override
	public Tensor put(long pos, double value) {
		matrix.put(pos, col, value);
		return this;
	}

	@Override
	public double get(long pos) {
		return matrix.get(pos, col);
	}

	@Override
	public Tensor zeroCopy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Long> traverseNonZeroElements() {
		return new Range(0, size());
	}

}

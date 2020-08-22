package workflowtest;

public class Range {
	private double lower;
	private double upper;
	private double difference;
	
	public Range(double lower,double upper) {
		if(lower > upper)
			throw new IllegalArgumentException("value must be between 0 and 1");
		this.lower = lower;
		this.upper = upper;
		this.difference = upper - lower;
	}
	
	/**
	 * 给定一个缩放至0-1之间的值
	 * @param value
	 * @return
	 */
	public double denormalize(double value) {
		if(value < 0 || value > 1)
			throw new IllegalArgumentException("value must be between 0 and 1");
		double res = lower + (difference * value);
		return res;
	}
	
	public double getLower() {
		return lower;
	}
	public double getUpper() {
		return upper;
	}
}

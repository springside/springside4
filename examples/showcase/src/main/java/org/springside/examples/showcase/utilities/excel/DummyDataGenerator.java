package org.springside.examples.showcase.utilities.excel;

/**
 * 报表演示的模拟数据提供类.
 * 
 * @author calvin
 */
public class DummyDataGenerator {

	public static TemperatureAnomaly[] getDummyData() {

		return new TemperatureAnomaly[] { new TemperatureAnomaly(1970, -0.068, -0.108),
				new TemperatureAnomaly(1971, -0.190, -0.104), new TemperatureAnomaly(1972, -0.056, -0.100),
				new TemperatureAnomaly(1973, 0.077, -0.097), new TemperatureAnomaly(1974, -0.213, -0.091),
				new TemperatureAnomaly(1975, -0.170, -0.082), new TemperatureAnomaly(1976, -0.254, -0.068),
				new TemperatureAnomaly(1977, 0.019, -0.050), new TemperatureAnomaly(1978, -0.063, -0.028),
				new TemperatureAnomaly(1979, 0.050, -0.006), new TemperatureAnomaly(1980, 0.077, 0.015),
				new TemperatureAnomaly(1981, 0.120, 0.032), new TemperatureAnomaly(1982, 0.011, 0.046),
				new TemperatureAnomaly(1983, 0.177, 0.058), new TemperatureAnomaly(1984, -0.021, 0.069),
				new TemperatureAnomaly(1985, -0.037, 0.081), new TemperatureAnomaly(1986, 0.030, 0.094),
				new TemperatureAnomaly(1987, 0.179, 0.108), new TemperatureAnomaly(1988, 0.180, 0.123),
				new TemperatureAnomaly(1989, 0.104, 0.137), new TemperatureAnomaly(1990, 0.255, 0.150),
				new TemperatureAnomaly(1991, 0.210, 0.163), new TemperatureAnomaly(1992, 0.065, 0.178),
				new TemperatureAnomaly(1993, 0.110, 0.195), new TemperatureAnomaly(1994, 0.172, 0.216),
				new TemperatureAnomaly(1995, 0.269, 0.241), new TemperatureAnomaly(1996, 0.141, 0.268),
				new TemperatureAnomaly(1997, 0.353, 0.296), new TemperatureAnomaly(1998, 0.548, 0.323),
				new TemperatureAnomaly(1999, 0.298, 0.348) };
	}

	/**
	 * 年度气温记录类.
	 * 
	 * @author calvin
	 */
	public static class TemperatureAnomaly {
		private int year;
		private double anomaly;
		private double smoothed;

		public TemperatureAnomaly(int year, double anomaly, double smoothed) {
			this.year = year;
			this.anomaly = anomaly;
			this.smoothed = smoothed;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public double getAnomaly() {
			return anomaly;
		}

		public void setAnomaly(double anomaly) {
			this.anomaly = anomaly;
		}

		public double getSmoothed() {
			return smoothed;
		}

		public void setSmoothed(double smoothed) {
			this.smoothed = smoothed;
		}
	}
}

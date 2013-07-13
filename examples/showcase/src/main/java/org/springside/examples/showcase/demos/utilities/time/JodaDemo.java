package org.springside.examples.showcase.demos.utilities.time;

import static org.junit.Assert.*;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

public class JodaDemo {

	@Test
	public void convertToString() {
		String format = "yyyy-MM-dd HH:mm:ss";
		DateTime fooDate = new DateTime(1978, 6, 1, 12, 10, 8, 0);
		// 第一种方法 直接使用DateTime的toString方法
		System.out.println(fooDate.toString(format));

		// 第二种方法,使用Formatter
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		System.out.println(fmt.print(fooDate));
	}

	@Test
	public void convertFromString() {
		String dateString = "1978-06-01 12:10:08";
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		// 第一种方法，直接构造函数,注意日期和时间之间用T分割
		DateTime dt1 = new DateTime("1978-06-01");
		assertEquals(1978, dt1.getYear());
		DateTime dt2 = new DateTime("1978-06-01T12:10:08");
		assertEquals(1978, dt2.getYear());

		// 第二种方法，使用Formatter
		DateTime dt3 = fmt.parseDateTime(dateString);
		assertEquals(1978, dt3.getYear());

	}

	@Test
	public void timeZone() {

		System.out.println("演示时区");

		String format = "yyyy-MM-dd HH:mm:ss zZZ";

		// DateTime的毫秒即System的毫秒,即1970到现在的UTC的毫秒数.
		System.out.println(new DateTime().getMillis() + " " + System.currentTimeMillis());

		// 将日期按默认时区打印
		DateTime fooDate = new DateTime(1978, 6, 1, 12, 10, 8, 0);
		System.out.println(fooDate.toString(format) + " " + fooDate.getMillis()); // "1978-06-01 12:10:08"

		// 将日期按UTC时区打印
		DateTime zoneWithUTC = fooDate.withZone(DateTimeZone.UTC);
		System.out.println(zoneWithUTC.toString(format) + " " + zoneWithUTC.getMillis());// "1978-06-01 04:10:08",
																							// sameMillis

		// 按不同的时区分析字符串,得到不同的时间
		String dateString = "1978-06-01 12:10:08";
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

		DateTime parserResult1 = fmt.withZone(DateTimeZone.forID("US/Pacific")).parseDateTime(dateString);
		DateTime parserResult2 = fmt.withZoneUTC().parseDateTime(dateString);

		System.out.println(parserResult1.toString(format) + " " + parserResult1.getMillis());
		System.out.println(parserResult2.toString(format) + " " + parserResult2.getMillis());
	}

	/**
	 * 打印当地语言年，月，日到写法
	 */
	@Test
	public void locale() {

		System.out.println("演示Locale");

		DateTime dateTime = new DateTime().withZone(DateTimeZone.UTC);

		// 打印中文与英文下不同长度的日期格式串
		System.out.println("S:  " + formatDateTime(dateTime, "SS", "zh"));
		System.out.println("M:  " + formatDateTime(dateTime, "MM", "zh"));
		System.out.println("L:  " + formatDateTime(dateTime, "LL", "zh"));
		System.out.println("XL: " + formatDateTime(dateTime, "FF", "zh"));
		System.out.println("");

		System.out.println("S:  " + formatDateTime(dateTime, "SS", "en"));
		System.out.println("M:  " + formatDateTime(dateTime, "MM", "en"));
		System.out.println("L:  " + formatDateTime(dateTime, "LL", "en"));
		System.out.println("XL: " + formatDateTime(dateTime, "FF", "en"));
		System.out.println("");
		System.out.println("");

		// 直接打印TimeStamp, 日期是M,时间是L
		DateTimeFormatter formatter = DateTimeFormat.forStyle("ML").withLocale(new Locale("zh"))
				.withZone(DateTimeZone.UTC);

		System.out.println("ML Mix: " + formatter.print(dateTime.getMillis()));

		// 只打印日期不打印时间
		System.out.println("Date only :" + formatDateTime(dateTime, "M-", "zh"));

	}

	public static String formatDateTime(DateTime dateTime, String style, String lang) {
		DateTimeFormatter formatter = DateTimeFormat.forStyle(style).withLocale(new Locale(lang));
		return dateTime.toString(formatter);
	}

	/**
	 * 演示日期的加减以及计算日期间的间隔，可使用任意时间单位进行加减和计算间隔.
	 */
	@Test
	public void daysPlusAndMinusBetweenAndBetweenx() {
		DateTime now = new DateTime();
		DateTime birthDate = now.minusYears(10);
		assertEquals(10, Years.yearsBetween(birthDate, new DateTime()).getYears());
		birthDate = now.minusYears(10).plusDays(2);
		assertEquals(9, Years.yearsBetween(birthDate, new DateTime()).getYears());
	}

	/**
	 * 取得月份的头一天和最后一天. 取得一天的0:00和23:59:59 其他如年，星期的头一天，最后一天同理可证
	 */
	@Test
	public void beginAndEndOfDates() {
		String dateString = "1978-06-10T12:10:08";
		DateTime dt = new DateTime(dateString);
		DateTime startOfMonth = dt.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
		System.out.println(startOfMonth.toString());

		DateTime endOfMonth = dt.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();
		System.out.println(endOfMonth);
	}
}

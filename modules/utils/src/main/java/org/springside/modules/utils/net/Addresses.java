package org.springside.modules.utils.net;

import java.net.Inet4Address;
import java.net.InetAddress;

import com.google.common.net.InetAddresses;

public class Addresses {

	public int toInt(InetAddress address) {
		return InetAddresses.coerceToInteger(address);
	}

	public static String toString(InetAddress ip) {
		return InetAddresses.toAddrString(ip);
	}

	public Inet4Address fromInt(int address) {
		return InetAddresses.fromInteger(address);
	}

	public InetAddress fromString(String address) {
		return InetAddresses.forString(address);
	}

	public static String intToString(int i) {
		StringBuilder buf = new StringBuilder(15);
		buf.append(i >> 24 & 0xff);
		buf.append('.');
		buf.append(i >> 16 & 0xff);
		buf.append('.');
		buf.append(i >> 8 & 0xff);
		buf.append('.');
		buf.append(i & 0xff);
		return buf.toString();
	}
}

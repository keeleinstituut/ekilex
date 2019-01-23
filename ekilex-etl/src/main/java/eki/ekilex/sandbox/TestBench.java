package eki.ekilex.sandbox;

import org.apache.commons.lang3.RegExUtils;

// try out anything
public class TestBench {

	public static void main(String[] args) throws Exception {

		String value = "aaa <eki-elem-1>bbb</eki-elem-1> ccc <eki-elem-2>ddd</eki-elem-2> eee";

		String clean = RegExUtils.removePattern(value, "<[^>]*>");

		System.out.println(clean);
	}

}

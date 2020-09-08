package eki.ekilex.sandbox;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

// try out anything
public class TestBench {

	public static void main(String[] args) throws Exception {

		new TestBench();
	}

	public TestBench() {
		String val1 = "Mama Toyota Papa Kuku";
		String val2 = "Mama Coyote Papa Kaka";
		System.out.println(StringUtils.difference(val1, val2));

		StringsComparator comparator = new StringsComparator(val1, val2);
		MyCommandsVisitor myCommandsVisitor = new MyCommandsVisitor();
		comparator.getScript().visit(myCommandsVisitor);
		System.out.println("FINAL DIFF = " + myCommandsVisitor.left + " | " + myCommandsVisitor.right);
	}

	public class MyCommandsVisitor implements CommandVisitor<Character> {

		String left = "";
		String right = "";

		@Override
		public void visitKeepCommand(Character c) {
			left = left + c;
			right = right + c;
		}

		@Override
		public void visitInsertCommand(Character c) {
			right = right + "(" + c + ")";
		}

		@Override
		public void visitDeleteCommand(Character c) {
			left = left + "{" + c + "}";
		}
	}
}

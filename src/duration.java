import java.util.Scanner;

public class duration implements Comparable<duration> {
	private double from;
	private double to;

	duration(double from, double to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int compareTo(duration o) {
		// TODO Auto-generated method stub
		if (to == 0 && from >= o.from && from <= o.to) {
			return 0;
		} else {
			if (o.from == from)
				return 0;
			else if (o.from < from)
				return 1;
			else
				return -1;
//			File f = new File("danh");
//			Scanner i = new Scanner(f);
		}
	}

	@Override
	public String toString() {
		String f = String.valueOf(from);
		String t = String.valueOf(to);
		return f + "->" + t;
	}

}

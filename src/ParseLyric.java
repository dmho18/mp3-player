import java.util.Map;
import java.util.TreeMap;

public class ParseLyric {
	private Map<duration, String> map;

	public ParseLyric() {
		map = new TreeMap<>();
	}

	public void add(double from, double to, String lyric) {
		map.put(new duration(from, to), lyric);

	}

	public String search(double time) {
		return map.get(new duration(time, 0));
	}

	public Map<duration, String> getMap() {
		return map;
	}

}

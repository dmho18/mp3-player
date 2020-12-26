import java.util.Collections;
import java.util.Comparator;

public class Sort {
	private Table table;

	public Sort(Table t) {
		table = t;
	}
	
	public void byOrder() {
		Collections.sort(table.getSongList(), new sortByOrder());
	}

	public void byTitle() {
		Collections.sort(table.getSongList(), new sortByTitle());
	}

	public void byLen() {
		Collections.sort(table.getSongList(), new sortByLen());
	}

	public void byArtist() {
		Collections.sort(table.getSongList(), new sortByArtist());

	}

	public void byAlbum() {
		Collections.sort(table.getSongList(), new sortByAlbum());

	}

	public class sortByOrder implements Comparator<Song> {

		@Override
		public int compare(Song s1, Song s2) {
			return s1.getDate() - s2.getDate();
		}
	}

	public class sortByTitle implements Comparator<Song> {

		@Override
		public int compare(Song s1, Song s2) {
			return s1.getTitle().compareTo(s2.getTitle());
		}
	}

	public class sortByLen implements Comparator<Song> {

		@Override
		public int compare(Song s1, Song s2) {
			return s1.getLength() - s2.getLength();
		}
	}

	public class sortByArtist implements Comparator<Song> {

		@Override
		public int compare(Song s1, Song s2) {
			if(s1.getArtist() == null && s2.getArtist() != null) {
				return 1;
			}
			else if(s1.getArtist() != null && s2.getArtist() == null) {
				return -1;
			}
			else if(s1.getArtist() == null && s2.getArtist() == null)
				return s1.getDate() - s2.getDate();
			return s1.getArtist().compareTo(s2.getArtist());
		}
	}

	public class sortByAlbum implements Comparator<Song> {

		@Override
		public int compare(Song s1, Song s2) {
			if(s1.getAlbum() == null && s2.getAlbum() != null) {
				return 1;
			}
			else if(s1.getAlbum() != null && s2.getAlbum() == null) {
				return -1;
			}
			else if(s1.getAlbum() == null && s2.getAlbum() == null)
				return s1.getDate() - s2.getDate();
			return s1.getAlbum().compareTo(s2.getAlbum());
		}
	}

}

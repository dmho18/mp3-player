import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

public class Song {
	private String title;
	private String artist;
	private String album;
	private String year;
	private String path = null;
	private int addedOrder = 0;
	private int duration; // duration in second

	public Song() {
		// left blank
	}

	public Song(String path, Color color) throws IOException {
		this.path = path;
		File f = new File(path);
		set(f);
	}

	public void set(File file) {
		try {
			BufferedInputStream songReader = new BufferedInputStream(new FileInputStream(file));
			// read duration here
			setLength(file);
			int size = (int) file.length();
			songReader.skip(size - 128);
			byte[] last128 = new byte[128];
			songReader.read(last128);
			String id3 = new String(last128);
			String tag = id3.substring(0, 3);
			if (tag.equals("TAG")) {
				title = id3.substring(3, 32);
				artist = id3.substring(33, 62);
				album = id3.substring(63, 91);
				year = id3.substring(93, 97);
			} else {
				title = file.getName();
			}

			songReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setLength(File file)
			throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		try {
			MP3File audioFile = (MP3File) AudioFileIO.read(file);
			duration = audioFile.getAudioHeader().getTrackLength();
		} catch (CannotReadException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			e.printStackTrace();
		}
	}

	public int getLength() {
		return duration;
	}

	public void setNum(int num) {
		addedOrder = num;
	}

	public int getDate() {
		return addedOrder;
	}

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getYear() {
		return year;
	}
}

import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class popUp extends JPopupMenu {
	private ArrayList<JMenuItem> items;

	public popUp(Song song, MyTree playlist) {
		items = new ArrayList<JMenuItem>();

		for (Playlist temp : playlist.getPlaylist()) {
			JMenuItem item = new JMenuItem(temp.getName());
			item.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
				}

				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
					JMenuItem item = (JMenuItem) e.getSource();
					try {
						playlist.addSong(song, item.getText());
						;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
				}

			});
			items.add(item);
			add(item);
		}
	}

	public void addItem(String name) {
		JMenuItem item = new JMenuItem(name);
		items.add(item);
		add(item);
		this.updateUI();
	}
}

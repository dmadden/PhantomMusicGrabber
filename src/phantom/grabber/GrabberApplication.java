package phantom.grabber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GrabberApplication {
	private static String[] artistTrack;
	public static String albumName = "";
	private static String magnetLink;
	private ArrayList<String> albumList = new ArrayList<String>();
	private static final String LOG_FILE = "C:\\Users\\maddzy\\workspace\\PhantomMusicGrabber\\data\\unsuccessfullog.txt";
	private static final String ALBUM_FILE = "C:\\Users\\maddzy\\workspace\\PhantomMusicGrabber\\data\\currentalbums.txt";


	public static void main(String[] args) throws IOException {
		Grabber grabber = new Grabber();
		artistTrack = grabber.grabCurrentTrack();
//		artistTrack = new String[2];
//		artistTrack[0] = "Muse";
//		artistTrack[1] = "Bliss";
		System.out.println("Artist = "+artistTrack[0]);
		System.out.println("Track = "+artistTrack[1]);
		artistTrack[0] = artistTrack[0].replace(" ", "+");
		artistTrack[1] = artistTrack[1].replace(" ", "+");
		albumName = grabber.findAlbumName(artistTrack);	
		
		if(albumName.equals("ontent=\"AllMusic")){
			System.out.println("Unable to find an album for "+artistTrack[0]+" - "+artistTrack[1]);
			logFailure("Unable to find an album for "+artistTrack[0]+" - "+artistTrack[1]);
		}
		else System.out.println("Album = "+albumName);
		
		magnetLink = grabber.grabAlbumMagnet(artistTrack[0],albumName);
		if(magnetLink!="")
			grabber.openMagnetLink(magnetLink);

	}
	
	public void addAlbumToList(){
		albumList.add(albumName+" by "+ artistTrack[0]);		
	}
	
	public static void logFailure(String message){
		Path path = Paths.get(LOG_FILE);
	    try {
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			writer.write(message);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

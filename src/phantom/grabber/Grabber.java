package phantom.grabber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import uriSchemeHandler.*;

public class Grabber {
	
	public String[] grabCurrentTrack(){
		String[] artistTrack = null;
		try {
			URL myUrl = new URL("http://www.phantom.ie/player_json.php");
			BufferedReader in = new BufferedReader(
			                    new InputStreamReader(
			                    myUrl.openStream()));

			String line;
			String parsingArtist = "", parsingTrack = "";
			artistTrack = new String[2];;
			while ((line = in.readLine()) != null)
				parsingArtist = line;
				parsingTrack = parsingArtist;
				parsingArtist = parsingArtist.substring(parsingArtist.indexOf("current_artist")+18, parsingArtist.length());
				artistTrack[0] = parsingArtist.substring(0,parsingArtist.indexOf("current_art")-4);
				parsingTrack = parsingTrack.substring(parsingTrack.indexOf("current_track")+17, parsingTrack.length());
				artistTrack[1] = parsingTrack.substring(0,parsingTrack.indexOf("current_artist")-4);

			in.close();
		} catch (MalformedURLException e) {
			System.out.println("Phantom.ie is down");
		} catch (IOException e) {
			System.out.println("No Phantom info available");
		} catch (IndexOutOfBoundsException e){
			System.out.println("Was unable to find Phantom info");
		} catch (NullPointerException e){
			System.out.println("Was unable to find Phantom info");
		}
	    
		return artistTrack;
	}
	
	public String findAlbumName(String[] artistTrack){
		String line;
		String trackUrl="";
		String albumName = "";
		boolean foundResults = false;
		boolean foundTrack = false;
		String formattedArtist, formattedTrack;
		
		if(artistTrack[0].contains("/")||artistTrack[0].contains("/")){
			formattedArtist = artistTrack[0].replace("/", " ");
			formattedTrack = artistTrack[1].replace("/", " ");
		}else{
			formattedArtist = artistTrack[0];
			formattedTrack = artistTrack[1];
		}
			
		try {
			URL myUrl = new URL("http://www.allmusic.com/search/songs/"+formattedArtist+"+"+formattedTrack);
			BufferedReader in = new BufferedReader(
			                    new InputStreamReader(
			                    myUrl.openStream()));

			//---------Test			
			//System.out.println("Search URL = http://www.allmusic.com/search/songs/"+formattedArtist+"+"+formattedTrack);
			//---------Test
			
			artistTrack[0] = artistTrack[0].replace("+", " ");
			artistTrack[1] = artistTrack[1].replace("+", " ");
			
			while ((line = in.readLine()) != null){
				artistTrack[0] = artistTrack[0].toLowerCase();
				artistTrack[1] = artistTrack[1].toLowerCase();
				line = line.toLowerCase();
				//System.out.println(line);
				if(foundResults){
					trackUrl = line;
					trackUrl = trackUrl.substring(trackUrl.indexOf("href")+6, trackUrl.length()-1);
					foundResults=false;
					
				}
				if(!foundTrack){
					if(line.contains("<a title=\""+artistTrack[1]+" by "+artistTrack[0]+"\"")){
						foundResults=true;
						foundTrack = true;
					}else if(line.contains("<a title=\""+artistTrack[1].replace("the", "")+" by "+artistTrack[0].replace("the", "")+"\"")){
						foundResults=true;
						foundTrack = true;
					}else if ((line.contains("<a title=\""+"The "+artistTrack[1]+" by "+artistTrack[0]+"\"")||line.contains("<a title=\""+artistTrack[1]+" by The"+artistTrack[0]+"\""))){
						foundResults=true;
						foundTrack = true;
					}else if ((line.contains("<a title=\""+artistTrack[1].replace("n", "n'")+" by "+artistTrack[0]+"\"")||line.contains("<a title=\""+artistTrack[1]+" by "+artistTrack[0].replace("n", "n'")+"\""))){
						foundResults=true;
						foundTrack = true;
					}
				}
			}
			in.close();
		} catch (MalformedURLException e) {
			System.out.println("allmusic.com is down");
		} catch (IOException e) {
			System.out.println("Was unable to find track info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		} catch (IndexOutOfBoundsException e){
			System.out.println("Was unable to find track info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		} catch (NullPointerException e){
			System.out.println("Was unable to find track info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		}
	    
		//---------Test
		//System.out.println("Track URL End = "+trackUrl);
		//---------Test
		
		try {
			URL myUrl = new URL("http://www.allmusic.com"+trackUrl);
			BufferedReader in = new BufferedReader(
			                    new InputStreamReader(
			                    myUrl.openStream()));
			//---------Test			
			//System.out.println("Track URL = http://www.allmusic.com"+trackUrl);
			//---------Test

			while ((line = in.readLine()) != null){
				//line = line.toLowerCase();
				if(line.contains("<meta name=\"keywords\" content=\"")){
					if(line.contains(","))
						albumName = line.substring(31, line.indexOf(","));
					else
						albumName = line.substring(31, line.indexOf("\">"));
				}
			}
			//---------Test
			//System.out.println("Album name = "+albumName);
			//---------Test
			

			in.close();
		} catch (MalformedURLException e) {
			System.out.println("allmusic.com is down");
		} catch (IOException e) {
			System.out.println("IOEx Was unable to find album info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		} catch (IndexOutOfBoundsException e){
			System.out.println("IndexEx Was unable to find album info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		} catch (NullPointerException e){
			System.out.println("NullEx Was unable to find album info for the Track "+artistTrack[1]+" by "+artistTrack[0]);
		}
	    
	    return albumName;
	}
	
	
	public String grabAlbumMagnet(String artist, String album){
		String line;
		boolean foundMagnet = false;
		String parseLine = "";
		artist = artist.replace(" ", "+");
		album = album.replace(" ", "+");
		try {
			URL myUrl = new URL("http://thepiratebay.se/search/"+artist+"+"+album+"/0/7/0");
			BufferedReader in = new BufferedReader(new InputStreamReader(myUrl.openStream()));

			while ((line = in.readLine()) != null){
				if(!foundMagnet && line.contains("<a href=\"magnet:")){
					parseLine=line;
					foundMagnet=true;
				}
			}
			if(foundMagnet)
				parseLine = parseLine.substring(9, parseLine.indexOf("\" title=\"Down"));

			in.close();
		} catch (MalformedURLException e) {
			System.out.println("Pirate Bay is down");
		} catch (IOException e) {
			System.out.println("IOEx Was unable to find torrent info for the album "+album+" by "+artist);
		} catch (IndexOutOfBoundsException e){
			System.out.println("IndexEx Was unable to find torrent info for the album "+album+" by "+artist);
		} catch (NullPointerException e){
			System.out.println("NullEx Was unable to find torrent info for the album "+album+" by "+artist);
		}
			
		return parseLine;
	}
	
	public void openMagnetLink(String mlink){
  
		try {
			URI magnetLinkUri = new URI(mlink);  
			URISchemeHandler uriSchemeHandler = new URISchemeHandler();  
			uriSchemeHandler.open(magnetLinkUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (CouldNotOpenUriSchemeHandler e) {
			e.printStackTrace();
		}  
	}

}

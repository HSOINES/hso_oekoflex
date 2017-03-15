package loggers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class LogWriterBPM {
	
	private StringBuilder  strBu = null;
	PrintWriter pw = null;
	String path;
	String filename;
	
	List<BPMLogInfo> list ;
	
	public LogWriterBPM(String path, String filename){
		this.path = path;
		this.filename = filename;
		strBu = new StringBuilder();
		list = new ArrayList<BPMLogInfo>();
	}
	
	public void writeToFile(){
		
			Writer fw = null;
			try {
				fw = new FileWriter( path + filename, true );
			} catch (IOException e) {
				e.printStackTrace();
			}
			 Writer bw = new BufferedWriter( fw );
			 pw = new PrintWriter( bw );
		
		
		try
		{
		    pw.println( strBu.toString() );
		}finally {
		  if ( pw != null )
			  pw.close();
		}

	}
	
	public void writeHeader(int count){
		strBu.append("tick;Brennstoff;");
		for (int i = 0; i < count; i++) {
			strBu.append("Name;MengeGebot;Leistungspreis;Arbeitspreis;Abgerufenene Meng; Abbruch;");
		}
		writeToFile();
		list.clear();
		strBu = new StringBuilder();
	}
	
	public void listToFile(){
		this.list.sort(new BPMLogInfo.ComparatorBPM());
		for (int i = 0;i < list.size(); i++) {
			strBu.append(list.get(i).infoAbout());
			strBu.append(list.get(i).toString());
			for (; i < list.size()-1 && list.get(i).getDescription().compareTo(list.get(i+1).getDescription()) == 0 ; i++) {
				strBu.append(list.get(i+1).toString());
			}
			if(i != list.size()-1){
				strBu.append("\n");
			}
			
		}
		
		
		writeToFile();
		list.clear();
		strBu = new StringBuilder();
	}
	
	public void addToList(BPMLogInfo bid){
		list.add(bid);
	}
	
	public void clearList(BPMLogInfo bid){
		list.clear();
	}
	
	public void addToStringBuilder(String string){
		strBu.append(string.toString());
	}
	
	public void addToStringBuilder(BPMLogInfo info){
		strBu.append(info.toString());
	}
	
	public void addNewLine(){
		strBu.append("\n");
	}
	
	public void clear(){
		strBu = new StringBuilder();
	}
	public void close(){
		if(pw != null)
			pw.close();
	}
}



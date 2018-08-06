// ________________________________________________________________________________
// ____________________ © Nader Trabelsi - Novembre 2016 _________________________
// ________________________________________________________________________________

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.util.*;


public class IndexingTool {
    
 
    
    String[] tokens = new String[600];
    ArrayList<String> lowercaseList = new ArrayList<String>();
    ArrayList<String> unponctuatedList = new ArrayList<String>();
    ArrayList<String> tokensList = new ArrayList<String>();
    List<String> stopList = Arrays.asList("a", "about", "above","after","again","against","all","am","an","and","any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd",
            "we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves");
    ArrayList<String> cleanList = new ArrayList<String>();
    ArrayList<termWithTF> termTFList = new ArrayList<termWithTF>();
    
    public void lowercase(File file){
        try{
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            while ((strLine = br.readLine()) != null)   {
                tokens = strLine.split(" ");
                
            }
            for(String token: tokens){
                lowercaseList.add(token.toLowerCase());
            }
            
        }
        catch (Exception e){}
        
    }
    
    public void removePonctuation(){
        for(String str : lowercaseList){
            unponctuatedList.add(str.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " "));
        }
    }
    
    public void Tokenize(){
        for(String lcStr: unponctuatedList){
            tokensList.add(lcStr);
        }
        
    }
    public void removeStopwords(){
        boolean TP;
        int k=0;
        while(k<tokensList.size()){
            TP=false;
            for(int j=0;j<stopList.size();j++){
                if(((tokensList.get(k)).equals(stopList.get(j)))){
                    TP=true;
                }
            }
            if(TP==false){cleanList.add(tokensList.get(k));}
            k++;
        }
    }
    
    public void calculateTF(){
        termWithTF ttf ;
        for(String term: cleanList){
            ttf=new termWithTF(term,Collections.frequency(cleanList, term));
            termTFList.add(ttf);
        }
        
        HashSet<termWithTF> hs = new HashSet<termWithTF>();
        hs.addAll(termTFList);
        termTFList.clear();
        termTFList.addAll(hs);
        
    }
    
    
    
    
    public void saveIndexFile(File file){
        try{
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes\\"+file.getName()+".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(termTFList);
            oos.close();        
          
        } catch(IOException ex){}
        
        
    }
    
}

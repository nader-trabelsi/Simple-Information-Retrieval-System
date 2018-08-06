// ________________________________________________________________________________
// ____________________ © Nader Trabelsi - Novembre 2016 _________________________
// ________________________________________________________________________________

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.math.*;
import java.util.Collections;

/**
 *
 * @author Nader
 */
public class SearchTool {

    String queryText;
    String[] queryTerms = new String[10];
    ArrayList<String> lowercaseQTermList = new ArrayList<String>();
    ArrayList<String> unponctuatedQTermList = new ArrayList<String>();
    List<String> stopList = Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd",
            "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves");
    ArrayList<String> cleanList = new ArrayList<String>();
    ArrayList<String> finalQTermList = new ArrayList<String>();
    ArrayList<String> MinusQTermList = new ArrayList<String>();
    ArrayList<String> AndQTermList = new ArrayList<String>();
    ArrayList<String> finalQTermListForOROnly = new ArrayList<String>();
    ArrayList<String> OROptionsList = new ArrayList<String>();
    ArrayList<String> ORAsidesList = new ArrayList<String>();
    ArrayList<ArrayList<docWithRelevance>> listOfListOROfResults = new ArrayList<ArrayList<docWithRelevance>>();
    ArrayList<termWithTF> auxListForBoolean = new ArrayList<termWithTF>();
    ArrayList<docWithRelevance> relDocList = new ArrayList<>();
    ArrayList<docWithRelevance> relDocListOfAnd = new ArrayList<>();
    ArrayList<docWithRelevance> relDocListOfNot = new ArrayList<>();
    ArrayList<docWithRelevance> relDocListOfMinus = new ArrayList<>();
    ArrayList<docWithRelevance> relDocListOfAsides = new ArrayList<>();
    ArrayList<docWithRelevance> relDocListOfOptions = new ArrayList<>();
    ArrayList<termWithTF> auxListForVS = new ArrayList<>();
    ArrayList<docTermWithTFIDF> TFIDFList = new ArrayList<>();
    ArrayList<docWithSimToQuery> docSimList = new ArrayList<>();
    ArrayList<termWithTF> auxListFNDWT = new ArrayList<>();
    ArrayList<qTermInDocWeight> qTermsWeightInDoc = new ArrayList<>();
    ArrayList<qTermInQueryWeight> qTermsWeightInQuery = new ArrayList<>();
    ArrayList<docWithSimToQuery> docWithSimToQueryList = new ArrayList<>();

    public void lowercaseQuery() {

        queryTerms = queryText.split(" ");
        for (String str : queryTerms) {
            lowercaseQTermList.add(str.toLowerCase());
        }
    }

    public void removeQueryPonctuation() {
        for (String str : lowercaseQTermList) {
            unponctuatedQTermList.add(str.replaceAll("[^a-zA-Z-\\s]", "").replaceAll("\\s+", " "));
        }
    }

    public void removeStopwords() {
        boolean TP;
        int k = 0;
        while (k < unponctuatedQTermList.size()) {
            TP = false;
            for (int j = 0; j < stopList.size(); j++) {
                if ((unponctuatedQTermList.get(k)).equals(stopList.get(j))) {
                    TP = true;
                }
            }
            if (TP == false) {
                cleanList.add(unponctuatedQTermList.get(k));
            }
            k++;
        }
    }

    public void recognizeQueryTerms() {
        this.lowercaseQuery();
        this.removeQueryPonctuation();
        this.removeStopwords();
        finalQTermList.addAll(cleanList);
    }

    public void removeDashesForOROnly() {
        for (String str : finalQTermList) {
            finalQTermListForOROnly.add(str.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", ""));
        }
    }

    public void removeDashesForORAndMinus() {
        for (String str : finalQTermList) {
            finalQTermListForOROnly.add(str.replaceAll("[^a-zA-Z-\\s]", "").replaceAll("\\s+", ""));
        }
    }

    public void weightQueryTermsInQuery() {

        for (String qt : finalQTermList) {
            double TF = (double) (Collections.frequency(finalQTermList, qt)) / (double) (finalQTermList.size());
            File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes");
            File[] listOfFiles = folder.listFiles();
            if (numberOfDocWithTerm(qt, listOfFiles) != 0) {
                double IDF = 1 + Math.log10(listOfFiles.length / numberOfDocWithTerm(qt, listOfFiles));
                qTermsWeightInQuery.add(new qTermInQueryWeight(qt, TF * IDF));
            } else {
                qTermsWeightInQuery.add(new qTermInQueryWeight(qt, 0));
            }
        }

    }

    public void splitListToMinusAndNotMinus() {
        MinusQTermList.clear();
        AndQTermList.clear();
        for (String str : finalQTermList) {
            if (str.charAt(0) == '-') {
                MinusQTermList.add(str.substring(1));
            } else {
                AndQTermList.add(str);
            }
        }
    }

    public void searchByBooleanModelWithMinusOnly() {
        try {
            File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes"); 
            File[] listOfFiles = folder.listFiles();
            docWithRelevance rd = null;
            relDocList.clear();
            relDocListOfAnd.clear();
            relDocListOfNot.clear();
            boolean allTermExist;
            if (finalQTermList.size() != 0) {
                if (!(AndQTermList.isEmpty())) {
                    for (int h = 0; h < listOfFiles.length; h++) {
                        allTermExist = true;
                        boolean termExist;
                        rd = null;
                        FileInputStream fin = new FileInputStream(listOfFiles[h]);
                        ObjectInputStream ois = new ObjectInputStream(fin);
                        auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                        int p = 0;
                        while (p < (AndQTermList.size()) && allTermExist == true) {
                            termExist = false;
                            for (termWithTF docTerm : auxListForBoolean) {
                                if (docTerm.term.equals(AndQTermList.get(p))) {
                                    termExist = true;
                                }
                            }
                                if (termExist == false) {
                                    allTermExist = false;
                                } else {
                                    allTermExist = true;
                                }
                            

                            p++;
                        }
                        if (allTermExist == true) {
                            rd = new docWithRelevance(listOfFiles[h], true);
                            relDocListOfAnd.add(rd);
                        }

                        ois.close();
                        fin.close();
                    }
                }
                if (!(MinusQTermList.isEmpty())) {
                    for (int h = 0; h < listOfFiles.length; h++) {
                        boolean termExist = false;
                        rd = null;
                        FileInputStream fin = new FileInputStream(listOfFiles[h]);
                        ObjectInputStream ois = new ObjectInputStream(fin);
                        auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());
                        
                            int p = 0;
                            while (p < (MinusQTermList.size()) && termExist == false) {
                                for (termWithTF docTerm : auxListForBoolean) {
                                    if ((docTerm.term).equals(MinusQTermList.get(p))) {
                                        termExist = true;
                                    }
                                }

                                p++;
                            }
                            if (termExist == true) {
                                rd = new docWithRelevance(listOfFiles[h], true);
                                relDocListOfNot.add(rd);
                            }
                        
                        ois.close();
                        fin.close();
                    }
                }
                
                for(docWithRelevance d:relDocListOfAnd){
                    System.out.println(d.docFile);
                }
                
                if ((relDocListOfNot.isEmpty()) && (relDocListOfAnd.isEmpty())) {
                    relDocList.clear();
                }

                if ((relDocListOfNot.isEmpty()) && (!(relDocListOfAnd.isEmpty()))) {
                    relDocList.addAll(relDocListOfAnd);
                }

                if (!(relDocListOfNot.isEmpty()) && (relDocListOfAnd.isEmpty())) {
                    
                    docWithRelevance d;
                    if(AndQTermList.isEmpty()){
                    for (int h = 0; h < listOfFiles.length; h++) {
                        d = new docWithRelevance(listOfFiles[h], true);
                        if (!(relDocListOfNot.contains(d))) {
                            relDocList.add(d);
                        }
                    }} else {
                            relDocList.clear();
                            }
                    
                }

                if (!(relDocListOfNot.isEmpty()) && !(relDocListOfAnd.isEmpty())) {
                    for (docWithRelevance d : relDocListOfAnd) {
                        if (!(relDocListOfNot.contains(d))) {
                            relDocList.add(d);
                        }
                    }
                }

            } else {
                allTermExist = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void detectOROptions() {
        OROptionsList.clear();
        if (finalQTermListForOROnly.get(1).equals("or")) {
            OROptionsList.add(finalQTermListForOROnly.get(0));
        }
        for (int m = 2; m < (finalQTermListForOROnly.size() - 1); m++) {
            if (finalQTermListForOROnly.get(m - 1).equals("or") || finalQTermListForOROnly.get(m + 1).equals("or")) {
                OROptionsList.add(finalQTermListForOROnly.get(m));
            }
        }
        if (finalQTermListForOROnly.get(finalQTermListForOROnly.size() - 2).equals("or")) {
            OROptionsList.add(finalQTermListForOROnly.get(finalQTermListForOROnly.size() - 1));
        }
    }

    public void detectORAsides() {
        ORAsidesList.clear();
        for (String str : finalQTermListForOROnly) {
            if (OROptionsList.contains(str) == false && (str.equals("or")) == false) {
                ORAsidesList.add(str);
            }
        }
    }

    public void searchByBooleanModelWithOROnly() {

        detectOROptions();
        detectORAsides();

        try {
            File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes"); 
            File[] listOfFiles = folder.listFiles();
            docWithRelevance rd;
            relDocList.clear();
            relDocListOfAsides.clear();
            relDocListOfOptions.clear();
            boolean allTermExist;
            if (ORAsidesList.size() != 0) {

                for (int h = 0; h < listOfFiles.length; h++) {
                    allTermExist = true;
                    boolean termExist;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                    int p = 0;
                    while (p < ORAsidesList.size() && allTermExist == true) {
                        termExist = false;
                        for (termWithTF docTerm : auxListForBoolean) {
                            if (docTerm.term.equals(ORAsidesList.get(p))) {
                                termExist = true;
                            }
                        }
                        if (termExist == false) {
                            allTermExist = false;
                        } else {
                            allTermExist = true;
                        }

                        p++;
                    }
                    if (allTermExist == true) {
                        rd = new docWithRelevance(listOfFiles[h], true);
                        relDocListOfAsides.add(rd);
                    }

                    ois.close();
                    fin.close();
                }
            } 

            if (OROptionsList.size() != 0) {

                for (int h = 0; h < listOfFiles.length; h++) {
                    // allTermExist = true;
                    boolean termExist = false;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                    int p = 0;
                    while (p < OROptionsList.size() && termExist == false) {
                        termExist = false;
                        for (termWithTF docTerm : auxListForBoolean) {
                            if (docTerm.term.equals(OROptionsList.get(p))) {
                                termExist = true;
                            }
                        }
                        p++;

                    }
                    if (termExist == true) {
                        rd = new docWithRelevance(listOfFiles[h], true);
                        if (relDocListOfOptions.contains(rd) == false) {
                            relDocListOfOptions.add(rd);
                        }
                    }

                    ois.close();
                    fin.close();
                }
                
            }

            if ((relDocListOfAsides.isEmpty()) && relDocListOfOptions.isEmpty()) {
                relDocList.clear();
            }
            if ((relDocListOfAsides.isEmpty()) && (!(relDocListOfOptions.isEmpty()))) {
                if (ORAsidesList.isEmpty()) {
                    relDocList.addAll(relDocListOfOptions);
                } else {
                    relDocList.clear();
                }
            }
            if ((!(relDocListOfAsides.isEmpty())) && (relDocListOfOptions.isEmpty())) {
                relDocList.addAll(relDocListOfAsides);
            }

            if ((!(relDocListOfAsides.isEmpty())) && (!(relDocListOfOptions.isEmpty()))) {

                for (docWithRelevance doc : relDocListOfAsides) {
                    if (relDocListOfOptions.contains(doc)) {
                        relDocList.add(doc);
                    }
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void splitListToMinusAndNotMinusForAsides() {
        MinusQTermList.clear();
        AndQTermList.clear();
        for (String str : ORAsidesList) {
            if (str.charAt(0) == '-') {
                MinusQTermList.add(str.substring(1));
            } else {
                AndQTermList.add(str);
            }
        }
    }

    public void searchByBooleanModelWithORAndMinus() {

        detectOROptions();
        detectORAsides();
        splitListToMinusAndNotMinusForAsides();

        try {
            File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes"); 
            File[] listOfFiles = folder.listFiles();
            docWithRelevance rd = null;
            relDocList.clear();
            relDocListOfAnd.clear();
            relDocListOfNot.clear();
            boolean allTermExist;
            if (ORAsidesList.size() != 0) {
                for (int h = 0; h < listOfFiles.length; h++) {
                    allTermExist = true;
                    boolean termExist;
                    rd = null;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                    if (!(AndQTermList.isEmpty())) {
                        int p = 0;
                        while (p < (AndQTermList.size()) && allTermExist == true) {
                            termExist = false;
                            for (termWithTF docTerm : auxListForBoolean) {
                                if (docTerm.term.equals(AndQTermList.get(p))) {
                                    termExist = true;
                                }
                            }

                            if (termExist == false) {
                                allTermExist = false;
                            } else {
                                allTermExist = true;
                            }

                            p++;
                        }
                        if (allTermExist == true) {
                            rd = new docWithRelevance(listOfFiles[h], true);
                            relDocListOfAnd.add(rd);
                        }
                    }
                    ois.close();
                    fin.close();
                }

                for (int h = 0; h < listOfFiles.length; h++) {
                    boolean termExist = false;
                    rd = null;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());
                    if (!(MinusQTermList.isEmpty())) {
                        int p = 0;
                        while (p < (MinusQTermList.size()) && termExist == false) {
                            for (termWithTF docTerm : auxListForBoolean) {
                                if ((docTerm.term).equals(MinusQTermList.get(p))) {
                                    termExist = true;
                                }
                            }

                            p++;
                        }
                        if (termExist == true) {
                            rd = new docWithRelevance(listOfFiles[h], true);
                            relDocListOfNot.add(rd);
                        }
                    }
                    ois.close();
                    fin.close();
                }

               
            } else {
                allTermExist = false;
            }

            if (OROptionsList.size() != 0) {

                for (int h = 0; h < listOfFiles.length; h++) {
                  
                    boolean termExist = false;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                    int p = 0;
                    while (p < OROptionsList.size() && termExist == false) {
                        termExist = false;
                        for (termWithTF docTerm : auxListForBoolean) {
                            if (docTerm.term.equals(OROptionsList.get(p))) {
                                termExist = true;
                            }
                        }
                        p++;

                    }
                    if (termExist == true) {
                        rd = new docWithRelevance(listOfFiles[h], true);
                        if (relDocListOfOptions.contains(rd) == false) {
                            relDocListOfOptions.add(rd);
                        }
                    }

                    ois.close();
                    fin.close();
                }
            }

            if ((relDocListOfAnd.isEmpty()) && (relDocListOfNot.isEmpty()) && relDocListOfOptions.isEmpty()) {
                relDocList.clear();
            }
            if ((relDocListOfAnd.isEmpty()) && (relDocListOfNot.isEmpty()) && (!(relDocListOfOptions.isEmpty()))) {
                if (ORAsidesList.isEmpty()) {
                    relDocList.addAll(relDocListOfOptions);
                } else {
                    if((!MinusQTermList.isEmpty()) && AndQTermList.isEmpty()){
                        relDocList.addAll(relDocListOfOptions);
                    }
                    if(MinusQTermList.isEmpty() && !(AndQTermList.isEmpty())){
                        relDocList.clear();
                    }
                    
                }
            }
            if ((relDocListOfAnd.isEmpty()) && !(relDocListOfNot.isEmpty()) && !(relDocListOfOptions.isEmpty())) {
                if(AndQTermList.isEmpty()){
                    for(docWithRelevance d:relDocListOfOptions){
                        if(!(relDocListOfNot.contains(d))){
                            relDocList.add(d);
                        }
                    }
                } else {
                    relDocList.clear();
                }
            }

            if ((relDocListOfAnd.isEmpty()) && !(relDocListOfNot.isEmpty()) && (relDocListOfOptions.isEmpty())) {
                relDocList.clear();
            }

            if (!(relDocListOfAnd.isEmpty()) && !(relDocListOfNot.isEmpty()) && (relDocListOfOptions.isEmpty())) {
                relDocList.clear();
            }

            if (!(relDocListOfAnd.isEmpty()) && !(relDocListOfNot.isEmpty()) && !(relDocListOfOptions.isEmpty())) {
                for (docWithRelevance doc : relDocListOfAnd) {
                    if (relDocListOfOptions.contains(doc) && !(relDocListOfNot.contains(doc))) {
                        relDocList.add(doc);
                    }
                }
            }

            if (!(relDocListOfAnd.isEmpty()) && (relDocListOfNot.isEmpty()) && !(relDocListOfOptions.isEmpty())) {
                for (docWithRelevance doc : relDocListOfAnd) {
                    if (relDocListOfOptions.contains(doc)) {
                        relDocList.add(doc);
                    }
                }
            }

            if (!(relDocListOfAnd.isEmpty()) && (relDocListOfNot.isEmpty()) && (relDocListOfOptions.isEmpty())) {

                relDocList.clear();

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void searchByBooleanModel() {
        try {
            File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes"); 
            File[] listOfFiles = folder.listFiles();
            docWithRelevance rd;
            relDocList.clear();
            boolean allTermExist;
            if (finalQTermList.size() != 0) {
                for (int h = 0; h < listOfFiles.length; h++) {
                    allTermExist = true;
                    boolean termExist;
                    FileInputStream fin = new FileInputStream(listOfFiles[h]);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    auxListForBoolean = (ArrayList<termWithTF>) (ois.readObject());

                    int p = 0;
                    while (p < finalQTermList.size() && allTermExist == true) {
                        termExist = false;
                        for (termWithTF docTerm : auxListForBoolean) {
                            if (docTerm.term.equals(finalQTermList.get(p))) {
                                termExist = true;
                            }
                        }
                        if (termExist == false) {
                            allTermExist = false;
                        } else {
                            allTermExist = true;
                        }
                        p++;

                    }
                    if (allTermExist == true) {
                        rd = new docWithRelevance(listOfFiles[h], true);
                        relDocList.add(rd);
                    }

                    ois.close();
                    fin.close();
                }
            } else {
                allTermExist = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void searchByVectorSpaceModel() {
        weightQueryTermsInQuery();
        File folder = new File(System.getProperty("user.dir")+"\\src\\Dataset\\Indexes");
        File[] listOfFiles = folder.listFiles();
        
        
        
        for (int i = 0; i < (listOfFiles.length); i++) {
            try {
                FileInputStream fin = new FileInputStream(listOfFiles[i]);
                ObjectInputStream ois = new ObjectInputStream(fin);
                auxListForVS = (ArrayList<termWithTF>) ois.readObject();
                docTermWithTFIDF ttfidf_aux;
                for (termWithTF ttf : auxListForVS) {
                    double IDF = 1 + Math.log10((double) (listOfFiles.length) / (double) (numberOfDocWithTerm(ttf.term, listOfFiles)));
                    ttfidf_aux = new docTermWithTFIDF(ttf.term, (double) ((ttf.TF) / auxListForVS.size()), IDF);
                    TFIDFList.add(ttfidf_aux);
                }
                ois.close();
                fin.close();
            } catch (IOException | ClassNotFoundException e) {
            }
            double weight;
            for (String qTerm : finalQTermList) {
                for (docTermWithTFIDF t : TFIDFList) {
                    if (t.term.equals(qTerm)) {
                        weight = (double) (t.IDF) * (double) (t.nTF);
                        qTermsWeightInDoc.add(new qTermInDocWeight(qTerm, listOfFiles[i], weight));
                    }
                }
            }
            TFIDFList.clear();
            double numerator = 0;
            for (qTermInQueryWeight qqt : qTermsWeightInQuery) {
                for (qTermInDocWeight dqt : qTermsWeightInDoc) {
                    if (dqt.qTerm.equals(qqt.qTerm)) {
                        if (qqt.weight == 0) {
                            numerator = numerator + 0;
                        } else {
                            numerator = numerator + (((double) (dqt.weight)) * ((double) (qqt.weight)));
                        }
                    }
                }
            }

            double denominator = 0;
            double first_sum = 0;
            double first_sqrt;
            double second_sum = 0;
            double second_sqrt;

            for (qTermInQueryWeight qqt : qTermsWeightInQuery) {
                first_sum = first_sum + ((qqt.weight) * (qqt.weight));
            }
            first_sqrt = Math.sqrt(first_sum);

            for (qTermInDocWeight dqt : qTermsWeightInDoc) {
                second_sum = second_sum + (dqt.weight) * (dqt.weight);
            }
            second_sqrt = Math.sqrt(second_sum);

            denominator = first_sqrt * second_sqrt;

            double sim = 0;
            if ((numerator == 0) || (denominator == 0)) {
                sim = 0;
            } else {
                sim = numerator / denominator;
                docWithSimToQueryList.add(new docWithSimToQuery(listOfFiles[i], sim));
            }

            qTermsWeightInDoc.clear();

        }
        qTermsWeightInQuery.clear();

    }

    public int numberOfDocWithTerm(String term, File[] listOfFiles) {

        int n = 0;

        for (int i = 0; i < (listOfFiles.length); i++) {
            try {
                FileInputStream fin = new FileInputStream(listOfFiles[i]);
                ObjectInputStream ois = new ObjectInputStream(fin);
                auxListFNDWT = (ArrayList<termWithTF>) ois.readObject();
                for (termWithTF ttf : auxListFNDWT) {
                    if (ttf.term.equals(term)) {
                        n++;
                    }
                }
                fin.close();
            } catch (Exception e) {
            }
        }
        return n;
    }

    public SearchTool(String queryText) {
        this.queryText = queryText;
    }

}

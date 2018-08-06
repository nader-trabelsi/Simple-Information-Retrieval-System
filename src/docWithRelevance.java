// ________________________________________________________________________________
// ____________________ © Nader Trabelsi - Novembre 2016 _________________________
// ________________________________________________________________________________


import java.io.*;

public class docWithRelevance {
    File docFile ;
    boolean rel=false;
    
    public docWithRelevance(File file,boolean r){
        docFile=file;
        rel=r;
    }
    @Override
    public boolean equals(Object object) {
        return object instanceof docWithRelevance && ((docWithRelevance) object).docFile.equals(docFile);
    }
    @Override
    public int hashCode() {
        return docFile.hashCode();
    }
    
}

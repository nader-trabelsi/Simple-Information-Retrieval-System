// ________________________________________________________________________________
// ____________________ © Nader Trabelsi - Novembre 2016 _________________________
// ________________________________________________________________________________

import java.io.Serializable;

public class termWithTF implements Serializable{
    String term;
    double TF;
    public termWithTF(String term,int TF){
        this.term=term;
        this.TF=TF;
    }
    @Override
    public boolean equals(Object object) {
        return object instanceof termWithTF && ((termWithTF) object).term.equals(term);
    }
    @Override
    public int hashCode() {
        return term.hashCode();
    }
}

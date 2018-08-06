// ________________________________________________________________________________
// ______________________ (c) Nader Trabelsi - Novembre 2016 ______________________
// ________________________email:nader.trabelsi@outlook.com________________________
// ________________________________________________________________________________

import java.util.Scanner.*;
import java.util.*;
import java.io.FileReader;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.*;
import java.util.regex.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.awt.Desktop;
import javax.swing.JRadioButton;
import java.util.Collections;
import java.awt.Component;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Nader
 */
public class mymain {

    public File file;
    private JFrame jf;
    private JPanel jp;
    private JButton jb1;
    private JButton jb1_1;
    private JButton jb2;
    private JTextField jtf;
    private JLabel jl1;
    private JLabel jl2;
    private JLabel jlExp;
    private JLabel jlExp2;
    private JLabel jl3;
    private JLabel jlbetween;
    private JLabel jlfirstbetween;
    private JRadioButton jrbBoolean;
    private JRadioButton jrbVS;
    private ButtonGroup myButtonGroup;

    Desktop desktop = Desktop.getDesktop();
    ArrayList<JButton> resButtonList;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new mymain();
    }

    public void window() {
        jf = new JFrame("Nader Trabelsi - Système d'indexation et de recherche textuel");

        jp = new JPanel();
        jb1 = new JButton("Sélectionner un fichier");
        jb1_1 = new JButton("Indexer le fichier");
        jb2 = new JButton("   Rechercher    ");
        jl1 = new JLabel("Indexation d'un fichier : ");
        jl1.setForeground(Color.BLUE);
        jl1.setFont(new Font("SansSerif", Font.BOLD, 15));
        jl2 = new JLabel("Recherche d'un fichier : ");
        jl2.setForeground(Color.BLUE);
        jl2.setFont(new Font("SansSerif", Font.BOLD, 15));
        jlExp = new JLabel("<html><body><span style='font-size:10px;color:#778899'>Pour le correspondance exact vous pourriez: </span><span style='font-size:7px;color:#B3B8BA'> <br>- Saisir OR entre tous les mots parmi lequels vous souhaitez en trouver au moins un: theory OR earth         <br>              - Placez un signe - (moins) devant les mots à exclure : -disease </span></body></html>");
        jlbetween = new JLabel("-------------------------------------------------------------------------------------------------------------------------------");
        jl3 = new JLabel("Correspondance:");
        jlfirstbetween = new JLabel("                                                                                                                       ");
        jtf = new JTextField();
        jtf.setColumns(15);
        jtf.setFont(new Font("serif",Font.PLAIN, 15));
        jrbBoolean = new JRadioButton("Exact", true);
        jrbVS = new JRadioButton("Approché par ordre");
        myButtonGroup = new ButtonGroup();
        myButtonGroup.add(jrbBoolean);
        myButtonGroup.add(jrbVS);
        ArrayList<JButton> tmpResButtonList = new ArrayList<JButton>();
        BufferedImage myPicture;
        jp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        try {
            URL path = mymain.class.getResource("image_main_byNader.png");
            myPicture = ImageIO.read(path);
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            jp.add(picLabel);
        } catch (Exception ex){}
        JSeparator sep= new JSeparator(SwingConstants.HORIZONTAL);
        sep.setPreferredSize(new Dimension(900, 20));
        sep.setBackground(Color.gray);
        sep.setForeground(Color.white);
        jp.add(sep);
        jp.add(jl1);
        jp.add(jb1);
        jp.add(jb1_1);
        JSeparator sepa= new JSeparator(SwingConstants.HORIZONTAL);
        sepa.setPreferredSize(new Dimension(900, 20));
        sepa.setBackground(Color.gray);
        sepa.setForeground(Color.white);
        jp.add(sepa);
        jp.add(jl2);
        jp.add(jlExp);
        jp.add(jl3);
        jp.add(jrbBoolean);
        jp.add(jrbVS);
        jp.add(jtf);
        jp.add(jb2);

        jb1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser input = new JFileChooser(System.getProperty("user.dir")+"\\src\\Dataset");
                int result = input.showSaveDialog(jb1);
                if (result == JFileChooser.APPROVE_OPTION) {
                    file = input.getSelectedFile();
                    jb1.setText(file.getName());

                } else if (result == JFileChooser.CANCEL_OPTION) {
                    file = null;
                }

            }
        }
        );

        jb1_1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane d = new JOptionPane();
                if (file != null) {
                    IndexingTool it = new IndexingTool();
                    it.lowercase(file);
                    it.removePonctuation();
                    it.Tokenize();
                    it.removeStopwords();
                    it.calculateTF();
                    it.saveIndexFile(file);

                    JOptionPane.showMessageDialog(jf, "Le fichier a été bien ajouté à l'index");
                    d.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(jf, "Veuillez sélectionner un fichier s'il vous plait", "Aucun fichier sélectionné!", JOptionPane.WARNING_MESSAGE);
                    d.setVisible(true);
                }
            }
        });

        jb2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane d = new JOptionPane();
                JButton res;
                
                CharSequence er="OR -";
                if(((jtf.getText().length()) !=0 ) && (((jtf.getText().contains(er))==true) || (((jtf.getText()).matches(".*-\\w*\\sOR.*"))==true))){
                      JOptionPane.showMessageDialog(jf, "Vérifiez l’orthographe des termes de recherche!","Termes de recherche ambigues!", JOptionPane.WARNING_MESSAGE);
                    d.setVisible(true);  
                    } 
                if (((jtf.getText()).length()) != 0 && ((jtf.getText().contains(er))==false) && (((jtf.getText()).matches("-\\w*\\sOR.*"))==false)) {
                    resButtonList = new ArrayList<>();
                    SearchTool st = new SearchTool(jtf.getText());
                    st.recognizeQueryTerms();
                    if (jrbBoolean.isSelected()) {
                        if((jtf.getText().contains("-")==false) && (jtf.getText().contains("OR")==false)){
                        st.searchByBooleanModel();
                        for (docWithRelevance rd : st.relDocList) {
                            String subname = rd.docFile.getName().replaceFirst(".tmp", "");
                            File resFile = new File(System.getProperty("user.dir")+"\\src\\Dataset\\" + subname);
                            res = new JButton();
                            res.setText(resFile.getName());
                            res.setBackground(Color.WHITE);

                            res.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        desktop.open(resFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            resButtonList.add(res);
                        }
                        for (JButton b : tmpResButtonList) {
                            jp.remove(b);
                            jp.repaint();
                        }
                        if (!(resButtonList.isEmpty())) {
                            resButtonList.get(0).setBounds(240, 480, 250, 40);
                            jp.add(resButtonList.get(0));
                            resButtonList.get(0).requestFocusInWindow();
                            for (int c = 1; c < resButtonList.size(); c++) {
                                resButtonList.get(c).setBounds(240, 480 + c * 50, 250, 40);
                                jp.add(resButtonList.get(c));
                                 resButtonList.get(c).requestFocus();
                            }
                        }
                        tmpResButtonList.addAll(resButtonList);
                        if (st.relDocList.isEmpty()) {
                            JOptionPane.showMessageDialog(jf, "Aucun document qui correspond à votre recherche n'a été trouvé!");
                            d.setVisible(true);
                        } else if (st.relDocList.size()==1){
                            JOptionPane.showMessageDialog(jf, st.relDocList.size() + " document a été trouvé");
                            d.setVisible(true);
                        }
                        else { JOptionPane.showMessageDialog(jf, st.relDocList.size() + " documents ont été trouvés");
                            d.setVisible(true);
                        }

                    } 
                        if((jtf.getText().contains("-")==true) && (jtf.getText().contains("OR")==false)){
                            st.splitListToMinusAndNotMinus();
                            st.searchByBooleanModelWithMinusOnly();
                            
                            for (docWithRelevance rd : st.relDocList) {
                            String subname = rd.docFile.getName().replaceFirst(".tmp", "");
                            File resFile = new File(System.getProperty("user.dir")+"\\src\\Dataset\\" + subname);
                            res = new JButton();
                            res.setText(resFile.getName());
                            res.setBackground(Color.WHITE);

                            res.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        desktop.open(resFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            resButtonList.add(res);
                        }
                        for (JButton b : tmpResButtonList) {
                            jp.remove(b);
                            jp.repaint();
                        }
                        if (!(resButtonList.isEmpty())) {
                            resButtonList.get(0).setBounds(240, 480, 250, 40);
                            jp.add(resButtonList.get(0));
                            resButtonList.get(0).requestFocusInWindow();
                            for (int c = 1; c < resButtonList.size(); c++) {
                                resButtonList.get(c).setBounds(240, 480 + c * 50, 250, 40);
                                jp.add(resButtonList.get(c));
                                resButtonList.get(c).requestFocusInWindow();
                            }
                        }
                        tmpResButtonList.addAll(resButtonList);
                        if (st.relDocList.isEmpty()) {
                            JOptionPane.showMessageDialog(jf, "Aucun document qui correspond à votre recherche n'a été trouvé!");
                            d.setVisible(true);
                        } else if (st.relDocList.size()==1){
                            JOptionPane.showMessageDialog(jf, st.relDocList.size() + " document a été trouvé");
                            d.setVisible(true);
                        }
                        else { JOptionPane.showMessageDialog(jf, st.relDocList.size() + " documents ont été trouvés");
                            d.setVisible(true);
                        }
                            
                        }
                        if((jtf.getText().contains("-")==false) && (jtf.getText().contains("OR")==true)){
                            st.removeDashesForOROnly();
                            st.searchByBooleanModelWithOROnly();
                            for (docWithRelevance rd : st.relDocList) {
                            String subname = rd.docFile.getName().replaceFirst(".tmp", "");
                            File resFile = new File(System.getProperty("user.dir")+"\\src\\Dataset\\" + subname);
                            res = new JButton();
                            res.setText(resFile.getName());
                            res.setBackground(Color.WHITE);

                            res.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        desktop.open(resFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            resButtonList.add(res);
                        }
                        for (JButton b : tmpResButtonList) {
                            jp.remove(b);
                            jp.repaint();
                        }
                        if (!(resButtonList.isEmpty())) {
                            resButtonList.get(0).setBounds(240, 480, 250, 40);
                            jp.add(resButtonList.get(0));
                            resButtonList.get(0).requestFocusInWindow();
                            for (int c = 1; c < resButtonList.size(); c++) {
                                resButtonList.get(c).setBounds(240, 480 + c * 50, 250, 40);
                                jp.add(resButtonList.get(c));
                                resButtonList.get(c).requestFocusInWindow();
                                
                            }
                        }
                        tmpResButtonList.addAll(resButtonList);
                        if (st.relDocList.isEmpty()) {
                            JOptionPane.showMessageDialog(jf, "Aucun document qui correspond à votre recherche n'a été trouvé!");
                            d.setVisible(true);
                        } else if (st.relDocList.size()==1){
                            JOptionPane.showMessageDialog(jf, st.relDocList.size() + " document a été trouvé");
                            d.setVisible(true);
                        }
                        else { JOptionPane.showMessageDialog(jf, st.relDocList.size() + " documents ont été trouvés");
                            d.setVisible(true);
                        }
                        }
                        if((jtf.getText().contains("-")==true) && (jtf.getText().contains("OR")==true)){
                            st.removeDashesForORAndMinus();
                            st.searchByBooleanModelWithORAndMinus();
                            for (docWithRelevance rd : st.relDocList) {
                            String subname = rd.docFile.getName().replaceFirst(".tmp", "");
                            File resFile = new File(System.getProperty("user.dir")+"\\src\\Dataset\\" + subname);
                            res = new JButton();
                            res.setText(resFile.getName());
                            res.setBackground(Color.WHITE);

                            res.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        desktop.open(resFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            resButtonList.add(res);
                        }
                        for (JButton b : tmpResButtonList) {
                            jp.remove(b);
                            jp.repaint();
                        }
                        if (!(resButtonList.isEmpty())) {
                            resButtonList.get(0).setBounds(240, 480, 250, 40);
                            jp.add(resButtonList.get(0));
                            resButtonList.get(0).requestFocusInWindow();
                            for (int c = 1; c < resButtonList.size(); c++) {
                                resButtonList.get(c).setBounds(240, 480 + c * 50, 250, 40);
                                jp.add(resButtonList.get(c));
                                resButtonList.get(c).requestFocusInWindow();
                            }
                        }
                        tmpResButtonList.addAll(resButtonList);
                        if (st.relDocList.isEmpty()) {
                            JOptionPane.showMessageDialog(jf, "Aucun document qui correspond à votre recherche n'a été trouvé!");
                            d.setVisible(true);
                        } else if (st.relDocList.size()==1){
                            JOptionPane.showMessageDialog(jf, st.relDocList.size() + " document a été trouvé");
                            d.setVisible(true);
                        }
                        else { JOptionPane.showMessageDialog(jf, st.relDocList.size() + " documents ont été trouvés");
                            d.setVisible(true);
                        }
                        }
                         
                    }
                    if ((jrbVS.isSelected())) {
                        st.searchByVectorSpaceModel();
                        Collections.sort(st.docWithSimToQueryList, new Comparator<docWithSimToQuery>() {
                            @Override
                            public int compare(docWithSimToQuery d1, docWithSimToQuery d2) {
                                if (d1.sim < d2.sim) {
                                    return 1;
                                }
                                if (d1.sim > d2.sim) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        for (docWithSimToQuery dsq : st.docWithSimToQueryList) {
                            String subname = dsq.docFile.getName().replaceFirst(".tmp", "");
                            File resFile = new File(System.getProperty("user.dir")+"\\src\\Dataset\\" + subname);
                            res = new JButton();
                            res.setText(resFile.getName());
                            res.setBackground(Color.WHITE);

                            res.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        desktop.open(resFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            resButtonList.add(res);
                        }
                        for (JButton b : tmpResButtonList) {
                            jp.remove(b);
                            jp.repaint();
                        }
                        if (!(resButtonList.isEmpty())) {
                            resButtonList.get(0).setBounds(240, 480, 250, 40);
                            jp.add(resButtonList.get(0));
                            resButtonList.get(0).requestFocusInWindow();
                            for (int c = 1; c < resButtonList.size(); c++) {
                                resButtonList.get(c).setBounds(240, 480 + c * 50, 250, 40);
                                jp.add(resButtonList.get(c));
                                resButtonList.get(c).requestFocusInWindow();
                            }
                        }

                        tmpResButtonList.addAll(resButtonList);
                        if (st.docWithSimToQueryList.isEmpty()) {
                            JOptionPane.showMessageDialog(jf, "Aucun document qui correspond à votre recherche n'a été trouvé!");
                            d.setVisible(true);
                        } else if (st.docWithSimToQueryList.size()==1) {
                            JOptionPane.showMessageDialog(jf,st.docWithSimToQueryList.size() + "document a été trouvé");
                            d.setVisible(true);
                        } 
                        else {
                            JOptionPane.showMessageDialog(jf,"Les "+st.docWithSimToQueryList.size() + " documents les plus pertinents ont été trouvés");
                            d.setVisible(true);
                        }
                    }
                } else {
                    if(jtf.getText().length()==0){
                    JOptionPane.showMessageDialog(jf, "Veuillez entrer les mots-clés de votre recherche", "Aucun mot-clé", JOptionPane.WARNING_MESSAGE);
                    d.setVisible(true);
                    }}
                
            }
        });
       
        jf.setContentPane(jp);
        jf.setSize(660, 700);
        URL path_icon = mymain.class.getResource("search.png");
        Image image = null;
        try {
            image = ImageIO.read(path_icon);
        } catch (IOException ex) {
            Logger.getLogger(mymain.class.getName()).log(Level.SEVERE, null, ex);
        }
        jf.setIconImage(image);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    public mymain() {
        window();
    }

}

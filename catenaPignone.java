import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class catenaPignone extends JFrame
{

 
public catenaPignone ()
    {
    	// Dichiarazione Array Dati Catene    	
    	String cateneDati[] = {"6","8","3/8","1/2","5/8","3/4","1","1 1/4","1 1/2","1 3/4","2","2 1/2","3","3 1/2","4","4 1/2"};
        String Passo[] = {"6","8","9.525","12.7","15.875","19.05","25.4","31.75","38.10","44.45","54.8","63.5","76.20","88.9","101.5","114.3"};
    	String [] corona  = new String[300];
        String [] pignone  = new String[300];

        for (int c=6; c<=305; c++) {
            corona[c-6] = ""+c;
            pignone[c-6] = ""+c;
        }
/**
 * +-------------------------------------------------+
 * | Dichiarazione Label riferimento catene          |
 * +-------------------------------------------------+
 */
        JLabel tCatena = new JLabel("Tipo Catena: ");
        tCatena.setBounds(135,20,100,25);
        JLabel passo = new JLabel("Passo Catena: ");
        passo.setBounds(285,20,100,25);
        JLabel lCorona = new JLabel("Corona");
        lCorona.setBounds(10,15,100,25);
        JLabel lPignone = new JLabel("Pignone");
        lPignone.setBounds(140,15,100,25);
        JLabel interasse = new JLabel("Interasse");
        interasse.setBounds(270,15,100,25);
        JTextField Jinterasse = new JTextField();
        Jinterasse.setBounds(270,40,150,25);
        Jinterasse.setHorizontalAlignment(JTextField.CENTER);
        JLabel dEsternoC = new JLabel("Diametro Esterno Corona");
        dEsternoC.setBounds(35,120,180,25);
        JLabel dEsternoP = new JLabel("Diametro Esterno Pignone");
        dEsternoP.setBounds(35,170,180,25);
        JTextField dEsternoCTF = new JTextField();
        dEsternoCTF.setBounds(25,145,180,25);
        dEsternoCTF.setEditable(false);
        dEsternoCTF.setHorizontalAlignment(JTextField.CENTER);
        JTextField dEsternoPTF = new JTextField();
        dEsternoPTF.setBounds(25,195,180,25);
        dEsternoPTF.setEditable(false);
        dEsternoPTF.setHorizontalAlignment(JTextField.CENTER);
        JLabel interMim = new JLabel("Interasse Minimo");
        interMim.setBounds(60,220,180,25);
        JTextField interMimTF = new JTextField();
        interMimTF.setBounds(25,245,180,25);
        interMimTF.setEditable(false);
        interMimTF.setHorizontalAlignment(JTextField.CENTER);
        JLabel sviluppoCat = new JLabel("Sviluppo Catena");
        sviluppoCat.setBounds(60,15,180,25);
        JTextField sviluppoCatTF = new JTextField();
        sviluppoCatTF.setBounds(25,40,180,25);
        sviluppoCatTF.setEditable(false);
        sviluppoCatTF.setHorizontalAlignment(JTextField.CENTER);
        JLabel MaglieCatena = new JLabel("Maglie Catena");
        MaglieCatena.setBounds(60,65,180,25);
        JTextField MaglieCatenaTF = new JTextField();
        MaglieCatenaTF.setBounds(25,90,180,25);
        MaglieCatenaTF.setEditable(false);
        MaglieCatenaTF.setHorizontalAlignment(JTextField.CENTER);
        JButton calcola = new JButton("Calcola Sviluppo");
        calcola.setBounds(25,235,180,25);
/**
 * +-------------------------------------------------+
 * | Evento JTextField Jinterasse                    |
 * +-------------------------------------------------+
 */
        Jinterasse.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char testo = e.getKeyChar();
                String conversione = Character.toString(testo);
                if (Character.isDigit(testo)) {

                } else if (conversione.equalsIgnoreCase(".")) {

                }
                else {
                    JOptionPane.showMessageDialog(null,"       Solo Valori Numerici!");
                    Jinterasse.setText("");
                }
            }
        });
/**
 * +-------------------------------------------------+
 * | Evento JTextField Jinterasse                    |
 * +-------------------------------------------------+
 */

        calcola.addActionListener(e -> {
            try {
                int value = Integer.parseInt(Jinterasse.getText());
                if (value != 0) {
                    // inserimento codice da per il calcolo
                    JOptionPane.showMessageDialog(null, "Valid number: " + value);
                } else {
                    JOptionPane.showMessageDialog(null, "Numero non valido: 0 non è consentito");
                    Jinterasse.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Inserimento non valido: inserisci un numero valido");
                Jinterasse.setText("");
            }
        });



        JLabel dpCorona = new JLabel("Diametro Primitivo Corona");
        dpCorona.setBounds(35,15,180,25);
        JTextField dpCoronaTF = new JTextField();
        dpCoronaTF.setBounds(25,40,180,25);
        dpCoronaTF.setEditable(false);

        JLabel dpPignone = new JLabel("Diametro Primitivo Pignone");
        dpPignone.setBounds(35,65,180,25);
        JTextField dpPignoneTF = new JTextField();
        dpPignoneTF.setBounds(25,90,180,25);
        dpPignoneTF.setEditable(false);        

        // Dichiarazione menu a discesa
        JComboBox elencoCatene = new JComboBox(cateneDati);
    	elencoCatene.setBounds(20,20,100,25);
    	elencoCatene.setEditable(false);
        elencoCatene.setSelectedIndex(3);

        // recupero indice default
        int id = elencoCatene.getSelectedIndex();

        JComboBox elencoCorona = new JComboBox(corona);
        elencoCorona.setBounds(10,40,120,25);
        elencoCorona.setEditable(false);
        elencoCorona.setSelectedIndex(3);

        // recupero indice default
        int idC = elencoCorona.getSelectedIndex();
        Double nDentiCorona = Double.parseDouble(corona[idC]);

        JComboBox elencoPignone = new JComboBox(pignone);
        elencoPignone.setBounds(140,40,120,25);
        elencoPignone.setEditable(false);
        elencoPignone.setSelectedIndex(3);

        // recupero indice default
        int idP = elencoPignone.getSelectedIndex();
        Double nDentiPignone = Double.parseDouble(pignone[idP]);
        // String v = Double.toString(diametroPrimitivo(nDentiPignone, 31.75));
        // dpPignoneTF.setText(v);

        // Recupero Valori Array di riferimento
        String idS = cateneDati[id];
        String idSP = Passo[id];
        Double vPassoCatena = Double.parseDouble(idSP);

        // Impostazione Valori default
        JLabel vtCatena = new JLabel(idS);
        vtCatena.setBounds(235,20,100,25);
        JLabel vpasso = new JLabel(idSP);
        vpasso.setBounds(385,20,100,25); 
        String vP = Double.toString(RD(diametroPrimitivo(nDentiPignone, vPassoCatena)));
        dpPignoneTF.setHorizontalAlignment(JTextField.CENTER);
        dpPignoneTF.setText(vP);
        String vC = Double.toString(RD(diametroPrimitivo(nDentiCorona, vPassoCatena)));
        dpCoronaTF.setHorizontalAlignment(JTextField.CENTER);
        dpCoronaTF.setText(vC);

    	dEsternoCTF.setText(Double.toString(RD(diametroEsterno(nDentiCorona, vPassoCatena))));
        dEsternoPTF.setText(Double.toString(RD(diametroEsterno(nDentiPignone, vPassoCatena))));
        interMimTF.setText(Double.toString(RD(InterasseMinimo(nDentiCorona, nDentiPignone, vPassoCatena))));
        sviluppoCatTF.setText(Double.toString(RD(sviluppoCatena(InterasseMinimo(nDentiCorona, nDentiPignone, vPassoCatena), nDentiCorona, nDentiPignone, vPassoCatena))));
        MaglieCatenaTF.setText(Double.toString(RD(maglieCatena(InterasseMinimo(nDentiCorona, nDentiPignone, vPassoCatena), nDentiCorona, nDentiPignone, vPassoCatena))));
        
        // definizione pannello componenti catena
        JPanel tipoCatena = new JPanel();
    	tipoCatena.setLayout(null);
      	tipoCatena.setBounds(10,5,460,70);
      	tipoCatena.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Tipo Catena", TitledBorder.CENTER, TitledBorder.TOP));
        
        // evento JComboBox
        elencoCatene.addActionListener(e -> {
            int selezionIndex = elencoCatene.getSelectedIndex();
            vtCatena.setText(cateneDati[selezionIndex]);
            vpasso.setText(Passo[selezionIndex]);
            int selezionIndexP = elencoPignone.getSelectedIndex();
            Double PVt = Double.parseDouble(vpasso.getText());
            Double pcVt = Double.parseDouble(pignone[selezionIndexP]);
            Double calcoloP = diametroPrimitivo(pcVt,PVt);
            String MP = Double.toString(RD(calcoloP));
            dpPignoneTF.setText(MP);
            dEsternoPTF.setText(dToS(diametroEsterno(pcVt,PVt)));
            int selezionIndexC = elencoCorona.getSelectedIndex();
            Double Vt = Double.parseDouble(vpasso.getText());
            Double cVt = Double.parseDouble(corona[selezionIndexC]);
            Double calcoloC = diametroPrimitivo(cVt,Vt);
            String MC = Double.toString(RD(calcoloC));
            dpCoronaTF.setText(MC);
            dEsternoCTF.setText(dToS(diametroEsterno(cVt,PVt)));
            interMimTF.setText(Double.toString(RD(InterasseMinimo(cVt, pcVt, PVt))));
            
            // JOptionPane.showMessageDialog(null, "Selected index: " + selectedIndex);
        });

        elencoCorona.addActionListener(e -> {
            int selezionIndexC = elencoCorona.getSelectedIndex();
            int selezionIndexP = elencoPignone.getSelectedIndex();
            Double Vt = Double.parseDouble(vpasso.getText());
            Double cVt = Double.parseDouble(corona[selezionIndexC]);
            Double pcVt = Double.parseDouble(pignone[selezionIndexP]);
            Double calcoloC = diametroPrimitivo(cVt,Vt);
            String MC = Double.toString(RD(calcoloC));
            dpCoronaTF.setText(MC);
            dEsternoCTF.setText(dToS(diametroEsterno(cVt,Vt))); 
            interMimTF.setText(Double.toString(RD(InterasseMinimo(cVt, pcVt, Vt))));
        });

        elencoPignone.addActionListener(e -> {
            int selezionIndexP = elencoPignone.getSelectedIndex();
            int selezionIndexC = elencoCorona.getSelectedIndex();
            Double PVt = Double.parseDouble(vpasso.getText());
            Double pcVt = Double.parseDouble(pignone[selezionIndexP]);
            Double cVt = Double.parseDouble(corona[selezionIndexC]);
            Double calcoloP = diametroPrimitivo(pcVt,PVt);
            String MP = Double.toString(RD(calcoloP));
            dpPignoneTF.setText(MP);
            dEsternoPTF.setText(dToS(diametroEsterno(pcVt,PVt))); 
            interMimTF.setText(Double.toString(RD(InterasseMinimo(cVt, pcVt, PVt))));
        });

        JPanel PignoniCorone = new JPanel();
        PignoniCorone.setLayout(null);
        PignoniCorone.setBounds(10,75,460,80);
        PignoniCorone.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), " - Corona - Pignone - Interasse - ", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel DatiOutput = new JPanel();
        DatiOutput.setLayout(null);
        DatiOutput.setBounds(10,160,230,290);
        DatiOutput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), " - Dati Output - ", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel SviluppoOutput = new JPanel();
        SviluppoOutput.setLayout(null);
        SviluppoOutput.setBounds(240,160,230,290);
        SviluppoOutput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), " - Dati Sviluppo - " , TitledBorder.CENTER, TitledBorder.TOP));
/**
 * +-------------------------------------------------+
 * | definizione proprietà frame                     |
 * +-------------------------------------------------+
 */

           	this.setTitle("Catena - Pignone - Corona");
    	this.setSize(500,500);
    	this.setLayout(null);
        tipoCatena.add(elencoCatene);
        tipoCatena.add(passo);
        tipoCatena.add(tCatena);
        tipoCatena.add(vtCatena);
        tipoCatena.add(vpasso);

        PignoniCorone.add(elencoCorona);
        PignoniCorone.add(elencoPignone);
        PignoniCorone.add(lCorona);
        PignoniCorone.add(lPignone);
        PignoniCorone.add(interasse);
        PignoniCorone.add(Jinterasse);

        DatiOutput.add(dpCorona);
        DatiOutput.add(dpCoronaTF);
        DatiOutput.add(dpPignone);
        DatiOutput.add(dpPignoneTF);
        DatiOutput.add(dEsternoC);
        DatiOutput.add(dEsternoP);
        DatiOutput.add(dEsternoCTF);
        DatiOutput.add(dEsternoPTF);
        DatiOutput.add(interMim);
        DatiOutput.add(interMimTF);

        SviluppoOutput.add(sviluppoCat);
        SviluppoOutput.add(sviluppoCatTF);
        SviluppoOutput.add(MaglieCatena);
        SviluppoOutput.add(MaglieCatenaTF);
        SviluppoOutput.add(calcola);

    	this.add(tipoCatena);
        this.add(PignoniCorone);
        this.add(DatiOutput);
        this.add(SviluppoOutput);
    	this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);       
        // this.setVisible(true);
        this.show();

    


}

/**
 * +-------------------------------------------------+
 * | CLASSE PRINCIPALE PROGRAMMA                     |
 * +-------------------------------------------------+
 */

public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new catenaPignone();
            }
        });
    } 
/**
 * +-------------------------------------------------+
 * | calcolo del diametro primitivo                  |
 * +-------------------------------------------------+
 */
    public Double diametroPrimitivo (Double nD, Double passoCatena) {
        Double radianti = 180 * Math.PI/180;
        Double dp = passoCatena/(Math.sin(radianti/nD));
        return dp;
    }

/**
 * +-------------------------------------------------+
 * | calcolo del diametro esterno                    |
 * +-------------------------------------------------+
 */
    public Double diametroEsterno (Double nD, Double passoCatena) {
        Double radianti = 180 * Math.PI/180;
        Double de = passoCatena*(0.6+(1/(Math.tan(radianti/nD))));
        return de;
    }
/**
 * +-------------------------------------------------+
 * | Interasse minimo Corona Pignome                 |
 * +-------------------------------------------------+
 */
    public Double InterasseMinimo (Double nDC, Double nDP, Double Passo) {
        Double radianti = 180 * Math.PI/180;
        Double dpC = Passo/(Math.sin(radianti/nDC));
        Double dpP = Passo/(Math.sin(radianti/nDP));
        Double intMin = ((dpC + dpP)/2)*1.1;
        return intMin;
    }
/**
 * +-------------------------------------------------+
 * | conversione da Stringa a Double                 |
 * +-------------------------------------------------+
 */
    public Double sToD (String vStringa){
        Double rValue = Double.parseDouble(vStringa);
        rValue = Math.round(rValue*100.0)/100.0;
        return rValue;
    }
/**
 * +-------------------------------------------------+
 * | Arrotonda Double                                |
 * +-------------------------------------------------+
 */
    public Double RD (Double arrotonda){
        Double arValue = Math.round(arrotonda*100.0)/100.0;
        return arValue;
    }
/**
 * +-------------------------------------------------+
 * | conversione da Double a Stringa                 |
 * +-------------------------------------------------+
 */
    public String dToS (Double vDouble){
        Double rValue = Math.round(vDouble*100.0)/100.0;
        String sValue = Double.toString(rValue);
        
        return sValue;
    }

 /**
 * +-------------------------------------------------+
 * | Sviluppo Catena                                 |
 * +-------------------------------------------------+
 */
    public Double sviluppoCatena (Double inter, Double nDCor, Double nDPig, Double PassoCat ){
        Double addOne = ((2*inter)/PassoCat);
        Double addTwo = ((nDCor+nDPig)/2);
        Double addThre = (Math.pow((nDCor-nDPig)/(Math.PI*2),2.0))/(inter/PassoCat);
        Double ValueSviluppo= (addOne+addTwo+addThre)*PassoCat;
                
        return ValueSviluppo;
    }   
/**
 * +-------------------------------------------------+
 * | Sviluppo Maglie Catena                          |
 * +-------------------------------------------------+
 */
    public Double maglieCatena (Double inter, Double nDCor, Double nDPig, Double PassoCat ){
        Double addOne = ((2*inter)/PassoCat);
        Double addTwo = ((nDCor+nDPig)/2);
        Double addThre = (Math.pow((nDCor-nDPig)/(Math.PI*2),2.0))/(inter/PassoCat);
        Double ValueSviluppo= Math.ceil((addOne+addTwo+addThre));
                
        return ValueSviluppo;
    } 
    }

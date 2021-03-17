package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Main main = new Main();
        JFrame frame = new JFrame();                  //JFrame oluşturuyoruz. Java GUI nin olmazsa olmazı.
        frame.add(main.getTabs("plate.jpeg"));        //JTabbedPane i ekliyoruz görsel adını vererek.
        frame.setVisible(true);
        frame.setSize(700,700);                       //frame in boyutlarını tanımlayıp görünür yapıyoruz.
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

   
    public String extractTypeFile(String path, int type){
        BufferedImage img;
        try {
            img = ImageIO.read(new File(path)); //Verilen jpeg dosyasını okuyoruz
            int imageWidth = img.getWidth();    //resmin genişliğini ve yüksekliğini alıyoruz.
            int imageHeight = img.getHeight();
            String[] givenFileName = path.split("\\.");       //Dosya adını '.' a göre ayırıyoruz ki dosyanın uzantısız
                                                              //adını alabilelim
            String extractedFileName = givenFileName[givenFileName.length-2]+".advprog"; //en son indiste dosya zuatisi var bir oncesinde dosya adi var
            //
            FileWriter writer = new FileWriter(extractedFileName);
            writer.write(type + "\n");
            writer.write(imageWidth + " " +imageHeight + "\n");
            writer.write("255\n");

            //Dosyayi pixel pixel dolasmak icin for dongusu yaziyoruz
            // Her pikselin rengini alip(img.getRGB) color da tutuyoruz

            for (int i = 0; i < imageHeight; i++){
                for (int j = 0; j < imageWidth; j++){
                    Color color = new Color(img.getRGB(j, i));
                    if (type == 2){
                        int r = color.getRed();
                        int g = color.getGreen();
                        int b = color.getBlue();
                        int colorValue = (int) ((0.3 * r) + (0.59 * g) + (0.11 * b));   // CONVERT RGB TO GRAY SCALE YAZARAK STACKOVER FLOWDAN BU FORMULU BULDUM Bu kisim alinti hocam 
                                                                                       // tip 2yse griye ceviriyoruz tip 3 se rgb degerleri yazdiriyoruz
                        writer.write(colorValue + " ");
                    }
                    else if (type == 3){
                        int r = color.getRed();
                        int g = color.getGreen();
                        int b = color.getBlue();

                        writer.write(r + " " + g + " " + b + "\n");
                    }

                }
            }

            writer.flush();  //Dosyayla olan iliskimizi kapatiyoruz. Iliskisini kesmezsek tekrar osyayi okurken sikinti oluyor
            return extractedFileName;

        } catch (IOException ignored) {
        }
        return "";
    }

    public Color[][] readTypeFile(String path){

        try {
            Scanner scanner = new Scanner(new File(path));
            int type = scanner.nextInt();
            int width = scanner.nextInt();        //Resmin genişliğini alıyoruz
            int height = scanner.nextInt();       //Resmin yüksekliğini alıyoruz
            if (type != 1){ //tip 1 de width height icerisinde 255 yoktu ya 0 yada 1 vardi ama oburlerinde 255 degeri oldugu icin okumamiz gerekiyor
                scanner.nextInt();
            }
            Color[][] imageColors = new Color[height][width];
            if (type == 3){
                for(int i=0; i<height; i++) {
                    for(int j=0; j<width; j++) {
                        int r = scanner.nextInt();   //r değerini okuyoruz.
                        int g = scanner.nextInt();   //g değerini okuyoruz.
                        int b = scanner.nextInt();   //b değerini okuyoruz.
                        imageColors[i][j] = new Color(r, g, b);
                    }
                }
            }else if(type == 2){
                for(int i=0;i<height;i++) {          //Image arrayini dolaşıyoruz.
                    for(int j=0;j<width;j++) {
                        int value = scanner.nextInt();
                        imageColors[i][j] = new Color(value, value, value);
                    }
                }
            }else{
                for(int i=0;i<height;i++) {
                    for(int j=0;j<width;j++) {
                        int value = scanner.nextInt();  //Her pikselin değerini sırasıyla okuyup image arrayinde tutuyoruz.
                        if (value == 1){                //Eğer o pikselin değeri 0 ise renk siyah değil ise beyaz.
                            imageColors[i][j] = Color.BLACK;
                        }else{
                            imageColors[i][j] = Color.WHITE;
                        }
                    }
                }
            }
            return imageColors;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String binarizeGrayTypeImage(String path){

        try {
            Scanner scanner = new Scanner(new File(path));//Verilen yoldaki dosyayı okuyacak Scanner nesnesi oluşturuyoruz.
            scanner.nextInt();                      //Dosya türünü okuyoruz.
            int width = scanner.nextInt();          //Resmin genişliğini alıyoruz
            int height = scanner.nextInt();         //Resmin yüksekliğini alıyoruz
            String[] fileName = path.split("\\.");
            String binarizedFileName = fileName[fileName.length-2]+"Binary.advprog";
            FileWriter writer = new FileWriter(binarizedFileName);
            writer.write(1 + "\n");
            writer.write(width + " " +height + "\n");
            for(int i=0;i<height;i++) {
                for(int j=0;j<width;j++) {
                    int value = scanner.nextInt();
                    if (value <= 127){
                        writer.write(1 + " ");
                    }else{
                        writer.write(0 + " ");
                    }
                }
            }
            writer.flush();

            return binarizedFileName;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public int[] getProjection(String binFilePath, String dimension){
        try {
            Scanner scanner = new Scanner(new File(binFilePath));//Verilen yoldaki dosyayı okuyacak Scanner nesnesi oluşturuyoruz.
            int type = scanner.nextInt();           //Dosya türünü okuyoruz.
            int width = scanner.nextInt();          //Resmin genişliğini alıyoruz
            int height = scanner.nextInt();         //Resmin yüksekliğini alıyoruz
            int[] projection = new int[0];          //projection arrayi olusturuyoruz
            if (dimension.equals("horizontal"))     //horizontal ise height kadar resmi dolasiyoruz
                projection = new int[height];
            else if (dimension.equals("vertical"))  //vertical ise width kadar resmi dolasiyoruz
                projection = new int[width];

            for(int i=0;i<height;i++) {               //Satir satir geziyor
                for(int j=0;j<width;j++) {            //Sutun sutun geziyor
                    if (dimension.equals("horizontal"))         //Eger dimension horizontal ise height degerini aliyoruz kacinci satirda oldugunun degerini
                        projection[i] += scanner.nextInt();
                    else if (dimension.equals("vertical"))
                        projection[j] += scanner.nextInt();     //Ornek projection 0 ilk sutundakac tane sifir var
                }
            }
            return projection;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JTabbedPane getTabs(String path){

        JTabbedPane pane = new JTabbedPane();

        String colorTypeFile = extractTypeFile(path, 3);        //renkli resim (jpeg) okuyoruz
        Color[][] colorImage = readTypeFile(colorTypeFile);         //type dosyasini okuyup iki boyutlu color arrayine ceviriyoruz
        ImagePanel p1 = new ImagePanel(colorImage);                 //image pannele vererek ekranda boyuyoruz
        pane.add("Original Image", p1);                        //yeni paneli tabe ekliyoruz

        String grayTypeFile = extractTypeFile(path, 2);  //gri resim
        Color[][] grayImage = readTypeFile(grayTypeFile);
        ImagePanel p2 = new ImagePanel(grayImage);
        pane.add("Gray Image", p2);

        String binaryFile = binarizeGrayTypeImage(grayTypeFile);
        Color[][] binaryImage = readTypeFile(binaryFile);
        ImagePanel p3 = new ImagePanel(binaryImage);
        pane.add("Binary Image", p3);

        int[] horizontalProjection = getProjection(binaryFile, "horizontal");
        for (int j : horizontalProjection) {
            System.out.println("Horizontal: " + j);
        }

        int[] verticalProjection = getProjection(binaryFile, "vertical");
        for (int j : verticalProjection) {
            System.out.println("Vertical: " + j);
        }

        ProjectionPanel p4 = new ProjectionPanel(horizontalProjection);
        pane.add("Horizontal Projection", p4);

        ProjectionPanel p5 = new ProjectionPanel(verticalProjection);
        pane.add("Vertical Projection", p5);

        List<Integer> horizontalPoints = getHorizontalLetterPoints(horizontalProjection);  //
        clearPlateEdges(horizontalPoints);
        List<Integer> verticalPoints = getVerticalLetterPoints(verticalProjection);
        clearPlateEdges(verticalPoints);
        LetterBoxPanel boxPanel = new LetterBoxPanel(colorImage, verticalPoints, horizontalPoints);
        pane.add("Detected Letters",boxPanel);

        return pane;
    }
// Bastan iki deger ve sondan iki deger siliyoruz cunku istemeden bastaki gereksiz cizgiyi ve cerceveyi de okumustuk
    public void clearPlateEdges(List<Integer> verticalPoints) {
        verticalPoints.remove(0);
        verticalPoints.remove(0);
        if (verticalPoints.size()%2!=0){
            verticalPoints.remove(verticalPoints.size()-1);         //'size' tek ise sondan bir elemani cikariyoruz cunku first point var ama last point yok
                                                                    // Bitis noktasi ararken dizi bitecek cunku
        }
        else{
            verticalPoints.remove(verticalPoints.size()-1);
            verticalPoints.remove(verticalPoints.size()-1);
        }
    }

    public List<Integer> getVerticalLetterPoints(int[] projection){

        List<Integer> points = new ArrayList<>();
        boolean isFirstPoint = true;
        for (int i=0; i<projection.length;i++) {
            if (isFirstPoint && projection[i] > 9) { /**
                                                                 Eğer aranan nokta harfin başlangıç noktası ise ve okunan değer
                                                                 10dan büyükse boşluktan harfin başladığı noktaya geçtiğini gösterir. Sonraki değerlerde isFirstPoint false
                                                                 olacağı için koşulu sağlamayıp okumaya devam edecektir böylece harfin başladığı noktayı korumuş olacağız.

             */
                points.add(i);
                isFirstPoint = false;
            } else if (!isFirstPoint && projection[i] < 10) { /**
                                                                 Eğer aranan nokta harfin bitiş noktası ise ve okunan değer
                                                                 10dan küçükse harfden boşluğun başladığı noktaya geçtiğini gösterir ve mevcut okunan değeri harfin bitiş noktası
                                                                 olarak alıyoruz.

             */
                points.add(i);
                isFirstPoint = true;
            }
        }
        return points;

    }

    /**
         * Buradaki amacımız harflerin x düzleminde başlangıç ve bitiş noktalarını belirlemek.
         * Bunun için 'points' listesinde her harf için iki değer tutacağız. Biri harfin başlangıç noktası
         * diğeri bitiş noktası. Yani çift indeksteki değerlerin başlangıç tek indeksdeki değerler bitiş noktaları.
         *
         * isFirstPoint değeri okuyacağımız değerin harfin başlangıç noktası mı bitiş noktası mı olduğunu belirlemek
         * için kullanıyoruz.
         *
         * if ler içinde karşılaştırmada kullandığımız değerler projectiondaki değerlerdeki incelemelere göre
         * harflerin arasındaki değerler hep 10dan küçük o yüzde 10 değeri uygun.

     */


    public List<Integer> getHorizontalLetterPoints(int[] projection){

        List<Integer> points = new ArrayList<>();
        boolean isFirstPoint = true;
        for (int i=0; i<projection.length;i++) {
            if (isFirstPoint && projection[i] > 99) {
                points.add(i);
                isFirstPoint = false;
            } else if (!isFirstPoint && projection[i] < 100) {
                points.add(i);
                isFirstPoint = true;
            }
        }
        return points;

    }

}

class ImagePanel  extends JPanel{

    Color[][] colorArray; //Ekranda gösterilecek resmin piksel renklerinin tutulduğu array
    int width;            //Ekranda gösterilecek resmin genişliği
    int height;           //Ekranda gösterilecek resmin yüksekliği

    ImagePanel(Color[][] array){
        this.colorArray = array;
        this.width = array[0].length; //2 boyutlu array sutun sayisini getirdik
        this.height = array.length;   // Satir sayisini getiriyoruz  array.length
    }

    @Override
    protected void paintComponent(Graphics g) {//JPanel e ait metot. Eklediğimiz panelin istediğimiz gibi boyamak için kullanıyoruz.
        super.paintComponent(g);
        for(int y=0;y<height;y++) {
            for(int x=0;x<width;x++) {
                g.setColor(colorArray[y][x]);// Pikselin rengini alıp
                g.fillRect(x, y, 1, 1);//resimdeki yerini boyuyoruz.
            }
        }
    }

}

class ProjectionPanel extends JPanel {// Java Swing in JPanel sınıfından extends ediyoruz

    int[] projection;

    ProjectionPanel(int[] projection) {
        this.projection = projection;
    }

    @Override
    protected void paintComponent(Graphics g) {//JPanel e ait metot. Eklediğimiz panelin istediğimiz gibi boyamak için kullanıyoruz.
        super.paintComponent(g);
        for (int i=0;i<projection.length;i++){
            g.drawLine(i,400,i,400-projection[i]);   //Y degerini 400 verdim biraz ortalarda olsun diye
                                                           
        }
    }

}

class LetterBoxPanel extends JPanel{

    List<Integer> verticalPoints;
    List<Integer> horizontalPoints;
    Color[][] image;


    /*

    */
    LetterBoxPanel(Color[][] image, List<Integer> vertical, List<Integer> horizontal){
        this.image = image;
        this.verticalPoints = vertical;
        this.horizontalPoints = horizontal;
    }

    @Override
    protected void paintComponent(Graphics g) {//JPanel e ait metot. Eklediğimiz panelin istediğimiz gibi boyamak için kullanıyoruz.
        super.paintComponent(g);
        for(int y=0;y<image.length;y++) {   //resmin kendisini boyuyoruz
            for(int x=0;x<image[0].length;x++) {
                g.setColor(image[y][x]);// Pikselin rengini alıp
                g.fillRect(x, y, 1, 1);//resimdeki yerini boyuyoruz.
            }
        }
        g.setColor(Color.RED); //Boyayi kirmiziya cevirdik dikdortgeni kirmizi yapabilmek icin
        for (int i=0; i<verticalPoints.size();i=i+2){   //vertical points x noktalarimiz
                                                        // horizontal points y noktalarimiz
            for (int j=0; j<horizontalPoints.size();j=j+2){
                System.out.println("i:"+i+", j:"+j);
                System.out.println(""+verticalPoints.get(i)+" "+horizontalPoints.get(j)+" "+
                        verticalPoints.get(i+1)+" "+
                        horizontalPoints.get(j+1));
                g.drawRect(verticalPoints.get(i),horizontalPoints.get(j),
                        verticalPoints.get(i+1)-verticalPoints.get(i),
                        horizontalPoints.get(j+1)-horizontalPoints.get(j));
            }
        }
    }

}

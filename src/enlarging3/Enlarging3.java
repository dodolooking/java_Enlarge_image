/***************************************************************************

  Enlarging.java

  This program reads a color image and enlarges it using bilinear 
  interpolation.

***************************************************************************/



import java.awt.image.*;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import com.pearsoneduc.ip.io.*;
import com.pearsoneduc.ip.gui.*;
import com.pearsoneduc.ip.op.OperationException;



public class Enlarging3 extends ImageSelector {


  public Enlarging3(String imageFile)
   throws IOException, ImageDecoderException, OperationException {
    super(imageFile);
  }


  // Checks that the image is suitable for simulation

  public boolean imageOK() {

    // Must be color...

    if (getSourceImage().getType() != BufferedImage.TYPE_INT_RGB)
      return false;
    
    return true;
  }


  // Resizes the image
  public BufferedImage resize(int n) {

    int width = getSourceImage().getWidth();
    int height = getSourceImage().getHeight();
    int destWidth = width*n;
    int destHeight = height*n;
    BufferedImage destImage =
     new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
    int[][] pixels = new int[height][width];
    
   
    for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++){
        pixels[i][j] = getSourceImage().getRGB(j,i);
        
      }
    
    int[][] newImage = new int[destHeight][destWidth];
    newImage = makeImage(pixels,n,width,height,destWidth,destHeight);
    
    
    for (int i = 0; i < destHeight; i++) {
        for (int j = 0; j < destWidth; j++) {
            
            destImage.setRGB(j,i,newImage[i][j]);
  
        }      
    }
    return destImage;
  }


  int[][] makeImage(int[][] pixels, int n, int width, int height,
          int destWidth, int destHeight)
  {
    int[][] temp = new int[destHeight][destWidth];
    int a, b, c, d;
    int red, green, blue;
    int _w, _h;
    float w, h;
    
    for ( int i = 0; i < destHeight; i++ ) {
        for ( int j = 0; j < destWidth; j++) {
            
            a = pixels[i/n][j/n];
            
            if( (j/n) + 1 < width )
                b = pixels[i/n][(j/n) + 1];
            else
                b = pixels[i/n][(j/n)];
            
            if( i/n + 1 < height )
                c = pixels[i/n + 1][j/n];
            else
                c = pixels[i/n][j/n];
            
            if( (j/n + 1 < width) && (i/n + 1 < height) ) 
                d = pixels[i/n + 1][j/n + 1];
            else /* last column and row */
                d = pixels[i/n][j/n];
            
            _w = j%n; w = (float)_w/n;
            _h = i%n; h = (float)_h/n;
                      
            blue = (int)((a & 0xff) * (1-w) * (1-h) + 
                    (b & 0xff) * w * (1-h) +
                    (c & 0xff) * h * (1-w) +
                    (d & 0xff) * w * h);
            
            green = (int)(((a>>8)&0xff) * (1-w) * (1-h) +
                    ((b>>8)&0xff) * w * (1-h) +
                    ((c>>8)&0xff) * h * (1-w) + 
                    ((d>>8)&0xff) * w * h);
            
            red = (int)(((a>>16)&0xff) * (1-w) * (1-h) + 
                    ((b>>16)&0xff) * w * (1-h) +
                    ((c>>16)&0xff) * h * (1-w) +
                    ((d>>16)&0xff) * w * h);
            
            temp[i][j] = 
                    (0xff000000) | // hardcode alpha
                    ((red << 16) & 0xff0000) | 
                    ((green << 8) & 0xff00) |
                    (blue & 0xff);
                
        }      
    }
    return temp;
  }

  // Creates simulated views of an image at different resolutions

  public Vector generateImages() {

    Vector enlargedImgs = new Vector();
    int width = getSourceImage().getWidth();
    int height = getSourceImage().getHeight();
    
    //Original Image
    String key = Integer.toString(width) + "x" + Integer.toString(height)+
            " Original Image";
    enlargedImgs.addElement(key);
    addImage(key, new ImageIcon(getSourceImage()));
    
    // Generate enlarged versions of source image

    for (int n = 2; n <= 3; n++) {
      key = Integer.toString(width*n) + "x" + Integer.toString(height*n)+" "+n+
              " times ";
      System.out.println(key + "...");
      enlargedImgs.addElement(key);
      addImage(key, new ImageIcon(resize(n)));
    }

    return enlargedImgs;

  }

  public static void main(String[] argv) {
    if (argv.length > 0) {
      try {
          

        JFrame frame = new Enlarging3(argv[0]);
        frame.pack();
        frame.setSize(2500, 2500);
        frame.setVisible(true);
      }
      catch (Exception e) {
	System.err.println(e);
	System.exit(1);
      }
    }
    else {
      System.err.println("usage: java Enlarging <imagefile>");
      System.exit(1);
    }
  }


}

import java.awt.image.BufferedImage;
import java.io.File;

public class Snapshotpy implements Runnable
{
    BufferedImage bufferedImg;

    // To receive content that will be outputted
    String str = "";
    int imgWidth, imgHeight, imgResolution;
    double x = 0;
    String imgPath = CharGrapher.ssgWS + "SSGSHOTS_IMG.jpg";
    File imgfile = new File(imgPath);

    public void run()
    {
        // If python script task is alive, then continue to output.
        while(!CharGrapher.pyhasdestroid)
        {
            try{
                    /* Thread.leep(MS) 100ms = .1s This means the delay in which the
                    software read image from the disk. If the value is lower, it may
                    result in exceeding disk consumption. Vice versa. */
                Thread.sleep(1);
                picproc();
            }catch(Exception ignored){}
        }
    }

    void picproc() {
        CGImage pic = new CGImage(imgfile);
        if(pic.resolution > CharGrapher.maxResolution)
        {
            // Get the ratio to compress
            x = CharGrapher.maxResolution / pic.resolution;
            x = Math.sqrt(x);
            // The 0.8 and 1.1 is ratio that adjust the output to suit the font.
            pic.compress((int) (pic.height * x * 0.8), (int) (pic.width * x * 1.1));
        }
        // Output the image.
        CharGrapher.output(pic.img, pic.height, pic.width, false);
    }
}
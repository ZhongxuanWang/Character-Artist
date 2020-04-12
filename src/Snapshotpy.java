import java.io.File;

public class Snapshotpy implements Runnable
{
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
        System.out.println(CharGrapher.ssgWS);
        File imgFile = new File(CharGrapher.ssgWS + "SSGSHOTS_IMG.jpg");

        CGImage pic = new CGImage(imgFile);
        if (pic.resolution > CharGrapher.maxResolution) {
            double x = Math.sqrt(CharGrapher.maxResolution / pic.resolution);
            pic.compress((int) (pic.height * x * 0.8), (int) (pic.width * x * 1.1));
        }
        // Output the image.
        pic.output(false);
    }
}
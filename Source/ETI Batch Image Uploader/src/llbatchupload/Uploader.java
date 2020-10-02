/*
 * Uploader.java
 *
 * Created on July 25, 2007, 2:37 AM
 * Updated on October 1, 2020, 11:00 PM
 */

package llbatchupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author greg, carlos
 */
public class Uploader extends SwingWorker<String[], Void>{
    private transient String cookie;
    private static Random rand = new Random();
    
    private File[] files;
    private MainFrame mf;
    
    /** Creates a new instance of Uploader */
    public Uploader(String cookie, File[] files, MainFrame frame) {
        this.cookie = cookie;
        this.files = files;
        this.mf = frame;
    }
    
    @Override
    protected String[] doInBackground() throws Exception {
        this.setProgress(0);
        
        int total = files.length;
        String[] ret = new String[total];
        
        float progchunk = 100f / (float)total;
        int i = 0;
        while (!isCancelled() && i < total) {
            ret[i] = upload(files[i]);
            i++;
            this.setProgress((int)(progchunk * i));
        }
        
        this.setProgress(100);
        return ret;
    }
    
    @Override
    protected void done() {
        try {
            for (String s: this.get()) {
                mf.appendText(s + '\n');
                mf.resetBar();
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    
    /** @returns URL */
    private String upload(File file) {
        int error = 0;
        //PostMethod filePost = new PostMethod("http://u" + (rand.nextInt(3) + 1) + ".endoftheinter.net/u.php");
        PostMethod filePost = new PostMethod("http://u.endoftheinter.net/u.php");
        filePost.setRequestHeader("Cookie", "PHPSESSID=" + cookie);
        filePost.getParams().getBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
        try {
            Part[] parts = {new FilePart("file", file)};

            filePost.setRequestEntity(new MultipartRequestEntity(parts,
                                                                 filePost.getParams()));
            HttpClient client = new HttpClient();

            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(filePost);

            if (status == HttpStatus.SC_OK) {
                System.out.println("Upload complete, response=" +
                                   HttpStatus.getStatusText(status));
                return generateURL(file);
            }
            System.out.println("Upload failed, response=" +
                               HttpStatus.getStatusText(status));
            return "Error " + status + " :( check your session ID!";
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getClass().getName() + " "+ ex.getMessage());
            ex.printStackTrace();
        } finally {
            filePost.releaseConnection();
        }
        return "Error :((";
    }
    
    private String generateURL(File f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fio = new FileInputStream(f);
            byte[] fb = new byte[fio.available()];
            fio.read(fb);
            md.update(fb);
            
            String md5 = Utils.byteArrayToHexString(md.digest());
            int server = rand.nextInt(3) + 1;
            
            String url = "http://i" + server + ".endoftheinter.net/i/n/" + md5 + "/" +
                    f.getName().toLowerCase();
            String markup = "<img src=\"" + url + "\">";
            
            return markup;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        
        return "Error! :(";
    }
    
}

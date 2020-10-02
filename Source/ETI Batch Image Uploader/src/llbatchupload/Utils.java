/*
 * Utils.java
 *
 * Created on July 25, 2007, 2:19 AM
 *
 */

package llbatchupload;

/**
 *
 * @author greg
 */
public class Utils {

    
    private static char[] ms_hex = new char[]
    {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    public static String byteArrayToHexString(byte[] bytes) {
        char[] result = new char[bytes.length * 2];
        int hi, lo;
        int index = 0;
        
        for (byte bt : bytes) {
            // Get high and low nibble
            hi = (bt & 0xf0) >> 4;
            lo = bt & 0x0f;
            
            result[index++] = ms_hex[hi];
            result[index++] = ms_hex[lo];
        }
        
        return new String(result);
    }
}

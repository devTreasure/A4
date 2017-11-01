package Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class old_DONOTUSE_FileSplitter {

    private static final int BUFSIZE = 64*1024;
    public boolean needsSplitting(String file, int chunkSize) {
        return new File(file).length() > chunkSize;
    }
    private static boolean isASplitFileChunk(String file) {
        return chunkIndexLen(file) > 0;
    }
    private static int chunkIndexLen(String file) {
        int n = numberOfTrailingDigits(file);
        if (n > 0) {
            String zeroes = new String(new char[n]).replace("\0", "0");
            if (file.matches(".*\\.part[0-9]{"+n+"}?of[0-9]{"+n+"}?$") && !file.endsWith(zeroes) && !chunkNumberStr(file, n).equals(zeroes)) {
                return n;
            }
        }
        return 0;
    }
    private static String getWholeFileName(String chunkName) {
        int n = chunkIndexLen(chunkName);
        if (n>0) {
            return chunkName.substring(0, chunkName.length() - 7 - 2*n); // 7+2n: 1+4+n+2+n : .part012of345
        }
        return chunkName;
    }
    private static int getNumberOfChunks(String filename) {
        int n = chunkIndexLen(filename);
        if (n > 0) {
            try {
                String digits = chunksTotalStr(filename, n);
                return Integer.parseInt(digits);
            } catch (NumberFormatException x) { // should never happen
            }
        }
        return 1;
    }
    private static int getChunkNumber(String filename) {
        int n = chunkIndexLen(filename);
        if (n > 0) {
            try {
                // filename.part001of200
                String digits = chunkNumberStr(filename, n);
                return Integer.parseInt(digits)-1;
            } catch (NumberFormatException x) {
            }
        }
        return 0;
    }
    private static int numberOfTrailingDigits(String s) {
        int n=0, l=s.length()-1;
        while (l>=0 && Character.isDigit(s.charAt(l))) {
            n++; l--;
        }
        return n;
    }
    private static String chunksTotalStr(String filename, int chunkIndexLen) {
        return filename.substring(filename.length()-chunkIndexLen);
    }
    protected static String chunkNumberStr(String filename, int chunkIndexLen) {
        int p = filename.length() - 2 - 2*chunkIndexLen; // 123of456
        return filename.substring(p,p+chunkIndexLen);
    }
    // 0,8 ==> part1of8; 7,8 ==> part8of8
    private static String chunkFileName(String filename, int n, int total, int chunkIndexLength) {
        return filename+String.format(".part%0"+chunkIndexLength+"dof%0"+chunkIndexLength+"d", n+1, total);
    }
    public static String[] splitFile(String fname, long chunkSize) throws IOException {
        FileInputStream fis = null;
        ArrayList<String> res = new ArrayList<String>();
        byte[] buffer = new byte[BUFSIZE];
        try {
            long totalSize = new File(fname).length();
            int nChunks = (int) ((totalSize + chunkSize - 1) / chunkSize);
            int chunkIndexLength = String.format("%d", nChunks).length();
            fis = new FileInputStream(fname);
            long written = 0;
            for (int i=0; written<totalSize; i++) {
                String chunkFName = chunkFileName(fname, i, nChunks, chunkIndexLength);
                FileOutputStream fos = new FileOutputStream(chunkFName);
                try {
                    written += copyStream(fis, buffer, fos, chunkSize);
                } finally {
                    fos.close();
                }
                res.add(chunkFName);
            }
        } finally {
            fis.close();
        }
        return res.toArray(new String[0]);
    }
    public static boolean canJoinFile(String chunkName) {
        int n = chunkIndexLen(chunkName);
        if (n>0) {
            int nChunks = getNumberOfChunks(chunkName);
            String filename = getWholeFileName(chunkName);
            for (int i=0; i<nChunks; i++) {
                if (!new File(chunkFileName(filename, i, nChunks, n)).exists()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static void joinChunks(String chunkName) throws IOException {
        int n = chunkIndexLen(chunkName);
        if (n>0) {
            int nChunks = getNumberOfChunks(chunkName);
            String filename = getWholeFileName(chunkName);
            byte[] buffer = new byte[BUFSIZE];
            FileOutputStream fos = new FileOutputStream(filename);
            try {
                for (int i=0; i<nChunks; i++) {
                    FileInputStream fis = new FileInputStream(chunkFileName(filename, i, nChunks, n));
                    try {
                        copyStream(fis, buffer, fos, -1);
                    } finally {
                        //Closer.closeSilently(fis);
                    	fis.close();
                    }
                }
            } finally {
               // Closer.closeSilently(fos);
            	fos.close();
            }
        }
    }
    public static boolean deleteAllChunks(String chunkName) {
        boolean res = true;
        int n = chunkIndexLen(chunkName);
        if (n>0) {
            int nChunks = getNumberOfChunks(chunkName);
            String filename = getWholeFileName(chunkName);
            for (int i=0; i<nChunks; i++) {
                File f = new File(chunkFileName(filename, i, nChunks, n));
                res &= (f.delete() || !f.exists());
            }
        }
        return res;
    }
    private static long copyStream(FileInputStream fis, byte[] buffer, FileOutputStream fos, long maxAmount) throws IOException {
        long chunkSizeWritten;
        for (chunkSizeWritten=0; chunkSizeWritten<maxAmount || maxAmount<0; ) {
            int toRead = maxAmount < 0 ? buffer.length : (int)Math.min(buffer.length, maxAmount - chunkSizeWritten);
            int lengthRead = fis.read(buffer, 0, toRead);
            if (lengthRead < 0) {
                break;
            }
            fos.write(buffer, 0, lengthRead);
            chunkSizeWritten += lengthRead;
        }
        return chunkSizeWritten;
    }
}
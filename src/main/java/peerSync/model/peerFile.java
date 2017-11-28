/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peerSync.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class peerFile {

    private File file;
    private String relativeDirectory;
    private String md5;

    public peerFile(File file, File baseFolder) {
        System.out.println(baseFolder);
        this.file = file;
        this.relativeDirectory = baseFolder.toURI().relativize(file.toURI()).getPath();
        this.md5 = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(this.file);
            this.md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
            fileInputStream.close();
        }
        catch (IOException ex) {
            System.out.println("File not Found");
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "peerFile{" + "file=" + file + ", relativeDirectory=" + relativeDirectory + ", md5=" + md5 + '}';
    }
    

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getRelativeDirectory() {
        return relativeDirectory;
    }

    public void setRelativeDirectory(String relativeDirectory) {
        this.relativeDirectory = relativeDirectory;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    

}

package com.example.demo;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.source.tree.BreakTree;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@RestController
public class HelloResponder {
    private int successfullDownloads;
    public static Set<String> hashHistory=new HashSet<>();
    private int total;
    private static String downloadDirectory="C:\\Users\\gengi\\Desktop\\spring_image_downloader\\imageApp\\downloadedImages";
    @GetMapping("/urls")
    public String urls(@RequestParam("urls") String[] urls){
        Set<String> urlSet = new HashSet<>();
        this.total=0;
        this.successfullDownloads=0;
        for(String url:urls){
            urlSet.add(url);
            this.total++;
        }


        for(String url:urlSet){
            if(validURL(url)){
                if(downloadHelper(url)) this.successfullDownloads++;
            }

        }

        StringJoiner joiner = new StringJoiner("<br/>");
        for(String url : urlSet){
            joiner.add(url);
        }
        joiner.add(String.format("Total links were %d and successfully downloaded images were %d",this.total,this.successfullDownloads));
        return joiner.toString();
    }
    public static boolean downloadHelper (String url) {

        try {
            URL imageUrl = new URL(url);
//            System.out.println("image url is " + imageUrl);
            String [] segments=imageUrl.getPath().split("/");
            String fileName =segments[segments.length-1];
            File outputFile = new File(downloadDirectory,fileName);
//            System.out.println(outputFile);
            if (outputFile.exists()) {
                return false; // skipping this for testing
            }
            OutputStream outputStream = new FileOutputStream(outputFile);

            // Create an input stream to read the bytes from the URL.
            InputStream inputStream =imageUrl.openStream();;


            // Create a buffer to store the bytes.
            byte[] buffer = new byte[1024];

//             Write the bytes to the file.
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
            Path filePath = Path.of(outputFile.getPath());
            System.out.println("filespath for checking md5 is  " + filePath);
            byte[] data = Files.readAllBytes(Paths.get(filePath.toUri()));
            byte[] hash = new byte[0];
            try {
                hash = MessageDigest.getInstance("MD5").digest(data);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            String checksum = new BigInteger(1, hash).toString(16);
            if(!hashHistory.isEmpty() &&hashHistory.contains(checksum)){
                //delete file
                outputFile.delete();
            }
            else{
                hashHistory.add(checksum);
            }

        }
        catch (IOException e){

            return false;
        }
        return  true;
    }
    public static boolean validURL(String url){
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
//            System.out.println("hehe not a valid url "+url);
            return false;
        }
    }

}

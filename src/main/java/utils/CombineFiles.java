package utils;

import java.io.File;

import com.google.gson.Gson;

public class CombineFiles {
    /**
     * Combines all the data files of the different games in the data/raw folder into one file
     */
    public static void main(String[] args){
        Gson gson = utilGsonV2.buildGson("data/raw/combined.jsonl");
        File directory = new File("data/raw");
        for (File file : directory.listFiles()) {
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);
            }
            
            if (file.getName() != "combined.jsonl" && extension.equals("jsonl"))
            {
                System.out.println("Rewriting " + file.getName());
                utilGsonV2.rewrite(file.getPath());
            }
        }
    }
}

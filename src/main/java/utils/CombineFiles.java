package utils;

import java.io.File;

import com.google.gson.Gson;

public class CombineFiles {
    /**
     * Combines all the data files of the different games in the data/raw folder into one file
     */
    public static void main(String[] args){
        Gson gson = utilGsonV2.buildGson("data/raw/combined_data.jsonl", "data/raw/combined_result.jsonl");
        File directory = new File("data/raw");
        for (File file : directory.listFiles()) {
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);
            }
            
            if (file.getName() != "combined_data.jsonl"
                && file.getName() != "combined_result.jsonl"
                && extension.equals("jsonl")
                && file.getName().substring(0, 4).equals("data"))    //result are combined at the same time as data in rexrite to keep the right order
            {
                System.out.println("Rewriting " + file.getName());
                utilGsonV2.rewrite(file.getPath(), file.getParent() + "/result" + file.getName().substring(4));
            }
        }
    }
}

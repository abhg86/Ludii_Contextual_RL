package utils;

import other.context.Context;
// import other.GameLoader;
// import other.move.Move;
// import other.trial.Trial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import features.feature_sets.network.SPatterNetFeatureSet;
import features.generation.AtomicFeatureGenerator;
// import game.Game;
// import main.collections.FastArrayList;

// import com.google.gson.JsonElement;
// import org.apache.commons.lang3.SerializationUtils;
// import main.collections.FastArrayList;


/**
 * Class used to build the Gson object used to serialize and deserialize the tuples needed for the AI
 * V2 with tuples of State/Action features, Move, Game description (String)
 * Can build a Gson object with the correct instance creators and type adapters with the buildGson(String whereToWrite) method
 * Can also save and load the objects to a file with the addTupple(...) then writeTupleListToFile() and readTupleFromFile() methods
 * /!\ WARNING /!\ : Needs to build the Gson object before using the writeTupleToFile() and readTupleFromFile() methods
 */
public class utilGsonV2 {
    private static String fileLocation;
    private static Gson gson;
    private static int[] supportedPlayers;

    /**
     * Builds the Gson object used to serialize and deserialize the states
     * @return the Gson object 
     */
    public static  Gson buildGson(String fileLocation) {
        utilGsonV2.fileLocation = fileLocation;
        GsonBuilder gsonbld = new GsonBuilder();
        Gson gson = gsonbld.create();
        utilGsonV2.gson = gson;
        return gson;
    }

    public static void modifyFileLocation(String fileLocation) {
        utilGsonV2.fileLocation = fileLocation;
    }

    /**
     * Writes the tuple at the end of the file
     * @param TupleAI tuple (i.e. a tuple of the form (State, Move, string description of the game)
     * @param context
     */
    public static void writeTupleToFile(TupleAI tuple, Context context) {
        File file = new File(fileLocation);

        if (!file.exists()) {
        
            try {
                File directory = new File(file.getParent());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Exception Occurred: " + e.toString());
            }
        }
        
        try {            
            FileWriter writer;
            writer = new FileWriter(file.getAbsoluteFile(), true);
            
            BufferedWriter bufferWriter = new BufferedWriter(writer);

            if (supportedPlayers == null) {
                supportedPlayers = new int[context.game().players().count()];
                for (int i = 0; i < supportedPlayers.length; ++i)
                    {
                        supportedPlayers[i] = i + 1;
                    }
                }
            AtomicFeatureGenerator atomicFeatureGenerator = new AtomicFeatureGenerator(context.game(), 2, 4);
            SPatterNetFeatureSet featureSet = new SPatterNetFeatureSet(atomicFeatureGenerator.getAspatialFeatures(), atomicFeatureGenerator.getSpatialFeatures());
            featureSet.init(context.game(), supportedPlayers, null);

            TupleIntermediaryV2 tupleMoveInString = new TupleIntermediaryV2(featureSet.computeFeatureVector(context, tuple.b, true).toString(),tuple.b.toTrialFormat(context),tuple.c);
            bufferWriter.write(gson.toJson(tupleMoveInString).toString());
            bufferWriter.newLine();
            bufferWriter.close();
        } catch (IOException e) {
            
            System.out.println("Hmm.. Got an error while saving data to file " + e.toString());
        }
    }

    public static void writeTupleToFile(TupleIntermediaryV2 tuple){
        File file = new File(fileLocation);

        if (!file.exists()) {
        
            try {
                File directory = new File(file.getParent());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Exception Occurred: " + e.toString());
            }
        }
        
        try {            
            FileWriter writer;
            writer = new FileWriter(file.getAbsoluteFile(), true);
            
            BufferedWriter bufferWriter = new BufferedWriter(writer);

            bufferWriter.write(gson.toJson(tuple).toString());
            bufferWriter.newLine();
            bufferWriter.close();
        } catch (IOException e) {
            
            System.out.println("Hmm.. Got an error while saving data to file " + e.toString());
        }
    }

    public static void addTuples(List<TupleIntermediaryV2> tuppleList){
        for (TupleIntermediaryV2 tupple : tuppleList) {
            writeTupleToFile(tupple);
        }
    }

    /**
     * Reads the tuples from the file
     * @return a TupleIntermediaryV2 list (i.e. a list of tuples with a state, a move and a description of the game in string format)
     */
    public static List<TupleIntermediaryV2> readTuplesFromFile() {
        File file = new File(fileLocation);
        
        if (!file.exists())
            System.out.println("File doesn't exist");
        
        InputStreamReader isReader;
        try {
            isReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            
            JsonReader myReader = new JsonReader(isReader);
            List<TupleIntermediaryV2> tuples = new ArrayList<>();
            try {
                while (true){
                    tuples.add(gson.fromJson(myReader, TupleIntermediaryV2.class));
                }
            } catch (Exception e) {
                // To catch EOF exception
                // System.out.println(e);
            }
            return tuples;
            
        } catch (Exception e) {
            System.out.println("error load cache from file " + e.toString());
        }
		return null;
    }

    /**
     * Rewrites the tuples from the file given to the file used when creating the Gson object
     */
    public static void rewrite(String filepath1) {
        File file = new File(filepath1);
        
        if (!file.exists())
            System.out.println("File doesn't exist");
        
        InputStreamReader isReader;
        try {
            isReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            
            JsonReader myReader = new JsonReader(isReader);
            try {
                while (true){
                    utilGsonV2.writeTupleToFile(gson.fromJson(myReader, TupleIntermediaryV2.class));
                }
            } catch (Exception e) {
                // To catch EOF exception
                // System.out.println(e);
            }
            
        } catch (Exception e) {
            System.out.println("error load cache from file " + e.toString());
        }
    }
// TESTS
//
    // public static void main(final String[] args){
    //     Game game = GameLoader.loadGameFromName("Chess.lud");
    //     Trial trial = new Trial(game);
    //     Context context = new Context(game, trial);
    //     game.start(context);
    //     buildGson("data/data.jsonl");

    //     final FastArrayList<Move> legalMoves = game.moves(context).moves();
    //     final Move firstMove = legalMoves.get(0);
    //     // writeTupleToFile(new TupleAI(context.state(), firstMove, game.description().raw()), context);
	// 	System.out.println("Applying move: " + firstMove);
	// 	game.apply(context, firstMove);
    //     final Move secondMove = game.moves(context).moves().get(0);
    //     System.out.println("Applying move: " + secondMove);
    //     game.apply(context, secondMove);
    //     final Move thirdMove = game.moves(context).moves().get(0);
    //     writeTupleToFile(new TupleAI(context.state(), thirdMove, game.description().raw()), context);
    //     System.out.println("Applying move: " + thirdMove);

    //     List<TupleIntermediaryV2> tuples = readTuplesFromFile();
    //     System.out.println("tuples: " + tuples.size());
        // System.out.println("tuples: " + tuples.get(1).b);

        // File file = new File(fileLocation);
        // if (!file.exists())
        //     System.out.println("File doesn't exist");
        // InputStreamReader isReader;
        // List<List<TupleIntermediaryV2>> tuples = new ArrayList<>();
        // try {
        //     isReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            
        //     JsonReader myReader = new JsonReader(isReader);
        //     byte[] json = gson.fromJson(myReader, byte[].class);
        //     String jsonInString2 = (String) SerializationUtils.deserialize(json);
        //     System.out.println("jsonInString2: " + jsonInString2);
        // } catch (Exception e) {
        //     System.out.println("error load cache from file " + e.toString());
        // }
    // }
}
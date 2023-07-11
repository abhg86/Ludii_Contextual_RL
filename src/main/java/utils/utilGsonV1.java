package utils;

import game.Game;
import other.GameLoader;
import other.context.Context;
import other.move.Move;
import other.state.State;
import other.trial.Trial;
import other.state.container.*;
import other.state.owned.*;
import other.state.puzzle.*;
import other.state.stacking.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

// import com.google.gson.JsonElement;
// import org.apache.commons.lang3.SerializationUtils;
// import main.collections.FastArrayList;

class StateInstanceCreator implements InstanceCreator<State> {
    private Context context;

    public StateInstanceCreator(Context context) {
        this.context = context;
    }

    @Override
    public State createInstance(Type type) {
        //Needs to create a new move from scratch whatever the instantiation, gson will override it
        final State state = new State(context.state());
        return state;
    }
}

/**
 * Class used to build the Gson object used to serialize and deserialize the tuples needed for the AI
 * V1 with tuples of State, Move, Game description (String)
 * Can build a Gson object with the correct instance creators and type adapters with the buildGson(String whereToWrite) method
 * Can also save and load the objects to a file with the addTupple(...) then writeTupleListToFile() and readTupleFromFile() methods
 * /!\ WARNING /!\ : Needs to build the Gson object before using the writeTupleToFile() and readTupleFromFile() methods
 */
public class utilGsonV1 {
    private static final Game game = GameLoader.loadGameFromName("Amazons.lud");    //only for the instance creators
    private static final Trial trial = new Trial(game);                             //only for the instance creators
    private static final Context context = new Context(game, trial);                //only for the instance creators
	private static String fileLocation;
    private static Gson gson;

    /**
     * Builds the Gson object used to serialize and deserialize the states
     * Takes into account the state instance creator as well as all the subtypes used in the State class (Containers and Owned) that needs to be registered
     * @return the Gson object 
     */
    public static  Gson buildGson(String fileLocation) {
        utilGsonV1.fileLocation = fileLocation;
        GsonBuilder gsonbld = new GsonBuilder()
                .registerTypeAdapter(State.class, new StateInstanceCreator(context));

        // /!\ WARNING /!\ : The upper classes MUST register all subclasses recursively

        // Registering all the subclasses of the ContainerState interface

        RuntimeTypeAdapterFactory<ContainerState> typeFactoryContainer = RuntimeTypeAdapterFactory.of(ContainerState.class, "type");
        typeFactoryContainer.registerSubtype(BaseContainerState.class)
                    .registerSubtype(ContainerFlatEdgeState.class)
                    .registerSubtype(ContainerFlatState.class)
                    .registerSubtype(ContainerFlatVertexState.class)
                    .registerSubtype( BaseContainerStateStacking.class)
                    .registerSubtype(ContainerGraphState.class)
                    .registerSubtype(BaseContainerStateDeductionPuzzles.class)
                    .registerSubtype(ContainerDeductionPuzzleState.class)
                    .registerSubtype(ContainerDeductionPuzzleStateLarge.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryContainer);

        RuntimeTypeAdapterFactory<BaseContainerState> typeFactoryBaseContainer = RuntimeTypeAdapterFactory.of(BaseContainerState.class, "type");
        typeFactoryBaseContainer.registerSubtype(ContainerFlatEdgeState.class)
                    .registerSubtype(ContainerFlatState.class)
                    .registerSubtype(ContainerFlatVertexState.class)
                    .registerSubtype( BaseContainerStateStacking.class)
                    .registerSubtype(ContainerGraphState.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryBaseContainer);

        RuntimeTypeAdapterFactory<ContainerFlatState> typeFactoryFlatContainer = RuntimeTypeAdapterFactory.of(ContainerFlatState.class, "type");
        typeFactoryFlatContainer.registerSubtype(ContainerGraphState.class)
                    .registerSubtype(ContainerFlatState.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryFlatContainer);

        RuntimeTypeAdapterFactory<BaseContainerStateStacking> typeFactoryBaseContainerStacking = RuntimeTypeAdapterFactory.of(BaseContainerStateStacking.class, "type");
        typeFactoryBaseContainerStacking.registerSubtype(ContainerStateStacks.class)
                    .registerSubtype(ContainerGraphStateStacks.class)
                    .registerSubtype(ContainerStateStacksLarge.class)
                    .registerSubtype(ContainerGraphStateStacksLarge.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryBaseContainerStacking);

        RuntimeTypeAdapterFactory<ContainerStateStacks> typeFactoryStateStacks = RuntimeTypeAdapterFactory.of(ContainerStateStacks.class, "type");
        typeFactoryStateStacks.registerSubtype(ContainerGraphStateStacks.class); 
        gsonbld.registerTypeAdapterFactory(typeFactoryStateStacks);
        
        RuntimeTypeAdapterFactory<ContainerStateStacksLarge> typeFactoryStateStacksLarge = RuntimeTypeAdapterFactory.of(ContainerStateStacksLarge.class, "type");
        typeFactoryStateStacksLarge.registerSubtype(ContainerGraphStateStacksLarge.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryStateStacksLarge);

        RuntimeTypeAdapterFactory<BaseContainerStateDeductionPuzzles> typeFactoryBaseContainerDeductionPuzzles = RuntimeTypeAdapterFactory.of(BaseContainerStateDeductionPuzzles.class, "type");
        typeFactoryBaseContainerDeductionPuzzles.registerSubtype(ContainerDeductionPuzzleState.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryBaseContainerDeductionPuzzles);

        RuntimeTypeAdapterFactory<ContainerDeductionPuzzleState> typeFactoryDeductionPuzzle = RuntimeTypeAdapterFactory.of(ContainerDeductionPuzzleState.class, "type");
        typeFactoryDeductionPuzzle.registerSubtype(ContainerDeductionPuzzleStateLarge.class)
                    .registerSubtype(ContainerDeductionPuzzleState.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryDeductionPuzzle);

        // Registering all the subclasses of the Owned interface
        RuntimeTypeAdapterFactory<Owned> typeFactoryOwned = RuntimeTypeAdapterFactory.of(Owned.class, "type");
        typeFactoryOwned.registerSubtype(CellOnlyOwned.class)
                    .registerSubtype(FlatCellOnlyOwned.class)
                    .registerSubtype(FlatVertexOnlyOwned.class)
                    .registerSubtype(FlatVertexOnlyOwnedSingleComp.class);
        gsonbld.registerTypeAdapterFactory(typeFactoryOwned);

        Gson gson = gsonbld.create();
        utilGsonV1.gson = gson;
        return gson;
    }

    /**
     * Writes the tuple at the end of the file
     * @param TupleAI tuple (i.e. a tuple of the form (State, Move, string description of the game)
     * @param context
     */
    public static void writeTupleToFile(TupleAI tuple, Context context) {
        File file = new File(fileLocation);
        
        
        // exists(): Tests whether the file or directory denoted by this abstract pathname exists.
        if (!file.exists()) {
        
            try {
                File directory = new File(file.getParent());
                if (!directory.exists()) {
                    
                    // mkdirs(): Creates the directory named by this abstract pathname, including any necessary but nonexistent parent directories.
                    // Note that if this operation fails it may have succeeded in creating some of the necessary parent directories.
                    directory.mkdirs();
                }
                
                // createNewFile(): Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist.
                // The check for the existence of the file and the creation of the file if it does not exist are a single operation
                // that is atomic with respect to all other filesystem activities that might affect the file.
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Exception Occurred: " + e.toString());
            }
        }
        
        try {
            
            // Convenience class for writing character files
            FileWriter writer;
            writer = new FileWriter(file.getAbsoluteFile(), true);
            
            // Writes text to a character-output stream
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            TupleIntermediary tupleMoveInString = new TupleIntermediary(tuple.a,tuple.b.toTrialFormat(context),tuple.c);
            bufferWriter.write(gson.toJson(tupleMoveInString).toString());
            bufferWriter.newLine();

            // If you want to write the data in bytes, uncomment the following line
            // bufferWriter.write((String) gson.toJson(SerializationUtils.serialize(gson.toJson(tupleList).toString())));
            bufferWriter.close();
        } catch (IOException e) {
            
            System.out.println("Hmm.. Got an error while saving data to file " + e.toString());
        }
    }

    /**
     * Reads the tuples from the file
     * @return a TupleAI list (i.e. a list of tuples with a state, a move and a description of the game in string format)
     */
    public static List<TupleAI> readTuplesFromFile() {
        
        // File: An abstract representation of file and directory pathnames.
        // User interfaces and operating systems use system-dependent pathname strings to name files and directories.
        File file = new File(fileLocation);
        
        if (!file.exists())
            System.out.println("File doesn't exist");
        
        InputStreamReader isReader;
        try {
            isReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            
            JsonReader myReader = new JsonReader(isReader);
            List<TupleAI> tuples = new ArrayList<>();
            try {
                while (true){
                    TupleIntermediary tupleMoveInString = gson.fromJson(myReader, TupleIntermediary.class);
                    tuples.add(new TupleAI(tupleMoveInString.a,new Move(tupleMoveInString.b),tupleMoveInString.c));
                }
            } catch (Exception e) {
                // To catch EOF exception
                System.out.println(e);
            }
            return tuples;
            
        } catch (Exception e) {
            System.out.println("error load cache from file " + e.toString());
        }
		return null;
    }

// TESTS
//
//     public static void main(final String[] args){
//         Game game = GameLoader.loadGameFromName("Hex.lud");
//         Trial trial = new Trial(game);
//         Context context = new Context(game, trial);
//         game.start(context);
//         buildGson("data/data.jsonl");

//         final FastArrayList<Move> legalMoves = game.moves(context).moves();
//         final Move firstMove = legalMoves.get(0);
//         writeTupleToFile(new TupleAI(context.state(), firstMove, game.description().raw()), context);
// 		System.out.println("Applying move: " + firstMove);
// 		game.apply(context, firstMove);
//         final Move secondMove = game.moves(context).moves().get(0);
//         writeTupleToFile(new TupleAI(context.state(), secondMove, game.description().raw()), context);
//         System.out.println("Applying move: " + secondMove);

//         List<TupleAI> tuples = readTuplesFromFile();
//         System.out.println("tuples: " + tuples.size());
//         System.out.println("tuples: " + tuples.get(1).b);

//         // File file = new File(fileLocation);
//         // if (!file.exists())
//         //     System.out.println("File doesn't exist");
//         // InputStreamReader isReader;
//         // List<List<TupleIntermediary>> tuples = new ArrayList<>();
//         // try {
//         //     isReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            
//         //     JsonReader myReader = new JsonReader(isReader);
//         //     byte[] json = gson.fromJson(myReader, byte[].class);
//         //     String jsonInString2 = (String) SerializationUtils.deserialize(json);
//         //     System.out.println("jsonInString2: " + jsonInString2);
//         // } catch (Exception e) {
//         //     System.out.println("error load cache from file " + e.toString());
//         // }
//     }
}
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DocumentsProcessor implements IDocumentsProcessor {

    /**
     * DocumentIterator Constructor
     */
    public DocumentsProcessor() {

    }

    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {
        File folder = new File(directoryPath);
        // listOfFiles is an array storing all file names
        File[] listOfFiles = folder.listFiles();
        // use HashMap to store <filename, list of words sequences>
        HashMap<String, List<String>> map = new HashMap<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile()) {
                String fileName = file.getName();
//                String filePath = directoryPath + "/" + fileName;
                List<String> sequenceList = new ArrayList<String>();
                try {
                    BufferedReader bfReader = new BufferedReader(new FileReader(file));
                    DocumentIterator iterator = new DocumentIterator(bfReader, n);

                    while (iterator.hasNext()) {
                        sequenceList.add(iterator.next());
                    }
                    // after iterating a file, append this entry to map
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                map.put(fileName, sequenceList);
            }
        }
        return map;
    }

    @Override
    public List<Tuple<String, Integer>> storeNWordSequences(Map<String, List<String>> docs,
            String nwordFilePath) {
        // create a outputStream
        List<Tuple<String, Integer>> res = new ArrayList<Tuple<String, Integer>>();
        try {
            FileOutputStream out = new FileOutputStream(nwordFilePath);
            for (String key : docs.keySet()) {
                int bytesCnt = 0;
                List<String> wordsSequence = new ArrayList<String>();
                wordsSequence = docs.get(key);
                for (String s : wordsSequence) {
                    s = s + " ";
                    byte[] seq = s.getBytes();
                    bytesCnt += seq.length;
                    try {
                        out.write(seq);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Tuple<String, Integer> tuple = new Tuple<String, Integer>(key, bytesCnt);
                res.add(tuple);
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public TreeSet<Similarities> computeSimilarities(String nwordFilePath,
            List<Tuple<String, Integer>> fileindex) {
        // the tree we want to return
        TreeSet<Similarities> tree = new TreeSet<>();
        Map<String, Set<String>> wordToFile = new HashMap<>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        try {
            FileInputStream fs = new FileInputStream(nwordFilePath);
            for (Tuple<String, Integer> tuple : fileindex) {
                // number of bytes
                Integer numberBytes = tuple.getRight();
                // file name
                String name = tuple.getLeft();
                byte[] bytes = new byte[numberBytes];
                try {
                    // read up the numberBytes bytes from the stream
                    fs.read(bytes);
                    String sequenceWithSpace = new String(bytes);
                    String[] sequences = sequenceWithSpace.split("\\s+");
                    // convert the sequences list to a set to remove duplicates
                    HashSet<String> seqSet = new HashSet<String>(Arrays.asList(sequences));
                    for (String seq : seqSet) {
                        if (wordToFile.containsKey(seq)) {
                            for (String existantFile : wordToFile.get(seq)) {
                                if (!map.containsKey(existantFile + " " + name)) {
                                    map.put(existantFile + " " + name, 1);
                                } else {
                                    map.put(existantFile + " " + name,
                                            map.get(existantFile + " " + name) + 1);
                                }
                            }
                            wordToFile.get(seq).add(name);
                        } else {
                            HashSet<String> fileSet = new HashSet<>();
                            fileSet.add(name);
                            wordToFile.put(seq, fileSet);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // try to close the file input stream
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String pair : map.keySet()) {
            String[] filePair = pair.split("\\s+");
            Similarities sim = new Similarities(filePair[0], filePair[1]);
            sim.setCount(map.get(pair));
            tree.add(sim);
        }
        return tree;
    }

    @Override
    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
        // create a new comparator
        Comparator<Similarities> comp = new Comparator<Similarities>() {
            @Override
            public int compare(Similarities o1, Similarities o2) {
                // for descending order
                if (o2.getCount() != o1.getCount()) {
                    return o2.getCount() - o1.getCount();
                } else {
                    if (!o1.getFile1().equals(o2.getFile1())) {
                        return o1.getFile1().compareTo(o2.getFile1());
                    } else {
                        return o1.getFile2().compareTo(o2.getFile2());
                    }
                }
            }
        };
        // create a new TreeSet to add sim with count over the threshold

        TreeSet<Similarities> newTree = new TreeSet<>(comp);
        newTree.addAll(sims);

        for (Similarities printSim : newTree) {
            if (printSim.getCount() > threshold) {
                System.out.printf("%d: %s, %s\n", printSim.getCount(), printSim.getFile1(),
                        printSim.getFile2());
            }
        }
    }

    // For part5, to save space, merge the process and store methods into one method
    /**
     * 
     * @param directoryPath - the path to the directory
     * @param sequenceFile  - the filename of the output file
     * @param n             - the number of words used to compose a sequence
     * @return List of tuples of files and their size in sequenceFile
     */
    public List<Tuple<String, Integer>> processAndStore(String directoryPath, String sequenceFile,
            int n) {
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();
        // initialize the list we will return as res
        List<Tuple<String, Integer>> res = new ArrayList<Tuple<String, Integer>>();
        // out is the stream to store the sequence
        try {
            FileOutputStream out = new FileOutputStream(sequenceFile);
            // iterate each file in the folder
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile()) {
                    String fileName = file.getName();
//                    String filePath = directoryPath + "/" + fileName; // full address for a file
                    String sequenceWithSpaceString = "";
//                    FileInputStream fStream;
                    try {
                        BufferedReader bfReader = new BufferedReader(new FileReader(file));
                        DocumentIterator iterator = new DocumentIterator(bfReader, n);
                        while (iterator.hasNext()) {
                            sequenceWithSpaceString += iterator.next();
                            sequenceWithSpaceString += " ";
                        }
                        byte[] bytes = sequenceWithSpaceString.getBytes();
                        // write the sequence as bytes to the sequenceFile
                        try {
                            out.write(bytes);
                            Tuple<String, Integer> tuple = new Tuple<String, Integer>(fileName,
                                    bytes.length);
                            res.add(tuple);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        // close the readStream after reading and write to put for each file

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) { // catch for open OutputStream
            e1.printStackTrace();
        }
        return res;
    }

}

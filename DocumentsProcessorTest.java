import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import org.junit.Test;

public class DocumentsProcessorTest {

    // test the sequence from "file1.txt"
    @Test
    public void processDocumentsTest1() {
        DocumentsProcessor dProcessor = new DocumentsProcessor();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = dProcessor.processDocuments("autograder/submission/test1_folder", 2);
        List<String> sequenceList = new ArrayList<String>();
        sequenceList = map.get("file1.txt");
        assertEquals("thisis", sequenceList.get(0));
        assertEquals("isa", sequenceList.get(1));
        assertEquals("afile", sequenceList.get(2));
    }

    // test the sequence from "file2.txt"
    @Test
    public void processDocumentsTest2() {
        DocumentsProcessor dProcessor = new DocumentsProcessor();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = dProcessor.processDocuments("autograder/submission/test1_folder", 2);
        List<String> sequenceList = new ArrayList<String>();
        sequenceList = map.get("file2.txt");
        assertEquals("thisis", sequenceList.get(0));
        assertEquals("isanother", sequenceList.get(1));
        assertEquals("anotherfile", sequenceList.get(2));
    }

    // test the size of the returned map
    @Test
    public void processDocumentsTest3() {
        DocumentsProcessor dProcessor = new DocumentsProcessor();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = dProcessor.processDocuments("autograder/submission/test1_folder", 2);
        assertEquals(2, map.size());
    }

    // Test part2 storeNwordSequence()
    @Test // test the fileNames in tuple left
    public void storeNWordSequencesTest1() {
        DocumentsProcessor dProcessor = new DocumentsProcessor();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = dProcessor.processDocuments("autograder/submission/test2_folder", 4);
        List<Tuple<String, Integer>> resList = new ArrayList<Tuple<String, Integer>>();
        resList = dProcessor.storeNWordSequences(map, "autograder/submission/output2.txt");
        assertEquals("file1.txt", resList.get(0).getLeft());
        assertEquals("file2.txt", resList.get(1).getLeft());
    }

    @Test // test the bytes number in tuple right
    public void storeNWordSequencesTest2() {
        DocumentsProcessor dProcessor = new DocumentsProcessor();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map = dProcessor.processDocuments("autograder/submission/test2_folder", 4);
        List<Tuple<String, Integer>> resList = new ArrayList<Tuple<String, Integer>>();
        resList = dProcessor.storeNWordSequences(map, "autograder/submission/output2.txt");
        assertEquals(Integer.valueOf(28), resList.get(0).getRight());
        assertEquals(Integer.valueOf(42), resList.get(1).getRight());
    }

    @Test // test the output file content
    public void storeNWrodSequencesTest3() {
        try {
            FileInputStream inputStream = new FileInputStream("autograder/submission/output2.txt");
            String content = "";
            Scanner scan = new Scanner(inputStream);
            while (scan.hasNextLine()) {
                content += scan.nextLine();
            }
            scan.close();
            assertEquals("thisisatest isatestdocument thisisalsoa isalsoatest alsoatestdocument ",
                    content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Part3 test for computeSimilarities()
    @Test
    public void computeSimilaritiesTest1() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        docs = dp.processDocuments("autograder/submission/test3_folder", 3);
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = dp.storeNWordSequences(docs, "autograder/submission/output3.txt");
        TreeSet<Similarities> treeSet = new TreeSet<>();
        treeSet = dp.computeSimilarities("autograder/submission/output3.txt", resList);
        Similarities o2 = new Similarities("file1.txt", "file3.txt");
        for (Similarities o : treeSet) {
            if (o.equals(o2)) {
                assertEquals(3, o.getCount());
            }
        }

    }

    @Test
    public void computeSimilaritiesTest2() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        docs = dp.processDocuments("autograder/submission/test3_folder", 3);
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = dp.storeNWordSequences(docs, "autograder/submission/output3.txt");
        TreeSet<Similarities> treeSet = new TreeSet<>();
        treeSet = dp.computeSimilarities("autograder/submission/output3.txt", resList);
        Similarities o2 = new Similarities("file1.txt", "file2.txt");
        for (Similarities o : treeSet) {
            if (o.equals(o2)) {
                assertEquals(3, o.getCount());
            }
        }
    }

    @Test
    public void computeSimilaritiesTest3() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        docs = dp.processDocuments("autograder/submission/test3_folder", 3);
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = dp.storeNWordSequences(docs, "autograder/submission/output3.txt");
        TreeSet<Similarities> treeSet = new TreeSet<>();
        treeSet = dp.computeSimilarities("autograder/submission/output3.txt", resList);
        Similarities o2 = new Similarities("file3.txt", "file2.txt");
        for (Similarities o : treeSet) {
            if (o.equals(o2)) {
                assertEquals(3, o.getCount());
            }
        }
    }

    // Part4 test printSimilarities
    @Test
    public void printSimilaritiesTest1() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        docs = dp.processDocuments("autograder/submission/test3_folder", 3);
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = dp.storeNWordSequences(docs, "autograder/submission/output3.txt");
        TreeSet<Similarities> treeSet = new TreeSet<>();
        treeSet = dp.computeSimilarities("autograder/submission/output3.txt", resList);
        // test output to the console
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream console = System.out;
        System.setOut(new PrintStream(baos));
        dp.printSimilarities(treeSet, 0);
        System.setOut(console);
        assertEquals("3: file1.txt, file2.txt\n" + "3: file1.txt, file3.txt\n"
                + "3: file3.txt, file2.txt\n", baos.toString());
    }

    @Test
    public void printSimilaritiesTest2() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        docs = dp.processDocuments("autograder/submission/test4_folder", 3);
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = dp.storeNWordSequences(docs, "autograder/submission/output4.txt");
        TreeSet<Similarities> treeSet = new TreeSet<>();
        treeSet = dp.computeSimilarities("autograder/submission/output4.txt", resList);
        // test output to the console
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream console = System.out;
        System.setOut(new PrintStream(baos));
        dp.printSimilarities(treeSet, 2);
        System.setOut(console);
        assertEquals("3: file3.txt, file2.txt\n", baos.toString());
        assertNotEquals("3: file3.txt, file2.txt\n", baos.toString().trim());
    }

    // Part5 test processAndStore
    @Test
    public void processAndStoreTest1() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> resList = new ArrayList<>();
        resList = (List<Tuple<String, Integer>>) dp.processAndStore(
                "autograder/submission/test5_folder", "autograder/submission/output5.txt", 4);
        assertEquals(Integer.valueOf(28), resList.get(0).getRight());
    }

}

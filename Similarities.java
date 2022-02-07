/**
 * @author ericfouh
 */
public class Similarities implements Comparable<Similarities> {
    /**
     * 
     */
    private String file1;
    private String file2;
    private int count;

    /**
     * @param file1
     * @param file2
     */
    public Similarities(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
        this.setCount(0);
    }

    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }

    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(Similarities o) {
        if (this.file1.compareTo(o.getFile1()) != 0) {
            return this.file1.compareTo(o.getFile1());
        } else {
            return this.file2.compareTo(o.getFile2());
        }
    }

}

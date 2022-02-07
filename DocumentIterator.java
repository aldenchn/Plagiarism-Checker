import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int n;
    private int c = -1;

    public DocumentIterator(Reader rmReader, int n) {
        this.r = rmReader;
        this.n = n;
        skipNonLetters();
    }

    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }

    @Override
    public boolean hasNext() {
        int backupC = this.c;
        if (c == -1) {
            return false;
        } else {
            try {
                r.mark(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < n; i++) {
                while (Character.isLetter(this.c)) {
                    try {
                        this.c = this.r.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                skipNonLetters();
                if (c == -1 && i < n - 1) {
                    // if during this loop, c is set to be -1, there is no enough n words left
                    this.c = backupC;
                    return false;
                }
            }
            try { // reaching this line means there is a n words sequence left
                r.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.c = backupC;
            return true;
        }

    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String answer = "";
        boolean set = false;
        int backupC = 0;
        try {
            for (int j = 0; j < n; j++) {
                // try to catch the next n-1 words, add to answer
                // after read the first word, mark the beginning of the second word for future
                // use
                if (j == 1) {
                    r.mark(10000);
                    set = true;
                    backupC = this.c;
                }
                while (Character.isLetter(this.c)) {
                    // convert it the lower case
                    this.c = Character.toLowerCase(this.c);
                    answer = answer + (char) this.c;
                    this.c = this.r.read();
                }
                skipNonLetters();
            }
        } catch (IOException e) {
            throw new NoSuchElementException();
        }
        // next time, we want the reader to start at the next word
        if (set) { // reset only if we have marked somewhere before
            try {
                r.reset();
                // backup is the first word in next sequence, restore it here, when n >= 2
                this.c = backupC;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    public char getC() {
        return (char) this.c;
    }

}

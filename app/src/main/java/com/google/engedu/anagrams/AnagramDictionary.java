package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS    = 3;
    private static final int DEFAULT_WORD_LENGTH = 4;
    private static final int MAX_WORD_LENGTH     = 7;

    private static int wordLength = DEFAULT_WORD_LENGTH;
    private static int modes      = 0;
    private static int level      = 1;


    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<Integer, ArrayList<String>> sizeToWords;
    private HashMap<String, ArrayList<String>> lettersToWords;
    private Random random = new Random();

    public AnagramDictionary(InputStream wordListStream) throws IOException {

        wordList       = new ArrayList<>();
        wordSet        = new HashSet();
        lettersToWords = new HashMap();
        sizeToWords    = new HashMap();

        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        int size;

        while((line = in.readLine()) != null) {

            String word       = line.trim();
            size              = word.length();
            String sortedWord = sortLetters(word);
            wordList.add(word);
            wordSet.add(word);

            //populate a hash map of words sizes
            if(sizeToWords.containsKey(size)){
                ArrayList lenAtKey = sizeToWords.get(size);
                lenAtKey.add(word);
                sizeToWords.put(size, lenAtKey);
            } else {
                ArrayList newLen = new ArrayList();
                newLen.add(word);
                sizeToWords.put(size, newLen);
            }

            //populate a hash map of sorted letters to anagrams
            if(lettersToWords.containsKey(sortedWord)){
                ArrayList listAtKey = lettersToWords.get(sortedWord);
                listAtKey.add(word);
                lettersToWords.put(sortedWord,listAtKey);
            } else {
                ArrayList newList = new ArrayList();
                newList.add(word);
                lettersToWords.put(sortedWord, newList);
            }
        }


    }

    public boolean isGoodWord(String word, String base) {

        Pattern subString  = Pattern.compile(base);
        Matcher fullString = subString.matcher(word);

        if(fullString.find()) return false;        //regex string base to see if it is in word
        if(!wordSet.contains(word)) return false;  // check to see if word is in hashset

        return true;
    }

 //                         Methods for Game Modes below
//--------------------------------------------------------------------------------------------------

    public ArrayList<String> getAnagrams(String targetWord) {

        ArrayList<String> result = new ArrayList<String>();
        String sortedTarget      = sortLetters(targetWord);

        switch(modes){

            // normal/easy mode
            case 0:
                if(lettersToWords.containsKey(sortedTarget)){
                    for(String dictionaryWord: lettersToWords.get(sortedTarget)){
                        result.add(dictionaryWord);
                    }
                }
                break;

            // medium mode: adding 1 letter to anagrams
            case 1:
                for(char alpha = 'a'; alpha <= 'z'; alpha++){

                    String tempWord = sortLetters(alpha + targetWord);

                    if(lettersToWords.containsKey(tempWord)){
                        for(String anagrams: lettersToWords.get(tempWord)){
                            if(isGoodWord(tempWord, targetWord))
                                result.add(anagrams);
                        }
                    }
                }
                break;

            // two letter mode code
            case 2:
                for(int i = 0; i < 2; i++){
                    getAnagramsWithOneMoreLetter(targetWord);
                }

                break;

            // two word mode
            case 3:
                break;
        }

        return result;

    }

    /**
     * Useful, using code in getAnagrams()
     * @param word
     * @return
     */
    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();

        for(char alpha = 'a'; alpha <= 'z'; alpha++){

            String tempWord = sortLetters(alpha + word);

            if(lettersToWords.containsKey(tempWord)){
                for(String anagrams: lettersToWords.get(tempWord)){
                    if(isGoodWord(tempWord, word))
                    result.add(anagrams);
                }
            }
        }
        return result;
    }

    // Hard mode: adding 2 letter to form anagrams
    // Harder mode: adding 2 words to form anagrams

//--------------------------------------------------------------------------------------------------
    public String pickGoodStarterWord() {


        int size = sizeToWords.get(wordLength).size();

        // set the length size of starting words by using sizeToWords hashmap
        ArrayList<String> defaultLen = sizeToWords.get(wordLength);
        String starterWord           = defaultLen.get(random.nextInt(size));

        while(getAnagrams(starterWord).size() < MIN_NUM_ANAGRAMS){
            starterWord = defaultLen.get(random.nextInt(size));
        }

        //every 4 words length's increase
        if(level%5 == 0 && wordLength < MAX_WORD_LENGTH) {
            wordLength++;
        }

        level++;

        return starterWord;

    }

 // ----------------------------------------helper-functions----------------------------------------
    public String sortLetters(String word){

        char [] letters  = word.toCharArray();
        Arrays.sort(letters);

        return new String(letters);
    }

    public void setMode(int level){
        this.modes = level;
    }
}

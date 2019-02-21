import java.io.File;
import java.util.Scanner;

 //remember to close file


public class SourceModel {
    private String modelStrName;
    private String corpusName;
    private double probability;
    private int[][] countMatrix = new int[26][26];
    private double[][] probabilityMatrix = new double[26][26];

    private Scanner scan = new Scanner(System.in);



    public SourceModel(String mStrName1, String corpusName1) throws Exception {
        modelStrName = mStrName1;
        corpusName = corpusName1;
        Scanner sc = new Scanner(new File(corpusName));
        char current;
        int lastCharIndex = -1;
        int currCharIndex = 0;

        System.out.print("Training " + modelStrName + " model ... ");

        while (sc.hasNext()) {
            String str = sc.next();
            char[] myChar = str.toCharArray();
            for (int a = 0; a < myChar.length; a++) {

                if (Character.isAlphabetic(myChar[a]) && lastCharIndex == -1) {
                    current = Character.toLowerCase(myChar[a]);
                    lastCharIndex = current - 'a';
                }   else if (Character.isAlphabetic(myChar[a])) {
                    current = Character.toLowerCase(myChar[a]);
                    currCharIndex = current - 'a';
                    countMatrix[lastCharIndex][currCharIndex] += 1;
                    lastCharIndex = currCharIndex;
                }
            }
        }
        System.out.println("done.");



        //probability matrix

        for (int row = 0; row < 26; row++) {
            int rowSum = 0;
            //change from for each to for iteration
            for (int num : countMatrix[row]) {
                rowSum += num;
            }

            for (int col = 0; col < 26; col++) {
                if (countMatrix[row][col] == 0) {
                    probabilityMatrix[row][col] = 0.01;
                }   else {
                    probabilityMatrix[row][col] = countMatrix[row][col]
                        / ((double) rowSum);
                }
            }

        }


    }

    public String getName() {
        return modelStrName;
    }

    public double probability(String testString) {
        probability = 1.0;
        String str = testString;
        str = str.replaceAll("[^a-zA-Z]", "");
        str = str.toLowerCase();
        str = str.replaceAll("\\s+", "");
        //System.out.print(str);
        for (int i = 0; i < str.length() - 1; i++) {
            char current = str.charAt(i);
            char next = str.charAt(i + 1);
            probability *= (probabilityMatrix[current - 'a'][next - 'a']);
            //System.out.println("= " + probability);
        }

        return probability;
    }

    public String toString() {
        String str  = "Model: " + getName() + "\n";
        str += " ";
        for (int i = 1; i < 27; i++) {
            str += "    " + (char) (i + 96);
        }
        str += "\n";

        int counter = 97;

        for (int row = 0; row < probabilityMatrix.length; row++) {
            str += (char) counter + " ";
            for (int col = 0; col < probabilityMatrix[row].length; col++) {
                str += String.format("%.02f", probabilityMatrix[row][col]);
                str += " ";
            }
            counter++;
            str += "\n";
        }




        return str;
    }



    public static void main(String[] args) throws Exception {
        SourceModel[] sm = new SourceModel[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            int index = args[i].indexOf('.');
            sm[i] = new SourceModel(args[i].substring(0, index), args[i]);
        }
        System.out.println("Analyzing: " + args[args.length - 1]);


        double totalP = 0.0;
        double largestP = sm[0].probability(args[args.length - 1]);
        String largestPName = sm[0].getName();
        for (int a = 0; a < sm.length; a++) {
            double p = sm[a].probability(args[args.length - 1]);
            totalP += p;
            if (p > largestP) {
                largestP = p;
                largestPName = sm[a].getName();
            }
        }
        for (int a = 0; a < sm.length; a++) {
            //System.out.println(sm[a]);
            System.out.printf("Probability that test string is %8s %.02f%n",
                               sm[a].getName(),
                               (sm[a].probability(args[args.length - 1]))
                               / totalP);
        }

        System.out.print("Test string is most likely " + largestPName + ".");



    }

}

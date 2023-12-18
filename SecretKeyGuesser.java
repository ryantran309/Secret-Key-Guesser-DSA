public class SecretKeyGuesser {

    private SecretKey secretKey;

    // Contains the guessing frequencies of the characters
    // 0: M, 1: O, 2: C, 3: H, 4: A
    private final int[] frequencies;

    private int guessAttempt;
    private int correctness;

    // should be a char array to make it easier to handle with
    private char highestFrequencyChar;
    private char[] resultSecret;

    // why choose "switch" over "if"? because it iz blazingly(TM) fast
    private static char getChar(int pos) {
        char rs = '\0';
        switch (pos) {
            case 0: rs = 'M'; break;
            case 1: rs = 'O'; break;
            case 2: rs = 'C'; break;
            case 3: rs = 'H'; break;
            case 4: rs = 'A'; break;
        };
        return rs;
    }

    private static int getPosition(char c) {
        int rs = -1;
        switch (c) {
            case 'M': rs = 0; break;
            case 'O': rs = 1; break;
            case 'C': rs = 2; break;
            case 'H': rs = 3; break;
            case 'A': rs = 4; break;
        }
        return rs;
    }

    public SecretKeyGuesser() {
        this.secretKey = new SecretKey();
        this.frequencies = new int[5];
        this.guessAttempt = 0;
        this.highestFrequencyChar = 'M';
        this.correctness = 0;
    }

    public void start() {

        int highestFrequency = 0;

        // 1. Attempt to get frequencies of characters
        for (int i = 0; i < 5; i++) {
            var singletonString = String.valueOf(getChar(i)).repeat(12);
            var freq = secretKey.guess(singletonString);

            guessAttempt += 1;
            frequencies[i] = freq;

            if (freq >= highestFrequency) {
                highestFrequency = freq;
                highestFrequencyChar = getChar(i);
            }
        }

        // 2. after gotten the frequencies of the strings, we create a singleton string of "highest frequencies" char.
        resultSecret = String.valueOf(highestFrequencyChar).repeat(12).toCharArray();
        correctness = highestFrequency;

        // 3. then, we guess in order, M -> O -> C -> H -> A, skip it was a "highestFrequencyChar" or non-existing chars
        for (int i = 0; i < 5; i++) {
            var guessingChar = getChar(i);
            if (guessingChar == highestFrequencyChar || frequencies[i] == 0) {
                continue;
            }

            System.out.println("Now testing: " + guessingChar);
            attemptChar(guessingChar);
            System.out.println();
        }
    }

    private void attemptChar(char guessingChar) {

        int charLeft = frequencies[getPosition(guessingChar)];

        // we place a char into "placeable" position (which has highestFrequencyChar)
        for (int i = 0; i < 12; i++) {

            if (resultSecret[i] != highestFrequencyChar) {
                continue;
            }

            // place the guessing char into and then test whether placement is correct
            resultSecret[i] = guessingChar;
            guessAttempt += 1;

            var testingCorrectness = secretKey.guess(getResultSecret());

            if (testingCorrectness > correctness) {
                correctness = testingCorrectness;
                charLeft -= 1;
                if (charLeft == 0) {
                    System.out.println("Temporary result secret: " + getResultSecret()
                            + ", with correctness: "  + correctness
                            + ", after guess attempt: " + guessAttempt);
                    return;
                }
                continue;
            }

            // failed test: remove guessing char
            resultSecret[i] = highestFrequencyChar;
        }
    }

    public int getGuessAttempt() {
        return guessAttempt;
    }

    public String getResultSecret() {
        return new String(resultSecret);
    }

    public static void main(String[] args) {
        var secretKeyGuesser = new SecretKeyGuesser();
        secretKeyGuesser.start();

        System.out.println(secretKeyGuesser.getResultSecret() + " after: " + secretKeyGuesser.getGuessAttempt());
    }
}

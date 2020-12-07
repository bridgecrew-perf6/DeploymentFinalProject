
import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


class WordsCounterJUnit {

	void test() throws IOException {

		ArrayList<String> words = new ArrayList<String>();
		GuiProject test = new GuiProject();

		String path = "//Users//hellenfernandes//Documents//GUI//poem.txt";

		FileInputStream fileInp = new FileInputStream(path);
		Scanner s = new Scanner(fileInp);

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		while (s.hasNext()) {
			String nextWord = s.next().toLowerCase();

			nextWord = nextWord.replaceAll("[^a-zA-Z]", "");

			if (map.containsKey(nextWord)) {
				int counter = map.get(nextWord) + 1;
				map.put(nextWord, counter);
			} else {
				map.put(nextWord, 1);
			}
		}
		s.close();

		System.out.println("\n****SORTED BY MOST FREQUENTLY USED WORD****\n");
		map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(20)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.forEach((a, integer) -> System.out
						.println(String.format("Word: %s\t\t| Frequency: %s times", a, integer)));

		HashMap<String, Integer> expected = test.wordsCounter(words, path);

		assertEquals(map, expected);

	}

}

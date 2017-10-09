package org.apache.lucene.morphology.russian;

import java.util.List;

import org.apache.lucene.morphology.LuceneMorphology;

public class RussianMorphologyTestBench {

	public static void main(String[] args) throws Exception {

		String sentence = "дворянские семьи";
		//String sentence = "много дворян";
		String[] words = sentence.split(" ");
		LuceneMorphology morphology = new RussianLuceneMorphology();
		for (String word : words) {
			System.out.println("word: " + word);
			List<String> lemmas = morphology.getLemmas(word);
			for (String lemma : lemmas) {
				System.out.println("lemma: " + lemma);
			}
			System.out.println("-------------------");
		}
	}

}

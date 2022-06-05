package main.compression;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for compressing and
 * decompressing text corpi with comparable distributions of characters.
 */
public class Huffman {

	private Map<Character, Integer> charFrequencies;
	private HuffNode trieRoot;
	private TreeMap<Character, String> encodingMap;
	private static final char ETB_CHAR = 23;

	public Huffman(String corpus) {
		charFrequencies = new HashMap<Character, Integer>();
		encodingMap = new TreeMap<Character, String>();
		if (corpus.isEmpty()) {
			trieRoot = new HuffNode(ETB_CHAR, 1);
			encode(trieRoot, "");
			return;
		}
		fillFrequencies(corpus);
		huffmanTrie();
		encode(trieRoot, "");
	}

	private void fillFrequencies(String corpus) {
		int character = 0;
		int i = 0;

		for (i = 0; i < corpus.length(); i++) {
			int counter = 0;
			for (character = 0; character < corpus.length(); character++) {
				if (corpus.charAt(i) == corpus.charAt(character)) {
					counter++;
				}
			}
			charFrequencies.put(corpus.charAt(i), counter);
		}
		charFrequencies.put(ETB_CHAR, 1);
	}

	private void huffmanTrie() {
		Queue<HuffNode> queue = new PriorityQueue<>();
		for (var entry : charFrequencies.entrySet()) {
			queue.add(new HuffNode(entry.getKey(), entry.getValue()));
		}
		System.out.println(charFrequencies.toString());
		while (queue.size() > 1) {
			HuffNode zeroChild = queue.poll();
			HuffNode oneChild = queue.poll();
			HuffNode parent;

			if (zeroChild.character < oneChild.character) {
				parent = new HuffNode(zeroChild.character, zeroChild.count + oneChild.count);
			} else {
				parent = new HuffNode(oneChild.character, zeroChild.count + oneChild.count);
			}
			parent.zeroChild = zeroChild;
			parent.oneChild = oneChild;
			queue.add(parent);

		}
		trieRoot = queue.poll();
	}

	private void encode(HuffNode node, String code) {
		if (node.isLeaf()) {
			encodingMap.put(node.character, code);
			return;
		} else {
			encode(node.zeroChild, code + "0");
			encode(node.oneChild, code + "1");
		}
	}

	/**
	 * Compresses the given String message / text corpus into its Huffman coded
	 * bitstring, as represented by an array of bytes. Uses the encodingMap field
	 * generated during construction for this purpose.
	 * 
	 * @param message String representing the corpus to compress.
	 * @return {@code byte[]} representing the compressed corpus with the Huffman
	 *         coded bytecode. Formatted as: (1) the bitstring containing the
	 *         message itself, (2) possible 0-padding on the final byte.
	 */
	public byte[] compress(String message) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String bitString = "";
		for (int i = 0; i < message.length(); i++) {
			if (encodingMap.containsKey(message.charAt(i)))
				;
			{
				bitString += encodingMap.get(message.charAt(i));
			}
		}
		bitString += encodingMap.get((char) ETB_CHAR);

		while (bitString.length() % 8 != 0) {
			bitString += "0";
		}
		System.out.println(encodingMap);
		System.out.println(bitString);
		for (int i = 0; i < bitString.length(); i += 8) {
			output.write((byte) (Integer.parseInt(bitString.substring(i, i + 8), 2)));
		}
		return output.toByteArray();
	}

	/**
	 * Decompresses the given compressed array of bytes into their original, String
	 * representation. Uses the trieRoot field (the Huffman Trie) that generated the
	 * compressed message during decoding.
	 * 
	 * @param compressedMsg {@code byte[]} representing the compressed corpus with
	 *                      the Huffman coded bytecode. Formatted as: (1) the
	 *                      bitstring containing the message itself, (2) possible
	 *                      0-padding on the final byte.
	 * @return Decompressed String representation of the compressed bytecode
	 *         message.
	 */
	public String decompress(byte[] compressedMsg) {
		String bits = "";
		int len = compressedMsg.length;
		for (int i = 0; i < len; i++) {
			bits = bits + String.format("%8s", Integer.toBinaryString(compressedMsg[i] & 0xff)).replace(' ', '0');
		}
		return decoder(trieRoot, bits, "");
	}

	private String decoder(HuffNode current, String bits, String empty) {
		if (current.isLeaf()) {
			if (current.character == ETB_CHAR) {
				return empty;
			} else {
				return decoder(trieRoot, bits, empty + Character.toString(current.character));
			}
		}
		if (bits.charAt(0) == '0') {
			return decoder(current.zeroChild, bits.substring(1), empty);
		} else if (bits.charAt(0) == '1') {
			return decoder(current.oneChild, bits.substring(1), empty);
		}
		return null;
	}

	/**
	 * Huffman Trie Node class used in construction of the Huffman Trie. Each node
	 * is a binary (having at most a left (0) and right (1) child), contains a
	 * character field that it represents, and a count field that holds the number
	 * of times the node's character (or those in its subtrees) appear in the
	 * corpus.
	 */
	private static class HuffNode implements Comparable<HuffNode> {

		HuffNode zeroChild, oneChild;
		char character;
		int count;

		public String toString() {
			return "" + character + " " + count;
		}

		HuffNode(char character, int count) {
			this.count = count;
			this.character = character;
		}

		public boolean isLeaf() {
			return this.zeroChild == null && this.oneChild == null;
		}

		public int compareTo(HuffNode other) {
			int freqDifference = this.count - other.count;
			if (freqDifference != 0) {
				return freqDifference;
			} else {
				return this.character - other.character;
			}
		}
	}
}

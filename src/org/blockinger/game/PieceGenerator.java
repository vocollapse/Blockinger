package org.blockinger.game;

import java.util.Random;

public class PieceGenerator {

	public static final int STRAT_RANDOM = 0;
	public static final int STRAT_7BAG = 1;
	
	int strategy;
	int bag[];
	int bagPointer;
	private Random rndgen;
	
	public PieceGenerator(int strat) {
		bag = new int[7];
		for(int i = 0; i < 7; i++) //initial Permutation
			bag[i] = i;
		
		rndgen = new Random(System.currentTimeMillis());
		if(strat==STRAT_RANDOM)
			this.strategy = STRAT_RANDOM;
		else
			this.strategy = STRAT_7BAG;
		
		// Fill initial Bag
		for(int i = 0; i < 6; i++) {
			int c = rndgen.nextInt(7-i);
			int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;	/* swap */
		}
		bagPointer = 0;
	}

	public int next() {
		if(strategy== STRAT_RANDOM)
			return rndgen.nextInt(7);
		else {
			if(bagPointer < 7) {
				bagPointer++;
				return bag[bagPointer - 1];
			} else {
				// Randomize Bag
				for(int i = 0; i < 6; i++) {
					int c = rndgen.nextInt(7-i);
					int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;	/* swap */
				}
				bagPointer = 1;
				return bag[bagPointer - 1];
			}
		}
	}
}
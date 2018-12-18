/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

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
            int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;    /* swap */
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
                    int t = bag[i]; bag[i] = bag[i+c]; bag[i+c] = t;    /* swap */
                }
                bagPointer = 1;
                return bag[bagPointer - 1];
            }
        }
    }
}

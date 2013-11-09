/**
 * File: $HeadURL$
 * Revision: $Rev$
 * Last modified: $Date$
 * Last modified by: $Author$
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contacting the authors:
 *   Mario Arias:               mario.arias@deri.org
 *   Javier D. Fernandez:       jfergar@infor.uva.es
 *   Miguel A. Martinez-Prieto: migumar2@infor.uva.es
 *   Alejandro Andres:          fuzzy.alej@gmail.com
 */

package org.rdfhdt.hdt.triples.impl;

import org.rdfhdt.hdt.compact.bitmap.AdjacencyList;
import org.rdfhdt.hdt.compact.sequence.DeflateIntegerIterator;
import org.rdfhdt.hdt.enums.ResultEstimationType;
import org.rdfhdt.hdt.enums.TripleComponentOrder;
import org.rdfhdt.hdt.exceptions.NotImplementedException;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;

/**
 * 
 * Iterates over all Y components of a BitmapTriples. 
 * i.e. In SPO it would iterate over all appearances of a predicate ?P?
 * 
 * @author mario.arias
 *
 */
public class BitmapTriplesIteratorYFOQ implements IteratorTripleID {
		private BitmapTriples triples;
		private TripleID pattern, returnTriple;
		private int patY;
		
		private AdjacencyList adjY, adjZ;
		private long posY, posZ;
		private long prevY, nextY, prevZ, nextZ;
		private int x, y, z;
		
		DeflateIntegerIterator index;
		
		BitmapTriplesIteratorYFOQ(BitmapTriples triples, TripleID pattern) {
			this.triples = triples;
			this.pattern = new TripleID(pattern);
			this.returnTriple = new TripleID();
			
			TripleOrderConvert.swapComponentOrder(this.pattern, TripleComponentOrder.SPO, triples.order);
			patY = this.pattern.getPredicate();
			if(patY==0) {
				throw new IllegalArgumentException("This structure is not meant to process this pattern");
			}
			
			adjY = new AdjacencyList(triples.seqY, triples.bitmapY);
			adjZ = new AdjacencyList(triples.seqZ, triples.bitmapZ);
			
			index = new DeflateIntegerIterator(triples.indexYBuffers);
					
			goToStart();
		}
		
		private void updateOutput() {
			returnTriple.setAll(x, y, z);
			TripleOrderConvert.swapComponentOrder(returnTriple, triples.order, TripleComponentOrder.SPO);
		}
		
		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return nextY!=-1 || posZ<=nextZ;
		}
		
		private long findNextAppearance(long basePos, long pattern) {
			if(triples.indexYBuffers!=null) {
				return index.next();
			} else {
				return adjY.findNextAppearance(nextY+1, patY);	
			}
		}
		
		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#next()
		 */
		@Override
		public TripleID next() {	
			if(posZ>nextZ) {
				prevY = posY;
				posY = nextY;
				nextY = findNextAppearance(nextY+1, patY);
				
				posZ = prevZ = adjZ.find(posY);
				nextZ = adjZ.last(posY); 
				
				x = (int) adjY.findListIndex(posY)+1;
				y = (int) adjY.get(posY);
	 			z = (int) adjZ.get(posZ);
			} else {
				z = (int) adjZ.get(posZ);
			}
			posZ++;	
		
			updateOutput();
			
			return returnTriple;
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#hasPrevious()
		 */
		@Override
		public boolean hasPrevious() {
			return prevY!=-1 || posZ>=prevZ;
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#previous()
		 */
		@Override
		public TripleID previous() {
			if(posZ<=prevZ) {
				nextY = posY;
				posY = prevY;
				prevY = adjY.findPreviousAppearance(prevY-1, patY);

				posZ = prevZ = adjZ.find(posY);
				nextZ = adjZ.last(posY); 
				
				x = (int) adjY.findListIndex(posY)+1;
				y = (int) adjY.get(posY);
	 			z = (int) adjZ.get(posZ);
			} else {
				posZ--;
				z = (int) adjZ.get(posZ);
			}
			
			updateOutput();

			return returnTriple;
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#goToStart()
		 */
		@Override
		public void goToStart() {
			prevY = -1;

			if(triples.indexYBuffers!=null) {
				index.reset(patY-1);
				posY = index.next();
				nextY = index.next();
			} else {
				posY = adjY.findNextAppearance(0, patY);
				nextY = adjY.findNextAppearance(posY+1, patY);
			}
			
			posZ = prevZ = adjZ.find(posY);
			nextZ = adjZ.last(posY);
			
			x = (int) adjY.findListIndex(posY)+1;
			y = (int) adjY.get(posY);
	        z = (int) adjZ.get(posZ);
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#estimatedNumResults()
		 */
		@Override
		public long estimatedNumResults() {
			return adjZ.getNumberOfElements();
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#numResultEstimation()
		 */
		@Override
		public ResultEstimationType numResultEstimation() {
		    return ResultEstimationType.UNKNOWN;
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#canGoTo()
		 */
		@Override
		public boolean canGoTo() {
			return false;
		}

		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#goTo(int)
		 */
		@Override
		public void goTo(long pos) {
			if(!canGoTo()) {
				throw new IllegalAccessError("Cannot goto on this bitmaptriples pattern");
			}
		}
		
		/* (non-Javadoc)
		 * @see hdt.iterator.IteratorTripleID#getOrder()
		 */
		@Override
		public TripleComponentOrder getOrder() {
			return triples.order;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
}
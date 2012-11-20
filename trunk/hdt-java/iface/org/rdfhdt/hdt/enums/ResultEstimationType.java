/**
 * File: $HeadURL: https://hdt-java.googlecode.com/svn/trunk/hdt-java/src/org/rdfhdt/hdt/enums/ResultEstimationType.java $
 * Revision: $Rev: 71 $
 * Last modified: $Date: 2012-09-21 18:31:19 +0100 (vie, 21 sep 2012) $
 * Last modified by: $Author: mario.arias $
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

package org.rdfhdt.hdt.enums;

/**
 * @author mario.arias
 *
 */
public enum ResultEstimationType {
	/**
	 * The number of results is completely unknown.
	 */
	UNKNOWN,
	
	/**
	 * The number of results is not exact, but approximately the specified value.
	 */
	APPROXIMATE,
	
	/**
	 * The number of results is unknown, but never bigger than the specified value.
	 */
	UP_TO,
	
	/**
	 *  The number of results is exactly the specified value.
	 */
	EXACT
}
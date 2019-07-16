/*-
 * #%L
 * This file is part of QuPath.
 * %%
 * Copyright (C) 2014 - 2016 The Queen's University of Belfast, Northern Ireland
 * Contact: IP Management (ipmanagement@qub.ac.uk)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package qupath.lib.measurements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * A MeasurementList that stores its measurements in either a float or a double array, 
 * to avoid the overhead of storing large numbers of Measurement objects.
 * 
 * This makes the storage quite efficient for lists that don't require supporting dynamic measurements.
 * 	
 * In this implementation, lookups by measurement name initially use indexOf with a list - and
 * can be rather slow.  Therefore while 'adding' is fast, 'putting' is not.
 * 
 * However, upon calling closeList(), name lists are shared between similarly closed NumericMeasurementLists,
 * and a map used to improve random access of measurements.  Therefore if many lists of the same measurements
 * are made, remembering to close each list when it is fully populated can improve performance and greatly
 * reduce memory requirements.
 * 
 * These lists can be instantiated through the MeasurementListFactory class.
 * 
 * @author Pete Bankhead
 *
 */
class NumericMeasurementList {
	
	final private static Logger logger = LoggerFactory.getLogger(NumericMeasurementList.class);
	
	private static Map<List<String>, NameMap> namesPool = Collections.synchronizedMap(new WeakHashMap<>());
	

	private static class NameMap {
		
		private List<String> names;
		private Map<String, Integer> map;
		
		NameMap(List<String> names) {
			this.names = Collections.unmodifiableList(new ArrayList<>(names)); // Make a defensive copy
			createHashMap();
		}
		
		private void createHashMap() {
			map = new HashMap<>();
			int i = 0;
			for (String s : names) {
				map.put(s, i);
				i++;
			}
		}
		
		List<String> getNames() {
			return names;
		}
		
		Map<String, Integer> getMap() {
			return map;
		}
		
	}
	
	
	
	private static abstract class AbstractNumericMeasurementList implements MeasurementList {
		
		private static final long serialVersionUID = 1L;
		
		protected static final int EXPAND = 8; // Amount by which to expand array as required
		
		List<String> names;
		boolean isClosed = false;

		private Map<String, Integer> map; // Optional map for fast measurement lookup

		AbstractNumericMeasurementList(int capacity) {
			names = new ArrayList<>(capacity);
		}
		
		/**
		 * Set the value at the specified list index
		 * 
		 * @param index
		 * @param value
		 */
		protected abstract void setValue(int index, double value);
		
		boolean isClosed() {
			return isClosed;
		}

		@Override
		public void closeList() {
			if (isClosed())
				return;
			compactStorage();
			// Try to get a shared list & map
			synchronized(namesPool) {
				NameMap nameMap = namesPool.get(names);
				if (nameMap == null) {
					nameMap = new NameMap(this.names);
					namesPool.put(nameMap.names, nameMap);
//					logger.info("CREATED");
				}
//				else
//					logger.info("Using....");					
				this.names = nameMap.getNames();
				this.map = nameMap.getMap();
			}
			isClosed = true;
		}

		@Override
		public boolean isEmpty() {
			return names.isEmpty();
		}
		
		/**
		 * Consider that this simply uses indexOf with a list - so it is not fast!
		 * @param name
		 * @return
		 */
		int getMeasurementIndex(String name) {
			// Read from map, if possible
			if (map != null) {
				Integer ind = map.get(name);
				return ind == null ? -1 : ind;
			}
			return names.indexOf(name);
		}
		
		@Override
		public boolean add(Measurement measurement) {
			if (measurement.isDynamic())
				throw new UnsupportedOperationException("This MeasurementList does not support dynamic measurements");
			return addMeasurement(measurement.getName(), measurement.getValue());
	    }
		
		@Override
		final public int size() {
			return names.size();
		}

		@Override
		public synchronized List<String> getMeasurementNames() {
			return Collections.unmodifiableList(names);
		}
		
		@Override
		public double getMeasurementValue(String name) {
			return getMeasurementValue(getMeasurementIndex(name));
		}

		@Override
		public boolean containsAllNamedMeasurements(Collection<String> measurementNames) {
			if (!isClosed)
				logger.debug("containsAllNamedMeasurements called on open NumericMeasurementList - consider closing list earlier for efficiency");
			return names == measurementNames || names.equals(measurementNames) || names.containsAll(measurementNames);
		}		
		
		@Override
		public boolean containsNamedMeasurement(String measurementName) {
			if (!isClosed)
				logger.trace("containsNamedMeasurement called on open NumericMeasurementList - consider closing list earlier for efficiency");
			return names.contains(measurementName);
		}

		@Override
		public String getMeasurementName(int ind) {
			return names.get(ind);
		}
		
		@Override
		public void clear() {
			ensureListOpen();
			names.clear();
			compactStorage();
		}
		
		void ensureListOpen() {
			if (isClosed()) {
				isClosed = false;
				map = null;
				names = new ArrayList<>(names);				
			}
		}
		
		@Override
		public synchronized boolean addMeasurement(String name, double value) {
			// If the list is closed, we have to reopen it
			ensureListOpen();
			names.add(name);
			setValue(size()-1, value);
			return true;
		}
		
		
		@Override
		public synchronized void putMeasurement(String name, double value) {
			ensureListOpen();
			int index = getMeasurementIndex(name);
			if (index >= 0)
				setValue(index, value);
			else
				addMeasurement(name, value);
		}
		

		@Override
		public boolean supportsDynamicMeasurements() {
			return false;
		}
		
		void compactStorage() {
			if (isClosed())
				return;
			if (names instanceof ArrayList)
				((ArrayList<String>)names).trimToSize();
		}
		
		/**
		 * Always returns false, as the list does not support dynamic measurements.
		 */
		@Override
		public boolean hasDynamicMeasurements() {
			return false;
		}
		
		@Override
		public Iterator<Measurement> iterator() {
			return new MeasurementIterator();
		}
		
		
		@Override
		public Measurement putMeasurement(Measurement measurement) {
			if (measurement.isDynamic())
				throw new UnsupportedOperationException("This MeasurementList does not support dynamic measurements");
			ensureListOpen();
			String name = measurement.getName();
			double value = measurement.getValue();
			int ind = getMeasurementIndex(name);
			if (ind >= 0) {
				Measurement temp = MeasurementFactory.createMeasurement(name, value);
				setValue(ind, value);
				return temp;
			}
			add(measurement);
			return null;
		}
		
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int n = size();
			sb.append("[");
			for (int i = 0; i < n; i++) {
				sb.append(getMeasurementName(i)).append(": ").append(getMeasurementValue(i));
				if (i < n - 1)
					sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
		}

		
		private class MeasurementIterator implements Iterator<Measurement> {
			
	        /**
	         * Index of element to be returned by subsequent call to next.
	         */
	        private int cursor = 0;

	        @Override
			public boolean hasNext() {
	            return cursor != size();
	        }

	        @Override
			public Measurement next() {
	            try {
	            	Measurement next = MeasurementFactory.createMeasurement(names.get(cursor), getMeasurementValue(cursor));
	                cursor++;
	                return next;
	            } catch (IndexOutOfBoundsException e) {
	                throw new NoSuchElementException();
	            }
	        }

	        @Override
			public void remove() {
	        	throw new UnsupportedOperationException("Measurements cannot be removed from this MeasurementList using an iterator");
	        }
	        	        
	    }		
		
	}



	public static class DoubleList extends AbstractNumericMeasurementList {
		
		private static final long serialVersionUID = 1L;
		
		private double[] values;

		public DoubleList(int capacity) {
			super(capacity);
			this.values = new double[capacity];
			// Close from the start... will be opened as needed
			closeList();
		}
		
		@Override
		public double getMeasurementValue(int ind) {
			if (ind >= 0 && ind < size())
				return values[ind];
			return Double.NaN;
		}

		private void ensureArraySize(int length) {
			if (values.length < length)
				values = Arrays.copyOf(values, Math.max(values.length + EXPAND, length));
		}

		@Override
		protected void setValue(int index, double value) {
			ensureArraySize(index + 1);
			values[index] = (float)value;
		}
		
		@Override
		public void compactStorage() {
			super.compactStorage();
			if (size() < values.length)
				values = Arrays.copyOf(values, size());
		}

		@Override
		public void removeMeasurements(String... measurementNames) {
			ensureListOpen();
			for (String name : measurementNames) {
				int ind = getMeasurementIndex(name);
				if (ind < 0)
					continue;
				names.remove(name);
				System.arraycopy(values, ind+1, values, ind, values.length-ind-1);
			}
		}

		
	}


	public static class FloatList extends AbstractNumericMeasurementList {
		
		private static final long serialVersionUID = 1L;
		
		private float[] values;

		public FloatList(int capacity) {
			super(capacity);
			this.values = new float[capacity];
			// Close from the start... will be opened as needed
			closeList();
		}

		@Override
		public double getMeasurementValue(int ind) {
			if (ind >= 0 && ind < size())
				return values[ind];
			return Double.NaN;
		}

		private void ensureArraySize(int length) {
			if (values.length < length)
				values = Arrays.copyOf(values, Math.max(values.length + EXPAND, length));
		}

		@Override
		protected void setValue(int index, double value) {
			ensureArraySize(index + 1);
			values[index] = (float)value;
		}
		
		@Override
		public void compactStorage() {
			super.compactStorage();
			if (size() < values.length)
				values = Arrays.copyOf(values, size());
		}

		
		@Override
		public void removeMeasurements(String... measurementNames) {
			ensureListOpen();
			for (String name : measurementNames) {
				int ind = getMeasurementIndex(name);
				if (ind < 0)
					continue;
				names.remove(name);
				System.arraycopy(values, ind+1, values, ind, values.length-ind-1);
			}
		}

	}
	

}
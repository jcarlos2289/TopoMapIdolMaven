package buildMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

public class ListMethods {
	
	public static ArrayList<String> getTopXNodes(int x, ConcurrentHashMap<String, Float> tagMap){
		//order the histoMean
		ArrayList<String> mean10 = new ArrayList<String>();
		LinkedHashMap<String, Float> sortedByValue = orderHashMap(tagMap, true);
	
		
		int h = 0;
		for(Entry<String, Float> e : sortedByValue.entrySet()){
			mean10.add(e.getKey());
			if(++h ==x) 
				break;
			}
		return mean10;
		}
	
	public static ArrayList<String> getTopNodes(float thre, ConcurrentHashMap<String, Float> tagMap){
		//order the histoMean
		ArrayList<String> mean10 = new ArrayList<String>();
		LinkedHashMap<String, Float> sortedByValue = orderHashMap(tagMap, true);
	
		//thre=0;
		//int h = 0;
		for(Entry<String, Float> e : sortedByValue.entrySet()){
			if(e.getValue()>thre)
				mean10.add(e.getKey());
			else 
				break;
			}
		return mean10;
		}
	
	public static ArrayList<Float> getTopNodesValues(float thre, ConcurrentHashMap<String, Float> tagMap){
		//order the histoMean
		ArrayList<Float> mean10 = new ArrayList<Float>();
		LinkedHashMap<String, Float> sortedByValue = orderHashMap(tagMap, true);
	
		//thre=0;
		//int h = 0;
		for(Entry<String, Float> e : sortedByValue.entrySet()){
			if(e.getValue()>thre){
				mean10.add(e.getValue());
				}
			else 
				break;
			}
		return mean10;
		}
	
	
	public static LinkedHashMap<String, Float> orderHashMap(ConcurrentHashMap<String,Float> hashData, boolean descendant){
		Set<Entry<String, Float>> entries = hashData.entrySet();
		Comparator<Entry<String, Float>> valueComparator = new Comparator<Entry<String, Float>>() {
			@Override
			public int compare(Entry<String, Float> e1, Entry<String, Float> e2) {
				Float v1 = e1.getValue();
				Float v2 = e2.getValue();
				if(descendant)
				return v2.compareTo(v1);
				else
					return v1.compareTo(v2);
							
			}
		};

		// Sort method needs a List, so let's first convert Set to List in Java
		List<Entry<String, Float>> listOfEntries = new ArrayList<Entry<String, Float>>(entries);

		// sorting HashMap by values using comparator
		Collections.sort(listOfEntries, valueComparator);

		LinkedHashMap<String, Float> sortedByValue = new LinkedHashMap<String, Float>(listOfEntries.size());

		// copying entries from List to Map
		for (Entry<String, Float> entry : listOfEntries) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		return sortedByValue;
				
	}
	
	//Ordenacion con Integer como Key
	public static LinkedHashMap<Integer, Float> orderHashMap3(ConcurrentHashMap<Integer,Float> hashData, boolean descendant){
		Set<Entry<Integer, Float>> entries = hashData.entrySet();
		Comparator<Entry<Integer, Float>> valueComparator = new Comparator<Entry<Integer, Float>>() {
			@Override
			public int compare(Entry<Integer, Float> e1, Entry<Integer, Float> e2) {
				Float v1 = e1.getValue();
				Float v2 = e2.getValue();
				if(descendant)
				return v2.compareTo(v1);
				else
					return v1.compareTo(v2);
							
			}
		};

		// Sort method needs a List, so let's first convert Set to List in Java
		List<Entry<Integer, Float>> listOfEntries = new ArrayList<Entry<Integer, Float>>(entries);

		// sorting HashMap by values using comparator
		Collections.sort(listOfEntries, valueComparator);

		LinkedHashMap<Integer, Float> sortedByValue = new LinkedHashMap<Integer, Float>(listOfEntries.size());

		// copying entries from List to Map
		for (Entry<Integer, Float> entry : listOfEntries) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		return sortedByValue;
				
	}
	
	
	
	
	
	
	
	public static LinkedHashMap<String, Integer> orderHashMap2(HashMap<String,Integer> hashData, boolean descendant){
		Set<Entry<String, Integer>> entries = hashData.entrySet();
		Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				Integer v1 = e1.getValue();
				Integer v2 = e2.getValue();
				if(descendant)
				return v2.compareTo(v1);
				else
					return v1.compareTo(v2);
							
			}
		};

		// Sort method needs a List, so let's first convert Set to List in Java
		List<Entry<String, Integer>> listOfEntries = new ArrayList<Entry<String, Integer>>(entries);

		// sorting HashMap by values using comparator
		Collections.sort(listOfEntries, valueComparator);

		LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<String, Integer>(listOfEntries.size());

		// copying entries from List to Map
		for (Entry<String, Integer> entry : listOfEntries) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		return sortedByValue;
				
	}
	
	
	
	public static ArrayList<Integer> getTopXElement(int x, ConcurrentHashMap<Integer, Float> tagMap){
		//order the histoMean
		ArrayList<Integer> mean10 = new ArrayList<Integer>();
		LinkedHashMap<Integer, Float> sortedByValue = orderHashMap3(tagMap, false);
	
		
		int h = 0;
		for(Entry<Integer, Float> e : sortedByValue.entrySet()){
			mean10.add(e.getKey());
			if(++h ==x) 
				break;
			}
		return mean10;
		}
	
	
	
	
	

}

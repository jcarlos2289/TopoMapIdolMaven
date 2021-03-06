package buildMap;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Map {
	ArrayList<Node> nodes;
	ArrayList<Edge> edges;
	boolean useHisto=true;
	float[] weights;
	float coefA, coefB, coefC, coefD, coefE;
	
	public Map () {
		nodes=new ArrayList<Node>();
		edges=new ArrayList<Edge>();
		coefA=-1;
		coefB=-1;
		coefC=-1;
		coefD=-1;
		coefE=-1;
	}
	
	public void setWeights (int size) {
		weights= new float[size];
		for (int i=0; i< size; i++) {
			weights[i]=(i/(float) size);
			//System.err.println(weights[i]);
		}
	}
	
	public Node createNode (ImageTags i) {
		Node n=new Node(useHisto, weights);
		n.add(i);
		nodes.add(n);
		return n;
	}
	
	public void printMap () {
		boolean [][]adj;
		adj=new boolean[nodes.size()][nodes.size()];
		
		for (int i=0; i<nodes.size(); i++) {
			nodes.get(i).printCoords();
		}
		for (int i=0; i<edges.size(); i++) 
			adj[nodes.indexOf(edges.get(i).getA())][nodes.indexOf(edges.get(i).getB())]=true;

		FileWriter fichero = null;
        PrintWriter pw = null;
        
        try {
        	fichero = new FileWriter("adj");
        	pw = new PrintWriter(fichero);

    		for (int i=0; i<nodes.size(); i++) {
    			for (int j=0; j<nodes.size(); j++) 
    				if (adj[i][j]) pw.print("1 ");
    				else pw.print("0 ");
    			pw.println("");
    		}
    	} catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (fichero != null)
        			fichero.close();
        	} catch (Exception e2) {
        		e2.printStackTrace();
        	}
        }
	}
	
	public int getMapSize () {
		return nodes.size();
	}
	
	public void addNode (Node n) {
		nodes.add(n);
	}
	
	public Node getNode (int i) {
		if (i>nodes.size()) 
			return null;
		return nodes.get(i);
	}
	
	public void createEdge (Node a, Node b) {
		for (int i=0; i<edges.size(); i++) {
			if ((edges.get(i).a==a && edges.get(i).b==b) ||
				(edges.get(i).a==b && edges.get(i).b==a))
				return;
		}
		Edge e = new Edge(a, b);
		edges.add(e);
	}
	
	public float getMapDev_E(){
		float E=0;
		
		
		float xAcum=0, yAcum=0, xMean, yMean, xDev, yDev;
		for (Node node : nodes) {
			
			xAcum+=node.representative.xcoord;
			yAcum+=node.representative.ycoord;
			
		}
			
		xMean = xAcum/nodes.size();
		yMean = yAcum/nodes.size();
		
		xAcum =0;
		yAcum = 0;
		
		for(Node node:nodes){
			xAcum +=Math.pow(node.representative.xcoord-xMean, 2)/nodes.size();
			yAcum  +=Math.pow(node.representative.ycoord-yMean, 2)/nodes.size();
		}
		
		xDev= (float) Math.sqrt(xAcum);
		yDev= (float) Math.sqrt(yAcum);
		E =  (float) Math.sqrt(Math.pow(xDev, 2)+ Math.pow(yDev, 2));
				
		return E;
	}
	
	
	public float getAvgEdgeDist_C(){
		float C=0;
		float distAcum=0;
		
		for (Edge e : edges) {
			distAcum+= e.getDistance()/edges.size();
			C= distAcum;
			}
				
		return C;
	}
	
	
	
	public ArrayList<NodeCoef> getNodeCoeficientsByNode(float dmax){
		ArrayList<NodeCoef> NodeMetricCoeficients = new ArrayList<NodeCoef>();
		
		for (Node node : nodes) {
			NodeMetricCoeficients.add(new NodeCoef(node.getQCat_A(), node.getQConnAvg_B(edges), node.getQImgDev_D()));
			}
		
		return NodeMetricCoeficients;
	}
	
	public NodeCoef AvgCoeficients(ArrayList<NodeCoef> NodeMetricCoeficients, float dmax){
		NodeCoef AvgCoef = new NodeCoef(0,0,0);
		
		float acumA = 0, acumB =0, acumD=0;
		//NodeCoef normValue;
		for (NodeCoef nodeCoef : NodeMetricCoeficients) {
			//normValue = normalize(nodeCoef, dmax);
			acumA += nodeCoef.getA()/nodes.size();
			acumB += nodeCoef.getB()/nodes.size();
			acumD += nodeCoef.getD()/nodes.size();
		}
		//Averaged Coeficients in the nodes of the map
		AvgCoef = new NodeCoef(acumA, acumB, acumD);
		
		return AvgCoef;
	}
	
		
	public NodeCoef normalize(NodeCoef nodeCoef, float dmax){
		NodeCoef dat = new NodeCoef(0, 0, 0);
		//gets the avg values for the noeficients by node to normalize 		
		dat.setA((1-nodeCoef.getA()));
		dat.setB(   (nodes.size()-1-nodeCoef.getB())/(nodes.size()-2)    );
		dat.setD(	(dmax/2 - nodeCoef.getD()) /(dmax/2) );
				
		return dat;
	}
	/*
	public ArrayList<NodeCoef> DetailNomrCoeficientsByNode(ArrayList<NodeCoef> NodeMetricCoeficients, float dmax){
		ArrayList<NodeCoef> NodeMetricCoeficientsNorm = new ArrayList<NodeCoef>();
		
				
		float acumA = 0, acumB =0, acumD=0;
		NodeCoef normValue;
		for (NodeCoef nodeCoef : NodeMetricCoeficients) {
			normValue = normalize(nodeCoef, dmax);
			acumA = normValue.getA();
			acumB = normValue.getB();
			acumD = normValue.getD();
			NodeMetricCoeficientsNorm.add(new NodeCoef(acumA, acumB, acumD));
			
		}
		
		return  NodeMetricCoeficientsNorm;
		
	}
	*/
	
	
	public String printMetricTable(float dmax){
		String table ="<html><h2>Metrics Coeficients</h2><br> "
				+ "<table border=\"1\"   style=\"font-size:10px\" >"
				+ "<tr><th>Node</th><th>A</th><th>B</th><th>D</th></tr>";
				//+ "<tr><th>Node</th><th>A</th><th>B</th><th>D</th><th>A<sub>Norm</sub></th><th>B<sub>Norm</sub></th><th>D<sub>Norm</sub></th></tr>";
		
		//Get the coeficients by node in an arraylist
		ArrayList<NodeCoef> NodeMetricCoeficients = getNodeCoeficientsByNode(dmax);
		//ArrayList<NodeCoef> NodeMetricCoeficientsNorm = DetailNomrCoeficientsByNode(NodeMetricCoeficients,dmax);
		
		//Get the AVG Coeficients
		NodeCoef AvgCoef = AvgCoeficients(NodeMetricCoeficients, dmax);
		//Get the AVG Coeficients Normalized
		NodeCoef AvgCoefNorm = normalize(AvgCoef, dmax);
		
		int i =0;
		for (Node node : nodes) {
			table += "<tr>";
			table += node.printNodeMet(i, edges);
			//table += "<td>"+(NodeMetricCoeficientsNorm.get(i).getA()) +"</td><td>"+NodeMetricCoeficientsNorm.get(i).getB() +"</td><td>"+NodeMetricCoeficientsNorm.get(i).getD() +"</td>";
			table += "</tr>";
			++i;
		}
		
		table+="</table><br>";
		
				
		
		float E =0, C =0, En=0, Cn=0;
		
		E = getMapDev_E();
		C = getAvgEdgeDist_C();
		En = E/(dmax/2);
		Cn = (dmax-C)/dmax;
			
		table +="<h2>Dmax=  "+dmax+"</h2>";
		table +="<h2>Nodes=  "+nodes.size()+"</h2>";
		table +="<h2>Edges=  "+edges.size()+"</h2>";
		table +="<h2>A= "+ AvgCoef.getA()+"  A<sub>Norm</sub>=  "+AvgCoefNorm.getA()+"</h2>";
		table +="<h2>B= "+ AvgCoef.getB()+"  B<sub>Norm</sub>=  "+AvgCoefNorm.getB()+"</h2>";
		table +="<h2>D= "+ AvgCoef.getD()+"  D<sub>Norm</sub>=  "+AvgCoefNorm.getD()+"</h2>";
		table +="<h2>C= "+ C+ "  C<sub>Norm</sub>=  "+Cn+"</h2>";
		table +="<h2>E= "+ E+ "  E<sub>Norm</sub>=  "+En+"</h2>";
			
				
		
		float metric = getMapMetric(dmax);
			
		table +="<h1>Metric=  "+metric+"</h1><br>";
		table+= "</html>";
		return table;
				
		
	}
	
	public void calcPrecRecallData(double th1, double th2, ArrayList<ImageTags> imgTags, String name){
		String resultado ="Nodo\tSize\tDif\tTP\tFP\tFN\tTN\tPre\tRecall\tIn\tOut\n";	
		int nCounter=0;
		DecimalFormatSymbols simbol = new DecimalFormatSymbols();
	       simbol.setDecimalSeparator('.');
		
		ArrayList<ArrayList<ImageTags>> fakeMap= new ArrayList<ArrayList<ImageTags>> ();
		
		for(Node nd: nodes){
			ArrayList<ImageTags> h = new ArrayList<ImageTags>();
			fakeMap.add(h);
		}
		
	
		for(ImageTags imgTg: imgTags){
			ConcurrentHashMap <Integer, Float> distances = new ConcurrentHashMap<Integer,Float>(); 
			
			for(Node nd: nodes){
				distances.put(nCounter,(float) nd.distance(imgTg));
				nCounter++;
			}
			ArrayList<Integer> minDist = ListMethods.getTopXElement(1, distances);
			fakeMap.get(minDist.get(0)).add(imgTg);
			nCounter=0;
		}
		
		
		
		
		
		nCounter =0;
		for(Node nd: nodes){
			int TP= 0, FP =0, FN =0, TN =0;
			
			
			ArrayList<ImageTags> listaImg = fakeMap.get(nCounter);
			
			for(ImageTags im: listaImg){
				if(nd.images.contains(im)){
					TP++;
				}else
					FP++;
			}
			
			ArrayList<ImageTags> imgTagsCopy =  new ArrayList<ImageTags>();
			imgTagsCopy.addAll(imgTags);
			imgTagsCopy.removeAll(nd.images);
			//Cantidad de elementos que no se añadieron al nodo al momento de construir el mapa
			//TN = imgTagsCopy.size();
			
			
			//elementos que no se añadieron al Nodo de Comprobacion
			imgTagsCopy.clear();
			imgTagsCopy.addAll(imgTags);
			imgTagsCopy.removeAll(listaImg);
			TN = imgTagsCopy.size();
			
			for(ImageTags im: imgTagsCopy){
				if(nd.images.contains(im)){
					FN++;
					TN--;
				}
			}
				
					 
		   
	       DecimalFormat formateador = new DecimalFormat("####.############",simbol);
	       
			
			double pre = 0, recall = 0;
			pre = (double)TP/(TP+FP);
			recall = (double)TP/(TP+FN);
			
			resultado +=  String.valueOf(nCounter)+"\t"
					    + String.valueOf(nd.images.size())+"\t"
					    + String.valueOf((imgTags.size() - nd.images.size()))+"\t"
					    + String.valueOf(TP)  +"\t"
					    + String.valueOf(FP)  +"\t"
					    + String.valueOf(FN)  +"\t"
					    + String.valueOf(TN)  +"\t"
					    + formateador.format(pre) +"\t"
					    + formateador.format(recall)  +"\t"
					    + String.valueOf(listaImg.size())  +"\t"
					    + String.valueOf(imgTagsCopy.size())  +"\n";
			nCounter++;
								
		}
		
		
		
		
		
		
		
		
		
		
		/*
		//Metodo 1 =(
		//Comprobar todas las imagenes en cada nodo
		 * int nCounter=1;
		for (Node node : nodes) {
			ArrayList<ImageTags> inNode  = new ArrayList<ImageTags>();
			ArrayList<ImageTags> outNode  = new ArrayList<ImageTags>();
			ArrayList<ImageTags> in  = new ArrayList<ImageTags>();
			ArrayList<ImageTags> out  = new ArrayList<ImageTags>();
			
			int TP= 0, FP =0, FN =0, TN =0;
			
			for(ImageTags imgTg: imgTags){
											
				double dist = node.distance(imgTg);
				if(dist<th2) {
					in.add(imgTg);
				}else
					out.add(imgTg);
								
				if(node.images.contains(imgTg)){
					inNode.add(imgTg);
				}else
					outNode.add(imgTg);
			}
		
			
			
			//los que se ven buenos
			for(ImageTags im: in){
				if(inNode.contains(im)){
					TP++; // True positive = correctly identified
				}else
					FP++; //  False positive = incorrectly identified
			}
			
			//los que se ven malos
			for(ImageTags im: out){
				if(outNode.contains(im)){
					TN++; // True negative = correctly rejected
				}else
					FN++; //   False negative = incorrectly rejected
				}
			
			
			//noooooooooooooooo
		//True positives (TP) - Correctly idd as success
		  //  ## True negatives (TN) - Correctly idd as failure
		 //   ## False positives (FP) - success incorrectly idd as failure
		 //   ## False negatives (FN) - failure incorrectly idd as success
		   
		   	DecimalFormatSymbols simbol = new DecimalFormatSymbols();
	        simbol.setDecimalSeparator('.');
	        DecimalFormat formateador = new DecimalFormat("####.###################",simbol);
			
			double pre = 0, recall = 0;
			pre = (double)TP/(TP+FP);
			recall = (double)TP/(TP+FN);
			
			resultado +=  String.valueOf(nCounter)+"\t"
					    + String.valueOf(node.images.size())+"\t"
					    + String.valueOf((imgTags.size() - node.images.size()))+"\t"
					    + String.valueOf(TP)  +"\t"
					    + String.valueOf(FP)  +"\t"
					    + String.valueOf(FN)  +"\t"
					    + String.valueOf(TN)  +"\t"
					    + formateador.format(pre) +"\t"
					    + formateador.format(recall)  +"\t"
					    + String.valueOf(in.size())  +"\t"
					    + String.valueOf(out.size())  +"\n";
			
			
			
			nCounter++;
		}
		//Fin Metodo 1--------------------------------------------------------------------
		*/
		
		DecimalFormat formateadorTh = new DecimalFormat("####.####",simbol);
		System.out.print("SeqLen="+ String.valueOf(imgTags.size())+ "\n"+resultado);
		FileMethods.saveFile(resultado, name+"_"+formateadorTh.format(th1)+"_"+ formateadorTh.format(th2)+"_ROC", false);
		
		
		
		
		
	}
	
	public float getMapMetric(float dmax){
		float metric=0;
		//Get the coeficients by node in an arraylist
		ArrayList<NodeCoef> NodeMetricCoeficients = getNodeCoeficientsByNode(dmax);
		
		//Get the AVG Coeficients
		NodeCoef AvgCoef = AvgCoeficients(NodeMetricCoeficients, dmax);
			
		
		float E=0, C=0;
	
		float wA=20, wB=20, wC=20, wD=20, wE =20;
		
		//Normalized Values 
		NodeCoef AvgCoefNorm = normalize(AvgCoef, dmax);	
		E = getMapDev_E()/(dmax/2);
		C = (dmax-getAvgEdgeDist_C())/dmax;
	
		metric = AvgCoefNorm.getA()*wA 
				+AvgCoefNorm.getB()*wB
				+AvgCoefNorm.getD()*wD
				+C*wC
				+E*wE;
		
		
		coefA=AvgCoefNorm.getA();
		coefB=AvgCoefNorm.getB();
		coefC=C;
		coefD=AvgCoefNorm.getD();
		coefE=E;
				
		return metric;
	}
	
	public void getEdgesInformation(String code){
		if(edges != null){
			FileMethods.saveFile("", code+"EdgesDataPath", false);
			for(Edge ed : edges){
				int sizeA, sizeB;
				sizeA = ed.a.images.size();
				sizeB = ed.b.images.size();
				
				ArrayList<String> imagesA = new ArrayList<String>();
				ArrayList<String> imagesB = new ArrayList<String>();
				ArrayList<String> imagesT = new ArrayList<String>();
				
				int aMiddle, bMiddle;
				
				aMiddle=sizeA/2;
				bMiddle=sizeB/2;
				
				if(sizeA>3){
					imagesA.add(ed.a.images.get(aMiddle-1).imageName);
					imagesA.add(ed.a.images.get(aMiddle).imageName);
					imagesA.add(ed.a.images.get(aMiddle+1).imageName);
					
				}else{
					imagesA.add("-");
					imagesA.add(ed.a.images.get(aMiddle).imageName);
					imagesA.add("-");
				}
					
				
				
				if(sizeB>3){
					imagesB.add(ed.b.images.get(bMiddle-1).imageName);
					imagesB.add(ed.b.images.get(bMiddle).imageName);
					imagesB.add(ed.b.images.get(bMiddle+1).imageName);
					
				}
				else{
					imagesB.add("-");
					imagesB.add(ed.b.images.get(bMiddle).imageName);
					imagesB.add("-");
				}
				
				imagesT.add(ed.a.images.get(sizeA-1).imageName);
				imagesT.add(ed.b.images.get(0).imageName);
				
				String cadTex;
				
				cadTex = String.valueOf(nodes.indexOf(ed.a))+"->"+String.valueOf(nodes.indexOf(ed.b))+ " ";
				
                for (String st : imagesA) {
					cadTex+=st +" ";
				}
				//cadTex+=" ";
                
				for (String st : imagesT) {
					cadTex+=st +" ";
				}
                
                
				//cadTex+=" ";
                
				for (String st : imagesB) {
					cadTex+=st +" ";
				}
				cadTex+="\n";
				
				FileMethods.saveFile(cadTex, code+"EdgesDataPath", true);
				
			}
		}
		
	
		
		
		if(edges != null){
			FileMethods.saveFile("", code+"EdgesData", false);
			for(Edge ed : edges){
				int sizeA, sizeB;
				sizeA = ed.a.images.size();
				sizeB = ed.b.images.size();
				
				ArrayList<String> imagesA = new ArrayList<String>();
				ArrayList<String> imagesB = new ArrayList<String>();
				ArrayList<String> imagesT = new ArrayList<String>();
				
				int aMiddle, bMiddle;
				
				aMiddle=sizeA/2;
				bMiddle=sizeB/2;
				
				if(sizeA>3){
					String[] name;
					name = ed.a.images.get(aMiddle-1).imageName.split("/");
					imagesA.add(name[name.length-1]);
					
					name = ed.a.images.get(aMiddle).imageName.split("/");
					imagesA.add(name[name.length-1]);
					
					name = ed.a.images.get(aMiddle+1).imageName.split("/");
					imagesA.add(name[name.length-1]);
					
					
					
				}else{
					imagesA.add("-");
					String[] name;
					name = ed.a.images.get(aMiddle).imageName.split("/");
					imagesA.add(name[name.length-1]);
					imagesA.add("-");
				}
					
				
				
				if(sizeB>3){
					String[] name;
					name = ed.b.images.get(bMiddle-1).imageName.split("/");
					imagesB.add(name[name.length-1]);
					
					name = ed.b.images.get(bMiddle).imageName.split("/");
					imagesB.add(name[name.length-1]);
					
					name = ed.b.images.get(bMiddle+1).imageName.split("/");
					imagesB.add(name[name.length-1]);
					
					
					
				}else{
					imagesB.add("-");
					String[] name;
					name = ed.b.images.get(bMiddle).imageName.split("/");
					imagesB.add(name[name.length-1]);
					imagesB.add("-");
				}
				
				
				String[] name;
				name = ed.a.images.get(sizeA-1).imageName.split("/");
				imagesT.add(name[name.length-1]);
				name = ed.b.images.get(0).imageName.split("/");
				imagesT.add(name[name.length-1]);
				
				String cadTex;
				
				cadTex = String.valueOf(nodes.indexOf(ed.a))+"->"+String.valueOf(nodes.indexOf(ed.b))+" ";
				
                for (String st : imagesA) {
					cadTex+=st +" ";
				}
				//cadTex+="\t";
                
				for (String st : imagesT) {
					cadTex+=st +" ";
				}
                
                
				//cadTex+="\t";
                
				for (String st : imagesB) {
					cadTex+=st +" ";
				}
				cadTex+="\n";
				
				FileMethods.saveFile(cadTex, code+"EdgesData", true);
				
			}
			}
	}
	public void printTrans(String code){
		ArrayList<Integer> nodesA = new ArrayList<Integer>();
		ArrayList<Integer> nodesB = new ArrayList<Integer>();
		ArrayList<Integer> evaluated = new ArrayList<Integer>();
		double cumulatedPercentaje = 0;
		
		int y = 0;
		System.out.printf("Mapa %s\n#\tA\tB\n", code);
		for(Edge ed : edges){
			//	String cadTex;
			//cadTex = String.valueOf(nodes.indexOf(ed.a))+"->"+String.valueOf(nodes.indexOf(ed.b))+ " ";

			System.out.printf("%d\t%d\t%d\n",y++,nodes.indexOf(ed.a),nodes.indexOf(ed.b));
			nodesA.add(nodes.indexOf(ed.a));
			nodesB.add(nodes.indexOf(ed.b));
		}

		for(int g : nodesA){

			if(!evaluated.contains(g)){
				int o = Collections.frequency(nodesA, g);
				if (o>=2){
					double pc= (float)o/(float)edges.size();
					cumulatedPercentaje+=pc;
				}
				evaluated.add(g);
			}


		}

		System.out.printf("\nRegresion Percentaje\t%.1f\n\n\n", cumulatedPercentaje*100);
		
		if (cumulatedPercentaje >0){
			FileMethods.saveFile(code +"\t" +String.valueOf(cumulatedPercentaje)+"\n", "TransicionsConsole", true);
		}
		
		
		
	}
	
	
	public String getMapInfo(String name, String th1, String th2, String cutNode){
		
				ArrayList<Integer> classCount = new ArrayList<Integer>();
				String cats ="Node Categories\n";
				String text = "<html>\n";
				text +="<h1> Sequence: "+ name+"</h1><br>";
				text += "<p>Th1 = "+ th1 + "</p><p>Th2 = " +th2 + "</p><p>CN = "+ cutNode+"</p><p> Nodes: "+this.getMapSize()+"</p><br>";
							
				text += "<table border=\"1\"   style=\"font-size:8px\"    >";
				text += "<tr><th>Node</th>";
				for(int i = 0;i < 10; ++i )
					text+="<th>Tag "+ String.valueOf(i+1)+ "</th>";
				text +="</tr>\n";
				
				
				int h = 0;
				
				for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
					
					Node node =  iterator.next();
					classCount.add(node.getCategoryAmount());
					ArrayList<String> nodetags = node.getTop10Nodes();
					text +="<tr>";
					text +="<td>" + ++h +"</td>";
					for (String tag : nodetags) {
						text +="<td>"+tag +"</td>";
					}
					text +="</tr>\n";
					
					cats+="<br>Node "+(h-1)+"\n";
					cats += node.countCategory();
					cats+= "\n\n\n";
				
					
				}
				
				Collections.sort(classCount);
				
				String catPercentage="<h2>Categories Count Percentage</h2><br><p>Cat_Amout &nbsp;&nbsp; %</p><br>";
				
				Set<Integer> data = new HashSet<>(classCount);
		    
				for(int dat: data){
					int  val = Collections.frequency(classCount, dat);
					catPercentage+="<p>"+dat+"&nbsp;&nbsp;"+(((float)val)*100/nodes.size())+"%</p>";
				}
				
				catPercentage+="<br>";
						
				text += "</table>";
				text +="\n\n\n<br> <p>" +catPercentage+"</p><br>";
										
				text +="\n\n\n<br> <p>" +cats+"</p>";
				text += "\n</html>";
		
		
		return text;
		
	}
	
	
	class NodeCoef{
		float A, B, D;
		
		public NodeCoef(float a, float b, float d){
			A =a;
			B=b;
			D=d;
		}
		
		public float getA(){
			return A;
		}
		
		public float getB(){
			return B;
		}
		
		public float getD(){
			return D;
		}
		
		
		public void setA(float a){
			A = a;
		}
		
		public void setB(float b){
			B = b;
		}
		
		public void setD(float d){
			 D = d;
		}
		
	}
	
	
	class Edge {
		Node a, b;
		
		public Edge (Node ia, Node ib) {
			a=ia;
			b=ib;
		}
		
		public Node getA () {
			return a;
		}
		
		public Node getB () {
			return b;
		}
		
		public float getDistance(){
			float distance=0;
			
			distance = (float) Math.sqrt(Math.pow((a.representative.xcoord-b.representative.xcoord),2) +Math.pow((a.representative.ycoord- b.representative.ycoord),2));
			return distance;
			
		}
		
	}

}

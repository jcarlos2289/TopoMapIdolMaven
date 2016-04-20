package buildMap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
//import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
/*
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;*/
import com.itextpdf.text.pdf.PdfWriter;



public class CanvasMap extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1366050590724868148L;
	Gui gui;
	ImageIcon img2;
	double xmean=5.9199204755, ymean=15.7735103837;
	double zoomFactor=28;
	double xdesp, ydesp;
	
	int radius=15;
	
	JDialog tags=null, nodeInfo=null, top=null, nodeDetailsDialog =null, mapInfoDialog=null, tagCloudDialog=null;
	//JDialog graf=null;
	
	public CanvasMap (Gui ig) {
		img2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage("idol_map.png"));
		gui=ig;
		xdesp=(gui.width/4.3)-65;
		ydesp=(gui.height/2.68)-74;
	    addMouseListener(this);
	    
	}
	
	private double distance (MouseEvent evt, Node n) {
		double dist=0.0;
		dist = Math.pow(evt.getX()-(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp+radius), 2.0);
		dist += Math.pow(evt.getY()-(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp+radius), 2.0);
		return Math.sqrt(dist);
	}
	
	public  float getMaxWidth(){
        float newX=0;
		newX = (float) (((float)(img2.getIconWidth() -xdesp))/zoomFactor +xmean);
		return newX;
	}
	
	public  float getMaxHeight(){
        float newY=0;
		newY = (float) (((float)(img2.getIconHeight() -ydesp))/zoomFactor +ymean);
		return newY;
	}
	
	public float MaxDistance(){
			
		float dmax = (float) Math.sqrt(Math.pow(getMaxWidth(), 2)+ Math.pow(getMaxHeight(), 2));
		
		return dmax;
	}
	

	public void paint(Graphics g) {
		int x,y, xAnt=0, yAnt=0;
		boolean firstTime=true;
		Graphics2D g2d = (Graphics2D) g;
		
		paintComponent(g);
		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (gui.background)
			img2.paintIcon(this, g, 0, 0);
		g.setColor(new Color(255,0,0));
		g2d.setStroke(new BasicStroke(2.0f));
		if (gui.original) {
			for (int i=0; i<gui.bm.imgTags.size(); i++) {
				if (gui.bm.imgTags.get(i).xcoord!=-1) {
					x=(int)(zoomFactor*(gui.bm.imgTags.get(i).xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(gui.bm.imgTags.get(i).ycoord-ymean)+ydesp);
					if (firstTime) {
						xAnt=x;
						yAnt=y;
						firstTime=false;
					}
					if (Math.sqrt(Math.pow(x-xAnt, 2.0)+Math.pow(y-yAnt,2.0)) < 30.0) 
						g2d.drawLine(xAnt, yAnt, x, y);
					xAnt=x;
					yAnt=y;
				}				
			}
		}
		g.setColor(new Color(0,0,255));
		if (gui.mapGenerated && gui.showMap) {
		
			if (gui.nodesMode && gui.selectedNode!=-1) {
				Node selectn = gui.bm.map.nodes.get(gui.selectedNode);
				for (ImageTags img:selectn.images) {
					x=(int)(zoomFactor*(img.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(img.ycoord-ymean)+ydesp);					
			        g.drawOval(x, y, radius, radius);
			        g.fillOval(x, y, radius, radius);
				}
				g.setColor(new Color(0,255,255));
				x=(int)(zoomFactor*(selectn.representative.xcoord-xmean)+xdesp);
				y=(int)(-zoomFactor*(selectn.representative.ycoord-ymean)+ydesp);
		        g.drawOval(x, y, radius, radius);
				g.setColor(new Color(255,0,255));
				for (Map.Edge e:gui.bm.map.edges) {
					if (e.a==selectn || e.b==selectn) {
						xAnt=(int)(zoomFactor*(e.a.representative.xcoord-xmean)+xdesp);
						yAnt=(int)(-zoomFactor*(e.a.representative.ycoord-ymean)+ydesp);
						x=(int)(zoomFactor*(e.b.representative.xcoord-xmean)+xdesp);
						y=(int)(-zoomFactor*(e.b.representative.ycoord-ymean)+ydesp);
						g2d.drawLine(xAnt+radius/2, yAnt+radius/2, x+radius/2, y+radius/2);
					}
				}
				if (gui.selectNodeChanged) {
					if (tags!=null) {
						 tags.dispose();
	                     nodeInfo.dispose();
	                    // graf.dispose();
	                     top.dispose();
	                     tagCloudDialog.dispose();
	                     tags=null;
	                     nodeInfo=null;
	                    // graf=null;
	                     top=null;
	                     tagCloudDialog=null;
					}
					showInfo(selectn);
					gui.selectNodeChanged=false;
				}
			}
			
			
			else {
				
				for (Map.Edge e:gui.bm.map.edges) {
					xAnt=(int)(zoomFactor*(e.a.representative.xcoord-xmean)+xdesp);
					yAnt=(int)(-zoomFactor*(e.a.representative.ycoord-ymean)+ydesp);
					x=(int)(zoomFactor*(e.b.representative.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(e.b.representative.ycoord-ymean)+ydesp);
					g2d.drawLine(xAnt+radius/2, yAnt+radius/2, x+radius/2, y+radius/2);
				}
				
				for (Node n:gui.bm.map.nodes) {
					x=(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp);
			       
					
			        g.setColor(new Color(255,255,255));
			        g.fillOval(x, y, radius, radius);
			        
			        g.setColor(new Color(0,0,255));
			        g.drawOval(x, y, radius, radius);

				}
			}
			
			
			
		}
		//else{
		//Para TagMode
		if(gui.tagMode && gui.mapGenerated){
			if(gui.selectTagChanged && !gui.selectedTag.equals(null)){
				//Proceso para mostrar los highTags
				
			
				
			//	ArrayList<String> aux;
				ArrayList<Float> values = new ArrayList<Float>();
			//	ArrayList<Float> valuesOrder = new ArrayList<Float>();
				for(Node n:gui.bm.map.nodes){
					values.add(n.histoMean.get(gui.selectedTag));
				}
				Collections.sort(values);
				
				float max = values.get(values.size()-1);
				float min = values.get(0);
				
				
				for(Node n:gui.bm.map.nodes){
					values.add(n.histoMean.get(gui.selectedTag));
					g.setColor(produceHeatColor(n.histoMean.get(gui.selectedTag)  , min, max));
					//g.setColor(produceHeatColor(((n.histoMean.get(gui.selectedTag)-min)/(max-min))  , 0, 1));
					x=(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp); 
					g.drawOval(x, y, radius, radius);
				    g.fillOval(x, y, radius, radius);
				}
				
				 g.setColor(Color.BLACK);
			        
			        Font oldFont=getFont();
			        g.setFont(oldFont);
			        g.drawString(gui.selectedTag, 223, 30);
				
				
				
				
				
				
				
				
				
				/*
				ArrayList<String> aux;
				
				for(Node n:gui.bm.map.nodes){
					aux = n.getTop10Nodes();
					if(aux.contains(gui.selectedTag)){
						x=(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp);
						y=(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp);
				        g.drawOval(x, y, radius, radius);
						g.setColor(new Color(0,255,0));
				        g.drawRect(x, y, radius, radius);
				        g.fillRect(x, y, radius, radius);
				        int h = aux.indexOf(gui.selectedTag)+1;
				        g.setColor(Color.BLACK);
				        
				        Font oldFont=getFont();
				        Font fuente=new Font("Monospaced", Font.PLAIN, 10);
				        g.setFont(fuente);
				        g.drawString(String.valueOf(h), x+1, y+10);
				        g.setFont(oldFont);
				        g.drawString(gui.selectedTag, 223, 30);
				        
				        
					}
				}
				
				gui.selectTagChanged=false;
					*/			
			       // gui.selectTagChanged=false;
				
			}
		}	
		
		
		//para thTag Mode
		if(gui.thTagMode && gui.mapGenerated ){
			if(gui.selectTagChanged && !gui.selectedTag.equals(null) && gui.thTag>=0){
				//Proceso para mostrar los highTags
				
				 ArrayList<ArrayList<String>> aux2 = new ArrayList<ArrayList<String>>();
			     ArrayList<ArrayList<Float>> auxFloat = new ArrayList<ArrayList<Float>>();
				
				for(Node n:gui.bm.map.nodes){
					  aux2.add(n.getTopNodes((float)gui.thTag));
				      auxFloat.add(n.getTopNodesValues((float)gui.thTag));
				}
				
				
				ArrayList<Integer> foundTags = new ArrayList<Integer>();
				ArrayList<Integer> foundTagsNode = new ArrayList<Integer>();
				
				int z = 0;
				for (ArrayList<String> listaTags : aux2) {
					if(!listaTags.isEmpty()){
						if(listaTags.contains(gui.selectedTag)){
							int h = listaTags.indexOf(gui.selectedTag);
							foundTags.add(h);
							foundTagsNode.add(z);
						}
				}
					++z;
				}
				
				if(!foundTags.isEmpty()){
				//ArrayList<Float> foundValues = new ArrayList<Float>();
				//ArrayList<Float> foundValuesOrder = new ArrayList<Float>();

				for (int i = 0; i < foundTagsNode.size(); i++) {
					//foundValues.add(auxFloat.get(foundTagsNode.get(i)).get(foundTags.get(i)));
					//foundValuesOrder.add(auxFloat.get(foundTagsNode.get(i)).get(foundTags.get(i)));
					int max = aux2.get(foundTagsNode.get(i)).size();
					int min = 0;
					Color heatColor = produceHeatColor(max-(foundTags.get(i)-min), min, max);
					g.setColor(heatColor);
					x=(int)(zoomFactor*(gui.bm.map.nodes.get(foundTagsNode.get(i)).representative.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(gui.bm.map.nodes.get(foundTagsNode.get(i)).representative.ycoord-ymean)+ydesp);
			        //g.drawOval(x, y, radius, radius);
			        //g.setColor(new Color(251,255,97));
			        g.drawOval(x, y, radius, radius);
			        g.fillOval(x, y, radius, radius);
					 
					
					
					
				}
				
				 g.setColor(Color.BLACK);
			        
			        Font oldFont=getFont();
			       // Font fuente=new Font("Monospaced", Font.PLAIN, 10);
			      //  g.setFont(fuente);
			       // g.drawString(String.valueOf(h), x+1, y+10);
			        g.setFont(oldFont);
			        g.drawString(gui.selectedTag, 223, 30);
				
				//Collections.sort(foundValuesOrder); //ordena de menor a mayor
				
				
				
				//float max = foundValuesOrder.get(foundValuesOrder.size()-1);
				//float min = foundValuesOrder.get(0);
				
							
				
				
				
				
				
				
				
				
					
				}	
					
				   /*ArrayList<String> aux;
				    *  aux = n.getTopNodes((float)gui.thTag);
					if(!aux.isEmpty()){
						if(aux.contains(gui.selectedTag)){
							x=(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp);
							y=(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp);
					        g.drawOval(x, y, radius, radius);
					        g.setColor(new Color(251,255,97));
					        g.drawRect(x, y, radius, radius);
					        g.fillRect(x, y, radius, radius);
					        int h = aux.indexOf(gui.selectedTag)+1;
					        g.setColor(Color.BLACK);
					        
					        Font oldFont=getFont();
					        Font fuente=new Font("Monospaced", Font.PLAIN, 10);
					        g.setFont(fuente);
					        g.drawString(String.valueOf(h), x+1, y+10);
					        g.setFont(oldFont);
					        g.drawString(gui.selectedTag, 223, 30);
				        
				        
					}
				}*/
				
				//gui.selectTagChanged=false;	
			}
		}
		
		
		/*Original
		 * 	if(gui.thTagMode && gui.mapGenerated ){
			if(gui.selectTagChanged && !gui.selectedTag.equals(null) && gui.thTag>=0){
				//Proceso para mostrar los highTags
				
				ArrayList<String> aux;
				
				for(Node n:gui.bm.map.nodes){
					aux = n.getTopNodes((float)gui.thTag);
					if(!aux.isEmpty()){
						if(aux.contains(gui.selectedTag)){
							x=(int)(zoomFactor*(n.representative.xcoord-xmean)+xdesp);
							y=(int)(-zoomFactor*(n.representative.ycoord-ymean)+ydesp);
					        g.drawOval(x, y, radius, radius);
					        g.setColor(new Color(251,255,97));
					        g.drawRect(x, y, radius, radius);
					        g.fillRect(x, y, radius, radius);
					        int h = aux.indexOf(gui.selectedTag)+1;
					        g.setColor(Color.BLACK);
					        
					        Font oldFont=getFont();
					        Font fuente=new Font("Monospaced", Font.PLAIN, 10);
					        g.setFont(fuente);
					        g.drawString(String.valueOf(h), x+1, y+10);
					        g.setFont(oldFont);
					        g.drawString(gui.selectedTag, 223, 30);
				        
				        
					}
				}
				}
				gui.selectTagChanged=false;				
			}
		}
		 * 
		 * 
		 * 
		 * 
		 */
		
		
		
		
	        //Recorro para encontrar el mayor
	        //Recorro para ver en quje nodo esta el tag
	        //busco su indice y uso el valor para generar la escala termica
	        //guardo solo las coordenadas del nodo y su color para despues de solo pintarlo
		
		
		
		
		
		
			if (gui.showCluster){
				
				int colors[][] = new int[gui.km.k][3];
				
				for (int i = 0; i <colors.length ; i++) {
					for (int j = 0; j < colors[i].length; j++) {
						colors [i][j] = (int)(Math.random()*255);
						//colors [i][1] = (int)(Math.random()*255);
						//colors [i][2] = (int)(Math.random()*255);
					}
					}
				
										
				for (int j = 0; j <gui.km.obtained.size(); j++) {
					
					Point point = gui.km.obtained.get(j);
					g.setColor(new Color(colors[gui.km.near.get(j)][0],colors[gui.km.near.get(j)][1],colors[gui.km.near.get(j)][2]));
					x=(int)(zoomFactor*(point.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(point.ycoord-ymean)+ydesp);
			        g.drawOval(x, y, 2, 2);
									
				}
				/*for (Iterator<Point> iterator = gui.km.obtained.iterator(); iterator.hasNext();) {
					Point point = iterator.next();
					x=(int)(zoomFactor*(point.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(point.ycoord-ymean)+ydesp);
			        g.drawOval(x, y, radius, radius);
				}*/
				
							
				
				/*int c = 0;
				for (Point point : gui.km.means) {
					
					g.setColor(new Color(colors[c][0],colors[c][1],colors[c][2]));
					x=(int)(zoomFactor*(point.xcoord-xmean)+xdesp);
					y=(int)(-zoomFactor*(point.ycoord-ymean)+ydesp);
			        g.drawOval(x, y, radius, radius+3);
			        g.fillOval(x, y, radius, radius+3);
			        ++c;
			        			        
					//System.out.println("X = " + point.xcoord +" y = " + point.ycoord);
					//System.out.println("X = " + x +" y = " + y);
			     
				}*/
				
				
				g.setColor(new Color(0,0,255));
				//g.draw3DRect(80, 100, 30 ,15, true);
				g.drawRect(360, 85, 50 ,20);
				g.fillRect(360, 85, 50 ,20);
				g.setColor(new Color(255,255,255));
				g.drawString("k = " + gui.km.k, 361, 100);
				//g.drawString(str, xAnt, yAnt);
								
			}
			
		//}
	}
	
	private Color produceHeatColor(float v, float vmin, float vmax) {
		// TODO Auto-generated method stub
		
		Color c = new Color(1,1,1); // white
		   double dv;
		   double r=1, g=1, b=1;

		   if (v < vmin)
		      v = vmin;
		   if (v > vmax)
		      v = vmax;
		   dv = vmax - vmin;

		   if (v < (vmin + 0.25 * dv)) {
		     r = 0;
		      g = 4 * (v - vmin) / dv;
		   } else if (v < (vmin + 0.5 * dv)) {
		     r = 0;
		      b = 1 + 4 * (vmin + 0.25 * dv - v) / dv;
		   } else if (v < (vmin + 0.75 * dv)) {
		      r = 4 * (v - vmin - 0.5 * dv) / dv;
		     b = 0;
		   } else {
		     g = 1 + 4 * (vmin + 0.75 * dv - v) / dv;
		      b = 0;
		   }
		/*   System.out.println("------------------------");
		   System.out.println(Math.round(r*255));
		   System.out.println(Math.round(g*255));
		   System.out.println(Math.round(b*255));
		   System.out.println("------------------------");*/
		   int ir = (int) Math.round(r*255);
		   int ig = (int) Math.round(g*255);
		   int ib = (int) Math.round(b*255);
		   r=1;
		   g=1;
		   b=1;
		   c = new Color(ir,ig,ib);
		   
		   return(c);
		}

	public void showInfo (Node sel) {
		JLabel textArea, presenceData;
		JScrollPane scroll, scroll2;
		
		gui.selectedNode=gui.bm.map.nodes.indexOf(sel);
		gui.showNodes.setSelected(true);
		gui.nodesMode=true;
		gui.nodes.setEnabled(true);
		gui.nodes.setSelectedIndex(gui.bm.map.nodes.indexOf(sel)+1);
		
		tags=new JDialog(gui);
		tags.setTitle("Tags");
		tags.setSize(450, 400);
		textArea= new JLabel(sel.getTextTags());
		tags.add(textArea);
		tags.setLocation(1050,0);
		tags.setVisible(true);
		nodeInfo=new JDialog(gui);
		nodeInfo.setTitle("Images");
		nodeInfo.setSize(450, 300);
		textArea= new JLabel(sel.getNodeInfo(gui.bm.map.edges, gui.bm.map.nodes));
		scroll=new JScrollPane(textArea);
		nodeInfo.add(scroll);
		nodeInfo.setLocation(1050,400);
		nodeInfo.setVisible(true);
		//FileMethods.saveFile(sel.getNodesContent(), "Node_"+String.valueOf(gui.bm.map.nodes.indexOf(sel)), false);
		//createChart(sel);
			
		presenceData = new JLabel(sel.getTop10());
		scroll2 = new JScrollPane(presenceData);
		top = new JDialog(gui);
		top.add(scroll2);
		top.setSize(450, 300);
		top.setLocation(850,400);
		top.setVisible(true);
		
		 tagCloudDialog =new JDialog(gui);
		 tagCloudDialog.setTitle("Tags Cloud");
		 	     
		 JLabel lab =  new JLabel(new ImageIcon(sel.getTagCloudImage()));
		 tagCloudDialog.setSize(new Dimension(lab.getIcon().getIconWidth(),lab.getIcon().getIconHeight()+50));
		 tagCloudDialog.add(lab);
		 tagCloudDialog.setLocation(850,0);
		// tagCloudDialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 
		 JButton pdf = new JButton("Save as pdf");
		 pdf.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//System.out.println("aloha world");
					try {
						savePDFTagCloud(lab);
					} catch (DocumentException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}

				private void savePDFTagCloud(JLabel lab) throws DocumentException, IOException {
					// TODO Auto-generated method stub
					Date date = new Date();
		        	  DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		        	  String imgName = gui.name+"_Node= "+gui.selectedNode+"_"+gui.bm.threshold1+"_"+gui.bm.threshold2+"_"+hourdateFormat.format(date);
		        	  
		        	  
		        	  
		        	 JPanel panel = new JPanel();
		      		panel.add(lab);
		     		// File miDir = new File(".");
		     	    // String c = miDir.getAbsolutePath();

		     	     //elimino el punto (.) nombre del archivo(virtual) que cree para obtener la ruta de la carpeta del proyecto
		     	   //  String ruta = c.substring(0, c.length() - 1);
		           //   ruta += "resultados/" + imgName.trim() + ".png";
		     		
		     		//File fichero = new File(ruta);
		     	    int w = panel.getWidth();
		     	    int h = panel.getHeight();
		     	    
		     	    w= lab.getIcon().getIconWidth();
		     	    h= lab.getIcon().getIconHeight();
		     	    
		     	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		     	    Graphics2D g = bi.createGraphics();
		     	    lab.getIcon().paintIcon(lab, g, 0, 0);
		     	    
		     	    Image imagen = Image.getInstance(bi,null);
		     	    
		     	      Document document = new Document();
		     	        //document.setPageSize(PageSize.A5);
		     	        document.setPageSize(new com.itextpdf.text.Rectangle(w,h));
		     	       document.setMargins(0, 0, 0, 0);
		     	      //  document.setMargins(36, 72, 108, 180);
		     	        // step 2
		     	        PdfWriter.getInstance(document, new FileOutputStream("resultados/" + imgName.trim() + "pdf"));
		     	        // step 3
		     	        document.open();
		     	        // step 4
		     	        // document.add(new Paragraph("Hello World"));
		     	        document.add(imagen);
		     	        // step 5
		     	        document.close();
		     	    
				}
			});
		 
		 JButton h = new JButton("Save as Image");
		 h.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println("aloha world");
				saveTagCloud(lab);
				
			}

			private void saveTagCloud(JLabel lab) {
				// TODO Auto-generated method stub
				Date date = new Date();
	        	  DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	        	  String imgName = gui.name+"_Node= "+gui.selectedNode+"_"+gui.bm.threshold1+"_"+gui.bm.threshold2+"_"+hourdateFormat.format(date);
	        	  
	        	  
	        	  
	        	 JPanel panel = new JPanel();
	      		panel.add(lab);
	     		 File miDir = new File(".");
	     	     String c = miDir.getAbsolutePath();

	     	     //elimino el punto (.) nombre del archivo(virtual) que cree para obtener la ruta de la carpeta del proyecto
	     	     String ruta = c.substring(0, c.length() - 1);
	              ruta += "resultados/" + imgName.trim() + ".png";
	     		
	     		File fichero = new File(ruta);
	     	    int w = panel.getWidth();
	     	    int h = panel.getHeight();
	     	    
	     	    w= lab.getIcon().getIconWidth();
	     	    h= lab.getIcon().getIconHeight();
	     	    
	     	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	     	    Graphics2D g = bi.createGraphics();
	     	    lab.getIcon().paintIcon(lab, g, 0, 0);
	     	   // panel.paint(g);
	     	    
	     	    try {
	     			ImageIO.write(bi, "png", fichero);
	     			System.out.println("Image " +imgName +" Saved");
	     		} catch (IOException e) {
	     			System.out.println("Writing Error");
	     		}
	        	  
	        	 	
			}
		});
		
		 JPanel f = new JPanel();
		 f.add(h);
		 f.add(pdf);
		 f.add(lab);
		 tagCloudDialog.add(f);
		 tagCloudDialog.setVisible(true);
			
		
	}
	
	public void showMapInfo(){
		if(mapInfoDialog!=null){
			mapInfoDialog.dispose();
			mapInfoDialog=null;
		}	
			mapInfoDialog = new JDialog(gui);
			mapInfoDialog.setSize(700, 500);
			
			JLabel dat = new JLabel(gui.bm.map.printMetricTable(MaxDistance()));
			JScrollPane scroll = new JScrollPane(dat);
			mapInfoDialog.setTitle("Map Metrics Information");
			mapInfoDialog.setContentPane(scroll);
			mapInfoDialog.setVisible(true);
		
	}
	
	
	public void showNodeDetails() {
		if(nodeDetailsDialog!=null){
			nodeDetailsDialog.dispose();
			nodeDetailsDialog=null;
		}
		
		String text = gui.bm.map.getMapInfo(gui.name, String.valueOf(gui.bm.threshold1), String.valueOf(gui.bm.threshold2), String.valueOf(gui.bm.cutNode));
			
		nodeDetailsDialog = new JDialog(gui);
		nodeDetailsDialog.setSize(450, 300);
		
		JLabel dat = new JLabel(text);
		JScrollPane scroll = new JScrollPane(dat);
		nodeDetailsDialog.setTitle("Top10 Nodes Tags");
		nodeDetailsDialog.setContentPane(scroll);
		nodeDetailsDialog.setVisible(true);
		
		//FileMethods.saveFile(text, gui.name+"_Node_Data", false);
			
	}
	
	
	public void createImage(String name) {

		JPanel panel = this;
		
		 File miDir = new File(".");
	     String c = miDir.getAbsolutePath();

	     //elimino el punto (.) nombre del archivo(virtual) que cree para obtener la ruta de la carpeta del proyecto
	     String ruta = c.substring(0, c.length() - 1);
         ruta += "resultados/" + name.trim() + ".png";
		
		File fichero = new File(ruta);
	    int w = panel.getWidth();
	    int h = panel.getHeight();
	    
	    w= img2.getIconWidth();
	    h= img2.getIconHeight();
	    
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    panel.paint(g);
	    
	    try {
			ImageIO.write(bi, "png", fichero);
			System.out.println("Image " +name +" Saved");
		} catch (IOException e) {
			System.out.println("Writing Error");
		}
	      
	}
	
		
	/*
	public void  createChart(Node sel){
		
		   final CategoryDataset dataset = sel.getDataset();
		      
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Tags Probability Mean",         // chart title
	                "",               // domain axis label
	                "",                  // range axis label
	                dataset,                  // data
	                PlotOrientation.HORIZONTAL, // orientation
	                false,                     // include legend
	                true,                     // tooltips?
	                false                     // URLs?
	            );

	            // set the background color for the chart...
	            chart.setBackgroundPaint(Color.white);

	            // get a reference to the plot for further customisation...
	            final CategoryPlot plot = chart.getCategoryPlot();
	            plot.setBackgroundPaint(Color.lightGray);
	            plot.setDomainGridlinePaint(Color.white);
	            plot.setRangeGridlinePaint(Color.white);

	            // set the range axis to display integers only...
	            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
	                  
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new Dimension(500, 270));
	        
	        graf = new JDialog(gui);
            graf.setContentPane(chartPanel);
            graf.setLocation(1050,650);
            graf.setSize(700, 400);
            graf.setTitle("Tag Histogram");
            graf.setVisible(true);
		
		
	}*/
		
	public void mousePressed(MouseEvent evt) {
		Node sel=null;
		
		if (gui.mapGenerated && gui.showMap) {
			if (tags!=null) {
				tags.dispose();
				nodeInfo.dispose();
				//graf.dispose();
				top.dispose();
				tagCloudDialog.dispose();
				gui.nodes.setSelectedIndex(0);
				tags=null;
				nodeInfo=null;
				//graf=null;
				top = null;
				tagCloudDialog=null;
			}
			for (Node n:gui.bm.map.nodes) {
				if (distance(evt, n) < radius) {
					sel=n;
					System.out.print(gui.bm.map.nodes.indexOf(n)+" ");
				}
			}
			if (sel!=null)  {
				System.out.println(" ");
				showInfo(sel);
			}
		}
	}
	
	public void mouseClicked (MouseEvent evt) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

}

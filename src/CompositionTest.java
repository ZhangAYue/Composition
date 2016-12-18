import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class CompositionTest {
	public static void main(String args[]){
		String str="";
		String[] array=new String[2];
		Scanner in=new Scanner(System.in);
		Tree t=new Tree();
		HashMap<String, Composition> nameTable=t.getData();
		while(true){
			System.out.println("\n在节点名称前输入指令(用空格隔开)：layer,depth,children,ancestrors,subtree,q for quit");
			str=in.nextLine();
			if(str.equalsIgnoreCase("q")){
				System.out.println("bye");
				return;
			}
			array=str.split(" ");
			
			Composition temp=nameTable.get(array[1]);
			
			if(temp==null)
				System.out.println("输入节点名称错误");
			else if(temp.operation(array[0])==0)
				System.out.println("输入指令错误");
		}
	}
}

abstract class Component{
	protected ArrayList<Component> children;
	protected String siteName;
	protected Component father;
	protected int layer;
	
	public String getSiteName(){
		return siteName;
	}
	public String getFatherName(){
		if(father==null)
			return null;
		return father.getSiteName();
	}
	public int getLayer(){
		return layer;
	}
	
	abstract public void add(Component child);
	abstract public void remove(Component child);
	abstract public int operation(String mandate);
	abstract public void resetLayer();
	abstract public boolean hasChild();
	abstract public ArrayList<Component> getChildren();
}

class Composition extends Component{
	Composition(String name, Composition father) {
		siteName=name;
		children=new ArrayList();
		if(father==null){
			this.father=null;
			layer=0;
		}
		else{
			this.father=father;
			father.add(this);
		}
	}

	public void setFather(Composition f){
		this.father=f;
	}
	
	public void resetLayer(){
		this.layer=father.getLayer()+1;
	}
	
	public Composition getFather(){
		if(father==null)
			return null;
		return (Composition) father;
	}
	
	public void add(Component child) {
		children.add(child);
	}

	public void remove(Component child) {
		children.remove(child);
	}

	public boolean hasChild(){
		return !children.isEmpty();
	}
	
	public ArrayList<Component> getChildren(){
		return children;
	}
	
	public int getDepth() {
		int depth=1;
		ArrayList<Component> c=new ArrayList();
		c.addAll(this.getChildren());
		int i=0;
		if(!c.isEmpty())
			depth++;
		while(i<c.size()){
			if(c.get(i).hasChild()){
				c.addAll(c.get(i).getChildren());
				depth++;
			}
			i++;
		}
		return depth;
	}

	public void printAncestors(Composition c) {
		if(c.getFatherName()==null)
			return;
		else{
			System.out.println(c.getFatherName());
			c=c.getFather();
			printAncestors(c);
		}
	}
	
	public int operation(String mandate){
		switch (mandate) {
		case "layer":{
			System.out.println("节点  "+siteName+" 在树的第"+getLayer()+"层");
			return 1;
		}
		case "depth":{
			System.out.println("以结点 "+getSiteName()+" 为根的子树深度为 "+getDepth());
			return 1;
		}
		case "children":{
			int i=0;
			while(i<children.size()){
				if(i+1==children.size())
					System.out.println(children.get(i).getSiteName());
				else
					System.out.print(children.get(i).getSiteName()+",");
				i++;
			}
			return 1;
		}
		case "ancestors":{
			printAncestors(this);
			return 1;
		}
		case "subtree":{
			System.out.println(siteName);
			ArrayList<Component> al= new ArrayList();
			al.addAll(getChildren());
			Component temp;
			int i=0;
			while(i<al.size()){
				temp=al.get(i);
				if(i+1==al.size()||al.get(i+1)==null){
					System.out.println(temp.getSiteName());
					i++;
				}
				else
					System.out.print(temp.getSiteName()+",");
				if(temp.hasChild()){
					al.add(null);
					al.addAll(temp.getChildren());
				}
				i++;
			}
			return 1;
		}
		default:{
			return 0;
		}
		}
	}
}

class Tree{
	private Composition root;
	private File f;
	private BufferedReader reader;
	private String temp1,temp2;
	private HashMap <String, String>son_father=new HashMap<String, String>();
	private HashMap <String, Composition>nameTable=new HashMap<String, Composition>();
	
	Tree(){
		setData();
	}
	
	private void setData(){
		String[] array=new String[2];
		f=new File("TreeInfo.txt");
		try {
			reader = new BufferedReader(new FileReader(f));
			while((temp2=reader.readLine())!=null){
				array=temp2.split(", ");
				son_father.put(array[0],array[1]);
				nameTable.put(array[0], new Composition(array[0], null));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	//	root=nameTable.get(son_father.get("null"));
		Iterator it=son_father.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			temp1=(String)entry.getKey();
			temp2=(String)entry.getValue();
			if(temp2.equalsIgnoreCase("null"))
				root=nameTable.get(temp1);
			else{
				nameTable.get(temp1).setFather(nameTable.get(temp2));
				nameTable.get(temp2).add(nameTable.get(temp1));
			}
		}
		
		ArrayList<Component> al= new ArrayList();
		al.addAll(root.getChildren());
		Component temp;
		for(int i=0; i<al.size();i++){
			temp=al.get(i);
			temp.resetLayer();
			if(temp.hasChild()){
				al.addAll(temp.getChildren());
			}
		}
	//	f.close();
	}
	
	public HashMap<String, Composition> getData(){
		return nameTable;
	}
}


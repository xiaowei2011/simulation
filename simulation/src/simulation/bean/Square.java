package simulation.bean;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @autor sunweijie
 * @since 2018年3月20日 下午7:33:09
 */
public class Square {
	
	//场地长度
	double length;
	//场地宽度
	double width;
	//节点数
	Node[] nodes;
	//运行时间
	int runTime;
	//当前时刻
	int curTime;
	//是否对称（所有节点占空比相同）
	boolean symmetrical;
	//动态
	public static boolean dynamic;
	public static double speed = 0.05;
	//最小移动速度
	public static double minSpeed = 0.01;
	//最大移动速度
	public static double maxSpeed = 0.1;
	
	BufferedWriter  writer;
	
	double dc;
	
	Random random = new Random();
	//采样间隔
	public static int sampleInterval = 100;
	
	static Map<Integer, double[][]> positions = new HashMap<>();
	
	public Square() {}
	
	public Square(double length, double width, int runTime) {
		this.length = length;
		this.width = width;
		this.runTime = runTime;
	}
	
	public void initNodes(Class<? extends Node> clazz, int nodeNum, double dutyCycle){
		double dc = dutyCycle;
		this.dc = dc;
		nodes = new Node[nodeNum];
		double[][] position = positions.get(nodeNum);
		if(position == null) {
			position = new double[nodeNum][2];
			for(int i = 0; i < position.length; i++) {
				position[i][0] = random.nextDouble() * length;
				position[i][1] = random.nextDouble() * width;
			}
			positions.put(nodeNum, position);
		}
		Constructor<?> con;
		try {
			con = clazz.getConstructor(double.class, int.class);
			for(int i = 0; i < nodes.length; i++) {
				nodes[i] = (Node) con.newInstance(dc, runTime);
				nodes[i].posX =  position[i][0];
				nodes[i].posY =  position[i][1];
				if(dynamic) {
					 calculateTargetAndSpeed(nodes[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void discovery() throws IOException {
		for(int i = 0; i < nodes.length; i++) {
			for(int j = i + 1; j < nodes.length; j++) {
				nodes[i].discovery(nodes[j], curTime);
			}
			if(nodes[i].logicNeighbors.size() == 0) {
				nodes[i].CDF = 0;
			}else {	
				nodes[i].CDF = (double)nodes[i].neighbors.size() / nodes[i].logicNeighbors.size();
			}
//			System.out.println("neighbors.size:" + nodes[i].neighbors.size());
//			System.out.println("logicNeighbors.size:" + nodes[i].logicNeighbors.size());
		}
		Node.AVG_CDF = (double) Node.NEIGHBOR_NUM / Node.LOGIC_NEIGHBOR_NUM;
		if(curTime % sampleInterval == 0 || Node.AVG_CDF == 1) {
			writer.write(String.format("%d %s\n", curTime, Node.AVG_CDF));		
		}
//		System.out.printf("time:%d, CDF:%s\n", curTime, Node.AVG_CDF);
//		System.out.printf("time:%d, 进度:%%%f\n", curTime, Node.AVG_CDF * 100);
		if(dynamic)
		for(int i = 0; i < nodes.length; i++) {
			move(nodes[i]);
		}
		curTime++;
	}

	public void run(String file) {
		Node.init();
		curTime = 0;
		try (BufferedWriter  writer = new BufferedWriter(new FileWriter(file))){	
			this.writer = writer;
			while(curTime < runTime) {
				discovery();
				if(Node.AVG_CDF == 1) {
					break;
				}
			}
			System.out.println("NEIGHBOR_NUM:" + Node.NEIGHBOR_NUM);
			System.out.println("Time:" + curTime);
			System.out.println("AVG_CDF:" + Node.AVG_CDF);
			System.out.println("AVG_DISCOVERY_TIME:" + Node.AVG_DISCOVERY_TIME);
			Node.AVG_INC_SLOT = Node.TOTAL_INC_SLOT / curTime;
			System.out.println("AVG_INC_SLOT:" + Node.AVG_INC_SLOT);
			calculateIncSlot();
//			System.out.printf("INC:%d,%d,%d,%d,%d\n", nodes[0].INC_SLOT, nodes[20].INC_SLOT, nodes[80].INC_SLOT, nodes[100].INC_SLOT, nodes[199].INC_SLOT);
			System.out.println("---------------------------");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 节点移动
	void move(Node node) {
		// 到达目标
		if (node.targetPosX - node.posX <= node.speedX && node.targetPosY - node.posY <= node.speedY) {
			calculateTargetAndSpeed(node);
		}
		node.posX += node.speedX;
		node.posY += node.speedY;
	}

	void calculateTargetAndSpeed(Node node) {
		node.targetPosX = random.nextDouble() * length;
		node.targetPosY = random.nextDouble() * width;
		double dif = maxSpeed - minSpeed;
		node.speed = minSpeed + random.nextDouble() * dif;
		double t = Math.sqrt(Math.pow(node.targetPosX - node.posX, 2) + Math.pow(node.targetPosY - node.posY, 2))
				/ node.speed;
		node.speedX = (node.targetPosX - node.posX) / t;
		node.speedY = (node.targetPosY - node.posY) / t;
	}
	
	void calculateIncSlot() {
//		int count = 0;
		int as = 0;
		for(Map.Entry<Integer, Integer> entry : Node.activeSlot.entrySet()) {
//			count += entry.getValue();
			if(entry.getKey() < curTime) {
				as += entry.getValue();
			}
		}
//		Node.TOTAL_INC_SLOT = as / nodes.length;
		double avg = (double) as / nodes.length / curTime;
		Node.AVG_INC_SLOT_RATE = avg / dc;
		System.out.println("INC_RATE:" + avg + "," + Node.AVG_INC_SLOT_RATE);
//		System.out.printf("total active count:%d, %d\n", count, as);
	}
}

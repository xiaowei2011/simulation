package simulation.bean;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import simulation.Disco;

/**
 * @autor sunweijie
 * @since 2018年3月20日 下午7:33:09
 */
public class DiscoSquare {
	
	//场地长度
	static double length;
	//场地宽度
	static double width;
	//节点数
	Node[] nodes;
	//运行时间
	int runTime;
	//当前时刻
	int curTime;
	//是否对称（所有节点占空比相同）
	boolean symmetrical;
	//动态
	public static boolean dynamic = false;
	public static double speed = 0.05;
	//最小移动速度
	public static double minSpeed = 0.01;
	//最大移动速度
	public static double maxSpeed = 0.1;
	
	BufferedWriter  writer;
	
	double dc;
	
	static Random random = new Random();
	//采样间隔
	public static int sampleInterval = 100;
	
	//键->占空比，值->节点/时间/位置/目标位置/速度
	static Map<Double, double[][][]> dynamicPositions = new HashMap<>();
	//键->占空比，值->节点/位置
	static Map<Double, double[][]> staticPositions = new HashMap<>();
	//键->占空比，值->节点/偏移量
	static Map<Double, int[]> offsets = new HashMap<>();
	
	static{
		init(0.02, 10000, 200);
	}
	
	public static void init(double maxDutyCycle, int maxTime, int maxNodeNum) {
		dynamicPositions.clear();
		staticPositions.clear();
		offsets.clear();
		double dc = 0.02;
		for(; dc <= maxDutyCycle; dc += 0.02) {
			double[][][] dp = new double[maxNodeNum][maxTime][7];
			double[][] sp = new double[maxNodeNum][2];
			int[] of = new int[maxNodeNum];
			for(int i = 0; i < maxNodeNum; i++) {
				double[] p = new double[2];
				p[0] =  random.nextDouble() * length;
				p[1] =  random.nextDouble() * length;
				sp[i] = p;
				double[][] dpp = new double[maxTime][7];
				dpp[0][0] = p[0];
				dpp[0][1] = p[1];
				calculateTargetAndSpeed(dpp[0]);
				for(int j = 1; j < maxTime; j++) {
					dpp[j] = getNextPosition(dpp[j - 1]);
				}
				dp[i] = dpp;
				int[] prime = Disco.findPrimePair(dc);
				of[i] = random.nextInt(prime[0] * prime[1]);
			}
			dynamicPositions.put(dc, dp);
			staticPositions.put(dc, sp); 
			offsets.put(dc, of);
		}
	}
	
	public DiscoSquare() {}
	
	public DiscoSquare(double length, double width, int runTime) {
		Square.length = length;
		Square.width = width;
		this.runTime = runTime;
	}
	
	public void initNodes(Class<? extends Node> clazz, int nodeNum, double dutyCycle){
		double dc = dutyCycle;
		this.dc = dc;
		nodes = new Node[nodeNum];
		int[] of = offsets.get(dc);
		Constructor<?> con;
		try {
			con = clazz.getConstructor(double.class, int.class, int.class);
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = (Node) con.newInstance(dc, runTime, of[i]);
				nodes[i].id = i;
				if (dynamic) {
					double[][][] dp = dynamicPositions.get(dc);
					double[] p = dp[i][0];
					nodes[i].posX = p[0];
					nodes[i].posY = p[1];
					nodes[i].targetPosX = p[2];
					nodes[i].targetPosY = p[3];
					nodes[i].speed = p[4];
					nodes[i].speedX = p[5];
					nodes[i].speedY = p[6];
				} else {
					double[][] sp = staticPositions.get(dc);
					nodes[i].posX = sp[i][0];
					nodes[i].posY = sp[i][1];
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
//		if(curTime % sampleInterval == 0 || Node.AVG_CDF == 1) {
//			writer.write(String.format("%d %s\n", curTime, Node.AVG_CDF));		
//		}
//		System.out.printf("time:%d, CDF:%s\n", curTime, Node.AVG_CDF);
//		System.out.printf("time:%d, 进度:%%%f\n", curTime, Node.AVG_CDF * 100);
		if(dynamic) {
			for(int i = 0; i < nodes.length; i++) {
				move(nodes[i]);
			}
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
		double[][][] dp = dynamicPositions.get(node.dutyCycle);
		node.cur++;
		double[] p = dp[node.id][node.cur];
		node.posX = p[0];
		node.posY = p[1];
		node.targetPosX = p[2];
		node.targetPosY = p[3];
		node.speed = p[4];
		node.speedX = p[5];
		node.speedY = p[6];
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
	
	static void calculateTargetAndSpeed(double[] p) {
		p[2] = random.nextDouble() * length;
		p[3] = random.nextDouble() * width;
		double dif = maxSpeed - minSpeed;
		p[4] = minSpeed + random.nextDouble() * dif;
		double t = Math.sqrt(Math.pow(p[2] - p[0], 2) + Math.pow(p[3] - p[1], 2))
				/ p[4];
		p[5] = (p[2] - p[0]) / t;
		p[6] = (p[3] - p[1]) / t;
	}
	
	static double[] getNextPosition(double[] po) {
		double[] p = Arrays.copyOf(po, po.length);
		if (p[2] - p[0] <= p[5] && p[3] - p[1] <= p[6]) {
			calculateTargetAndSpeed(p);
		}
		p[0] += p[5];
		p[1] += p[6];
		return po;
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

package simulation.group;

import simulation.Disco;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月23日 下午3:38:41
 */
public class Main {
	
	public static void main(String[] args) {
//		avgDTandNodeNum();
//		avgDTandDC();
//		cdfbaseDisco();
		cdfOneHop();
		System.out.println("发现完成");
	}
	
	public static void cdfAndDOI() {
		Square square = new Square(500, 500, 10000);
		Square.dynamic = true;
		Square.minSpeed = Square.maxSpeed = 0.02;
		int n = 6, m = 3;
		double[][][] result = new double[3][n][3];
		double dc = 0.02;
		int nodeNum = 200;
		for(int i = 0; i < n; i++, Node.DOI += 0.1) {
			System.out.println("DOI:" + Node.DOI);
			square.initNodes(Disco.class, nodeNum, dc);
			System.out.println("Disco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
			result[0][i][0] = Node.AVG_DISCOVERY_TIME;
			result[0][i][1] = Node.AVG_INC_SLOT_RATE;
			result[0][i][2] = Node.AVG_CDF;
			
			square.initNodes(GroupBaseDisco.class, nodeNum, dc);
			System.out.println("GroupBaseDisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
			result[1][i][0] = Node.AVG_DISCOVERY_TIME;
			GBAADisco.ENBR = result[1][i][1] = Node.AVG_INC_SLOT_RATE;
			result[1][i][2] = Node.AVG_CDF;
			
			square.initNodes(GBAADisco.class, nodeNum, dc);
			System.out.println("GBAADisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBAADisco.txt");
			result[2][i][0] = Node.AVG_DISCOVERY_TIME;
			result[2][i][1] = Node.AVG_INC_SLOT_RATE;
			result[2][i][2] = Node.AVG_CDF;
			System.out.println("........................");
		}
		 printResult(result, n, m);
	}
	
	public static void cdfOneHop() {
		Square square = new Square(100, 100, 10000);
		Square.write = true;
		int nodeNum = 200;
		double dc = 0.02;
		GroupBaseDisco.NBP = 0.5;
		Node.radioRange = 150;
		square.initNodes(Disco.class, nodeNum, dc);
		System.out.println("Disco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		
		square.initNodes(GroupBaseDisco.class, nodeNum, dc);
		System.out.println("GroupBaseDisco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
		
		square.initNodes(GBFADisco.class, nodeNum, dc);
		System.out.println("GBFADisco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
	}
	
	public static void cdfbaseDisco() {
		Square square = new Square(500, 500, 10000);
		int nodeNum = 200;
		double dc = 0.02;
		square.initNodes(Disco.class, nodeNum, dc);
		System.out.println("Disco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		
//		square.initNodes(SearchLight.class, nodeNum, dc);
//		System.out.println("SearchLight");
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\searchLight.txt");
		
		square.initNodes(GroupBaseDisco.class, nodeNum, dc);
		GroupBaseDisco.NBP = 0.5;
		System.out.println("GroupBaseDisco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
		
		square.initNodes(GBFADisco.class, nodeNum, dc);
		GBFADisco.NBP = 0.7;
		System.out.println("GBFADisco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
		
//		square.initNodes(GroupBaseSearchLight.class, nodeNum, dc);
//		System.out.println("GroupBaseSearchLight");
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseSearchLight.txt");
//		
//		square.initNodes(GBFASearchLight.class, nodeNum, dc);
//		System.out.println("GBFASearchLight");
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFASearchLight.txt");
	}
	
	//静态场景下平均发现延迟受占空比的影响
	public static void avgDTandDC() {
		Square square = new Square(500, 500, 50000);
//		Square.dynamic = true;
//		Square.maxSpeed = Square.minSpeed = 0.02;
		int n = 5;
		int nodeNum = 200;
		double[][][] result = new double[3][n][3];
		double dc = 0.02;
		GroupBaseDisco.NBP = 1.4;
		GBFADisco.NBP = 0.4;
		GBAADisco.ENBR = 0.1;
		for(int i = 0; i < n; i++, dc += 0.02) {
			System.out.println(dc);
			square.initNodes(Disco.class, nodeNum, dc);
			System.out.println("Disco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
			result[0][i][0] = Node.AVG_DISCOVERY_TIME;
			result[0][i][1] = Node.AVG_INC_SLOT_RATE;
			result[0][i][2] = Node.AVG_CDF;
			
			square.initNodes(GroupBaseDisco.class, nodeNum, dc);
			System.out.println("GroupBaseDisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
			result[1][i][0] = Node.AVG_DISCOVERY_TIME;
			result[1][i][1] = Node.AVG_INC_SLOT_RATE;
			result[1][i][2] = Node.AVG_CDF;
			
			square.initNodes(GBFADisco.class, nodeNum, dc);
			System.out.println("GBFADisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
			result[2][i][0] = Node.AVG_DISCOVERY_TIME;
			result[2][i][1] = Node.AVG_INC_SLOT_RATE;
			result[2][i][2] = Node.AVG_CDF;
			System.out.println("........................");
		}
		printResult(result, n, 3);
	}
	
	//静态场景下平均发现延迟受节点密度的影响
	public static void avgDTandNodeNum() {
		Square square = new Square(500, 500, 10000);
		Square.dynamic = true;
		Square.maxSpeed = Square.minSpeed = 0.05;
//		Node.radioRange = 100;
		GroupBaseDisco.NBP = 0.8;
		GBFADisco.NBP = 0.6;
		int n = 20;
		double[][][] result = new double[3][n][3];
		double dc = 0.02;
		int nodeNum = 32;
		for(int i = 0; i < n; i++, nodeNum += 32) {
			System.out.println("节点密度" + (i + 1));
			square.initNodes(Disco.class, nodeNum, dc);
			System.out.println("Disco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
			result[0][i][0] = Node.AVG_DISCOVERY_TIME;
			result[0][i][1] = Node.AVG_INC_SLOT_RATE;
			result[0][i][2] = Node.AVG_CDF;
			
			square.initNodes(GroupBaseDisco.class, nodeNum, dc);
			System.out.println("GroupBaseDisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
			result[1][i][0] = Node.AVG_DISCOVERY_TIME;
			GBAADisco.ENBR = result[1][i][1] = Node.AVG_INC_SLOT_RATE;
			result[1][i][2] = Node.AVG_CDF;
			
			square.initNodes(GBFADisco.class, nodeNum, dc);
			System.out.println("GBFADisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
			result[2][i][0] = Node.AVG_DISCOVERY_TIME;
			result[2][i][1] = Node.AVG_INC_SLOT_RATE;
			result[2][i][2] = Node.AVG_CDF;
			System.out.println("........................");
		}
		 printResult(result, n, 3);
	}
	
	static void avgDTandSpeed() {
		Square square = new Square(500, 500, 10000);
		Square.dynamic = true;
		GroupBaseDisco.NBP = 0.5;
		GBAADisco.ENBR = 0.1;
		int n = 9, m = 3;
		double[][][] result = new double[3][n][3];
		double dc = 0.05;
		int nodeNum = 200;
		double speed = 0;
		for(int i = 0; i < n; i++, speed += 0.05) {
			Square.maxSpeed = Square.minSpeed = Square.speed =speed;
			System.out.println("节点速度" + speed);
			square.initNodes(Disco.class, nodeNum, dc);
			System.out.println("Disco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
			result[0][i][0] = Node.AVG_DISCOVERY_TIME;
			result[0][i][1] = Node.AVG_INC_SLOT_RATE;
			result[0][i][2] = Node.AVG_CDF;
			
			square.initNodes(GroupBaseDisco.class, nodeNum, dc);
			System.out.println("GroupBaseDisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
			result[1][i][0] = Node.AVG_DISCOVERY_TIME;
			result[1][i][1] = Node.AVG_INC_SLOT_RATE;
			result[1][i][2] = Node.AVG_CDF;
			
			square.initNodes(GBAADisco.class, nodeNum, dc);
			System.out.println("GBFADisco");
			square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
			result[2][i][0] = Node.AVG_DISCOVERY_TIME;
			result[2][i][1] = Node.AVG_INC_SLOT_RATE;
			result[2][i][2] = Node.AVG_CDF;
			System.out.println("........................");
		}
		 printResult(result, n, m);
	}
	
	static void printResult(double[][][] result, int n, int m) {
		System.out.println("Discovery Latency:");
		for(int i = 0; i < 3; i++) {
			System.out.print(i);
			for(int j = 0; j < n; j++) {	
				System.out.print("," + result[i][j][0]);
			}
			System.out.println();
		}
		System.out.println("INC_SLOT_RATE:");
		for(int i = 0; i < 3; i++) {
			System.out.print(i);
			for(int j = 0; j < n; j++) {	
				System.out.print("," + result[i][j][1]);
			}
			System.out.println();
		}
		if(m > 2) {
			System.out.println("AVG_CDF:");
			for(int i = 0; i < 3; i++) {
				System.out.print(i);
				for(int j = 0; j < n; j++) {	
					System.out.print("," + result[i][j][2]);
				}
				System.out.println();
			}
		}
	}
	
}

package simulation.overlap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @autor sunweijie
 * @since 2018年3月11日 下午5:01:03
 */
public class Overlap {
	
	double R = 1;
	double r;
	double d;
	double inc = 0.01;
	double right;
	double top;
	double ix, iy, jx, jy;
	public Overlap(double d, double r) {
		this.d = d;
		this.r = r;
		right = 2 * R - d;
		top = 2 * Math.sqrt(R * R - d * d / 4);
		iy = jy = top / 2;
		ix = R - d;
		iy = R; 
	}
	public Map<Point, Double[]> overlapArea() {
		Map<Point, Double[]> overlap = new HashMap<>();
		Set<Point> shadow = shadowRegion();
		Set<Point> region = overlapRegion();
		System.out.println(shadow.size());
		System.out.println(region.size());
		double d = r * r;
		int count, area = circleArea(r);
		for(Point pr : region) {
			count = 0;
			Double[] value = new Double[3];
			for(Point ps : shadow) {
				if(dis(ps.x, ps.y, pr.x, pr.y) <= d) {
					count++;
				}
			}
			double di = Math.sqrt(dis(pr.x, pr.y, ix, iy));
			double dj = Math.sqrt(dis(pr.x, pr.y, jx, jy));
			value[1] = di;
			value[0] = dj;
			value[2] = (double)count/area;
			overlap.put(pr, value);
		}
		return overlap;
	}
	
	private Set<Point> overlapRegion() {
		Set<Point> region = new HashSet<>();
		double x, y, di, dj, d;
		d = R * R;
		for(x = 0; x <= right; x += inc) {
			for(y = 0; y <= top; y += inc) {
				di = dis(x, y, ix, iy);
				dj = dis(x, y, jx, jy);
				if(di <= d && dj <= d) {	
					region.add(new Point(x, y));
				}
			}
		}
		return region;
	}
	
	private Set<Point> shadowRegion() {
		Set<Point> shadow = new HashSet<>();
		double x, y, di, dj, d;
		d = R * R;
		for(x = 0; x <= right; x += inc) {
			for(y = 0; y <= top; y += inc) {
				di = dis(x, y, ix, iy);
				dj = dis(x, y, jx, jy);
				if(di >= d && dj <= d) {	
					shadow.add(new Point(x, y));
				}
			}
		}
		return shadow;
	}
	
	private int circleArea(double r) {
		int count = 0;
		double d = r * r;
		for(double x = -r; x <= r; x += inc) {
			for(double y = -r; y <= r; y += inc) {
				if(x * x + y * y <= d) {
					count++;
				}
			}
		}
		return count;
	}
	
	private double dis(double x1, double y1, double x2, double y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
	
	public static void output(String path, Map<Point, Double[]> overlap) {
		File file = new File(path);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			Point p;
			for(Map.Entry<Point, Double[]> e : overlap.entrySet()) {
				p = e.getKey();
				Double[] v = e.getValue();
				writer.write(p.x + " " + p.y + " " + v[0] + " " + v[1] + " " + v[2]);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Overlap overlap = new Overlap(1d,0.25);
		Map<Point, Double[]> result = overlap.overlapArea();
		System.out.println(result);
		Overlap.output("C:\\Users\\Administrator\\Desktop\\论文\\数据\\overlap.txt", result);
	}

}

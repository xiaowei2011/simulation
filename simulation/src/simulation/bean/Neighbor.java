package simulation.bean;

/**
 * @autor sunweijie
 * @since 2018年3月22日 上午10:51:58
 */
public class Neighbor implements Comparable<Neighbor>{
	
	public Node node;
	//发现时间
	public int discoveryTime;
	public int TTL = Integer.MAX_VALUE;
	//相遇时间
	public int meetTime;
	
	public Neighbor(Node node, int meetTime) {
		this.node = node;
		this.meetTime = meetTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Neighbor other = (Neighbor) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	@Override
	public int compareTo(Neighbor neighbor) {
		return node.compareTo(neighbor.node);
	}
	
	
}

+ Stream {
	convertArray {
		arg arr = Array.new();
		this.do({
			arg item;
			arr = arr.add(item)
		});
		^arr
	}
}
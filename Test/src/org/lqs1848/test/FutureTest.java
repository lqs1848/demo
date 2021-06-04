package org.lqs1848.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FutureTest {

	public static void main(String[] args) {
		
		List<TestInterface> list = new ArrayList<>();
		list.add(new TestInterface() {
			@Override
			public String async() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return "a";
			}
		});
		list.add(new TestInterface() {
			@Override
			public String async() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return "b";
			}
		});
		
		long startTime = System.currentTimeMillis();
		List<CompletableFuture<String>> ocrFuture = list.stream().map(u -> CompletableFuture.supplyAsync(() -> u.async())).collect(Collectors.toList());
		List<String> resultList = ocrFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
		System.out.println("time:"+(System.currentTimeMillis() - startTime));
		resultList.forEach(str->{
			System.out.println(str);
		});
	}
	
	public interface TestInterface {
		public String async();
	}// class
	
}




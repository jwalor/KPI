package com.arkin.kpi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KpiQuartzApplicationTests {

	@Test
	public void contextLoads() throws InterruptedException {
		System.out.println("");
		
		Callable<Integer> task1 = () -> {
		    try {
		        TimeUnit.SECONDS.sleep(10);
		        return 123;
		    }
		    catch (InterruptedException e) {
		        throw new IllegalStateException("task interrupted", e);
		    }
		};
		
		Callable<Integer> task = () -> {
		    try {
		        TimeUnit.SECONDS.sleep(10);
		        return 123;
		    }
		    catch (InterruptedException e) {
		        throw new IllegalStateException("task interrupted", e);
		    }
		};
		
		List<Callable<Integer>> callables = new ArrayList<>();
		callables.add(task);
		callables.add(task1);
		
		ExecutorService executor = Executors.newWorkStealingPool();
		executor.invokeAll(callables).stream()
			    .map(future -> {
			        try {
			            return future.get();
			        }
			        catch (Exception e) {
			            throw new IllegalStateException(e);
			        }
			    });
		/*Future<Integer> future = 
		System.out.println("future done? " + future.isDone());

		Integer result;
		try {
			result = future.get();
			
			System.out.println("future done? " + future.isDone());
			System.out.print("result: " + result);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		
	}

}
